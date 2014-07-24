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

import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.model.DeleteScene;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.control.actions.model.scene.NewScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.editor.model.events.ResourceEvent;
import es.eucm.ead.editor.view.builders.mockup.camera.Picture;
import es.eucm.ead.editor.view.builders.mockup.camera.Video;
import es.eucm.ead.editor.view.builders.mockup.edition.SceneEdition;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.editor.view.widgets.mockup.buttons.SceneButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * A gallery that only displays {@link es.eucm.ead.schema.entities.ModelEntity}
 * s.
 */
public class SceneGallery extends BaseGalleryWithNavigation<SceneButton> {

	private static final String IC_PHOTOCAMERA = "ic_photocamera",
			IC_VIDEOCAMERA = "ic_videocamera", ADD_ELEMENT_BUTTON = "ic_new";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .25F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .12F;

	/**
	 * The element that is being deleted when the user chooses to delete
	 * elements.
	 */
	private SceneButton deletingEntity;
	/**
	 * If true, LoadModelListener was added.
	 */
	private boolean listenersAdded;

	public SceneGallery() {
		this.listenersAdded = false;
	}

	@Override
	public void initialize(Controller controller) {
		addModelListeners(controller);
		super.initialize(controller);
	}

	private void addModelListeners(Controller controller) {
		if (this.listenersAdded)
			return;
		this.listenersAdded = true;
		Model model = controller.getModel();
		ModelListener<ResourceEvent> updateMapListener = new ModelListener<ResourceEvent>() {

			@Override
			public void modelChanged(ResourceEvent event) {
				if (event.getType() == ResourceEvent.Type.REMOVED) {
					SceneGallery.super
							.onEntityDeleted(SceneGallery.this.deletingEntity);
				}
			}
		};
		model.addResourceListener(updateMapListener);
	}

	@Override
	protected boolean updateGalleryElements(Controller controller,
			Array<SceneButton> elements, Vector2 viewport, I18N i18n, Skin skin) {

		elements.clear();
		Map<String, Resource> map = controller.getModel().getResources(
				ResourceCategory.SCENE);
		for (Entry<String, Resource> entry : map.entrySet()) {
			SceneButton sceneWidget = new SceneButton(viewport, i18n,
					(ModelEntity) entry.getValue().getObject(), entry.getKey(),
					skin, controller);

			elements.add(sceneWidget);
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
			Skin skin, final Controller controller) {
		Button addSceneButton = new IconButton(viewport, skin,
				ADD_ELEMENT_BUTTON);

		addSceneButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(NewScene.class);
				controller.action(ChangeView.class, SceneGallery.class);
			}
		});
		return addSceneButton;
	}

	@Override
	protected void entityClicked(InputEvent event, SceneButton target,
			Controller controller, I18N i18n) {
		// Start editing the clicked scene
		controller.action(EditScene.class, target.getKey());
		controller.action(ChangeView.class, SceneEdition.class);
	}

	@Override
	protected void entityDeleted(SceneButton entity, Controller controller) {
		this.deletingEntity = entity;
		controller.action(DeleteScene.class, entity.getKey(),
				entity.getModelEntityScene());
	}

	@Override
	protected boolean elementHasTag(SceneButton element, String tag) {
		return element.hasTag(tag);
	}

	@Override
	protected String getTitle(I18N i18n) {
		return i18n.m("general.mockup.scenes");
	}
}
