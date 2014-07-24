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
package es.eucm.ead.editor.ui.scenes.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.ShowContextMenu;
import es.eucm.ead.editor.control.actions.model.ChangeInitialScene;
import es.eucm.ead.editor.control.actions.model.DeleteScene;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.widgets.dragndrop.focus.FocusItemList.FocusEvent;
import es.eucm.ead.editor.view.widgets.dragndrop.focus.FocusItemList.FocusListener;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;
import es.eucm.ead.editor.view.widgets.menu.ContextMenuItem;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * A widget used in the {@link SceneMapWidget} that represents a scene.
 */
public class SceneWidget extends Button {

	private static final float PAD = 10F;

	private static final ClickListener contextListener = new ClickListener(
			Buttons.RIGHT) {

		public void clicked(InputEvent event, float x, float y) {
			contextMenu.showContext((SceneWidget) event.getListenerActor(), x,
					y);
		};
	};
	private static SceneContextMenu contextMenu;

	private InitialSceneListener initialSceneListener = new InitialSceneListener();
	private Controller controller;
	private ModelEntity scene;
	private String sceneId;
	private Image initial;
	private Label name;
	private Runnable focusRunnable = new Runnable() {

		public void run() {
			fireFocus();
		}
	};

	public SceneWidget(Controller control, String sceneId) {
		super(control.getApplicationAssets().getSkin());
		defaults().expand().fill();
		this.controller = control;
		this.sceneId = sceneId;
		Model model = controller.getModel();
		this.scene = (ModelEntity) model.getResourceObject(sceneId,
				ResourceCategory.SCENE);

		Skin skin = controller.getApplicationAssets().getSkin();

		final Image image = new Image();
		image.setScaling(Scaling.fit);
		controller.getEditorGameAssets().get(
				Q.getThumbnail(controller, scene).getThumbnail(),
				Texture.class, new AssetLoadedCallback<Texture>() {

					@Override
					public void loaded(String fileName, Texture asset) {
						image.setDrawable(new TextureRegionDrawable(
								new TextureRegion(asset)));
					}

				}, true);
		Documentation documentation = Q
				.getComponent(scene, Documentation.class);
		String name = documentation.getName();
		this.name = new Label(name == null ? sceneId : name, skin);
		this.name.setAlignment(Align.bottom | Align.center);

		initial = new Image(skin.getDrawable("mirrory24x24"));
		initial.setScaling(Scaling.none);
		GameData component = Q.getComponent(model.getGame(), GameData.class);
		String initialScene = component.getInitialScene();
		setInitial(initialScene != null && initialScene.equals(sceneId));
		model.addFieldListener(component, initialSceneListener);

		Object modelEntity = model.getSelection().getSingle(Selection.SCENE);
		setChecked(modelEntity == scene);

		Table topRow = new Table();
		topRow.add(initial).top().left().expand();

		stack(image, topRow);
		row();
		add(this.name);
		pad(PAD);

		if (contextMenu == null) {
			contextMenu = new SceneContextMenu(controller);
		}
		addListener(contextListener);

		addListener(new FocusListener() {

			@Override
			public void focusChanged(FocusEvent event) {
				controller.action(SetSelection.class, Selection.SCENE_MAP,
						Selection.SCENE, scene);
			}
		});
	}

	private void setInitial(boolean initial) {
		this.initial.setVisible(initial);
	}

	public boolean isInitial() {
		return initial.isVisible();
	}

	@Override
	public void clearListeners() {
		controller.getModel()
				.removeListener(
						Q.getComponent(controller.getModel().getGame(),
								GameData.class), initialSceneListener);
		super.clearListeners();
	}

	@Override
	public void setChecked(boolean isChecked) {
		boolean wasChecked = isChecked();
		super.setChecked(isChecked);
		if (!wasChecked && isChecked) {
			Gdx.app.postRunnable(focusRunnable);
		}
	}

	/**
	 * Fires that some actor has gained focus
	 */
	private void fireFocus() {
		FocusEvent dropEvent = Pools.obtain(FocusEvent.class);
		dropEvent.setActor(this);
		fire(dropEvent);
		Pools.free(dropEvent);
	}

	public String getSceneId() {
		return sceneId;
	}

	public void setName(String name) {
		this.name.setText(name);
	}

	public void updateInitial() {

	}

	/**
	 * Notifies that the initial scene has changed.
	 */
	private class InitialSceneListener implements FieldListener {

		@Override
		public void modelChanged(FieldEvent event) {
			Object value = event.getValue();
			if (value != null) {
				String iniSceneId = value.toString();
				setInitial(iniSceneId.equals(sceneId));
			}
		}

		@Override
		public boolean listenToField(String fieldName) {
			return FieldName.INITIAL_SCENE.equals(fieldName);
		}
	}

	private static class SceneContextMenu extends ContextMenu {

		private Controller controller;
		private SceneWidget sceneWidget;
		private ContextMenuItem makeInitial;
		private ContextMenuItem deleteScene;

		public SceneContextMenu(Controller control) {
			super(control.getApplicationAssets().getSkin());
			this.controller = control;
			I18N i18n = controller.getApplicationAssets().getI18N();

			makeInitial = item(i18n.m("general.make-initial"));
			separator();
			deleteScene = item(i18n.m("scene.delete"));

			InputListener itemsListener = new InputListener() {

				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					Actor listenerActor = event.getListenerActor();
					if (listenerActor == deleteScene) {
						controller.action(DeleteScene.class,
								sceneWidget.getSceneId());
						sceneWidget.setChecked(true);
					} else if (listenerActor == makeInitial) {
						controller.action(ChangeInitialScene.class,
								sceneWidget.getSceneId());
						sceneWidget.setChecked(true);
					}
					return true;
				}
			};
			makeInitial.addListener(itemsListener);
			deleteScene.addListener(itemsListener);
		}

		private void showContext(SceneWidget widget, float x, float y) {
			sceneWidget = widget;
			makeInitial.setDisabled(widget.isInitial());
			controller.action(ShowContextMenu.class, widget, this, x, y);
		}
	}
}