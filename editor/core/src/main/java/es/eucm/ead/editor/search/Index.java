/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.eucm.ead.editor.search;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.MapEvent;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.editor.game.EditorGame;
import es.eucm.ead.schema.effects.ChangeRenderer;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.game.Game;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Allows easy search operations on the model. Uses Lucene for indexing and
 * retrieval.
 * 
 * @author mfreire
 */
public class Index {

	/**
	 * Lucene index
	 */
	private Directory searchIndex;
	/**
	 * Lucene updater
	 */
	private IndexWriter indexWriter;
	/**
	 * Max search hits in an ordered query
	 */
	private static final int MAX_SEARCH_HITS = 100;
	/**
	 * Query parser for 'all fields' queries
	 */
	private QueryParser queryParser;
	/**
	 * Field analyzer
	 */
	private Analyzer searchAnalyzer;

	/**
	 * Model-object to doc-id
	 */
	private final Map<Object, Integer> modelToIds = new HashMap<Object, Integer>();
	/**
	 * Doc-id to model-object
	 */
	private final Map<Integer, Object> idsToModel = new HashMap<Integer, Object>();
	/**
	 * Model-class to indexed-fields-accessors
	 */
	private final Map<Class, Field[]> indexedFieldsCache = new HashMap<Class, Field[]>();

	private static int nextId = 0;

	/**
	 * Name of field that stores the modelId
	 */
	private static final String MODEL_ID_FIELD_NAME = "_id";
	/**
	 * Name of string-typed field in model classes that stores a comma-separated
	 * list of field names that will be indexed by this Index. For example,
	 * <code>
	 * 	private string indexed = "field1, field2"
	 * </code> would cause the contents of field1 and field2 to be included in
	 * the index
	 */
	private static final String INDEXED_FIELDS_FIELD_NAME = "indexed";

	/**
	 * Returned when no fields have been indexed
	 */
	private static final Field[] NO_FIELDS = new Field[0];

	/**
	 * Configure Lucene indexing
	 */
	public Index() {
		clear();
	}

	/**
	 * Returns the indexed fields of object 'o'. This looks up the
	 * INDEXED_FIELDS_FIELD_NAME property to find which fields should be
	 * considered indexed. Previous results are cached for further use.
	 * 
	 * @param o
	 *            object to inspect for indexed fields
	 * @return the fields (or an empty array if no indexable fields)
	 */
	public Field[] getIndexedFields(Object o) {
		Field[] found = NO_FIELDS;
		Class<?> oc = o.getClass();
		if (!indexedFieldsCache.containsKey(oc)) {
			for (Field f : ClassReflection.getDeclaredFields(oc)) {
				if (f.getName().equals(INDEXED_FIELDS_FIELD_NAME)) {
					try {
						f.setAccessible(true);
						String[] names = f.get(o).toString().trim()
								.split("[, ]+");
						f.setAccessible(false);
						// sorting makes order predictable; this is good for
						// tests
						Arrays.sort(names);
						found = new Field[names.length];
						for (int i = 0; i < names.length; i++) {
							found[i] = ClassReflection.getDeclaredField(oc,
									names[i]);
							found[i].setAccessible(true);
						}
						break;
					} catch (ReflectionException ex) {
						Gdx.app.log("index",
								"Could not access indexed fields in " + oc, ex);
					}
				}
			}
			indexedFieldsCache.put(oc, found);
		}
		return indexedFieldsCache.get(oc);
	}

	/**
	 * Loads a game, inspecting it for indexable fields.
	 * 
	 * @param game
	 */
	public void loadGame(EditorGame game) {
		clear();
		for (Object o : game.getVariablesDefinitions()) {
			refresh(o);
		}
	}

	/**
	 * Loads a scene, inspecting it for indexable fields.
	 * 
	 * @param scene
	 */
	public void loadScene(Scene scene) {
		refresh(scene);
		for (SceneElement e : scene.getChildren()) {
			loadSceneElement(e);
		}
	}

	/**
	 * Recursively indexes a scene-element
	 * 
	 * @param se
	 *            sceneelement to index
	 */
	private void loadSceneElement(SceneElement se) {
		refresh(se);
		refresh(se.getRenderer());
		for (SceneElement e : se.getChildren()) {
			loadSceneElement(e);
		}
		for (Behavior b : se.getBehaviors()) {
			refresh(b);
			loadEffect(b.getEffect());
		}
		for (Effect ef : se.getEffects()) {
			loadEffect(ef);
		}
	}

	/**
	 * Indexes an effect
	 */
	private void loadEffect(Effect ef) {
		refresh(ef);
		if (ef instanceof ChangeRenderer) {
			refresh(((ChangeRenderer) ef).getNewRenderer());
		}
	}

	/**
	 * Listens to events, updating the index as necessary.
	 * 
	 * @param event
	 *            that potentially changes the index
	 */
	public void notify(ModelEvent event) {
		if (event instanceof ListEvent) {
			ListEvent le = (ListEvent) event;
			switch (le.getType()) {
			case ADDED:
				add(le.getElement(), true);
				break;
			case REMOVED:
				remove(le.getElement(), true);
				break;
			}
		} else if (event instanceof MapEvent) {
			MapEvent me = (MapEvent) event;
			switch (me.getType()) {
			case ENTRY_ADDED:
				add(me.getValue(), true);
				break;
			case ENTRY_REMOVED:
				remove(me.getValue(), true);
				break;
			}
		} else {
			refresh(event.getTarget());
		}
	}

	/**
	 * Add indexable fields of an object to the index.
	 * 
	 * @param o
	 *            the object to inspect
	 * @param commit
	 *            if index changes should be immediately committed. If many
	 *            updates are to be performed back-to-back, it is better to
	 *            commit only once.
	 */
	public void add(Object o, boolean commit) {
		if (o == null || getIndexedFields(o).length == 0) {
			// ignore null or un-indexed objects
			return;
		}

		Document doc = new Document();
		if (modelToIds.containsKey(o)) {
			throw new IllegalArgumentException(
					"Cannot add already-present object " + o);
		}
		int id = nextId++;
		modelToIds.put(o, id);
		idsToModel.put(id, o);
		NumericField nf = new NumericField(MODEL_ID_FIELD_NAME,
				org.apache.lucene.document.Field.Store.YES, true);
		nf.setIntValue(id);
		doc.add(nf);

		addFieldsToDoc(o, doc);

		try {
			indexWriter.addDocument(doc);
		} catch (IOException ex) {
			Gdx.app.log("index", "Error indexing newly-created " + o, ex);
		}

		if (commit) {
			try {
				indexWriter.commit();
			} catch (IOException ex) {
				Gdx.app.log("index", "Error committing to index after add", ex);
			}
		}
	}

	/**
	 * Normalizes a query string or an indexable snippet of text.
	 * 
	 * @param queryOrIndexableTerm
	 *            to normalize
	 * @return normalized result, guaranteed to be alphanumeric
	 */
	private static String removeConfusingCharacters(String queryOrIndexableTerm) {
		return queryOrIndexableTerm.replaceAll("[^\\p{Alnum} ]+", " ");
	}

	/**
	 * Adds all indexable fields in an object to its document.
	 * 
	 * @param o
	 *            object to index
	 * @param doc
	 *            to add indexed terms to
	 */
	private void addFieldsToDoc(Object o, Document doc) {
		for (Field f : getIndexedFields(o)) {
			try {
				String value = removeConfusingCharacters(f.get(o).toString());
				Gdx.app.debug("index", f.getName() + ": " + value);
				doc.add(new org.apache.lucene.document.Field(f.getName(),
						value, Store.YES,
						org.apache.lucene.document.Field.Index.ANALYZED));
			} catch (ReflectionException ex) {
				Gdx.app.log(
						"index",
						"Error reading indexed field-values for field "
								+ f.getName() + " of " + o.getClass(), ex);
			}
		}
	}

	/**
	 * Remove references to an object from the index
	 * 
	 * @param o
	 *            the object to remove
	 * @param commit
	 *            if index changes should be immediately committed. If many
	 *            updates are to be performed back-to-back, it is better to
	 *            commit only once.
	 */
	public void remove(Object o, boolean commit) {
		if (o == null || getIndexedFields(o).length == 0) {
			// ignore null or un-indexed objects
			return;
		}

		if (modelToIds.containsKey(o)) {
			int id = modelToIds.get(o);
			idsToModel.remove(id);
			modelToIds.remove(o);
			try {
				indexWriter.deleteDocuments(NumericRangeQuery.newIntRange(
						MODEL_ID_FIELD_NAME, id, id, true, true));
			} catch (IOException ex) {
				Gdx.app.log("index", "Error indexing newly-created " + o, ex);
			}
			if (commit) {
				try {
					indexWriter.commit();
				} catch (IOException ex) {
					Gdx.app.log("index",
							"Error committing to index after remove", ex);
				}
			}
		}
	}

	/**
	 * Updates references to an object in the index. To be called, for example
	 * after potentially-indexable fields have changed values.
	 * 
	 * @param o
	 *            object to update.
	 */
	public void refresh(Object o) {
		if (o == null || getIndexedFields(o).length == 0) {
			// ignore null or un-indexed objects
			return;
		}

		if (modelToIds.containsKey(o)) {
			remove(o, false);
		} else {
			Gdx.app.log("index", "Missing index for " + o
					+ ", adding instead of refreshing");
		}
		add(o, true);
	}

	/**
	 * Purges the contents of this modelIndex
	 */
	public final void clear() {
		modelToIds.clear();
		idsToModel.clear();
		indexedFieldsCache.clear();
		try {
			searchIndex = new RAMDirectory();
			// use a very simple analyzer; no fancy stop-words, stemming, ...
			searchAnalyzer = new WhitespaceAnalyzer(Version.LUCENE_35);
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35,
					searchAnalyzer);
			indexWriter = new IndexWriter(searchIndex, config);
		} catch (IOException e) {
			Gdx.app.log("index", "Could not initialize search index (?)", e);
			throw new IllegalArgumentException(
					"Could not initialize search index (?)", e);
		}
	}

	/**
	 * Lazily create or return the query parser
	 */
	private QueryParser getQueryAllParser() {

		if (queryParser == null) {
			try {
				IndexReader reader = IndexReader.open(searchIndex);

				ArrayList<String> al = new ArrayList<String>(
						reader.getFieldNames(IndexReader.FieldOption.INDEXED));
				al.remove(MODEL_ID_FIELD_NAME);
				String[] allFields = al.toArray(new String[al.size()]);
				queryParser = new MultiFieldQueryParser(Version.LUCENE_35,
						allFields, searchAnalyzer);
				queryParser.setLowercaseExpandedTerms(false);
			} catch (IOException ioe) {
				Gdx.app.log("index", "Error constructing query parser", ioe);
			}
		}
		return queryParser;
	}

	/**
	 * An individual node match for a query, with score and matched fields
	 */
	public static class Match implements Comparable<Match> {
		private HashSet<String> fields = new HashSet<String>();
		private double score;
		private Object o;

		private Match(Object o, double score, String... matchedFields) {
			this.o = o;
			this.score = score;
			if (matchedFields != null) {
				for (String f : matchedFields) {
					fields.add(f);
				}
			}
		}

		private void merge(Match m) {
			this.score += m.score;
			this.fields.addAll(m.fields);
		}

		/**
		 * @return the names of the fields that matched
		 */
		public HashSet<String> getFields() {
			return fields;
		}

		/**
		 * @return the match score (a positive floating-point number; higher =
		 *         better)
		 */
		public double getScore() {
			return score;
		}

		/**
		 * @return the object that matched
		 */
		public Object getObject() {
			return o;
		}

		/**
		 * Sorting support
		 * 
		 * @param o
		 *            another match
		 * @return a score comparison
		 */
		@Override
		public int compareTo(Match o) {
			return Double.compare(o.score, score);
		}
	}

	/**
	 * Represents query results
	 */
	public class SearchResult {

		private final TreeMap<Integer, Match> matches = new TreeMap<Integer, Match>();

		public SearchResult() {
			// used for "empty" searches: no results
		}

		public SearchResult(IndexSearcher searcher, Query query, ScoreDoc[] hits)
				throws IOException {

			try {
				for (ScoreDoc hit : hits) {
					int id = Integer.parseInt(searcher.doc(hit.doc).get(
							MODEL_ID_FIELD_NAME));
					Gdx.app.debug("index", "Adding: " + id + " ...");
					// FIXME: matched fields not extracted yet
					Match m = new Match(idsToModel.get(id), hit.score, null);
					matches.put(id, m);
					Gdx.app.debug("index", "... added " + id);
				}
				searcher.close();
			} catch (CorruptIndexException e) {
				throw new IOException("Corrupt index", e);
			}
		}

		/**
		 * Retrieves matches in this result
		 * 
		 * @return a sorted list of matches
		 */
		public ArrayList<Match> getMatches() {
			ArrayList<Match> all = new ArrayList<Match>(matches.values());
			Collections.sort(all);
			return all;
		}
	}

	/**
	 * Query the index.
	 * 
	 * @param queryText
	 *            textual query.
	 * @return an object with the results of the search: matches will be objects
	 *         that have fields with contents that matched the query.
	 */
	public SearchResult search(String queryText) {

		try {
			IndexReader reader = IndexReader.open(searchIndex);
			queryText = (removeConfusingCharacters(queryText) + " ")
					.replaceAll("([^ ])[ ]+", "$1* ");
			Query query = getQueryAllParser().parse(queryText);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(
					MAX_SEARCH_HITS, true);
			Gdx.app.debug("index", "Looking up: " + query);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			SearchResult sr = new SearchResult(searcher, query, hits);
			return sr;
		} catch (IOException e) {
			Gdx.app.error("index", "Parsing or looking up " + queryText, e);
		} catch (ParseException e) {
			Gdx.app.error("index", "Parsing or looking up " + queryText, e);
		}
		return new SearchResult();
	}
}
