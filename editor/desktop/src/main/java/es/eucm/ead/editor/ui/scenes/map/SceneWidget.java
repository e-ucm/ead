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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.CreateThumbnail;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * A widget used in the {@link SceneMapWidget} that represents a scene.
 */
public class SceneWidget extends Button {

	private static final int THUMBNAIL_HEIGHT = 64;
	private static final int THUMBNAIL_WIDTH = 64;
	private static final float PAD = 10F;

	private Controller controller;
	private ModelEntity scene;
	private String sceneId;
	private Label name;

	public SceneWidget(Controller controller, String sceneId) {
		super(controller.getApplicationAssets().getSkin());
		this.controller = controller;
		this.sceneId = sceneId;
		this.scene = (ModelEntity) controller.getModel().getResource(sceneId,
				ResourceCategory.SCENE);

		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		String thumbSavingPath = GameStructure.THUMBNAILS_PATH;
		FileHandle thumbSavingDir = gameAssets.resolve(thumbSavingPath);
		if (!thumbSavingDir.exists()) {
			thumbSavingDir.mkdirs();
		}

		thumbSavingPath += sceneId;
		FileHandle thumbSavingImage = null;
		int i = 0;
		do {
			thumbSavingImage = gameAssets.resolve(thumbSavingPath + (++i)
					+ ".png");
		} while (thumbSavingImage.exists());

		thumbSavingPath = GameStructure.THUMBNAILS_PATH
				+ thumbSavingImage.name();

		controller.action(CreateThumbnail.class, thumbSavingPath, scene,
				THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);

		Skin skin = controller.getApplicationAssets().getSkin();

		final Image image = new Image();
		image.setScaling(Scaling.fit);
		gameAssets.get(thumbSavingPath, Texture.class,
				new AssetLoadedCallback<Texture>() {

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

		stack(image, this.name);
		pad(PAD);
	}

	@Override
	public void setChecked(boolean isChecked) {
		super.setChecked(isChecked);
		if (isChecked) {
			controller.action(SetSelection.class, Selection.SCENE_MAP,
					Selection.SCENE, scene);
		}
	}

	public String getSceneId() {
		return sceneId;
	}

	public void setName(String name) {
		this.name.setText(name);
	}
}