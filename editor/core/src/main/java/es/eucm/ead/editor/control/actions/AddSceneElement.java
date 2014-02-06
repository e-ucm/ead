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
package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.graphics.Texture;

import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.platform.Platform.StringListener;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.renderers.Image;

public class AddSceneElement extends EditorAction implements StringListener {

	public static final String NAME = "addSceneElement";

	public AddSceneElement() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		controller.action(ChooseFile.NAME, this);
	}

	@Override
	public void string(String result) {
		addFromImage(result);
	}

	public void addFromImage(String result) {
		SceneElement sceneElement = new SceneElement();
		Image renderer = new Image();
		String newPath = controller.getProjectAssets().copyAndLoad(result,
				Texture.class);
		controller.getProjectAssets().finishLoading();
		renderer.setUri(newPath);
		sceneElement.setRenderer(renderer);
		Scene scene = controller.getModel().getEditScene();

		controller.command(new AddToListCommand(scene.getChildren(),
				sceneElement));
	}
}
