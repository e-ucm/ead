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
package es.eucm.ead.editor.view.generic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import es.eucm.ead.editor.model.DependencyNode;

public class ColorOption extends AbstractOption<Color> {

	private Color controlValue = null;

	public ColorOption(String title, String toolTipText, DependencyNode... changed) {
		super(title, toolTipText, changed);
	}

	@Override
	protected WidgetGroup createControl() {
//		colorButton = new JButton();
//		oldValue = accessor.read();
//		setControlValue(oldValue);
//		colorButton.setToolTipText(getToolTipText());
//
//		colorButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent ae) {
//				setControlValue(accessor.read());
//				jcc.setColor(controlValue);
//				JOptionPane.showMessageDialog(colorButton.getParent(), jcc,
//						"Select a color", JOptionPane.QUESTION_MESSAGE);
//				if (!controlValue.equals(jcc.getColor())
//						&& jcc.getColor() != null) {
//					setControlValue(jcc.getColor());
//					update();
//				}
//			}
//		});
//		return colorButton;
        return new WidgetGroup();
	}

	@Override
	public Color getControlValue() {
		return controlValue;
	}

	@Override
	protected void setControlValue(Color newValue) {
		controlValue = newValue;
	}
}
