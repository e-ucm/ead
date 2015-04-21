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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * Changes the {@link Label} component of the current selection.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Color}</em> the font color of the
 * text</dd>
 * <dd><strong>OR</strong></dd>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> the new text or the new
 * style</dd>
 * <dd><strong>args[1]</strong> <em>Optional {@link Boolean}</em> whether
 * args[0] is the new Text (true) or the new Style (false). If no args[1] is
 * provided the changed value will be the text.
 * </dl>
 */
public class ChangeSelectionText extends ModelAction {

	private GlyphLayout glyphLayout = new GlyphLayout();

	public ChangeSelectionText() {
		super(true, false, new Class[] { String.class }, new Class[] {
				String.class, Boolean.class }, new Class[] { Color.class });
	}

	@Override
	public Command perform(Object... args) {
		ModelEntity element = (ModelEntity) controller.getModel()
				.getSelection().getSingle(Selection.SCENE_ELEMENT);

		Label label = Q.getComponent(element, Label.class);

		Skin skin = controller.getEditorGameAssets().getSkin();
		LabelStyle newLabelStyle = skin.get(label.getStyle(), LabelStyle.class);
		String newText = label.getText();

		CompositeCommand command = new CompositeCommand();

		if (args.length == 1) {
			if (args[0] instanceof Color) {
				Color color = (Color) args[0];
				es.eucm.ead.schema.data.Color schemaColor = new es.eucm.ead.schema.data.Color();
				schemaColor.setR(color.r);
				schemaColor.setG(color.g);
				schemaColor.setB(color.b);
				schemaColor.setA(color.a);
				return new FieldCommand(label, FieldName.COLOR, schemaColor);
			} else {
				newText = args[0].toString();
				command.addCommand(new FieldCommand(label, FieldName.TEXT,
						newText));

			}
		} else {
			if ((Boolean) args[1]) {
				newText = args[0].toString();
				command.addCommand(new FieldCommand(label, FieldName.TEXT,
						newText));
			} else {
				newLabelStyle = skin.get(args[0].toString(), LabelStyle.class);
				command.addCommand(new FieldCommand(label, FieldName.STYLE,
						args[0].toString()));
			}
		}

		// Actualize origin
		glyphLayout.setText(newLabelStyle.font, newText);

		command.addCommand(new FieldCommand(element, FieldName.ORIGIN_X,
				glyphLayout.width * 0.5f));
		command.addCommand(new FieldCommand(element, FieldName.ORIGIN_Y,
				glyphLayout.height * 0.5f));

		return command;
	}

}