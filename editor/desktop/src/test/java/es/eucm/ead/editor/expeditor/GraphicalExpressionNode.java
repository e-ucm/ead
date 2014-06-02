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

import es.eucm.ead.engine.Accessor;
import es.eucm.ead.engine.GameLayers;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.Operation;
import es.eucm.ead.engine.expressions.Parser;
import es.eucm.ead.engine.expressions.operators.OperatorFactory;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

/**
 * An expression node.
 * 
 * Created by mfreire on 4/4/14.
 */
public abstract class GraphicalExpressionNode extends JPanel implements
		PropertyChangeListener {

	public static final String NODE_CHANGED = "node_changed";

	public static final OperatorFactory operators = new OperatorFactory(null,
			new Accessor(new HashMap<String, Object>(), null), new GameLayers(
					new GameLoop()));

	protected Expression expression;
	protected boolean valid;
	protected boolean updating = false;

	protected JPanel selfPanel = new JPanel();

	public GraphicalExpressionNode(Expression node) {
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(2, 20, 2, 0), 0, 0);
		setLayout(new GridBagLayout());
		add(selfPanel, gbc);
		this.expression = node;
	}

	public String getText() {
		return expression == null ? "" : expression.toString();
	}

	public void propertyChange(PropertyChangeEvent pce) {
		if (pce.getPropertyName().equals(NODE_CHANGED)) {
			// always fire changes upstream
			fireSelfChanged();
		}
	}

	protected void fireSelfChanged() {
		if (!updating) {
			updating = true;
			Component parent = this;
			while (!(parent instanceof GraphicalOperationNode)) {
				parent = parent.getParent();
			}
			while (parent.getParent() instanceof GraphicalOperationNode) {
				parent = parent.getParent();
			}
			((GraphicalOperationNode) parent).firePropertyChange(NODE_CHANGED,
					null, expression);
			updating = false;
		}
	}

	public void update(String text) {
		update(Parser.parse(text, operators));
	}

	protected abstract void paintSelf();

	public void update(Expression expression) {
		if (updating) {
			return;
		}
		updating = true;

		this.expression = expression;

		// paint self in selfPanel
		paintSelf();
		selfPanel.repaint();

		// add children
		if (expression instanceof Operation) {
			// remove all non-first children
			for (int i = getComponentCount() - 1; i >= 1; i--) {
				remove(i);
			}
			// rebuild children
			GridBagConstraints gbc = new GridBagConstraints(0, 1, 1, 1, 1, 0,
					GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
					new Insets(2, 20, 2, 0), 0, 0);
			Operation o = (Operation) expression;
			for (Expression child : o.getChildren()) {
				gbc.gridy++;
				add(child instanceof Operation ? new GraphicalOperationNode(
						child) : new GraphicalAtomNode(child), gbc);
			}
			// add some filler at bottom
			gbc.gridy++;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1;
			gbc.weighty = 1;
			add(new JPanel());
			revalidate();
		}

		updating = false;
	}

	public void deleteInParent() {
		Component p = this.getParent();
		while (!(p instanceof GraphicalOperationNode)) {
			if (p == null) {
				return;
			}
			p = p.getParent();
		}
		GraphicalOperationNode gon = (GraphicalOperationNode) p;
		Operation op = (Operation) gon.expression;
		op.getChildren().remove(expression);
		gon.update(op);
		gon.fireSelfChanged();

	}

	public void updateInParent(Expression e) {
		Component p = this.getParent();
		while (!(p instanceof GraphicalOperationNode)) {
			if (p == null) {
				return;
			}
			p = p.getParent();
		}
		GraphicalOperationNode gon = (GraphicalOperationNode) p;
		Operation op = (Operation) gon.expression;
		int childIndex = op.getChildren().indexOf(expression);
		if (childIndex != -1) {
			op.getChildren().set(childIndex, e);
		}
		expression = e;
	}

	public void setValid(boolean valid) {
		if (!valid) {
			setBorder(BorderFactory.createEmptyBorder());
		} else {
			setBorder(BorderFactory.createLineBorder(Color.red, 1));
		}
		this.valid = valid;
	}

	public void deletePressed() {
		deleteInParent();
	}
}
