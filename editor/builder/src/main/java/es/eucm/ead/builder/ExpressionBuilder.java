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
package es.eucm.ead.builder;

import es.eucm.ead.schemax.Layer;
import es.eucm.ead.schemax.OperationsNames;

/**
 * Helper to build recurrent expressions following the syntax specified by
 * OperationsFactory.
 * 
 * (To learn more about this language, see also class ParserTest.
 * 
 * Created by jtorrente on 27/10/2015.
 */
public class ExpressionBuilder extends OperationsNames {

	/**
	 * Creates an expression that will return a list with all the entities found
	 * to have the given tag
	 */
	public String allEntitiesWithTag(String tag) {
		return op(ENTITY_COLLECTION, hasTag(tag));
	}

	public String hasTag(String tag) {
		return op(ENTITY_HAS_TAG, varReference("entity"), string(tag));
	}

	public String varReference(String varName) {
		if (varName.startsWith("$")) {
			return varName;
		}
		return "$" + varName;
	}

	public String integer(String n) {
		return "i" + n;
	}

	public String integer(int n) {
		return integer("" + n);
	}

	public String float_(String f) {
		return "f" + f;
	}

	public String float_(float f) {
		return float_("" + f);
	}

	public String string(Object s) {
		if (s == null) {
			return "snull";
		}
		return "s" + s.toString();
	}

	/**
	 * Expression that retrieves the first entity that has the given tag
	 */
	public String entityWithTag(String tag) {
		return op(GET_FROM_COLLECTION, allEntitiesWithTag(tag));
	}

	/**
	 * Expression that evaluates a variable against a given value. Examples:
	 * var1 == 2, var1 < 3.2
	 * 
	 * @param var
	 *            The name of the variable, with or without dollar ($) sign
	 * @param comparison
	 *            "<", "<=", "=", ">" or ">="
	 * @param value
	 *            The numeric value to compare to. Both integers and floats are
	 *            supported. Other types of numbers (e.g. doubles and longs)
	 *            trigger exceptions.
	 */
	public String variableComparedTo(String var, String comparison, Number value) {
		String number;
		if (Integer.class.isAssignableFrom(value.getClass())) {
			number = integer(value.intValue());
		} else if (Float.class.isAssignableFrom(value.getClass())) {
			number = float_(value.floatValue());
		} else {
			throw new RuntimeException(
					"Method variableEqualsTo does not accept double or long values,"
							+ "as these are not supported in Mokap");
		}

		if (!var.startsWith("$")) {
			var = varReference(var);
		}

		String operator = null;
		if (comparison.equals("<")) {
			operator = LOWER_THAN;
		} else if (comparison.equals("<=") || comparison.equals("=<")) {
			operator = LOWER_EQUALS;
		} else if (comparison.equals("=") || comparison.equals("==")) {
			operator = EQUALS;
		} else if (comparison.equals(">")) {
			operator = GREATER_THAN;
		} else if (comparison.equals(">=") || comparison.equals("=>")) {
			operator = GREATER_EQUALS;
		} else if (comparison.equals("!=") || comparison.equals("<>")) {
			operator = DIFFERENT;
		} else {
			throw new RuntimeException("Operator \"" + comparison
					+ "\" is not supported");
		}

		return op(operator, var, number);
	}

/**
     * Variable < value.
     * equivalent to {@code variableComparedTo(var, "<", value)}
     */
	public String variableLowerThan(String var, Number value) {
		return variableComparedTo(var, "<", value);
	}

	/**
	 * Variable <= value. equivalent to
	 * {@code variableComparedTo(var, "<=", value)}
	 */
	public String variableLowerEquals(String var, Number value) {
		return variableComparedTo(var, "<=", value);
	}

	/**
	 * Variable == value. equivalent to
	 * {@code variableComparedTo(var, "=", value)}
	 */
	public String variableEqualsTo(String var, Number value) {
		return variableComparedTo(var, "=", value);
	}

	/**
	 * @return An expression that checks if the given variable is true or false,
	 *         depending on the value provided
	 * @param var
	 *            Name of the variable, with or without starting variable
	 *            reference symbol ("$")
	 * @param value
	 *            The value to be checked against (True or False)
	 */
	public String variableEqualsTo(String var, Boolean value) {
		if (!var.startsWith("$")) {
			var = varReference(var);
		}
		return op(EQUALS, var, bool(value));
	}

	/**
	 * Variable == true. Equivalent to {@code variableEqualsTo(var, true)}
	 */
	public String variableIsTrue(String var) {
		return variableEqualsTo(var, true);
	}

	/**
	 * Variable == false. Equivalent to {@code variableEqualsTo(var, false)}
	 */
	public String variableIsFalse(String var) {
		return variableEqualsTo(var, false);
	}

	/**
	 * Variable > value. equivalent to
	 * {@code variableComparedTo(var, ">", value)}
	 */
	public String variableGreaterThan(String var, Number value) {
		return variableComparedTo(var, ">", value);
	}

	/**
	 * Variable > value. equivalent to
	 * {@code variableComparedTo(var, ">=", value)}
	 */
	public String variableGreaterEquals(String var, Number value) {
		return variableComparedTo(var, ">=", value);
	}

	/**
	 * Expression to select the entity that serves as layer for the scene
	 * content
	 */
	public String layerSceneContent() {
		return op(GET_LAYER, string(Layer.SCENE_CONTENT));
	}

	public String variableDifferentTo(String var, Number value) {
		return variableComparedTo(var, "!=", value);
	}

	public String variableDifferentTo(String var, Boolean value) {
		return not(variableEqualsTo(var, value));
	}

	public String variableDifferentTo(String variable, String value) {
		if (value.startsWith("b")) {
			return variableDifferentTo(variable,
					Boolean.parseBoolean(value.substring(1)));
		}
		if (value.startsWith("i")) {
			return variableDifferentTo(variable,
					Integer.parseInt(value.substring(1)));
		}
		if (value.startsWith("f")) {
			return variableDifferentTo(variable,
					Float.parseFloat(value.substring(1)));
		}
		return null;
	}

	public String or(String... subExpressions) {
		return op(OR, subExpressions);
	}

	public String and(String... subExpressions) {
		return op(AND, subExpressions);
	}

	public String not(String expression) {
		return op(NOT, expression);
	}

	public String bool(boolean value) {
		if (value) {
			return "btrue";
		}
		return "bfalse";
	}

	public String thisEntity() {
		return varReference("_this");
	}

	public String op(String operand, String... arguments) {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append(operand);
		for (String arg : arguments) {
			builder.append(" ");
			builder.append(arg);
		}
		builder.append(")");
		return builder.toString();
	}

	public String entityX() {
		return string("group.x");
	}

	public String entityY() {
		return string("group.y");
	}

	public String entityVarProperty(String var, String property) {
		return op(PROPERTY_IN_OBJECT, varReference(var), property);
	}

	public String entityVarX(String var) {
		return entityVarProperty(var, entityX());
	}

	public String entityVarY(String var) {
		return entityVarProperty(var, entityY());
	}

	public String sum(Object... operands) {
		String[] convertedOperands = new String[operands.length];
		for (int i = 0; i < operands.length; i++) {
			if (operands[i] instanceof Integer) {
				convertedOperands[i] = integer((Integer) operands[i]);
			} else if (operands[i] instanceof Float) {
				convertedOperands[i] = float_((Float) operands[i]);
			} else {
				convertedOperands[i] = operands[i].toString();
			}
		}
		return op(SUM, convertedOperands);
	}

	public String intersectsThisByTag(String tag) {
		return op(ENTITIES_INTERSECT, thisEntity(), entityWithTag(tag));
	}

	public String intersectsThisAnyByTag(String tag) {
		return op(ENTITIES_INTERSECT, thisEntity(), allEntitiesWithTag(tag));
	}

	public String intersectionSetThisByTag(String tag) {
		return op(ENTITY_INTERSECTION_SET, thisEntity(), entityWithTag(tag));
	}

	public String firstToIntersectThisByTag(String tag) {
		return op(GET_FROM_COLLECTION, intersectionSetThisByTag(tag));
	}

	public String plusOne(String varName) {
		return increment(varName, 1);
	}

	public String minusOne(String varName) {
		return increment(varName, -1);
	}

	public String increment(String varName, int increment) {
		return sum(varReference(varName), integer(increment));
	}
}
