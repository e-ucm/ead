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
package es.eucm.ead.editor.control.views;

import java.util.Collection;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.ui.scenes.map.SceneEditionWidget;
import es.eucm.ead.editor.ui.scenes.map.SceneList;
import es.eucm.ead.editor.ui.scenes.map.SceneMapWidget;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.Separator;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.editor.components.Thumbnail;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

public class HomeView implements ViewBuilder {

	private static final float DEFAULT_MARGIN = 10F;

	private SceneEditionWidget sceneEdition;

	private SceneList scenesFiltering;

	private SceneMapWidget sceneMap;

	private Controller controller;

	private Actor view;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;

		LinearLayout leftBar = new LinearLayout(false);
		leftBar.add(this.sceneEdition = new SceneEditionWidget(controller))
				.margin(DEFAULT_MARGIN);
		leftBar.add(
				new Separator(true, controller.getApplicationAssets().getSkin()))
				.expandX();
		leftBar.add(this.scenesFiltering = new SceneList(controller)).left()
				.margin(DEFAULT_MARGIN);

		Table placeHolder = new Table();
		placeHolder.add(leftBar).top();
		placeHolder.add(
				new Separator(false, controller.getApplicationAssets()
						.getSkin())).expandY();
		placeHolder.add(sceneMap = new SceneMapWidget(controller))
				.expand(true, true).pad(DEFAULT_MARGIN);
		placeHolder.setFillParent(true);

		view = placeHolder;
	}

	@Override
	public Actor getView(Object... args) {
		Collection<Object> values = controller.getModel()
				.getResources(ResourceCategory.SCENE).values();
		EditorGameAssets editorGameAssets = controller.getEditorGameAssets();
		for (Object object : values) {
			if (object instanceof ModelEntity) {
				ModelEntity modelEntity = (ModelEntity) object;
				Thumbnail thumbnailComp = Q.getComponent(modelEntity,
						Thumbnail.class);
				String thumbnail = thumbnailComp.getThumbnail();
				if (thumbnail != null) {
					if (editorGameAssets.isLoaded(thumbnail, Texture.class)) {
						editorGameAssets.unload(thumbnail);
					}
					thumbnailComp.setThumbnail(null);
				}
				Q.getThumbnail(controller, modelEntity);
			}
		}
		editorGameAssets.finishLoading();
		scenesFiltering.prepare();
		sceneEdition.prepare();
		sceneMap.prepare();
		return view;
	}

	@Override
	public void release(Controller controller) {
		scenesFiltering.release();
		sceneEdition.release();
		sceneMap.release();
	}
}
