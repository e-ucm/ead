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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.ResourceEvent;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.ReaderUtil;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows easy search operations on the model. Uses Lucene for indexing and
 * retrieval.
 * 
 * @author mfreire
 */
public class Index {

	private static int nextId = 0;

	/**
	 * Symbol used by lucene for fuzz search
	 */
	public static final String FUZZY_SEARCH_SYMBOL = "~";

	private float fuzzyFactor = 0.5f;

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
	private int maxSearchHits = 10;
	/**
	 * Query parser for 'all fields' queries
	 */
	private QueryParser queryParser;

	private IndexSearcher indexSearcher;

	/**
	 * If the query parser must re-read the searchIndex
	 */
	private boolean queryParserDirty;

	/**
	 * Field analyzer
	 */
	private Analyzer searchAnalyzer;

	private Map<Integer, SearchNode> idsToNodes;

	private Map<Object, Integer> modelsToIds;

	private Array<Class> ignoredClasses;

	/**
	 * Name of field that stores the modelId
	 */
	private static final String DOCUMENT_ID_FIELD_NAME = "_id";

	/**
	 * Configure Lucene indexing
	 */
	public Index() {
		idsToNodes = new HashMap<Integer, SearchNode>();
		modelsToIds = new IdentityHashMap<Object, Integer>();
		ignoredClasses = new Array<Class>();
		clear();
	}

	/**
	 * Objects of the given clazz won't be indexed
	 */
	public void ignoreClass(Class clazz) {
		ignoredClasses.add(clazz);
	}

	/**
	 * Purges the contents of this modelIndex
	 */
	public final void clear() {
		idsToNodes.clear();
		modelsToIds.clear();

		searchIndex = new RAMDirectory();
		searchAnalyzer = new ReusableAnalyzerBase() {
			@Override
			protected TokenStreamComponents createComponents(String s,
					Reader reader) {
				KeywordTokenizer source = new KeywordTokenizer(reader);
				TokenFilter filter = new LowerCaseFilter(Version.LUCENE_36,
						source);
				filter = new EdgeNGramTokenFilter(filter,
						EdgeNGramTokenFilter.Side.BACK, 2, 50);
				return new TokenStreamComponents(source, filter);
			}
		};

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
				searchAnalyzer);
		try {
			indexWriter = new IndexWriter(searchIndex, config);
		} catch (IOException e) {
			Gdx.app.error("index", "Could not initialize search index", e);
		}
	}

	/**
	 * Sets the factor for the fuzzy search. Greater the factor, more similar
	 * the words will have
	 * 
	 * @param fuzzyFactor
	 *            a value between 0 and 1 (inclusive)
	 */
	public void setFuzzyFactor(float fuzzyFactor) {
		this.fuzzyFactor = fuzzyFactor;
	}

	/**
	 * Set the maximum number of hits a search returns
	 */
	public void setMaxSearchHits(int maxSearchHits) {
		this.maxSearchHits = maxSearchHits;
	}

	/**
	 * Listens to events, updating the index as necessary.
	 * 
	 * @param event
	 *            that potentially changes the index
	 */
	public void updateIndex(ModelEvent event) {
		if (event instanceof ListEvent) {
			ListEvent le = (ListEvent) event;
			switch (le.getType()) {
			case ADDED:
				SearchNode parentNode = getSearchNode(le.getParent());
				if (parentNode != null) {
					SearchNode childNode = add(parentNode, le.getElement());
					if (childNode != null) {
						parentNode.addChild(childNode);
					}
				} else {
					Gdx.app.error("Index",
							"No list found in the index associated to event.");
				}
				break;
			case REMOVED:
				remove(le.getElement());
				break;
			}
		} else if (event instanceof ResourceEvent) {
			ResourceEvent resourceEvent = (ResourceEvent) event;
			switch (resourceEvent.getType()) {
			case ADDED:
				add(null, resourceEvent.getResource());
				break;
			case REMOVED:
				remove(resourceEvent.getResource());
				break;
			}
		} else if (event instanceof LoadEvent) {
			clear();
			Model model = ((LoadEvent) event).getModel();
			for (Map.Entry<String, Object> e : model.listNamedResources()) {
				add(null, e.getValue());
			}
		} else if (event instanceof FieldEvent) {
			refresh(event.getTarget());
		}
		commit();
	}

	/**
	 * @return the search node associated to given schema object. Could be
	 *         {@code null}
	 */
	public SearchNode getSearchNode(Object object) {
		Integer id = modelsToIds.get(object);
		return id == null ? null : idsToNodes.get(id);
	}

	/**
	 * Commits index changes
	 */
	public void commit() {
		if (indexWriter != null) {
			try {
				indexWriter.commit();
				queryParserDirty = true;
			} catch (IOException ex) {
				Gdx.app.error("Index",
						"Error committing to index after remove", ex);
			}
		}
	}

	/**
	 * Add and object to the index
	 * 
	 * @param parent
	 *            parent node
	 * @param o
	 *            the object to index
	 */
	private SearchNode add(SearchNode parent, Object o) {
		if (indexWriter == null || o == null
				|| ignoredClasses.contains(o.getClass(), true)) {
			return null;
		}

		if (modelsToIds.containsKey(o)) {
			Gdx.app.error("Index", "Cannot add already-present object " + o);
			return null;
		}

		// Create document associated to the object
		Document doc = new Document();
		int id = nextId++;
		NumericField documentIdField = new NumericField(DOCUMENT_ID_FIELD_NAME,
				org.apache.lucene.document.Field.Store.YES, true);
		documentIdField.setIntValue(id);
		doc.add(documentIdField);
		modelsToIds.put(o, id);

		// Create search node associated to the object
		SearchNode searchNode = new SearchNode(id, parent, o);
		if (parent != null) {
			parent.addChild(searchNode);
		}
		idsToNodes.put(id, searchNode);

		addFieldsToDoc(searchNode, o, doc);

		try {
			indexWriter.addDocument(doc);
		} catch (IOException ex) {
			Gdx.app.error("Index", "Error indexing newly-created " + o, ex);
		}
		return searchNode;
	}

	/**
	 * Adds all indexable fields in an object to its document.
	 * 
	 * @param searchNode
	 *            parent node
	 * @param o
	 *            object to index
	 * @param doc
	 *            to add indexed terms to
	 */
	private void addFieldsToDoc(SearchNode searchNode, Object o, Document doc) {
		for (Field field : ClassReflection.getDeclaredFields(o.getClass())) {
			field.setAccessible(true);
			try {
				Object fieldValue = field.get(o);
				if (fieldValue != null) {
					indexField(searchNode, doc, field.getName(), fieldValue);
				}
			} catch (ReflectionException ex) {
				Gdx.app.error(
						"Index",
						"Error reading indexed field-values for field "
								+ field.getName() + " of " + o.getClass(), ex);
			}
		}
	}

	private void indexField(SearchNode searchNode, Document doc,
			String fieldName, Object fieldValue) {
		Class clazz = fieldValue.getClass();
		if (clazz == String.class) {
			String indexedValue = fieldValue.toString();
			doc.add(new org.apache.lucene.document.Field(fieldName,
					indexedValue, Store.YES,
					org.apache.lucene.document.Field.Index.ANALYZED));
		} else if (isList(clazz)) {
			List list = (List) fieldValue;
			for (Object child : list) {
				indexField(searchNode, doc, fieldName, child);
			}
		} else if (!isSimpleClass(clazz)) {
			add(searchNode, fieldValue);
		}
	}

	private boolean isSimpleClass(Class clazz) {
		return ClassReflection.isAssignableFrom(Number.class, clazz)
				|| clazz == Boolean.class || clazz == Character.class
				|| clazz == int.class || clazz == float.class
				|| clazz == double.class || clazz == byte.class
				|| clazz == char.class || clazz == boolean.class
				|| clazz.isEnum();
	}

	private boolean isList(Class clazz) {
		return ClassReflection.isAssignableFrom(List.class, clazz);
	}

	/**
	 * Remove references to an object from the index
	 * 
	 * @param o
	 *            the object to remove
	 */
	private void remove(Object o) {
		if (indexWriter == null || o == null) {
			return;
		}

		if (modelsToIds.containsKey(o)) {
			int id = modelsToIds.get(o);
			removeNode(id);
		}
	}

	private void removeNode(int id) {
		SearchNode searchNode = idsToNodes.remove(id);
		if (searchNode != null) {
			try {
				modelsToIds.remove(searchNode.getObject());
				indexWriter.deleteDocuments(NumericRangeQuery.newIntRange(
						DOCUMENT_ID_FIELD_NAME, id, id, true, true));
				for (SearchNode child : searchNode.children) {
					removeNode(child.id);
				}
			} catch (IOException ex) {
				Gdx.app.error("Index", "Error removing search node " + id, ex);
			}
		} else {
			Gdx.app.error("Index", "Node with id " + id + " already removed.");
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
		if (o == null) {
			return;
		}

		if (modelsToIds.containsKey(o)) {
			int id = modelsToIds.get(o);
			SearchNode parentNode = idsToNodes.get(id).getParent();
			remove(o);
			add(parentNode, o);
		} else {
			Gdx.app.error("Index",
					"Cannot refresh an object not present in the index");
		}
	}

	private QueryParser getQueryParser() {
		if (queryParser == null || queryParserDirty) {
			try {
				IndexReader reader = IndexReader.open(searchIndex);
				Array<String> indexedFields = new Array<String>(String.class);
				for (String fieldName : ReaderUtil.getIndexedFields(reader)) {
					indexedFields.add(fieldName);
				}

				indexedFields.removeValue(DOCUMENT_ID_FIELD_NAME, false);

				indexSearcher = new IndexSearcher(reader);
				queryParser = new MultiFieldQueryParser(Version.LUCENE_36,
						indexedFields.toArray(), searchAnalyzer);
				queryParser.setLowercaseExpandedTerms(false);

				queryParserDirty = false;
			} catch (IOException ioe) {
				Gdx.app.error("Index", "Error constructing query parser", ioe);
			}
		}
		return queryParser;
	}

	/**
	 * An individual node match for a query, with score and matched fields
	 */
	public static class Match implements Comparable<Match> {

		private SearchNode searchNode;

		private double score;

		private Match(SearchNode searchNode, double score) {
			this.searchNode = searchNode;
			this.score = score;
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
			return searchNode.getObject();
		}

		/**
		 * @return search node associated to this match. This object can be used
		 *         to navigate through the match hierarchy
		 */
		public SearchNode getSearchNode() {
			return searchNode;
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
	 * Query the index.
	 * 
	 * @param queryText
	 *            textual query.
	 * @return an object with the results of the search: matches will be objects
	 *         that have fields with contents that matched the query.
	 */
	public Array<Match> search(String queryText) {
		Array<Match> matches = new Array<Match>();
		try {
			Query query = getQueryParser().parse(
					queryText + FUZZY_SEARCH_SYMBOL + fuzzyFactor);

			TopScoreDocCollector collector = TopScoreDocCollector.create(
					maxSearchHits, true);
			indexSearcher.search(query, collector);

			for (ScoreDoc hit : collector.topDocs().scoreDocs) {
				Document doc = indexSearcher.doc(hit.doc);
				Integer id = Integer.parseInt(doc.getFieldable(
						DOCUMENT_ID_FIELD_NAME).stringValue());
				SearchNode node = idsToNodes.get(id);
				Match match = new Match(node, hit.score);
				matches.add(match);
			}
			matches.sort();
		} catch (Exception e) {
			Gdx.app.error("Index", "Error parsing or looking up " + queryText,
					e);
		}
		return matches;
	}

	public static class SearchNode {

		private int id;

		private SearchNode parent;

		private Object object;

		private Array<SearchNode> children;

		private SearchNode(int id, SearchNode parent, Object object) {
			this.id = id;
			this.parent = parent;
			this.object = object;
			this.children = new Array<SearchNode>();
		}

		private void addChild(SearchNode searchNode) {
			children.add(searchNode);
		}

		public SearchNode getParent() {
			return parent;
		}

		public Object getObject() {
			return object;
		}

		public Array<SearchNode> getChildren() {
			return children;
		}
	}
}
