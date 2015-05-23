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
 * Helper subclass to CharSequenceTranslator to allow for translations that will
 * replace up to one character at a time.
 * 
 * @since 3.0
 * @version $Id: CodePointTranslator.java 1553931 2013-12-28 21:24:44Z ggregory
 *          $
 */
public abstract class CodePointTranslator extends CharSequenceTranslator {

	/**
	 * Implementation of translate that maps onto the abstract translate(int,
	 * Writer) method. {@inheritDoc}
	 */
	@Override
	public final int translate(final CharSequence input, final int index,
			final Writer out) throws IOException {
		final int codepoint = Character.codePointAt(input, index);
		final boolean consumed = translate(codepoint, out);
		return consumed ? 1 : 0;
	}

	/**
	 * Translate the specified codepoint into another.
	 * 
	 * @param codepoint
	 *            int character input to translate
	 * @param out
	 *            Writer to optionally push the translated output to
	 * @return boolean as to whether translation occurred or not
	 * @throws IOException
	 *             if and only if the Writer produces an IOException
	 */
	public abstract boolean translate(int codepoint, Writer out)
			throws IOException;

}
