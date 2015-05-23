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
import java.util.Arrays;
import java.util.EnumSet;

/**
 * File adapted from Apache's StringEscapeUtils class, which can be found in
 * package commons-lang3. Original file licensed under terms of Apache 2.0
 * license: http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Adapted for the Mokap project by jtorrente
 * 
 * Translate XML numeric entities of the form &amp;#[xX]?\d+;? to the specific
 * codepoint.
 * 
 * Note that the semi-colon is optional.
 * 
 * @since 3.0
 * @version $Id: NumericEntityUnescaper.java 1583482 2014-03-31 22:54:57Z niallp
 *          $
 */
public class NumericEntityUnescaper extends CharSequenceTranslator {

	public static enum OPTION {
		semiColonRequired, semiColonOptional, errorIfNoSemiColon
	}

	// TODO?: Create an OptionsSet class to hide some of the conditional logic
	// below
	private final EnumSet<OPTION> options;

	/**
	 * Create a UnicodeUnescaper.
	 * 
	 * The constructor takes a list of options, only one type of which is
	 * currently available (whether to allow, error or ignore the semi-colon on
	 * the end of a numeric entity to being missing).
	 * 
	 * For example, to support numeric entities without a ';': new
	 * NumericEntityUnescaper(NumericEntityUnescaper.OPTION.semiColonOptional)
	 * and to throw an IllegalArgumentException when they're missing: new
	 * NumericEntityUnescaper(NumericEntityUnescaper.OPTION.errorIfNoSemiColon)
	 * 
	 * Note that the default behaviour is to ignore them.
	 * 
	 * @param options
	 *            to apply to this unescaper
	 */
	public NumericEntityUnescaper(final OPTION... options) {
		if (options.length > 0) {
			this.options = EnumSet.copyOf(Arrays.asList(options));
		} else {
			this.options = EnumSet.copyOf(Arrays
					.asList(new OPTION[] { OPTION.semiColonRequired }));
		}
	}

	/**
	 * Whether the passed in option is currently set.
	 * 
	 * @param option
	 *            to check state of
	 * @return whether the option is set
	 */
	public boolean isSet(final OPTION option) {
		return options == null ? false : options.contains(option);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int translate(final CharSequence input, final int index,
			final Writer out) throws IOException {
		final int seqEnd = input.length();
		// Uses -2 to ensure there is something after the &#
		if (input.charAt(index) == '&' && index < seqEnd - 2
				&& input.charAt(index + 1) == '#') {
			int start = index + 2;
			boolean isHex = false;

			final char firstChar = input.charAt(start);
			if (firstChar == 'x' || firstChar == 'X') {
				start++;
				isHex = true;

				// Check there's more than just an x after the &#
				if (start == seqEnd) {
					return 0;
				}
			}

			int end = start;
			// Note that this supports character codes without a ; on the end
			while (end < seqEnd
					&& (input.charAt(end) >= '0' && input.charAt(end) <= '9'
							|| input.charAt(end) >= 'a'
							&& input.charAt(end) <= 'f' || input.charAt(end) >= 'A'
							&& input.charAt(end) <= 'F')) {
				end++;
			}

			final boolean semiNext = end != seqEnd && input.charAt(end) == ';';

			if (!semiNext) {
				if (isSet(OPTION.semiColonRequired)) {
					return 0;
				} else if (isSet(OPTION.errorIfNoSemiColon)) {
					throw new IllegalArgumentException(
							"Semi-colon required at end of numeric entity");
				}
			}

			int entityValue;
			try {
				if (isHex) {
					entityValue = Integer.parseInt(input
							.subSequence(start, end).toString(), 16);
				} else {
					entityValue = Integer.parseInt(input
							.subSequence(start, end).toString(), 10);
				}
			} catch (final NumberFormatException nfe) {
				return 0;
			}

			if (entityValue > 0xFFFF) {
				final char[] chrs = Character.toChars(entityValue);
				out.write(chrs[0]);
				out.write(chrs[1]);
			} else {
				out.write(entityValue);
			}

			return 2 + end - start + (isHex ? 1 : 0) + (semiNext ? 1 : 0);
		}
		return 0;
	}
}
