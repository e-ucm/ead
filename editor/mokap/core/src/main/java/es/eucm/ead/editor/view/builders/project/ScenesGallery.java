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
package es.eucm.ead.editor.view.builders.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.control.actions.model.scene.NewScene;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.util.Map.Entry;

public class ScenesGallery extends AbstractWidget implements
		AssetLoadedCallback<Texture> {

	public static final int SCENES_PER_ROW = 3;

	private Controller controller;

	private Button addScene;

	private Table scenes;

	private ScrollPane scrollPane;

	public ScenesGallery(Controller controller) {
		this.controller = controller;
		Skin skin = controller.getApplicationAssets().getSkin();
		addActor(scrollPane = new ScrollPane(scenes = new Table(), skin));
		addActor(addScene = WidgetBuilder.button(SkinConstants.STYLE_ADD,
				NewScene.class, ""));
	}

	public void prepare() {
		scenes.clearChildren();
		scenes.top().left();

		int i = 0;
		for (Entry<String, Resource> resource : controller.getModel()
				.getResources(ResourceCategory.SCENE).entrySet()) {
			ModelEntity scene = (ModelEntity) resource.getValue().getObject();
			String thumbnail = Q.getThumbnail(controller, scene).getPath();

			ImageButton sceneButton = WidgetBuilder.imageButton(
					SkinConstants.STYLE_SCENE, EditScene.class,
					resource.getKey());
			sceneButton.setStyle(new ImageButtonStyle(sceneButton.getStyle()));

			sceneButton.setName(thumbnail);
			if (i % SCENES_PER_ROW == 0) {
				scenes.row().fillX();
			}
			scenes.add(sceneButton).width(Value.percentWidth(0.33f, scenes));

			controller.getEditorGameAssets()
					.get(thumbnail, Texture.class, this);
			i++;
		}
		controller.getEditorGameAssets().finishLoading();

	}

	@Override
	public void loaded(String fileName, Texture asset) {
		Actor image = scenes.findActor(fileName);
		if (image instanceof ImageButton) {
			((ImageButton) image).getStyle().imageUp = new TextureRegionDrawable(
					new TextureRegion(asset));
		}
	}

	@Override
	public void layout() {
		setBounds(scrollPane, 0, 0, getWidth(), getHeight());
		scenes.setWidth(getWidth());
		scenes.setHeight(Math.max(getHeight(), getPrefHeight(scenes)));

		float width = getPrefWidth(addScene);
		setBounds(addScene, getWidth() - width - WidgetBuilder.dpToPixels(32),
				WidgetBuilder.dpToPixels(32), width, getPrefHeight(addScene));
	}

}
