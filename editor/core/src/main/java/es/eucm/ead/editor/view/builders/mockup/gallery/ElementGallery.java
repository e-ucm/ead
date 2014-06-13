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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.control.actions.model.RemoveFromScene;
import es.eucm.ead.editor.control.actions.model.scene.SetEditionContext;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.builders.mockup.camera.Picture;
import es.eucm.ead.editor.view.builders.mockup.edition.ElementEdition;
import es.eucm.ead.editor.view.builders.mockup.edition.SceneEdition;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ElementButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ModelEntityCategory;

/**
 * A gallery that only displays {@link es.eucm.ead.schema.entities.ModelEntity}
 * s.
 */
public class ElementGallery extends BaseGalleryWithNavigation<ElementButton> {

	private static final String ADD_ELEMENT_BUTTON = "ic_new";
	private static final String IC_PHOTOCAMERA = "ic_photocamera";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .25F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .12F;

	private Class<?> arg;

	@Override
	protected Button bottomLeftButton(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		final MenuButton pictureButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.photo"), skin, IC_PHOTOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT, controller, ChangeView.class, Picture.class,
				getClass());
		return pictureButton;
	}

	@Override
	protected Button bottomRightButton(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		return null;
	}

	private void setButtonDisabled(boolean disabled, Button button) {
		Touchable t = disabled ? Touchable.disabled : Touchable.enabled;

		button.setDisabled(disabled);
		button.setTouchable(t);
	}

	@Override
	public Actor getView(Object... args) {
		if (args.length == 1) {
			// We receive an argument when the previous view was SceneEdition
			// and the user wanted to import an element from this gallery into
			// the edit scene
			arg = (Class<?>) args[0];
			// We must take care that some features are not available since the
			// user only has to choose an element. He can't delete them nor go
			// to take a picture
			setSelectable(false);
			setButtonDisabled(true, getBottomLeftButton());
			setButtonDisabled(true, getFirstPositionActor());
		} else {
			arg = null;
			setSelectable(true);
			setButtonDisabled(false, getBottomLeftButton());
			setButtonDisabled(false, getFirstPositionActor());
		}
		return super.getView(args);
	}

	@Override
	protected boolean updateGalleryElements(Controller controller,
			Array<ElementButton> elements, Vector2 viewport, I18N i18n,
			Skin skin) {
		elements.clear();
		final Map<String, ModelEntity> map = controller.getModel().getEntities(
				ModelEntityCategory.SCENE);
		for (final Entry<String, ModelEntity> entry : map.entrySet()) {
			final ModelEntity currEditorScene = entry.getValue();
			final List<ModelEntity> sceneChildren = currEditorScene
					.getChildren();
			final int totalChildren = sceneChildren.size();
			for (int i = 0; i < totalChildren; ++i) {
				final ModelEntity currentChildren = sceneChildren.get(i);
				elements.add(new ElementButton(viewport, i18n, currentChildren,
						entry.getKey(), skin, controller));
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
		// Set the editScene to the element's parent
		controller.action(EditScene.class, target.getParentKey());
		if (arg == SceneEdition.class) {
			// If we came from SceneEdition then the user wants to add the
			// chosen element to the scene
			controller.action(AddSceneElement.class, target.getSceneElement());
			controller.action(ChangeView.class, arg);
		} else {
			controller
					.action(SetEditionContext.class, target.getSceneElement());
			// Start editing the clicked element...
			controller.action(ChangeView.class, ElementEdition.class);
		}
	}

	@Override
	protected void entitySelected(ElementButton actor, int entitiesCount,
			Controller controller) {
		// Do nothing since we only have elements in this gallery
	}

	@Override
	protected void addExtrasToTopToolbar(ToolBar topToolbar, Vector2 viewport,
			Skin skin, I18N i18n, Controller controller) {
		// Do nothing since we only have elements in this gallery
	}

	@Override
	protected void entityDeleted(ElementButton entity, Controller controller) {
		ModelEntity toRemove = entity.getSceneElement();
		controller.action(RemoveFromScene.class,
				Model.getComponent(toRemove, Parent.class).getParent(),
				toRemove);
		onEntityDeleted(entity);
	}

	@Override
	protected boolean elementHasTag(ElementButton element, String tag) {
		return element.hasTag(tag);
	}

	@Override
	protected String getTitle(I18N i18n) {
		return i18n.m("general.mockup.elements");
	}
}
