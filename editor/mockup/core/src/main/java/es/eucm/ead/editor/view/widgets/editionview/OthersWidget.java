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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.ChangeDocumentation;
import es.eucm.ead.editor.control.actions.model.ChangeInitialScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.listeners.SceneDocumentationListener;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithScalePanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * A widget that displays some edition options for a specific scene.
 */
public class OthersWidget extends IconWithScalePanel implements FieldListener {

	private static final int PREF_DOCUMENTATION_ROWS = 5;

	private static final float DEFAULT_PAD = 10F;
	private static final float DOUBLE_PAD = DEFAULT_PAD * 2F;

	private Controller controller;

	private Skin skin;

	private I18N i18N;

	private TextField name;

	private TextArea description;

	private TextButton makeInitial;

	private SceneDocumentationListener nameDocListener;

	private Documentation documentation;

	public OthersWidget(Controller controlle, float iconPad, float iconSize) {
		super("others80x80", iconPad, 0f, iconSize, controlle
				.getApplicationAssets().getSkin());
		this.controller = controlle;

		Assets assets = controller.getApplicationAssets();
		skin = assets.getSkin();
		i18N = assets.getI18N();

		Label sceneData = new Label(i18N.m("scene.data"), skin);
		Label sceneName = new Label(i18N.m("name") + ":", skin);
		name = new TextField("", skin);
		name.setMessageText(i18N.m("gallery.enterAName"));
		name.setFocusTraversal(false);
		Label sceneDescription = new Label(i18N.m("description") + ":", skin);
		description = new TextArea("", skin);
		description.setMessageText(i18N.m("scene.writeADescription"));
		description.setFocusTraversal(false);
		description.setPrefRows(PREF_DOCUMENTATION_ROWS);
		InputListener nameDocInput = new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == name) {
					controller.action(ChangeDocumentation.class, documentation,
							true, name.getText());
					return true;
				} else if (listenerActor == description) {
					controller.action(ChangeDocumentation.class, documentation,
							false, description.getText());
					return true;
				}
				return false;
			}
		};
		name.addListener(nameDocInput);
		description.addListener(nameDocInput);
		nameDocListener = new SceneDocumentationListener(controller) {
			@Override
			public void nameChanged(String newName) {
				if (!panel.hasParent()) {
					showPanel();
				}
				int cursorPosition = name.getCursorPosition();
				name.setText(newName);
				name.setCursorPosition(Math.max(cursorPosition,
						newName.length()));
			}

			@Override
			public void descriptionChanged(String newDescription) {
				if (!panel.hasParent()) {
					showPanel();
				}
				int cursorPosition = description.getCursorPosition();
				description.setText(newDescription);
				description.setCursorPosition(Math.max(cursorPosition,
						newDescription.length()));
			}
		};
		makeInitial = new TextButton("", skin, "white");
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
				}
			}

		});

		Model model = controller.getModel();
		model.addSelectionListener(new SelectionListener() {
			@Override
			public boolean listenToContext(String contextId) {
				return Selection.SCENE.equals(contextId);
			}

			@Override
			public void modelChanged(SelectionEvent event) {
				if (event.getType() != SelectionEvent.Type.REMOVED) {
					Object object = event.getSelection()[0];
					if (object instanceof ModelEntity) {
						read((ModelEntity) object);
					}
				}
			}
		});
		model.addLoadListener(new ModelListener<LoadEvent>() {

			@Override
			public void modelChanged(LoadEvent event) {
				if (event.getType() == LoadEvent.Type.LOADED) {
					prepareInitialSceneListener();
				}
			}
		});

		float prefW = name.getPrefWidth() * 2;
		panel.defaults().pad(DEFAULT_PAD).space(DEFAULT_PAD);
		panel.top();
		panel.add(sceneData).padTop(DOUBLE_PAD);
		panel.row();
		panel.add(sceneName).left().padLeft(DOUBLE_PAD);
		panel.row();
		panel.add(name).prefWidth(prefW);
		panel.row();
		panel.add(sceneDescription).left().padLeft(DOUBLE_PAD);
		panel.row();
		panel.add(description).prefWidth(prefW);
		panel.row();
		panel.add(makeInitial);

		prepareInitialSceneListener();
		Object single = model.getSelection().getSingle(Selection.SCENE);
		if (single instanceof ModelEntity) {
			read((ModelEntity) single);
		}
	}

	public void prepareInitialSceneListener() {
		Model model = controller.getModel();
		model.removeListenerFromAllTargets(this);
		ModelEntity game = model.getGame();
		GameData gameData = Q.getComponent(game, GameData.class);
		model.addFieldListener(gameData, this);
	}

	public void read(ModelEntity scene) {
		documentation = Q.getComponent(scene, Documentation.class);
		nameDocListener.setUp(scene);

		String docName = documentation.getName();
		if (docName == null) {
			docName = "";
		}
		name.setText(docName);

		String docDescription = documentation.getDescription();
		if (docDescription == null) {
			docDescription = "";
		}
		description.setText(docDescription);

		updateInitialSceneButton(scene);
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
			if (!panel.hasParent()) {
				showPanel();
			}
			updateInitialSceneButton(scene);
		}

	}

	private void updateInitialSceneButton(ModelEntity scene) {
		if (isInitial(scene)) {
			makeInitial.setDisabled(true);
			makeInitial.setText(i18N.m("scene.isInitial"));
		} else {
			makeInitial.setDisabled(false);
			makeInitial.setText(i18N.m("scene.makeInitial"));
		}
	}

	@Override
	public boolean listenToField(String fieldName) {
		return FieldName.INITIAL_SCENE.equals(fieldName);
	}

}
