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

package es.eucm.ead.engine.expressions;

import es.eucm.ead.engine.VarsContext;

/**
 * An expression node for a literal.
 * 
 * @author mfreire
 */
public class Literal extends Expression {

	public Literal(Object value) {
		this.value = value;
	}

	public Literal(String prefixedString) {
		String remainder = prefixedString.substring(1);
		switch (prefixedString.charAt(0)) {
		case 's':
			this.value = remainder;
			break;
		case 'b':
			this.value = Boolean.parseBoolean(remainder);
			break;
		case 'f':
			this.value = Float.parseFloat(remainder);
			break;
		case 'i':
			this.value = Integer.parseInt(remainder);
			break;
		default:
			throw new IllegalArgumentException("Unrecognized type prefix in '"
					+ prefixedString + "'");
		}
	}

	@Override
	protected StringBuilder buildString(StringBuilder sb,
			boolean updateTokenPositions) {
		if (updateTokenPositions) {
			tokenPosition = sb.length();
		}

		if (value.getClass().equals(String.class)) {
			sb.append("s\"").append(value.toString()).append("\"");
		} else if (value.getClass().equals(Boolean.class)) {
			sb.append("b").append(value.toString());
		} else if (value.getClass().equals(Float.class)) {
			sb.append("f").append(value.toString());
		} else if (value.getClass().equals(Integer.class)) {
			sb.append("i").append(value.toString());
		}

		return sb;
	}

	@Override
	public Object evaluate(VarsContext context, boolean lazy)
			throws ExpressionEvaluationException {
		return value;
	}
}
