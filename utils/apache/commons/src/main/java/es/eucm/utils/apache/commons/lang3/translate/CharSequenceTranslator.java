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
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

/**
 * File adapted from Apache's StringEscapeUtils class, which can be found in
 * package commons-lang3. Original file licensed under terms of Apache 2.0
 * license: http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Adapted for the Mokap project by jtorrente
 * 
 * An API for translating text. Its core use is to escape and unescape text.
 * Because escaping and unescaping is completely contextual, the API does not
 * present two separate signatures.
 * 
 * @since 3.0
 * @version $Id: CharSequenceTranslator.java 1666535 2015-03-13 18:18:59Z
 *          britter $
 */
public abstract class CharSequenceTranslator {

	static final char[] HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Translate a set of codepoints, represented by an int index into a
	 * CharSequence, into another set of codepoints. The number of codepoints
	 * consumed must be returned, and the only IOExceptions thrown must be from
	 * interacting with the Writer so that the top level API may reliably ignore
	 * StringWriter IOExceptions.
	 * 
	 * @param input
	 *            CharSequence that is being translated
	 * @param index
	 *            int representing the current point of translation
	 * @param out
	 *            Writer to translate the text to
	 * @return int count of codepoints consumed
	 * @throws IOException
	 *             if and only if the Writer produces an IOException
	 */
	public abstract int translate(CharSequence input, int index, Writer out)
			throws IOException;

	/**
	 * Helper for non-Writer usage.
	 * 
	 * @param input
	 *            CharSequence to be translated
	 * @return String output of translation
	 */
	public final String translate(final CharSequence input) {
		if (input == null) {
			return null;
		}
		try {
			final StringWriter writer = new StringWriter(input.length() * 2);
			translate(input, writer);
			return writer.toString();
		} catch (final IOException ioe) {
			// this should never ever happen while writing to a StringWriter
			throw new RuntimeException(ioe);
		}
	}

	/**
	 * Translate an input onto a Writer. This is intentionally final as its
	 * algorithm is tightly coupled with the abstract method of this class.
	 * 
	 * @param input
	 *            CharSequence that is being translated
	 * @param out
	 *            Writer to translate the text to
	 * @throws IOException
	 *             if and only if the Writer produces an IOException
	 */
	public final void translate(final CharSequence input, final Writer out)
			throws IOException {
		if (out == null) {
			throw new IllegalArgumentException("The Writer must not be null");
		}
		if (input == null) {
			return;
		}
		int pos = 0;
		final int len = input.length();
		while (pos < len) {
			final int consumed = translate(input, pos, out);
			if (consumed == 0) {
				// inlined implementation of
				// Character.toChars(Character.codePointAt(input, pos))
				// avoids allocating temp char arrays and duplicate checks
				char c1 = input.charAt(pos);
				out.write(c1);
				pos++;
				if (Character.isHighSurrogate(c1) && pos < len) {
					char c2 = input.charAt(pos);
					if (Character.isLowSurrogate(c2)) {
						out.write(c2);
						pos++;
					}
				}
				continue;
			}
			// contract with translators is that they have to understand
			// codepoints
			// and they just took care of a surrogate pair
			for (int pt = 0; pt < consumed; pt++) {
				pos += Character.charCount(Character.codePointAt(input, pos));
			}
		}
	}

	/**
	 * Helper method to create a merger of this translator with another set of
	 * translators. Useful in customizing the standard functionality.
	 * 
	 * @param translators
	 *            CharSequenceTranslator array of translators to merge with this
	 *            one
	 * @return CharSequenceTranslator merging this translator with the others
	 */
	public final CharSequenceTranslator with(
			final CharSequenceTranslator... translators) {
		final CharSequenceTranslator[] newArray = new CharSequenceTranslator[translators.length + 1];
		newArray[0] = this;
		System.arraycopy(translators, 0, newArray, 1, translators.length);
		return new AggregateTranslator(newArray);
	}

	/**
	 * <p>
	 * Returns an upper case hexadecimal <code>String</code> for the given
	 * character.
	 * </p>
	 * 
	 * @param codepoint
	 *            The codepoint to convert.
	 * @return An upper case hexadecimal <code>String</code>
	 */
	public static String hex(final int codepoint) {
		return Integer.toHexString(codepoint).toUpperCase(Locale.ENGLISH);
	}

}
