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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.model.DeleteScene;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.control.actions.model.RemoveFromScene;
import es.eucm.ead.editor.control.actions.model.scene.SetEditionContext;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.MapEvent;
import es.eucm.ead.editor.view.builders.mockup.camera.Picture;
import es.eucm.ead.editor.view.builders.mockup.camera.Video;
import es.eucm.ead.editor.view.builders.mockup.edition.ElementEdition;
import es.eucm.ead.editor.view.builders.mockup.edition.SceneEdition;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.DescriptionCard;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ElementButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.editor.view.widgets.mockup.buttons.SceneButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Map;
import java.util.Map.Entry;

/**
 * This gallery displays both {@link es.eucm.ead.schema.entities.ModelEntity}s
 * and {@link es.eucm.ead.schema.entities.ModelEntity}s.
 */
public class Gallery extends BaseGalleryWithNavigation<DescriptionCard> {

	private static final String ADD_TO_GALLERY_BUTTON = "ic_new";
	private static final String IC_PHOTOCAMERA = "ic_photocamera",
			IC_VIDEOCAMERA = "ic_videocamera";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .25F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .12F;

	/**
	 * The entity that is being deleted when the user chooses to delete entity.
	 * It's needed for the {@link SceneButton}s in order to correctly update the
	 * UI when they are deleted because in some cases an
	 * {@link es.eucm.ead.schema.entities.ModelEntity} can't be deleted (e.g.
	 * deleting a scene from a game that only has one scene).
	 */
	private DescriptionCard deletingEntity;
	/**
	 * If true next time we show this view the gallery elements will be updated.
	 */
	private boolean needsUpdate;
	/**
	 * If true, onLoadModelListener was added.
	 */
	private boolean listenersAdded;

	public Gallery() {
		this.listenersAdded = false;
	}

	@Override
	public void initialize(Controller controller) {
		addModelListeners(controller);
		this.needsUpdate = true;
		super.initialize(controller);
	}

	private void addModelListeners(Controller controller) {
		if (this.listenersAdded)
			return;
		this.listenersAdded = true;
		final Model model = controller.getModel();
		final ModelListener<MapEvent> updateMapListener = new ModelListener<MapEvent>() {
			@Override
			public void modelChanged(MapEvent event) {
				Gallery.this.needsUpdate = true;
				if (event.getType() == MapEvent.Type.ENTRY_REMOVED) {
					Gallery.super.onEntityDeleted(Gallery.this.deletingEntity);
				}
			}
		};
		model.addMapListener(model.getResources(ResourceCategory.SCENE),
				updateMapListener);
		model.addLoadListener(new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {
				model.addMapListener(
						model.getResources(ResourceCategory.SCENE),
						updateMapListener);
				Gallery.this.needsUpdate = true;
			}
		});
	}

	@Override
	protected boolean updateGalleryElements(Controller controller,
			Array<DescriptionCard> elements, Vector2 viewport, I18N i18n,
			Skin skin) {
		Map<String, Object> map = controller.getModel().getResources(
				ResourceCategory.SCENE);
		if (this.needsUpdate) {
			this.needsUpdate = false;
			elements.clear();
			for (Entry<String, Object> entry : map.entrySet()) {
				ModelEntity editorScene = (ModelEntity) entry.getValue();
				SceneButton sceneWidget = new SceneButton(viewport, i18n,
						editorScene, entry.getKey(), skin, controller);
				elements.add(sceneWidget);
			}
		}

		for (final Entry<String, Object> entry : map.entrySet()) {
			ModelEntity editorScene = (ModelEntity) entry.getValue();
			Array<ModelEntity> sceneChildren = editorScene.getChildren();
			int totalChildren = sceneChildren.size;
			for (int i = 0; i < totalChildren; ++i) {
				ModelEntity currentChildren = (ModelEntity) sceneChildren
						.get(i);
				ElementButton childrenElementButton = new ElementButton(
						viewport, i18n, currentChildren, entry.getKey(), skin,
						controller);
				if (!elements.contains(childrenElementButton, false))
					elements.add(childrenElementButton);
			}
		}
		return true;
	}

	@Override
	protected Button bottomLeftButton(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		MenuButton pictureButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.photo"), skin, IC_PHOTOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT, controller, ChangeView.class, Picture.class,
				getClass());
		return pictureButton;
	}

	@Override
	protected Button bottomRightButton(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		MenuButton videoButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.video"), skin, IC_VIDEOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.LEFT, controller, ChangeView.class, Video.class);
		return videoButton;
	}

	@Override
	protected Button getFirstPositionActor(Vector2 viewport, I18N i18n,
			Skin skin, Controller controller) {
		Button addToGalleryButton = new IconButton(viewport, skin,
				ADD_TO_GALLERY_BUTTON);
		return addToGalleryButton;
	}

	@Override
	protected void entityClicked(InputEvent event, DescriptionCard target,
			Controller controller, I18N i18n) {
		if (target instanceof SceneButton) {
			// Start editing the clicked scene...
			controller.action(EditScene.class, ((SceneButton) target).getKey());
			controller.action(ChangeView.class, SceneEdition.class);
		} else if (target instanceof ElementButton) {
			// Set the editScene to the element's parent
			ElementButton elem = (ElementButton) target;
			controller.action(EditScene.class, elem.getParentKey());
			// Start editing the clicked element...
			controller.action(SetEditionContext.class, elem.getSceneElement());
			controller.action(ChangeView.class, ElementEdition.class);
		}
	}

	@Override
	protected void entityDeleted(DescriptionCard entity, Controller controller) {
		if (entity instanceof SceneButton) {
			// Start deleting the clicked scene...
			this.deletingEntity = entity;
			controller.action(DeleteScene.class,
					((SceneButton) entity).getKey());
		} else if (entity instanceof ElementButton) {
			// Start deleting the clicked element...
			final ElementButton element = (ElementButton) entity;
			ModelEntity toRemove = element.getSceneElement();
			controller.action(RemoveFromScene.class,
					Model.getComponent(toRemove, Parent.class).getParent(),
					toRemove);
			onEntityDeleted(entity);
		}
	}

	@Override
	protected boolean elementHasTag(DescriptionCard element, String tag) {
		return element.hasTag(tag);
	}

	@Override
	protected String getTitle(I18N i18n) {
		return i18n.m("general.mockup.gallery");
	}
}
