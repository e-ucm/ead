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
package es.eucm.ead.editor.view.builders.mockup.gallery;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.model.RemoveFromScene;
import es.eucm.ead.editor.view.builders.mockup.camera.Picture;
import es.eucm.ead.editor.view.builders.mockup.edition.ElementEdition;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ElementButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.editor.actors.EditorScene;

/**
 * A gallery that only displays {@link SceneElement}s.
 */
public class ElementGallery extends BaseGalleryWithNavigation<ElementButton> {

	public static final String NAME = "mockup_element";

	private static final String ADD_ELEMENT_BUTTON = "ic_newproject";
	private static final String IC_PHOTOCAMERA = "ic_photocamera";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .25F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .12F;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected Button bottomLeftButton(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		final MenuButton pictureButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.photo"), skin, IC_PHOTOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT, controller, ChangeView.class, Picture.NAME);
		return pictureButton;
	}

	@Override
	protected Button bottomRightButton(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		return null;
	}

	@Override
	protected boolean updateGalleryElements(Controller controller,
			Array<ElementButton> elements, Vector2 viewport, I18N i18n,
			Skin skin) {
		elements.clear();
		final Map<String, EditorScene> map = controller.getModel().getScenes();
		for (final Entry<String, EditorScene> entry : map.entrySet()) {
			final EditorScene currEditorScene = entry.getValue();
			final List<SceneElement> sceneChildren = currEditorScene
					.getChildren();
			final int totalChildren = sceneChildren.size();
			for (int i = 0; i < totalChildren; ++i) {
				final SceneElement currentChildren = sceneChildren.get(i);
				elements.add(new ElementButton(viewport, i18n, currentChildren,
						currEditorScene, skin, controller));
			}
		}
		return true;
	}

	@Override
	protected Button getFirstPositionActor(Vector2 viewport, I18N i18n,
			Skin skin, Controller controller) {
		final Button addElementButton = new IconButton(viewport, skin,
				ADD_ELEMENT_BUTTON);
		return addElementButton;
	}

	@Override
	protected void entityClicked(InputEvent event, ElementButton target,
			Controller controller, I18N i18n) {
		// Start editing the clicked element...
		controller.action(ChangeView.class, ElementEdition.NAME);
	}
	
	@Override
	protected void entitySelected(ElementButton actor, int entitiesCount, Controller controller) {
		// Do nothing since we only have elements in this gallery
	}

	@Override
	protected void addExtrasToTopToolbar(ToolBar topToolbar, Vector2 viewport,
			Skin skin, I18N i18n, Controller controller) {
		// Do nothing since we only have elements in this gallery
	}
	
	@Override
	protected void entityDeleted(ElementButton entity, Controller controller) {
		controller.action(RemoveFromScene.class, entity.getEditorSceneParent(),
				entity.getSceneElement());
		onEntityDeleted(entity);
	}

	@Override
	protected boolean elementHasTag(ElementButton element, String tag) {
		return element.hasTag(tag);
	}
}
