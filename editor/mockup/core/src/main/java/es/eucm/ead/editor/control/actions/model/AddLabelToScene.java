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
package es.eucm.ead.editor.control.actions.model;

import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * 
 * <p>
 * Add a {@link Label} in the actual scene
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> is the <em>{@link Label}</em></dd>
 * <dd>If no <strong>args[0]</strong> is provided a new <em>{@link Label}</em>
 * will be added.</dd>
 * </dl>
 * 
 */
public class AddLabelToScene extends ModelAction {

	@Override
	public boolean validate(Object... args) {
		return true;
	}

	@Override
	public Command perform(Object... args) {
		ModelEntity textLabel = new ModelEntity();
		GameData gameData = Q.getComponent(controller.getModel().getGame(),
				GameData.class);

		Label label = null;
		if (args.length == 1 && args[0] instanceof Label) {
			label = (Label) args[0];
			textLabel.getComponents().add(label);
		} else if (args.length == 0) {
			label = new Label();
			label.setText(controller.getApplicationAssets().getI18N()
					.m("general.text")); // default text
			label.setStyle("welcome"); // default style
			textLabel.getComponents().add(label);
		} else {
			throw new EditorActionException("Error in action "
					+ this.getClass().getCanonicalName()
					+ ": the argument received should be a Label");
		}

		Skin skin = controller.getEditorGameAssets().getSkin();
		LabelStyle labelStyle = skin.get(label.getStyle(), LabelStyle.class);
		TextBounds bounds = labelStyle.font.getMultiLineBounds(label.getText());
		textLabel.setX((gameData.getWidth() - bounds.width) * 0.5f);
		textLabel.setY((gameData.getHeight() - bounds.height) * 0.5f);
		textLabel.setOriginX(bounds.width * 0.5f);
		textLabel.setOriginY(bounds.height * 0.5f);

		return controller.getActions().getAction(AddSceneElement.class)
				.perform(textLabel);
	}

}
