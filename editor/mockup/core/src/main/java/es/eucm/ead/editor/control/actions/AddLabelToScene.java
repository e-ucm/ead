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
package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

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

		ModelEntity parent = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE);

		if (args.length == 1 && args[0] instanceof Label) {
			textLabel.getComponents().add((Label) args[0]);
		} else if (args.length == 0) {
			Label label = new Label();
			label.setText(controller.getApplicationAssets().getI18N()
					.m("general.text")); // default text
			label.setStyle("welcome"); // default style
			textLabel.getComponents().add(label);
		} else {
			throw new EditorActionException("Error in action "
					+ this.getClass().getCanonicalName()
					+ ": the argument received should be a Label");
		}

		CompositeCommand compositeCommand = new CompositeCommand();
		compositeCommand.addCommand(new AddToListCommand(parent, parent
				.getChildren(), textLabel));

		Parent parentComponent = Q.getComponent(textLabel, Parent.class);
		compositeCommand.addCommand(new FieldCommand(parentComponent,
				FieldName.PARENT, parent));

		return compositeCommand;
	}

}
