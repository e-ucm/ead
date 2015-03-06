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
package es.eucm.ead.editor.view.builders.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Predicate;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel.TypePanel;
import es.eucm.ead.editor.control.actions.editor.ShowModal;
import es.eucm.ead.editor.control.actions.editor.ShowToast;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.model.events.SelectionEvent.Type;
import es.eucm.ead.editor.utils.Actions2;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor;
import es.eucm.ead.editor.view.builders.scene.groupeditor.input.EditStateMachine;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.entities.actors.EntityGroup;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.SceneEditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

public class SceneGroupEditor extends GroupEditor implements ModelView,
		BackListener {

	private Controller controller;

	private Model model;

	private EntitiesLoader entitiesLoader;

	private EngineEntity scene;

	private ModelEntity sceneEntity;

	private SceneEditor sceneEditor;

	private SceneEditListener sceneEditListener = new SceneEditListener();

	private TransformationFieldListener transformationListener = new TransformationFieldListener();

	private ChildrenListListener childrenListListener = new ChildrenListListener();

	private LabelFieldListener labelListener = new LabelFieldListener();

	private ModelEntityPredicate entityPredicate = new ModelEntityPredicate();

	private ComponentPredicate componentPredicate = new ComponentPredicate();

	private SceneSelectionListener sceneSelectionListener = new SceneSelectionListener();

	private AbstractWidget editionButtons;

	private ImageButton fitButton;

	public SceneGroupEditor(Controller control, final SceneEditor sceneEditor) {
		super(control.getApplicationAssets().getSkin());
		this.sceneEditor = sceneEditor;
		this.controller = control;
		this.model = controller.getModel();
		this.entitiesLoader = controller.getEngine().getEntitiesLoader();
		addListener(new EditStateMachine(sceneEditor, this, selectionGroup));
		addListener(new SceneListener(controller));
		addListener(new ActorGestureListener() {

			private final float DISTANCE_HELP_X = AbstractWidget
					.cmToXPixels(0.7f);
			private final float DISTANCE_HELP_Y = AbstractWidget
					.cmToYPixels(0.7f);

			private Rectangle lastTap = new Rectangle();

			@Override
			public void tap(InputEvent event, float x, float y, int count,
					int button) {
				if (count == 1) {
					if (lastTap.contains(x, y)) {
						controller.action(ShowInfoPanel.class,
								TypePanel.ACCURATE_SELECTION,
								Preferences.HELP_ACCURATE_SELECTION);
					}
					lastTap.set(x - DISTANCE_HELP_X / 2, y - DISTANCE_HELP_Y
							/ 2, DISTANCE_HELP_X, DISTANCE_HELP_Y);
				}
			}
		});

		fitButton = WidgetBuilder.imageButton(SkinConstants.IC_FIT,
				SkinConstants.STYLE_SECONDARY_CIRCLE);
		fitButton.addListener(new ClickListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				event.cancel();
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void clicked(InputEvent event, float x, float y) {
				fit(true);
				fitButton.setVisible(false);
			}
		});
		fitButton.setVisible(false);
		editionButtons = new AbstractWidget();
		editionButtons.addActor(fitButton);
		addActor(editionButtons);
	}

	public AbstractWidget getEditionButtons() {
		return editionButtons;
	}

	@Override
	public void enterGroupEdition(Group group) {
		super.enterGroupEdition(group);
		sceneEditor.lockPanels(getRootGroup() != getEditedGroup());
	}

	@Override
	public boolean endGroupEdition() {
		boolean result = super.endGroupEdition();
		sceneEditor.lockPanels(getRootGroup() != getEditedGroup());
		return result;
	}

	@Override
	public void pan(float deltaX, float deltaY, boolean animated) {
		super.pan(deltaX, deltaY, animated);
		fitButton.setVisible(true);
	}

	@Override
	public void layout() {
		super.layout();
		fitButton.pack();
		setPosition(fitButton, WidgetBuilder.dpToPixels(32),
				WidgetBuilder.dpToPixels(32));
	}

	public boolean selectLayer(float x, float y) {
		boolean result = super.selectLayer(x, y);
		if (!result) {
			controller.action(ShowToast.class, controller
					.getApplicationAssets().getI18N().m("nothing_to_select"));
		}
		return result;
	}

	@Override
	protected void showLayerSelector(float x, float y) {
		layerSelector.prepare(layersTouched);
		controller.action(ShowModal.class, layerSelector, x, y);
	}

	@Override
	public void prepare() {
		model.addSelectionListener(sceneSelectionListener);
		readSceneContext();
		readEditedGroup();
		readSelection();
	}

	@Override
	public void release() {
		model.removeListenerFromAllTargets(sceneEditListener);
		model.removeListenerFromAllTargets(transformationListener);
		model.removeListenerFromAllTargets(childrenListListener);
		model.removeListenerFromAllTargets(labelListener);
		if (scene != null) {
			removeListeners(Q.getModelEntity(scene.getGroup()));
		}
		controller.getModel().removeSelectionListener(sceneSelectionListener);
	}

	protected void readSceneContext() {
		if (sceneEntity != null) {
			model.removeListener(
					Q.getComponent(sceneEntity, SceneEditState.class),
					sceneEditListener);
		}
		sceneEntity = (ModelEntity) model.getSelection().getSingle(
				Selection.SCENE);
		if (sceneEntity != null) {
			scene = entitiesLoader.toEngineEntity(sceneEntity);
			GameData gameData = Q.getComponent(controller.getModel().getGame(),
					GameData.class);

			SceneEditState state = Q.getComponent(sceneEntity,
					SceneEditState.class);
			controller.getModel().addFieldListener(state, sceneEditListener);
			addListeners(scene.getGroup());

			panToX(state.getX(), false);
			panToY(state.getY(), false);
			setZoom(state.getZoom(), false);

			scene.getGroup().setSize(gameData.getWidth(), gameData.getHeight());
			setRootGroup(scene.getGroup());
			updateFitVisibility(state);
		} else {
			setRootGroup(null);
		}
	}

	private void updateFitVisibility(SceneEditState state) {
		fitButton.setVisible(!MathUtils.isZero(state.getX())
				|| !MathUtils.isZero(state.getY())
				|| !MathUtils.isEqual(fitZoom, state.getZoom(), 0.001f));
	}

	private void readEditedGroup() {
		ModelEntity editedGroupEntity = (ModelEntity) model.getSelection()
				.getSingle(Selection.EDITED_GROUP);
		if (editedGroupEntity != null) {
			entityPredicate.setEntity(editedGroupEntity);
			Actor actor = findActor(scene.getGroup(), entityPredicate);
			if (actor instanceof Group) {
				enterGroupEdition((Group) actor);
			}
		}
	}

	@Override
	protected Group newGroup() {
		return new EntityGroup();
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
			setSelection(actors);
		} else {
			clearSelection();
		}
		actors.clear();
		Pools.free(actors);
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
					if (child instanceof Label) {
						model.addFieldListener(
								Q.getComponent(
										entity,
										es.eucm.ead.schema.components.controls.Label.class),
								labelListener);
					}
					addListeners(child);
				}
			}
		}
	}

	private void removeListeners(ModelEntity entity) {
		model.removeListener(entity, transformationListener);
		model.removeListener(entity.getChildren(), childrenListListener);
		if (Q.hasComponent(entity,
				es.eucm.ead.schema.components.controls.Label.class)) {
			model.removeListener(Q.getComponent(entity,
					es.eucm.ead.schema.components.controls.Label.class),
					labelListener);
		}
		for (ModelEntity child : entity.getChildren()) {
			removeListeners(child);
		}
	}

	@Override
	public boolean onBackPressed() {
		if (controller.getModel().getSelection().get(Selection.SCENE_ELEMENT).length > 0) {
			controller.action(SetSelection.class, Selection.EDITED_GROUP,
					Selection.SCENE_ELEMENT);
			return true;
		} else {
			return endGroupEdition();
		}
	}

	/**
	 * Handles changes in entities children
	 */
	public class ChildrenListListener implements ModelListener<ListEvent> {

		@Override
		public void modelChanged(ListEvent event) {
			// If parent not in the scene, return
			entityPredicate.setEntity((ModelEntity) event.getParent());
			Actor parent = findActor(getEditedGroup(), entityPredicate);
			if (parent == null) {
				return;
			}

			ModelEntity sceneElement = (ModelEntity) event.getElement();
			switch (event.getType()) {
			case ADDED:

				entityPredicate.setEntity(sceneElement);
				Actor addedActor = findActor(getEditedGroup(), entityPredicate);
				if (addedActor == null) {
					EngineEntity engineEntity = entitiesLoader
							.toEngineEntity(sceneElement);
					addedActor = engineEntity.getGroup();
				}
				((Group) parent).addActorAt(event.getIndex(), addedActor);

				if (controller.getModel().getSelection()
						.contains(Selection.SCENE_ELEMENT, sceneElement)) {
					addToSelection(addedActor);
				}

				// Apply animation only if it is added to the edited group
				if (parent == getEditedGroup()) {
					addedActor.clearActions();
					addedActor.setTouchable(Touchable.disabled);
					float y = sceneElement.getY();
					addedActor.setX(sceneElement.getX());
					addedActor.setY(Gdx.graphics.getHeight());
					addedActor.getColor().a = 0.0f;
					addedActor.setTouchable(Touchable.disabled);

					addedActor.addAction(Actions.sequence(Actions.parallel(
							Actions2.moveToY(y, TIME, Interpolation.exp5Out),
							Actions.alpha(1.0f, TIME, Interpolation.exp5Out)),
							Actions.touchable(Touchable.enabled)));
					addListeners(addedActor);
				}
				break;
			case REMOVED:
				removeListeners(sceneElement);
				entityPredicate.setEntity(sceneElement);

				Actor removedActor = findActor(getEditedGroup(),
						entityPredicate);
				if (removedActor != null) {
					removedActor.clearActions();
					removedActor.setTouchable(Touchable.disabled);
					removedActor.addAction(Actions.sequence(Actions.parallel(
							Actions.scaleTo(0, 0, TIME, Interpolation.exp5In),
							Actions.alpha(0, TIME, Interpolation.exp5In),
							Actions2.moveToY(Gdx.graphics.getHeight(), TIME,
									Interpolation.exp5In)), Actions
							.removeActor()));
				}
				break;
			}
		}
	}

	/**
	 * Handles transformation in the {@link SceneEditState} of the current
	 * scene.
	 */
	public class SceneEditListener implements FieldListener {

		private final Array<String> TRANSFORMATION_FIELDS = new Array<String>(
				new String[] { FieldName.X, FieldName.Y, FieldName.ZOOM });

		@Override
		public boolean listenToField(String fieldName) {
			return TRANSFORMATION_FIELDS.contains(fieldName, false);
		}

		@Override
		public void modelChanged(FieldEvent event) {
			float value = (Float) event.getValue();
			if (FieldName.X.equals(event.getField())) {
				panToX(value, true);
			} else if (FieldName.Y.equals(event.getField())) {
				panToY(value, true);
			} else if (FieldName.ZOOM.equals(event.getField())) {
				setZoom(value, true);
			}
			updateFitVisibility((SceneEditState) event.getTarget());
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
			if (actor != null) {
				float value = (Float) event.getValue();
				if (FieldName.X.equals(event.getField()))
					actor.addAction(Actions2.moveToX(value, TIME,
							Interpolation.exp5Out));
				else if (FieldName.Y.equals(event.getField()))
					actor.addAction(Actions2.moveToY(value, TIME,
							Interpolation.exp5Out));
				else if (FieldName.ROTATION.equals(event.getField()))
					actor.addAction(Actions.rotateTo(value, TIME,
							Interpolation.exp5Out));
				else if (FieldName.ORIGIN_Y.equals(event.getField()))
					actor.setOriginY(value);
				else if (FieldName.ORIGIN_X.equals(event.getField()))
					actor.setOriginX(value);
				else if (FieldName.SCALE_X.equals(event.getField()))
					actor.addAction(Actions2.scaleToX(value, TIME,
							Interpolation.exp5Out));
				else if (FieldName.SCALE_Y.equals(event.getField()))
					actor.addAction(Actions2.scaleToY(value, TIME,
							Interpolation.exp5Out));
				refreshSelectionBox();
			}
		}
	}

	public class LabelFieldListener implements FieldListener {

		private final Array<String> TRANSFORMATION_FIELDS = new Array<String>(
				new String[] { FieldName.TEXT, FieldName.COLOR, FieldName.STYLE });

		@Override
		public boolean listenToField(String fieldName) {
			return TRANSFORMATION_FIELDS.contains(fieldName, false);
		}

		@Override
		public void modelChanged(FieldEvent event) {
			componentPredicate.setComponent((ModelComponent) event.getTarget());
			Actor actor = findActor(scene.getGroup(), componentPredicate);
			Label label = (Label) ((Container) actor).getActor();
			Object value = event.getValue();

			if (FieldName.TEXT.equals(event.getField())) {
				label.setText((String) value);
			} else if (FieldName.COLOR.equals(event.getField())) {
				es.eucm.ead.schema.data.Color modelColor = (es.eucm.ead.schema.data.Color) value;
				Color color = new Color();
				color.a = modelColor.getA();
				color.r = modelColor.getR();
				color.g = modelColor.getG();
				color.b = modelColor.getB();
				label.setColor(color);
			} else if (FieldName.STYLE.equals(event.getField())) {
				label.setStyle(controller.getEditorGameAssets().getSkin()
						.get((String) value, LabelStyle.class));
			}
			refreshSelectionBox();
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
			if (Selection.SCENE.equals(context)
					&& event.getType() == Type.FOCUSED) {
				readSceneContext();
			} else if (Selection.EDITED_GROUP.equals(context)) {
				readEditedGroup();
			} else if (Selection.SCENE_ELEMENT.equals(context)) {
				readSelection();
			}
		}
	}

	public static class ModelEntityPredicate implements Predicate<Actor> {

		private ModelEntity modelEntity;

		public void setEntity(ModelEntity modelEntity) {
			this.modelEntity = modelEntity;
		}

		@Override
		public boolean evaluate(Actor actor) {
			return Q.getModelEntity(actor) == modelEntity;
		}
	}

	public static class ComponentPredicate implements Predicate<Actor> {

		private ModelComponent component;

		public void setComponent(ModelComponent component) {
			this.component = component;
		}

		@Override
		public boolean evaluate(Actor actor) {
			return Q.getModelEntity(actor) != null
					&& Q.getModelEntity(actor).getComponents()
							.contains(component, true);
		}
	}
}
