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
package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.widgets.mockup.panels.GalleryEntity;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.editor.actors.EditorScene;

/**
 * A button displaying a {@link Scene} (name, description, image...)
 */
public class SceneButton extends GalleryEntity {

	/**
	 * The string used to refer to this scene in the editor UI
	 */
	private final String name;

	private final EditorScene scene;

	public SceneButton(Vector2 viewport, I18N i18n, EditorScene scene,
			Skin skin, Controller controller) {
		super(scene.getNotes(), viewport, i18n, i18n.m("scene"), scene
				.getNotes().getTitle(), scene.getNotes().getDescription(),
				null, skin, controller);
		this.name = scene.getName();
		this.scene = scene;
	}

	public SceneButton(Vector2 viewport, I18N i18n, EditorScene scene,
			Skin skin, Controller controller, Class<?> action, Object... args) {
		super(scene.getNotes(), viewport, i18n, i18n.m("scene"), scene
				.getNotes().getTitle(), scene.getNotes().getDescription(),
				null, skin, controller, action, args);
		this.name = scene.getName();
		this.scene = scene;
	}

	/**
	 * @return the key linked to this {@link EditorScene} in the {@link Model}
	 */
	public String getKey() {
		return this.name;
	}

	@Override
	public boolean hasTag(String tag) {
		for (final SceneElement element : this.scene.getChildren()) {
			if (element.getTags().contains(tag)) {
				return true;
			}
		}
		return false;
	}
}
