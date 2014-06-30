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
package es.eucm.ead.editor.view.widgets.scenes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Predicate;
import com.badlogic.gdx.utils.SnapshotArray;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorConfiguration;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * This widget holds the edition of a scene. Contains a {@link GroupEditor}.
 */
public abstract class SceneEditor extends AbstractWidget {

	private Controller controller;

	private Model model;

	private EntitiesLoader entitiesLoader;

	protected GroupEditor groupEditor;

	private EngineEntity scene;

	private TransformationFieldListener transformationListener = new TransformationFieldListener();

	private EditSceneListener editSceneListener = new EditSceneListener();

	private ChildrenListListener childrenListListener = new ChildrenListListener();

	private ModelEntityPredicate modelEntityPredicate = new ModelEntityPredicate();

	public SceneEditor(Controller controller) {
		this.controller = controller;
		model = controller.getModel();
		entitiesLoader = controller.getEngine().getEntitiesLoader();

		model.addLoadListener(new LoadListener());
		model.addSelectionListener(new SelectionListener());

		addWidgets(controller.getApplicationAssets().getSkin());

		// If there is a game loaded, load it
		if (model.getGame() != null) {
			load();
		}
	}

	protected void addWidgets(Skin skin) {
		groupEditor = new GroupEditor(controller.getShapeRenderer(),
				createGroupEditorConfiguration());
		groupEditor.addListener(new SceneListener(controller));
		addActor(groupEditor);

	}

	/**
	 * Creates a {@link GroupEditorConfiguration} to initialize the
	 * {@link GroupEditor}.
	 */
	protected abstract GroupEditorConfiguration createGroupEditorConfiguration();

	@Override
	public void layout() {
		groupEditor.setBounds(0, 0, getWidth(), getHeight());
	}

	private void load() {
		ModelEntity game = model.getGame();
		EditState editState = Model.getComponent(game, EditState.class);
		model.removeListenerFromAllTargets(editSceneListener);
		model.addFieldListener(editState, editSceneListener);

		editscene(editState.getEditScene());
	}

	@Override
	public void act(float delta) {
		super.act(0);
	}

	/**
	 * Starts the edition of the scene with the given id
	 */
	public void editscene(String sceneId) {
		ModelEntity sceneEntity = (ModelEntity) model.getResource(sceneId,
				ResourceCategory.SCENE);
		if (sceneEntity != null) {
			scene = entitiesLoader.toEngineEntity(sceneEntity);

			/*
			 * All the assets must be loaded, so all actors has their correct
			 * width and height
			 */
			controller.getEditorGameAssets().finishLoading();
			GameData gameData = Model.getComponent(model.getGame(),
					GameData.class);

			scene.getGroup().setSize(gameData.getWidth(), gameData.getHeight());
			groupEditor.setRootGroup(scene.getGroup());

			model.removeListenerFromAllTargets(transformationListener);
			model.removeListenerFromAllTargets(childrenListListener);

			addListeners(scene.getGroup());
		}
	}

	/**
	 * Adds the appropriate listeners to the given actor and all its children,
	 * recursively
	 */
	private void addListeners(Actor actor) {
		ModelEntity entity = Model.getModelEntity(actor);
		if (entity != null) {
			model.addFieldListener(entity, transformationListener);
			model.addListListener(entity.getChildren(), childrenListListener);

			if (actor instanceof Group) {
				for (Actor child : ((Group) actor).getChildren()) {
					addListeners(child);
				}
			}
		}
	}

	/**
	 * Listens to load events
	 */
	public class LoadListener implements ModelListener<LoadEvent> {

		@Override
		public void modelChanged(LoadEvent event) {
			switch (event.getType()) {
			case LOADED:
				load();
				break;
			}
		}

	}

	/**
	 * Listens to changes in the edited scene
	 */
	public class EditSceneListener implements FieldListener {

		@Override
		public boolean listenToField(FieldName fieldName) {
			return fieldName == FieldName.EDIT_SCENE;
		}

		@Override
		public void modelChanged(FieldEvent event) {
			String scene = event.getValue().toString();
			editscene(scene);
		}
	}

	/**
	 * Handles changes in entities children
	 */
	public class ChildrenListListener implements ModelListener<ListEvent> {

		@Override
		public void modelChanged(ListEvent event) {
			modelEntityPredicate
					.setModelEntity((ModelEntity) event.getParent());
			Actor actor = findActor(scene.getGroup(), modelEntityPredicate);
			switch (event.getType()) {
			case ADDED:
				ModelEntity added = (ModelEntity) event.getElement();
				modelEntityPredicate.setModelEntity(added);
				model.addListListener(added.getChildren(), this);
				model.addFieldListener(added, transformationListener);

				Actor addedActor = findActor(scene.getGroup(),
						modelEntityPredicate);
				if (addedActor == null) {
					EngineEntity engineEntity = entitiesLoader
							.toEngineEntity(added);
					groupEditor.adjustGroup(engineEntity.getGroup());
					((Group) actor).addActorAt(event.getIndex(),
							engineEntity.getGroup());
				}
				break;
			case REMOVED:
				ModelEntity removed = (ModelEntity) event.getElement();
				model.removeListener(removed, transformationListener);
				model.removeListener(removed.getChildren(), this);
				modelEntityPredicate.setModelEntity(removed);

				Actor removedActor = findActor(scene.getGroup(),
						modelEntityPredicate);
				if (removedActor != null) {
					removedActor.remove();
				}
				break;
			}
			readSelection();
		}
	}

	private void readSelection() {
		Array actors = Pools.obtain(Array.class);
		actors.clear();
		SnapshotArray<Object> selection = model.getSelection();
		Object[] objects = selection.begin();
		for (int i = 0; i < selection.size; i++) {
			if (objects[i] instanceof ModelEntity) {
				// Check if this model entity is inside the current scene
				modelEntityPredicate.setModelEntity((ModelEntity) objects[i]);
				Actor actor = findActor(scene.getGroup(), modelEntityPredicate);
				if (actor != null) {
					actors.add(actor);
				}
			}
		}
		selection.end();
		if (actors.size > 0) {
			groupEditor.setSelection(actors);
		} else {
			groupEditor.deselectAll();
		}
		actors.clear();
		Pools.free(actors);
	}

	/**
	 * Handles transformation in children
	 */
	public class TransformationFieldListener implements FieldListener {

		@Override
		public boolean listenToField(FieldName fieldName) {
			return fieldName == FieldName.X || fieldName == FieldName.Y
					|| fieldName == FieldName.ORIGIN_X
					|| fieldName == FieldName.ORIGIN_Y
					|| fieldName == FieldName.ROTATION
					|| fieldName == FieldName.SCALE_X
					|| fieldName == FieldName.SCALE_Y;
		}

		@Override
		public void modelChanged(FieldEvent event) {
			modelEntityPredicate
					.setModelEntity((ModelEntity) event.getTarget());
			Actor actor = findActor(scene.getGroup(), modelEntityPredicate);
			float value = (Float) event.getValue();
			switch (event.getField()) {
			case X:
				actor.setX(value);
				break;
			case Y:
				actor.setY(value);
				break;
			case ROTATION:
				actor.setRotation(value);
				break;
			case ORIGIN_Y:
				actor.setOriginY(value);
				break;
			case ORIGIN_X:
				actor.setOriginX(value);
				break;
			case SCALE_X:
				actor.setScaleX(value);
				break;
			case SCALE_Y:
				actor.setScaleY(value);
				break;
			}
			groupEditor.refresh();
		}
	}

	private class SelectionListener implements ModelListener<SelectionEvent> {

		@Override
		public void modelChanged(SelectionEvent event) {
			readSelection();
		}
	}

	/**
	 * Finds an actor that fulfills the given predicate, starting the search in
	 * the given root
	 * 
	 * @return the actor found. Could be {@code null} if no actor matched the
	 *         predicate
	 */
	public Actor findActor(Group root, Predicate<Actor> predicate) {
		if (predicate.evaluate(root)) {
			return root;
		}

		for (Actor child : root.getChildren()) {
			if (predicate.evaluate(child)) {
				return child;
			} else if (child instanceof Group) {
				Actor actor = findActor((Group) child, predicate);
				if (actor != null) {
					return actor;
				}
			}
		}
		return null;
	}

	public class ModelEntityPredicate implements Predicate<Actor> {

		private ModelEntity modelEntity;

		public void setModelEntity(ModelEntity modelEntity) {
			this.modelEntity = modelEntity;
		}

		@Override
		public boolean evaluate(Actor actor) {
			return Model.getModelEntity(actor) == modelEntity;
		}
	}
}
