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
package es.eucm.ead.editor.indexes;

import com.badlogic.gdx.utils.Array;

/**
 * Implements an index of {@link Term}s that can be searched
 */
public class FuzzyIndex {

	private Array<Term> terms = new Array<Term>();

	/**
	 * @return terms contained by the index
	 */
	public Array<Term> getTerms() {
		return terms;
	}

	/**
	 * Adds a term to the index
	 * 
	 * @param termString
	 *            the string for the term (used in the search matching)
	 * @param data
	 *            some data associated to the term. Could be {@code null}
	 */
	public void addTerm(String termString, Object data) {
		terms.add(new Term(termString, data));
	}

	/**
	 * Remove the term with the given id and data
	 * 
	 * @return the term, if indeed was in the index
	 */
	public Term removeTerm(String termString, Object data) {
		for (Term term : terms) {
			if (term.getTermString().equals(termString)
					&& term.data.equals(data)) {
				terms.removeValue(term, true);
				return term;
			}
		}
		return null;
	}

	/**
	 * @return returns the first associated term with the given data
	 */
	public Term getTerm(Object data) {
		for (Term term : terms) {
			if (term.data == data) {
				return term;
			}
		}
		return null;
	}

	/**
	 * Makes a fuzzy search in the index
	 * 
	 * @param query
	 *            the query for the search
	 * @param results
	 *            an array to hold the results of the query
	 */
	public void search(String query, Array<Term> results) {
		results.clear();
		for (Term term : terms) {
			if (match(query, term.getTermString())) {
				results.add(term);
			}
		}
	}

	// match code converted from http://jsfiddle.net/trevordixon/pXzj3/4/
	private boolean match(String search, String text) {
		search = search.toUpperCase();
		text = text.toUpperCase();

		int j = -1;
		for (int i = 0; i < search.length(); i++) {
			char l = search.charAt(i);
			if (l == ' ')
				continue;

			j = text.indexOf(l, j + 1);
			if (j == -1)
				return false;
		}
		return true;
	}

	public static class Term {
		private String termString;
		private Object data;

		private Term(String termString, Object data) {
			this.termString = termString;
			this.data = data;
		}

		public String getTermString() {
			return termString;
		}

		/**
		 * @return the data associated to the term
		 */
		public Object getData() {
			return data;
		}
	}
}
