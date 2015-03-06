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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Predicate;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor.GroupEvent;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor.GroupListener;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorConfiguration;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.editor.components.ElementEditState;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.SceneEditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * This widget holds the edition of a scene. Contains a {@link GroupEditor}.
 */
public abstract class SceneEditor extends AbstractWidget {

	public static final String NAME = "sceneEditor";

	protected Controller controller;

	private Model model;

	private EntitiesLoader entitiesLoader;

	protected GroupEditor groupEditor;

	private EngineEntity scene;

	private ModelEntity sceneEntity;

	private TransformationFieldListener transformationListener = new TransformationFieldListener();

	private ChildrenListListener childrenListListener = new ChildrenListListener();

	private ModelEntityPredicate entityPredicate = new ModelEntityPredicate();

	private ComponentPredicate componentPredicate = new ComponentPredicate();

	private SceneSelectionListener sceneSelectionListener = new SceneSelectionListener();

	private boolean fit;

	public SceneEditor(Controller controller) {
		this.setName(NAME);
		this.controller = controller;
		model = controller.getModel();
		entitiesLoader = controller.getEngine().getEntitiesLoader();
		addWidgets(controller.getApplicationAssets().getSkin());
	}

	protected void addWidgets(Skin skin) {
		groupEditor = new GroupEditor(controller.getShapeRenderer(),
				createGroupEditorConfiguration());
		groupEditor.addListener(new SceneListener(controller));
		groupEditor.addListener(new GroupListener() {
			@Override
			public void containerUpdated(GroupEvent event, Group container) {
				updateEditState();
			}
		});
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
		if (fit) {
			fit = false;
			groupEditor.fit(true);
		}
	}

	@Override
	public void act(float delta) {
		super.act(0);
	}

	public void prepare() {
		release();
		model.addSelectionListener(sceneSelectionListener);
		readSceneContext();
		readEditedGroup();
		readSelection();
	}

	public void release() {
		model.removeListenerFromAllTargets(transformationListener);
		model.removeListenerFromAllTargets(childrenListListener);
		if (scene != null) {
			removeListeners(Q.getModelEntity(scene.getGroup()));
		}
		controller.getModel().removeSelectionListener(sceneSelectionListener);
	}

	protected void readSceneContext() {
		sceneEntity = (ModelEntity) model.getSelection().getSingle(
				Selection.SCENE);
		if (sceneEntity != null) {
			scene = entitiesLoader.toEngineEntity(sceneEntity);

			/*
			 * All the assets must be loaded, so all actors has their correct
			 * width and height
			 */
			controller.getEditorGameAssets().getAssetManager().finishLoading();
			GameData gameData = Q.getComponent(model.getGame(), GameData.class);

			scene.getGroup().setSize(gameData.getWidth(), gameData.getHeight());
			groupEditor.setRootGroup(scene.getGroup());

			addListeners(scene.getGroup());

			setTouchableAndVisible(scene.getGroup());

			if (Q.hasComponent(sceneEntity, SceneEditState.class)) {
				SceneEditState state = Q.getComponent(sceneEntity,
						SceneEditState.class);
				groupEditor.setZoom(state.getZoom());
				groupEditor.setPanningOffset(state.getX(), state.getY());
			} else {
				// At this point, we cant know for sure if the view is added, so
				// we have to postpone fit
				fit = true;
			}
		} else {
			groupEditor.setRootGroup(null);
		}
	}

	private void setTouchableAndVisible(Actor actor) {
		ModelEntity entity = Q.getModelEntity(actor);
		if (entity != null) {
			ElementEditState elementState = Q.getComponent(entity,
					ElementEditState.class);
			if (elementState.isLock()) {
				actor.setTouchable(Touchable.disabled);
			} else {
				actor.setTouchable(Touchable.enabled);
			}
			actor.setVisible(!elementState.isInvisible());
			if (actor instanceof Group) {
				for (Actor child : ((Group) actor).getChildren()) {
					setTouchableAndVisible(child);
				}
			}
		}

	}

	private void readEditedGroup() {
		ModelEntity editedGroupEntity = (ModelEntity) model.getSelection()
				.getSingle(Selection.EDITED_GROUP);
		if (editedGroupEntity != null) {
			entityPredicate.setEntity(editedGroupEntity);
			Actor actor = findActor(scene.getGroup(), entityPredicate);
			if (actor instanceof Group) {
				groupEditor.getGroupEditorDragListener().enterGroupEdition(
						(Group) actor);
			}
		}
	}

	private void readSelection() {
		Array actors = Pools.obtain(Array.class);
		actors.clear();
		Object[] selection = model.getSelection().get(Selection.SCENE_ELEMENT);
		for (Object object : selection) {
			if (object instanceof ModelEntity) {
				Actor actor = findActor((ModelEntity) object);
				if (actor != null) {
					actors.add(actor);
				}
			}
		}
		if (actors.size > 0) {
			groupEditor.setSelection(actors);
		} else {
			groupEditor.deselectAll();
		}
		actors.clear();
		Pools.free(actors);
	}

	private void updateEditState() {
		if (sceneEntity != null) {
			SceneEditState state = Q.getComponent(sceneEntity,
					SceneEditState.class);
			state.setZoom(groupEditor.getZoom());
			state.setX(groupEditor.getPanningX());
			state.setY(groupEditor.getPanningY());
		}
	}

	public Actor findActor(ModelEntity entity) {
		// Check if this model entity is inside the current scene
		entityPredicate.setEntity(entity);
		return findActor(scene.getGroup(), entityPredicate);
	}

	/**
	 * Adds the appropriate listeners to the given actor and all its children,
	 * recursively
	 */
	private void addListeners(Actor actor) {
		ModelEntity entity = Q.getModelEntity(actor);
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

	private void removeListeners(ModelEntity entity) {
		model.removeListener(entity, transformationListener);
		model.removeListener(entity.getChildren(), childrenListListener);
		for (ModelEntity child : entity.getChildren()) {
			removeListeners(child);
		}
	}

	/**
	 * Sets in the given vector the scene center, according to the current
	 * viewport and scene transformations
	 */
	public void getViewPortCenter(Vector2 center) {
		groupEditor.getViewPortCenter(center);
	}

	/**
	 * Handles changes in entities children
	 */
	public class ChildrenListListener implements ModelListener<ListEvent> {

		@Override
		public void modelChanged(ListEvent event) {
			entityPredicate.setEntity((ModelEntity) event.getParent());
			Actor actor = findActor(scene.getGroup(), entityPredicate);
			if (actor == null) {
				return;
			}
			switch (event.getType()) {
			case ADDED:
				ModelEntity added = (ModelEntity) event.getElement();
				entityPredicate.setEntity(added);

				Actor addedActor = findActor(scene.getGroup(), entityPredicate);
				if (addedActor == null) {
					EngineEntity engineEntity = entitiesLoader
							.toEngineEntity(added);
					addedActor = engineEntity.getGroup();
					groupEditor.adjustGroup(engineEntity.getGroup());

					((Group) actor).addActorAt(event.getIndex(), addedActor);
				}
				addListeners(addedActor);
				break;
			case REMOVED:
				ModelEntity removed = (ModelEntity) event.getElement();
				removeListeners(removed);
				entityPredicate.setEntity(removed);

				Actor removedActor = findActor(scene.getGroup(),
						entityPredicate);
				if (removedActor != null) {
					removedActor.remove();
				}
				break;
			}
		}
	}

	/**
	 * Handles transformation in children
	 */
	public class TransformationFieldListener implements FieldListener {

		private final Array<String> TRANSFORMATION_FIELDS = new Array<String>(
				new String[] { FieldName.X, FieldName.Y, FieldName.ORIGIN_X,
						FieldName.ORIGIN_Y, FieldName.ROTATION,
						FieldName.SCALE_X, FieldName.SCALE_Y });

		@Override
		public boolean listenToField(String fieldName) {
			return TRANSFORMATION_FIELDS.contains(fieldName, false);
		}

		@Override
		public void modelChanged(FieldEvent event) {
			entityPredicate.setEntity((ModelEntity) event.getTarget());
			Actor actor = findActor(scene.getGroup(), entityPredicate);
			float value = (Float) event.getValue();
			if (FieldName.X.equals(event.getField()))
				actor.setX(value);
			else if (FieldName.Y.equals(event.getField()))
				actor.setY(value);
			else if (FieldName.ROTATION.equals(event.getField()))
				actor.setRotation(value);
			else if (FieldName.ORIGIN_Y.equals(event.getField()))
				actor.setOriginY(value);
			else if (FieldName.ORIGIN_X.equals(event.getField()))
				actor.setOriginX(value);
			else if (FieldName.SCALE_X.equals(event.getField()))
				actor.setScaleX(value);
			else if (FieldName.SCALE_Y.equals(event.getField()))
				actor.setScaleY(value);
			groupEditor.refresh();
		}
	}

	private class SceneSelectionListener implements SelectionListener {

		private final Array<String> CONTEXTS = new Array<String>(new String[] {
				Selection.SCENE, Selection.EDITED_GROUP,
				Selection.SCENE_ELEMENT });

		@Override
		public boolean listenToContext(String contextId) {
			return CONTEXTS.contains(contextId, false);
		}

		@Override
		public void modelChanged(SelectionEvent event) {
			String context = event.getContextId();
			if (Selection.SCENE.equals(context)) {
				readSceneContext();
			} else if (Selection.EDITED_GROUP.equals(context)) {
				readEditedGroup();
			} else if (Selection.SCENE_ELEMENT.equals(context)) {
				readSelection();
			}
		}
	}

	public class ModelEntityPredicate implements Predicate<Actor> {

		private ModelEntity modelEntity;

		public void setEntity(ModelEntity modelEntity) {
			this.modelEntity = modelEntity;
		}

		@Override
		public boolean evaluate(Actor actor) {
			return Q.getModelEntity(actor) == modelEntity;
		}
	}

	public class ComponentPredicate implements Predicate<Actor> {

		private ModelComponent component;

		public void setComponent(ModelComponent component) {
			this.component = component;
		}

		@Override
		public boolean evaluate(Actor actor) {
			return Q.getModelEntity(actor).getComponents()
					.contains(component, true);
		}
	}
}
