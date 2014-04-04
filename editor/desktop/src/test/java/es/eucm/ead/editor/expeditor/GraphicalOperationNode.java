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
package es.eucm.ead.editor.expeditor;

import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.Literal;
import es.eucm.ead.engine.expressions.Operation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * An expression node for an operation (= has children).
 * 
 * Created by mfreire on 4/4/14.
 */
public class GraphicalOperationNode extends GraphicalExpressionNode {

	JComboBox atomTypes = new JComboBox();
	JButton addAtom = new JButton("+");
	JButton addOperation = new JButton("+ ...");
	JButton delete = new JButton("-");

	enum OpType {
		And("<html><b>and</b> booleano: <i>(and bool ... ) &#8594; bool</i>",
				"and"), Or(
				"<html><b>or</b> booleano: <i>(or bool ... ) &#8594; bool</i>",
				"or"), Xor(
				"<html><b>xor</b> booleano: <i>(xor bool1 bool2) &#8594; bool</i>",
				"xor"), Not(
				"<html><b>or</b> booleano: <i>(not bool1) &#8594; bool</i>",
				"not"),

		Add(
				"<html><b> + </b> suma entera/flotante: <i>( + num ... ) &#8594; num</i>",
				"+"), Sub(
				"<html><b> - </b> resta entera/flotante: <i>( - num1 num2 ) &#8594; num</i>",
				"-"), Mul(
				"<html><b> * </b> multiplicación entera/flotante: <i>( * num ... ) &#8594; num</i>",
				"*"), Div(
				"<html><b> / </b> división entera/flotante: <i>( / num1 num2) [num2 != 0] &#8594; num</i>",
				"/"),

		Mod(
				"<html><b> % </b> módulo entero: <i>( / int1 int2) [int2 != 0] &#8594; int</i>",
				"%"),

		Pow(
				"<html><b>pow</b> exponenciación entera/flotante: <i>( pow num1 num2 ) &#8594; num</i>",
				"pow"),

		Sqrt(
				"<html><b>sqrt</b> raiz cuadrada entera/flotante: <i>( sqrt num ) [num >= 0] &#8594; num</i>\"",
				"sqrt"),

		Rand(
				"<html><b>rand</b> número aleatorio en un rango <i>(rand num1 num2) [num1 > num2] &#8594; num",
				"rand"),

		Eq(
				"<html><b> == </b> igualdad (valores compatibles) <i>( eq val1 val2) &#8594; bool",
				"eq"), Lt(
				"<html><b> < </b> menor (valores compatibles) <i>( lt val1 val2) &#8594; bool",
				"lt"), Le(
				"<html><b> <= </b> menor-o-igual (valores compatibles) <i>( le val1 val2) &#8594; bool",
				"le"), Gt(
				"<html><b> > </b> mayor (valores compatibles) <i>( gt val1 val2) &#8594; bool",
				"gt"), Ge(
				"<html><b> >= </b> mayor-o-igual (valores compatibles) <i>( ge val1 val2) &#8594; bool",
				"ge"),

		Int(
				"<html><b> (int) </b> conversión forzada a entero <i>(int val)  &#8594; int",
				"int"), Float(
				"<html><b> (float) </b> conversión forzada a flotante <i>(float val)  &#8594; float",
				"float"), Bool(
				"<html><b> (bool) </b> conversión forzada a booleano <i>(bool val)  &#8594; bool",
				"bool"), String(
				"<html><b> (string) </b> conversión forzada a texto <i>(string val)  &#8594; string",
				"string");

		public String name;
		public String code;
		private static HashMap<String, OpType> lookup = new HashMap<String, OpType>();

		OpType(String name, String code) {
			this.name = name;
			this.code = code;
		}

		public static OpType find(String opName) {
			if (lookup.isEmpty()) {
				for (OpType ot : values())
					lookup.put(ot.code, ot);
			}
			return lookup.get(opName);
		}

		public String toString() {
			return name;
		}
	}

	public GraphicalOperationNode(Expression node) {
		super(node);

		selfPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 0), 0, 0);
		selfPanel.add(atomTypes, gbc);
		atomTypes.setModel(new DefaultComboBoxModel(OpType.values()));
		atomTypes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				operationTypeChanged();
			}
		});
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		selfPanel.add(addAtom, gbc);
		addAtom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addAtomPressed();
			}
		});
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		selfPanel.add(addOperation, gbc);
		addOperation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addOperationPressed();
			}
		});
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		selfPanel.add(delete, gbc);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deletePressed();
			}
		});

		update(expression);
		revalidate();
	}

	public void operationTypeChanged() {
		Operation next = GraphicalExpressionNode.operators
				.createOperation(((OpType) atomTypes.getSelectedItem()).code);
		for (Expression ce : ((Operation) expression).getChildren()) {
			next.getChildren().add(ce);
		}
		updateInParent(next);
		update(expression);
		revalidate();
		fireSelfChanged();
	}

	public void addOperationPressed() {
		Expression e = GraphicalExpressionNode.operators.createOperation("+");
		addChildExpression(e);
	}

	public void addAtomPressed() {
		Expression e = new Literal("i1");
		addChildExpression(e);
	}

	public void addChildExpression(Expression e) {
		System.err.println("Adding atom: " + e);
		((Operation) expression).getChildren().add(e);
		update(expression);
		fireSelfChanged();
	}

	public void paintSelf() {
		if (expression != null) {
			atomTypes.setSelectedItem(OpType.find(((Operation) expression)
					.getName()));
		} else {
			System.err.println("I am null!");
		}
	}
}
