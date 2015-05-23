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
 * Translates codepoints to their XML numeric entity escaped value.
 * 
 * @since 3.0
 * @version $Id: NumericEntityEscaper.java 1436768 2013-01-22 07:07:42Z ggregory
 *          $
 */
public class NumericEntityEscaper extends CodePointTranslator {

	private final int below;
	private final int above;
	private final boolean between;

	/**
	 * <p>
	 * Constructs a <code>NumericEntityEscaper</code> for the specified range.
	 * This is the underlying method for the other constructors/builders. The
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
	private NumericEntityEscaper(final int below, final int above,
			final boolean between) {
		this.below = below;
		this.above = above;
		this.between = between;
	}

	/**
	 * <p>
	 * Constructs a <code>NumericEntityEscaper</code> for all characters.
	 * </p>
	 */
	public NumericEntityEscaper() {
		this(0, Integer.MAX_VALUE, true);
	}

	/**
	 * <p>
	 * Constructs a <code>NumericEntityEscaper</code> below the specified value
	 * (exclusive).
	 * </p>
	 * 
	 * @param codepoint
	 *            below which to escape
	 * @return the newly created {@code NumericEntityEscaper} instance
	 */
	public static NumericEntityEscaper below(final int codepoint) {
		return outsideOf(codepoint, Integer.MAX_VALUE);
	}

	/**
	 * <p>
	 * Constructs a <code>NumericEntityEscaper</code> above the specified value
	 * (exclusive).
	 * </p>
	 * 
	 * @param codepoint
	 *            above which to escape
	 * @return the newly created {@code NumericEntityEscaper} instance
	 */
	public static NumericEntityEscaper above(final int codepoint) {
		return outsideOf(0, codepoint);
	}

	/**
	 * <p>
	 * Constructs a <code>NumericEntityEscaper</code> between the specified
	 * values (inclusive).
	 * </p>
	 * 
	 * @param codepointLow
	 *            above which to escape
	 * @param codepointHigh
	 *            below which to escape
	 * @return the newly created {@code NumericEntityEscaper} instance
	 */
	public static NumericEntityEscaper between(final int codepointLow,
			final int codepointHigh) {
		return new NumericEntityEscaper(codepointLow, codepointHigh, true);
	}

	/**
	 * <p>
	 * Constructs a <code>NumericEntityEscaper</code> outside of the specified
	 * values (exclusive).
	 * </p>
	 * 
	 * @param codepointLow
	 *            below which to escape
	 * @param codepointHigh
	 *            above which to escape
	 * @return the newly created {@code NumericEntityEscaper} instance
	 */
	public static NumericEntityEscaper outsideOf(final int codepointLow,
			final int codepointHigh) {
		return new NumericEntityEscaper(codepointLow, codepointHigh, false);
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

		out.write("&#");
		out.write(Integer.toString(codepoint, 10));
		out.write(';');
		return true;
	}
}
