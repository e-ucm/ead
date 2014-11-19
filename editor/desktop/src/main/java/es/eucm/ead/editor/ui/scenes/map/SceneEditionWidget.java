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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.ChangeInitialScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.controllers.ClassOptionsController;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * A widget that displays some edition options for a specific scene.
 */
public class SceneEditionWidget extends LinearLayout implements FieldListener {

	protected Controller controller;

	protected Skin skin;

	protected I18N i18N;

	private ClassOptionsController<Documentation> optionsController;

	private Button makeInitial;

	private Image thumbnail;

	private AssetLoadedCallback<Texture> thumbnailCallback = new AssetLoadedCallback<Texture>() {

		@Override
		public void loaded(String fileName, Texture asset) {
			thumbnail.setDrawable(new TextureRegionDrawable(new TextureRegion(
					asset)));
		}
	};

	public SceneEditionWidget(Controller control) {
		super(false);
		this.controller = control;

		Assets assets = controller.getApplicationAssets();
		skin = assets.getSkin();
		i18N = assets.getI18N();

		thumbnail = new Image();
		thumbnail.setScaling(Scaling.fit);
		optionsController = new ClassOptionsController<Documentation>(
				controller, skin, Documentation.class);
		makeInitial = new TextButton(i18N.m("general.make-initial"), skin);
		makeInitial.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Object object = controller.getModel().getSelection()
						.getSingle(Selection.SCENE);
				if (object instanceof ModelEntity) {
					ModelEntity scene = (ModelEntity) object;
					Model model = controller.getModel();
					String sceneId = model.getIdFor(scene);
					controller.action(ChangeInitialScene.class, sceneId);
					makeInitial.setDisabled(true);
				}
			}

		});
		controller.getModel().addSelectionListener(new SelectionListener() {
			@Override
			public boolean listenToContext(String contextId) {
				return Selection.SCENE.equals(contextId);
			}

			@Override
			public void modelChanged(SelectionEvent event) {
				if (event.getType() == SelectionEvent.Type.FOCUSED) {
					Object object = event.getSelection()[0];
					if (object instanceof ModelEntity) {
						read((ModelEntity) object);
					}
				}
			}
		});
	}

	public void read(ModelEntity scene) {
		Documentation doc = Q.getComponent(scene, Documentation.class);
		optionsController.read(doc);
		makeInitial.setDisabled(isInitial(scene));

		controller.getEditorGameAssets().get(
				Q.getThumbnail(controller, scene).getPath(), Texture.class,
				thumbnailCallback, true);

		clearChildren();
		add(thumbnail);
		add(optionsController.getPanel());
		add(makeInitial).right();
	}

	public void prepare() {
		Model model = controller.getModel();
		model.addFieldListener(Q.getComponent(model.getGame(), GameData.class),
				this);
	}

	public void release() {
		controller.getModel().removeListenerFromAllTargets(this);
	}

	private boolean isInitial(ModelEntity entity) {
		Model model = controller.getModel();
		String sceneId = model.getIdFor(entity);
		String initialScene = Q.getComponent(model.getGame(), GameData.class)
				.getInitialScene();
		return initialScene != null && initialScene.equals(sceneId);
	}

	@Override
	public void modelChanged(FieldEvent event) {
		Object sel = controller.getModel().getSelection()
				.getSingle(Selection.SCENE);
		if (sel instanceof ModelEntity) {
			ModelEntity scene = (ModelEntity) sel;
			makeInitial.setDisabled(isInitial(scene));
		}

	}

	@Override
	public boolean listenToField(String fieldName) {
		return FieldName.INITIAL_SCENE.equals(fieldName);
	}

}
