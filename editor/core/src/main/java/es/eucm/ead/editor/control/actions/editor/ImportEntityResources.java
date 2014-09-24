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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.Renderer;
import es.eucm.ead.schema.renderers.State;
import es.eucm.ead.schema.renderers.States;

/**
 * <p>
 * Given a local element, adds its resources into the game project (only Images
 * for now).
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>ModelEntity</em> the scene element to add to
 * list</dd>
 * <dd><strong>args[1]</strong> <em>String</em> the path where the resources of
 * this element are located</dd>
 * </dl>
 */
public class ImportEntityResources extends EditorAction {

	public ImportEntityResources() {
		super(true, false, ModelEntity.class, String.class);
	}

	@Override
	public void perform(Object... args) {
		ModelEntity elem = (ModelEntity) args[0];
		String resourceElementPath = args[1].toString() + "/";
		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		if (Q.hasComponent(elem, Image.class)) {
			copyToProject(Q.getComponent(elem, Image.class),
					resourceElementPath, gameAssets);
		}
		if (Q.hasComponent(elem, Frames.class)) {
			copyToProject(Q.getComponent(elem, Frames.class),
					resourceElementPath, gameAssets);
		}
		if (Q.hasComponent(elem, States.class)) {
			copyToProject(Q.getComponent(elem, States.class),
					resourceElementPath, gameAssets);
		}
	}

	private void copyToProject(Renderer renderer, String resourceElementPath,
			EditorGameAssets gameAssets) {
		if (renderer instanceof Image) {
			Image image = (Image) renderer;
			String fullResourceElementPath = resourceElementPath
					+ image.getUri();
			String newUri = gameAssets.copyToProjectIfNeeded(
					fullResourceElementPath, Texture.class);
			image.setUri(newUri == null ? fullResourceElementPath : newUri);

		} else if (renderer instanceof Frames) {
			Frames frames = (Frames) renderer;
			for (Frame frame : frames.getFrames()) {
				copyToProject(frame.getRenderer(), resourceElementPath,
						gameAssets);
			}
		} else if (renderer instanceof States) {
			States states = (States) renderer;
			for (State state : states.getStates()) {
				copyToProject(state.getRenderer(), resourceElementPath,
						gameAssets);
			}
		}
	}
}
