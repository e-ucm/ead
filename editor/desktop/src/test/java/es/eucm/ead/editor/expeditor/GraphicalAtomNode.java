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
import es.eucm.ead.engine.expressions.VariableRef;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * An expression atom (= literal)
 * 
 * Created by mfreire on 4/4/14.
 */
public class GraphicalAtomNode extends GraphicalExpressionNode {

	JComboBox atomTypes = new JComboBox();
	JTextField value = new JTextField();
	JButton delete = new JButton("-");

	private boolean avoidUpdates = false;

	enum AtomType {
		Integer("int"), Float("float"), String("string"), Boolean("boolean"), Variable(
				"variable");
		public String name;

		AtomType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public GraphicalAtomNode(Expression node) {
		super(node);

		selfPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						2, 2, 2, 0), 0, 0);
		selfPanel.add(atomTypes, gbc);
		atomTypes.setModel(new DefaultComboBoxModel(AtomType.values()));
		atomTypes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				atomTypeChanged();
			}
		});
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		selfPanel.add(value, gbc);
		value.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (!avoidUpdates)
					valueChanged();
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
	}

	public void updateExpression() {
		AtomType t = (AtomType) atomTypes.getSelectedItem();
		String v = value.getText();

		System.err.println("text is " + v);
		Expression e = null;
		try {
			switch (t) {
			case String:
				e = new Literal("s" + v);
				break;
			case Float:
				e = new Literal("f" + v);
				break;
			case Integer:
				e = new Literal("i" + v);
				break;
			case Boolean:
				e = new Literal("b" + v);
				break;
			case Variable:
				e = new VariableRef(v);
				break;
			default:
				throw new IllegalArgumentException("Cannot happen");
			}
		} catch (Exception ex) {
			System.err.println("bad idea: " + ex + " '" + v + "'");
			ex.printStackTrace();
		}

		if (e != null) {
			updateInParent(e);
			System.err.println("Setting expression to '" + e + "'");
			paintSelf();
			setBorder(BorderFactory.createEmptyBorder());
			System.err.println("Atom is now " + expression);
			fireSelfChanged();
		} else {
			setBorder(BorderFactory.createLineBorder(Color.red, 1));
		}
	}

	public void atomTypeChanged() {
		updateExpression();
	}

	public void valueChanged() {
		updateExpression();
	}

	public void paintSelf() {
		if (expression instanceof Literal) {
			setText(((Literal) expression).toNakedString());
		} else if (expression instanceof VariableRef) {
			setText(((VariableRef) expression).toNakedString());
		} else {
		}
	}

	/**
	 * Sets text, without triggering any weird updates
	 * 
	 * @param text
	 */
	public void setText(String text) {
		if (!avoidUpdates) {
			if (!text.equals(value.getText())) {
				System.err.println("requested to set '" + value.getText()
						+ "' to '" + text + "'");
			} else {
				System.err.println("avoiding faux update");
				return;
			}
			avoidUpdates = true;
			value.setText(text);
			avoidUpdates = false;
		}
	}
}
