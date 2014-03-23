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

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.RemoveFromScene;
import es.eucm.ead.editor.view.widgets.mockup.panels.GalleryEntity;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.editor.actors.EditorScene;

/**
 * A button displaying a {@link SceneElement} (name, description, image...)
 */
public class ElementButton extends GalleryEntity {

	/**
	 * Used to know if this SceneElement has a specified tag (gallery
	 * filtering).
	 */
	private final List<String> tags;
	/**
	 * Used to know what {@link EditorScene} we should pass to
	 * {@link RemoveFromScene} action as first argument when this button is
	 * deleted.
	 */
	private final EditorScene parent;
	/**
	 * Used to know what {@link SceneElement} we should pass to
	 * {@link RemoveFromScene} action as second argument when this button is
	 * deleted.
	 */
	private final SceneElement sceneElement;

	public ElementButton(Vector2 viewport, I18N i18n,
			SceneElement sceneElement, EditorScene parent, Skin skin,
			Controller controller) {
		super(null, viewport, i18n, i18n.m("element"), null, null, null, skin,
				controller);
		this.tags = sceneElement.getTags();
		this.sceneElement = sceneElement;
		this.parent = parent;
	}

	public ElementButton(Vector2 viewport, I18N i18n,
			SceneElement sceneElement, EditorScene parent, Skin skin,
			Controller controller, Class<?> action, Object... args) {
		super(null, viewport, i18n, i18n.m("element"), null, null, null, skin,
				controller, action, args);
		this.tags = sceneElement.getTags();
		this.sceneElement = sceneElement;
		this.parent = parent;
	}

	@Override
	public boolean hasTag(String tag) {
		return this.tags.contains(tag);
	}

	public EditorScene getEditorSceneParent() {
		return this.parent;
	}

	public SceneElement getSceneElement() {
		return this.sceneElement;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ElementButton) {
			return this.sceneElement == ((ElementButton) other)
					.getSceneElement();
		}
		return false;

	}
}
