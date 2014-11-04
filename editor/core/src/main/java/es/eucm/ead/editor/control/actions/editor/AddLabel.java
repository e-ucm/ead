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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.data.Color;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * 
 * <p>
 * A text is requested to create a {@link Label} in the current scene.
 * </p>
 * 
 */
public class AddLabel extends EditorAction implements Input.TextInputListener {

	public AddLabel() {
		super(true, false, new Class[] {}, new Class[] { Label.class });
	}

	@Override
	public void perform(Object... args) {
		if (args.length == 0) {
			I18N i18n = controller.getApplicationAssets().getI18N();
			((Platform) controller.getPlatform()).getMultilineTextInput(this,
					i18n.m("toolbar.text.input"), "", i18n);
		} else {
			addText((Label) args[0]);
		}
	}

	private void addText(Label label) {

		Skin skin = controller.getEditorGameAssets().getSkin();

		LabelStyle labelStyle = skin.get(label.getStyle(), LabelStyle.class);

		TextBounds bounds = labelStyle.font.getMultiLineBounds(label.getText());

		ModelEntity textLabel = Q.createCenteredEntity(controller,
				bounds.height, bounds.width, label);

		controller.action(AddSceneElement.class, textLabel);
	}

	@Override
	public void input(String text) {
		if (text != null && !text.replace(" ", "").isEmpty()) {
			Label label = new Label();
			label.setText(text);
			Color c = new Color();
			c.setR(0);
			c.setG(0);
			c.setB(0);
			c.setA(1);
			label.setColor(c);
			label.setStyle("roboto-small"); // default style
			addText(label);
		}
	}

	@Override
	public void canceled() {

	}
}
