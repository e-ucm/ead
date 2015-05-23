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
package es.eucm.utils.apache.commons.lang3.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;

/**
 * File adapted from Apache's StringEscapeUtils class, which can be found in
 * package commons-lang3. Original file licensed under terms of Apache 2.0
 * license: http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Adapted for the Mokap project by jtorrente
 * 
 * Translates a value using a lookup table.
 * 
 * @since 3.0
 * @version $Id: LookupTranslator.java 1669520 2015-03-27 08:03:41Z britter $
 */
public class LookupTranslator extends CharSequenceTranslator {

	private final HashMap<String, String> lookupMap;
	private final HashSet<Character> prefixSet;
	private final int shortest;
	private final int longest;

	/**
	 * Define the lookup table to be used in translation
	 * 
	 * Note that, as of Lang 3.1, the key to the lookup table is converted to a
	 * java.lang.String. This is because we need the key to support hashCode and
	 * equals(Object), allowing it to be the key for a HashMap. See LANG-882.
	 * 
	 * @param lookup
	 *            CharSequence[][] table of size [*][2]
	 */
	public LookupTranslator(final CharSequence[]... lookup) {
		lookupMap = new HashMap<String, String>();
		prefixSet = new HashSet<Character>();
		int _shortest = Integer.MAX_VALUE;
		int _longest = 0;
		if (lookup != null) {
			for (final CharSequence[] seq : lookup) {
				this.lookupMap.put(seq[0].toString(), seq[1].toString());
				this.prefixSet.add(seq[0].charAt(0));
				final int sz = seq[0].length();
				if (sz < _shortest) {
					_shortest = sz;
				}
				if (sz > _longest) {
					_longest = sz;
				}
			}
		}
		shortest = _shortest;
		longest = _longest;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int translate(final CharSequence input, final int index,
			final Writer out) throws IOException {
		// check if translation exists for the input at position index
		if (prefixSet.contains(input.charAt(index))) {
			int max = longest;
			if (index + longest > input.length()) {
				max = input.length() - index;
			}
			// implement greedy algorithm by trying maximum match first
			for (int i = max; i >= shortest; i--) {
				final CharSequence subSeq = input.subSequence(index, index + i);
				final String result = lookupMap.get(subSeq.toString());

				if (result != null) {
					out.write(result);
					return i;
				}
			}
		}
		return 0;
	}
}
