/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

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
	private Map<Object, Integer> modelToIds = new HashMap<Object, Integer>();
	/**
	 * Doc-id to model-object
	 */
	private Map<Integer, Object> idsToModel = new HashMap<Integer, Object>();

	private static final Map<Class, Field[]> indexedFieldsCache = new HashMap<Class, Field[]>();

	private static int nextId = 0;

	/**
	 * Name of field that stores the modelId
	 */
	private static final String modelIdFieldName = "_id";
	/**
	 * Name of field that stores the modelId
	 */
	private static final String indexedFieldName = "_indexed";

	/**
	 * Returned when no fields have been indexed
	 */
	private static final Field[] noFieldsAtAll = new Field[0];

	/**
	 * Configure Lucene indexing
	 */
	public Index() {
		clear();
	}

	public Field[] getIndexedFields(Object o) {
		Field[] found = noFieldsAtAll;
		Class<?> oc = o.getClass();
		if (!indexedFieldsCache.containsKey(oc)) {
			for (Field f : ClassReflection.getDeclaredFields(oc)) {
				if (f.getName().equals(indexedFieldName)) {
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

	public void add(Object o, boolean commit) {
		if (o == null) {
			// ignore null objects
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
		NumericField nf = new NumericField(modelIdFieldName,
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

	private void addFieldsToDoc(Object o, Document doc) {
		for (Field f : getIndexedFields(o)) {
			try {
				String value = f.get(o).toString();
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

	public void remove(Object o, boolean commit) {
		if (o == null) {
			// ignore null objects
			return;
		}

		int id = modelToIds.get(o);
		idsToModel.remove(id);
		modelToIds.remove(o);
		try {
			indexWriter.deleteDocuments(NumericRangeQuery.newIntRange(
					modelIdFieldName, id, id, true, true));
		} catch (IOException ex) {
			Gdx.app.log("index", "Error indexing newly-created " + o, ex);
		}
		if (commit) {
			try {
				indexWriter.commit();
			} catch (IOException ex) {
				Gdx.app.log("index", "Error committing to index after remove",
						ex);
			}
		}
	}

	public void refresh(Object o) {
		if (o == null) {
			// ignore null objects
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
		try {
			searchIndex = new RAMDirectory();
			// use a very simple analyzer; no fancy stopwords, stemming, ...
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
				String[] allFields = al.toArray(new String[al.size()]);
				queryParser = new MultiFieldQueryParser(Version.LUCENE_35,
						allFields, searchAnalyzer);
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

		private Match(Object o, double score, String field) {
			this.o = o;
			this.score = score;
			if (field != null) {
				fields.add(field);
			}
		}

		private void merge(Match m) {
			this.score += m.score;
			this.fields.addAll(m.fields);
		}

		public HashSet<String> getFields() {
			return fields;
		}

		public double getScore() {
			return score;
		}

		public Object getObject() {
			return o;
		}

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
							modelIdFieldName));
					Gdx.app.log("index", "Adding " + id);
					Match m = new Match(idsToModel.get(id), hit.score, null);
					matches.put(id, m);
					Gdx.app.log("index", "Adding " + id);
				}
				searcher.close();
			} catch (CorruptIndexException e) {
				throw new IOException("Corrupt index", e);
			}
		}

		public ArrayList<Match> getMatches() {
			ArrayList<Match> all = new ArrayList<Match>(matches.values());
			Collections.sort(all);
			return all;
		}

		public Match getMatchFor(int id) {
			return matches.get(id);
		}

		public void merge(SearchResult other) {
			for (Map.Entry<Integer, Match> e : other.matches.entrySet()) {
				if (!matches.containsKey(e.getKey())) {
					matches.put(e.getKey(), new Match(e.getValue().getObject(),
							0, null));
				}
				matches.get(e.getKey()).merge(e.getValue());
			}
		}
	}

	/**
	 * Query the index.
	 * 
	 * @return an object with the results of the search
	 */
	public SearchResult search(String queryText) {

		try {
			IndexReader reader = IndexReader.open(searchIndex);
			Query query = getQueryAllParser().parse(queryText);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(
					MAX_SEARCH_HITS, true);
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
