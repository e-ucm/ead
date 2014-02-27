/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.view.controllers.constraints;

import es.eucm.ead.engine.I18N;

/**
 * Constraint to test if a string length is less than a value
 */
public class MinLengthConstraint implements Constraint<String> {

	private I18N i18N;

	private int minLength;

	public MinLengthConstraint(I18N i18N, int minLength) {
		this.i18N = i18N;
		this.minLength = minLength;
	}

	@Override
	public String getErrorMessage() {
		return minLength == 1 ? i18N.m("constraints.stringnotempty") : i18N.m(
				"constraints.minlength", minLength);
	}

	@Override
	public boolean validate(String value) {
		return (value == null ? 0 : value.length()) >= minLength;
	}
}
