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

/**
 * File adapted from Apache's StringEscapeUtils class, which can be found in
 * package commons-lang3. Original file licensed under terms of Apache 2.0
 * license: http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Adapted for the Mokap project by jtorrente
 * 
 * Translates escaped Unicode values of the form \\u+\d\d\d\d back to Unicode.
 * It supports multiple 'u' characters and will work with or without the +.
 * 
 * @since 3.0
 * @version $Id: UnicodeUnescaper.java 1606060 2014-06-27 12:33:07Z ggregory $
 */
public class UnicodeUnescaper extends CharSequenceTranslator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int translate(final CharSequence input, final int index,
			final Writer out) throws IOException {
		if (input.charAt(index) == '\\' && index + 1 < input.length()
				&& input.charAt(index + 1) == 'u') {
			// consume optional additional 'u' chars
			int i = 2;
			while (index + i < input.length() && input.charAt(index + i) == 'u') {
				i++;
			}

			if (index + i < input.length() && input.charAt(index + i) == '+') {
				i++;
			}

			if (index + i + 4 <= input.length()) {
				// Get 4 hex digits
				final CharSequence unicode = input.subSequence(index + i, index
						+ i + 4);

				try {
					final int value = Integer.parseInt(unicode.toString(), 16);
					out.write((char) value);
				} catch (final NumberFormatException nfe) {
					throw new IllegalArgumentException(
							"Unable to parse unicode value: " + unicode, nfe);
				}
				return i + 4;
			}
			throw new IllegalArgumentException(
					"Less than 4 hex digits in unicode value: '"
							+ input.subSequence(index, input.length())
							+ "' due to end of CharSequence");
		}
		return 0;
	}
}
