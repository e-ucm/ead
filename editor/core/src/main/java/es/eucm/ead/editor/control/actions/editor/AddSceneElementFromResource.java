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

import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * <p>
 * Adds a scene element to the current edited scene
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>Number (optional)</em> x coordinate for the
 * resources</dd>
 * <dd><strong>args[1]</strong> <em>Number (optional)</em> y coordinate for the
 * resources</dd>
 * </dl>
 */
public class AddSceneElementFromResource extends EditorAction implements
		FileChooserListener {

	private Number x;
	private Number y;

	public AddSceneElementFromResource() {
		super(true, false, new Class[] {}, new Class[] { Number.class,
				Number.class });
	}

	@Override
	public void perform(Object... args) {
		controller.action(ChooseFile.class, false, this);
		if (args.length == 2) {
			x = (Number) args[0];
			y = (Number) args[1];
		} else {
			GameData component = Q.getComponent(
					controller.getModel().getGame(), GameData.class);
			x = component.getWidth() * .5f;
			y = component.getHeight() * .5f;
		}
	}

	@Override
	public void fileChosen(String path) {
		if (path != null) {
			generateSceneElementFromImage(path);
		}
	}

	private void generateSceneElementFromImage(String result) {
		ModelEntity sceneElement = controller.getTemplates()
				.createSceneElement(result, x.floatValue(), y.floatValue());
		controller.action(AddSceneElement.class, sceneElement);
	}

}
