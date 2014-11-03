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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.widgets.dragndrop.focus.FocusButton;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * A widget used in the {@link SceneMapWidget} that represents a scene.
 */
public class SceneWidget extends FocusButton {

	private static final float PAD = 10F;

	private InitialSceneListener initialSceneListener = new InitialSceneListener();
	private Controller controller;
	private ModelEntity scene;
	private String sceneId;
	private Image initial;
	private Label name;

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
				Q.getThumbnail(controller, scene).getPath(),
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

	public String getSceneId() {
		return sceneId;
	}

	public void setName(String name) {
		this.name.setText(name);
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

	public ModelEntity getScene() {
		return scene;
	}
}