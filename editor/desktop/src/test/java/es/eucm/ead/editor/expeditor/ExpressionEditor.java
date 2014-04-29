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
import es.eucm.ead.engine.expressions.Parser;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * An expression editor. Whatever is edited in the tree view will appear in the
 * text view, and (if parseable), vice-versa.
 * 
 * Created by mfreire on 4/4/14.
 */
public class ExpressionEditor extends JPanel {

	private GraphicalExpressionNode graphicalRoot = new GraphicalOperationNode(
			null);
	private JTextArea textualExpression = new JTextArea(
			"(+ i1 i1 ( - i4 i2) (* i6 i8))");
	private boolean updating = false;

	private JScrollPane jspGraphical = new JScrollPane(graphicalRoot);
	private JScrollPane jspTextual = new JScrollPane(textualExpression);

	private ExpressionEditor() {
		jspGraphical.setBorder(BorderFactory
				.createTitledBorder("Graphical representation"));
		jspGraphical.setBackground(new Color(.9f, .9f, 1f));
		jspTextual.setBorder(BorderFactory
				.createTitledBorder("Textual representation"));
		jspTextual.setBackground(new Color(1f, 1f, .8f));
		JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		setLayout(new BorderLayout());
		add(jsp, BorderLayout.CENTER);
		jsp.setDividerLocation(400);

		jsp.add(jspGraphical);
		jsp.add(jspTextual);

		graphicalRoot.addPropertyChangeListener(
				GraphicalExpressionNode.NODE_CHANGED,
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						System.err
								.println("Received graphical prop-change evt");
						if (!updating) {
							updating = true;
							textualExpression.setText(graphicalRoot.getText());
							updating = false;
						}
					}
				});

		textualExpression
				.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						updateExpressionText();
					}
				});
		textualExpression.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				updateExpressionText();
			}
		});
	}

	public void updateExpressionText() {
		if (!updating) {
			updating = true;
			System.err
					.println("Text changed to " + textualExpression.getText());
			Expression e = null;
			try {
				e = Parser.parse(textualExpression.getText(),
						GraphicalExpressionNode.operators);
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
			if (e != null) {
				graphicalRoot.update(e);
			}
			updating = false;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame jf = new JFrame("Expression Editor");
				jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jf.setLayout(new BorderLayout());
				jf.add(new ExpressionEditor(), BorderLayout.CENTER);
				jf.pack();
				jf.setSize(800, 600);
				jf.setLocation(100, 100);
				jf.setVisible(true);
			}
		});
	}
}
