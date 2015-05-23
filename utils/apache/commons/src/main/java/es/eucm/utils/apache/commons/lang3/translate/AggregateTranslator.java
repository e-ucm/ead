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
 * Executes a sequence of translators one after the other. Execution ends
 * whenever the first translator consumes codepoints from the input.
 * 
 * @since 3.0
 * @version $Id: AggregateTranslator.java 1436770 2013-01-22 07:09:45Z ggregory
 *          $
 */
public class AggregateTranslator extends CharSequenceTranslator {

	private final CharSequenceTranslator[] translators;

	/**
	 * Specify the translators to be used at creation time.
	 * 
	 * @param translators
	 *            CharSequenceTranslator array to aggregate
	 */
	public AggregateTranslator(final CharSequenceTranslator... translators) {
		this.translators = translators == null ? null : translators.clone();
	}

	/**
	 * The first translator to consume codepoints from the input is the
	 * 'winner'. Execution stops with the number of consumed codepoints being
	 * returned. {@inheritDoc}
	 */
	@Override
	public int translate(final CharSequence input, final int index,
			final Writer out) throws IOException {
		for (final CharSequenceTranslator translator : translators) {
			final int consumed = translator.translate(input, index, out);
			if (consumed != 0) {
				return consumed;
			}
		}
		return 0;
	}

}
