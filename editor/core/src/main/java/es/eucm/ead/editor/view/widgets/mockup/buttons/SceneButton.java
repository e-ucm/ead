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
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.entities.ModelEntity;

import java.util.List;

/**
 * A button displaying a scene (name, description, image...)
 */
public class SceneButton extends GalleryEntity {

	/**
	 * The string used to refer to this scene in the editor UI
	 */
	private final String name;

	private final ModelEntity scene;

	public SceneButton(Vector2 viewport, I18N i18n, ModelEntity scene,
			Skin skin, Controller controller) {
		this(viewport, i18n, scene, skin, controller, null);
	}

	public SceneButton(Vector2 viewport, I18N i18n, ModelEntity scene,
			Skin skin, Controller controller, Class<?> action, Object... args) {
		super(Model.getComponent(scene, Note.class), viewport, i18n, i18n
				.m("scene"), Model.getComponent(scene, Note.class).getTitle(),
				Model.getComponent(scene, Note.class).getDescription(), null,
				skin, controller, action, args);
		this.name = Model.getComponent(scene, Note.class).getTitle();
		this.scene = scene;
	}

	/**
	 * @return the key linked to this scene in the {@link Model}
	 */
	public String getKey() {
		return this.name;
	}

	@Override
	public boolean hasTag(String tag) {
		for (final ModelEntity element : this.scene.getChildren()) {
			List<String> tags = null;
			for (ModelComponent c : element.getComponents()) {
				if (c instanceof Tags) {
					tags = ((Tags) c).getTags();
				}
			}
			if (tags != null && tags.contains(tag)) {
				return true;
			}
		}
		return false;
	}
}
