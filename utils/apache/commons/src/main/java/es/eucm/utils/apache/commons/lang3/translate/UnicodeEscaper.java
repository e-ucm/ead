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
 * ranslates codepoints to their Unicode escaped value.
 * 
 * @since 3.0
 * @version $Id: UnicodeEscaper.java 1666535 2015-03-13 18:18:59Z britter $
 */
public class UnicodeEscaper extends CodePointTranslator {

	private final int below;
	private final int above;
	private final boolean between;

	/**
	 * <p>
	 * Constructs a <code>UnicodeEscaper</code> for all characters.
	 * </p>
	 */
	public UnicodeEscaper() {
		this(0, Integer.MAX_VALUE, true);
	}

	/**
	 * <p>
	 * Constructs a <code>UnicodeEscaper</code> for the specified range. This is
	 * the underlying method for the other constructors/builders. The
	 * <code>below</code> and <code>above</code> boundaries are inclusive when
	 * <code>between</code> is <code>true</code> and exclusive when it is
	 * <code>false</code>.
	 * </p>
	 * 
	 * @param below
	 *            int value representing the lowest codepoint boundary
	 * @param above
	 *            int value representing the highest codepoint boundary
	 * @param between
	 *            whether to escape between the boundaries or outside them
	 */
	protected UnicodeEscaper(final int below, final int above,
			final boolean between) {
		this.below = below;
		this.above = above;
		this.between = between;
	}

	/**
	 * <p>
	 * Constructs a <code>UnicodeEscaper</code> below the specified value
	 * (exclusive).
	 * </p>
	 * 
	 * @param codepoint
	 *            below which to escape
	 * @return the newly created {@code UnicodeEscaper} instance
	 */
	public static UnicodeEscaper below(final int codepoint) {
		return outsideOf(codepoint, Integer.MAX_VALUE);
	}

	/**
	 * <p>
	 * Constructs a <code>UnicodeEscaper</code> above the specified value
	 * (exclusive).
	 * </p>
	 * 
	 * @param codepoint
	 *            above which to escape
	 * @return the newly created {@code UnicodeEscaper} instance
	 */
	public static UnicodeEscaper above(final int codepoint) {
		return outsideOf(0, codepoint);
	}

	/**
	 * <p>
	 * Constructs a <code>UnicodeEscaper</code> outside of the specified values
	 * (exclusive).
	 * </p>
	 * 
	 * @param codepointLow
	 *            below which to escape
	 * @param codepointHigh
	 *            above which to escape
	 * @return the newly created {@code UnicodeEscaper} instance
	 */
	public static UnicodeEscaper outsideOf(final int codepointLow,
			final int codepointHigh) {
		return new UnicodeEscaper(codepointLow, codepointHigh, false);
	}

	/**
	 * <p>
	 * Constructs a <code>UnicodeEscaper</code> between the specified values
	 * (inclusive).
	 * </p>
	 * 
	 * @param codepointLow
	 *            above which to escape
	 * @param codepointHigh
	 *            below which to escape
	 * @return the newly created {@code UnicodeEscaper} instance
	 */
	public static UnicodeEscaper between(final int codepointLow,
			final int codepointHigh) {
		return new UnicodeEscaper(codepointLow, codepointHigh, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean translate(final int codepoint, final Writer out)
			throws IOException {
		if (between) {
			if (codepoint < below || codepoint > above) {
				return false;
			}
		} else {
			if (codepoint >= below && codepoint <= above) {
				return false;
			}
		}

		// TODO: Handle potential + sign per various Unicode escape
		// implementations
		if (codepoint > 0xffff) {
			out.write(toUtf16Escape(codepoint));
		} else {
			out.write("\\u");
			out.write(HEX_DIGITS[(codepoint >> 12) & 15]);
			out.write(HEX_DIGITS[(codepoint >> 8) & 15]);
			out.write(HEX_DIGITS[(codepoint >> 4) & 15]);
			out.write(HEX_DIGITS[(codepoint) & 15]);
		}
		return true;
	}

	/**
	 * Converts the given codepoint to a hex string of the form
	 * {@code "\\uXXXX"}
	 * 
	 * @param codepoint
	 *            a Unicode code point
	 * @return the hex string for the given codepoint
	 * 
	 * @since 3.2
	 */
	protected String toUtf16Escape(final int codepoint) {
		return "\\u" + hex(codepoint);
	}
}
