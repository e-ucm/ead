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
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Actions;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Changes the {@link Label} component of the current selection.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> new text</dd>
 * <dd><strong>args[1]</strong> <em>Optional {@link Color}</em> the font color
 * of the text</dd>
 * </dl>
 */
public class ChangeSelectionText extends ModelAction {

	private ReplaceEntity replaceEntity;
	private SetSelection setSelection;

	public ChangeSelectionText() {
		super(true, false, new Class[] { String.class }, new Class[] {
				String.class, Color.class });
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		Actions actions = controller.getActions();
		replaceEntity = actions.getAction(ReplaceEntity.class);
		setSelection = actions.getAction(SetSelection.class);
	}

	@Override
	public Command perform(Object... args) {
		ModelEntity element = (ModelEntity) controller.getModel()
				.getSelection().getSingle(Selection.SCENE_ELEMENT);

		ModelEntity copy = controller.getEditorGameAssets().copy(element);

		Q.getComponent(copy, Parent.class).setParent(
				Q.getComponent(element, Parent.class).getParent());
		Label labelComponent = Q.getComponent(copy, Label.class);
		labelComponent.setText(args[0].toString());

		if (args.length == 2) {
			Color color = (Color) args[1];
			es.eucm.ead.schema.data.Color schemaColor = new es.eucm.ead.schema.data.Color();
			schemaColor.setR(color.r);
			schemaColor.setG(color.g);
			schemaColor.setB(color.b);
			schemaColor.setA(color.a);
			labelComponent.setColor(schemaColor);
		}

		Skin skin = controller.getEditorGameAssets().getSkin();
		LabelStyle labelStyle = skin.get(labelComponent.getStyle(),
				LabelStyle.class);
		TextBounds bounds = labelStyle.font.getMultiLineBounds(labelComponent
				.getText());

		copy.setOriginX(bounds.width * 0.5f);
		copy.setOriginY(bounds.height * 0.5f);

		CompositeCommand composite = replaceEntity.perform(element, copy);
		composite.addCommand(setSelection.perform(Selection.EDITED_GROUP,
				Selection.SCENE_ELEMENT, copy));
		return composite;
	}

}