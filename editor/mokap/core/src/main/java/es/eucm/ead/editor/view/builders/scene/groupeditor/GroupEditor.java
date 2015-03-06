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
package es.eucm.ead.editor.view.builders.scene.groupeditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.utils.Actions2;
import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor.GroupEvent.Type;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.engine.utils.EngineUtils;

public class GroupEditor extends AbstractWidget {

	public static final float TIME = 0.25f;

	public static final Vector2 tmp = new Vector2();

	public static final float NEAR_CM = 1.0f;

	public static final float ALPHA_FACTOR = 0.24f;

	private GroupEditorStyle style;

	private boolean multipleSelection;

	private Group rootGroup;

	private Group editedGroup;

	protected SelectionGroup selectionGroup;

	protected Array<Actor> layersTouched;

	protected LayerSelector layerSelector;

	private boolean onlySelection;

	private AbstractWidget sceneContainer;

	private Container sceneBackground;

	protected float zoom = 1.0f, fitZoom, maxZoom, minZoom;

	private Runnable containerUpdated = new Runnable() {

		@Override
		public void run() {
			fireContainerUpdated();
		}

	};

	public GroupEditor(Skin skin) {
		this(skin.get(GroupEditorStyle.class));
	}

	public GroupEditor(GroupEditorStyle style) {
		this.style = style;
		layersTouched = new Array<Actor>();
		layerSelector = new LayerSelector(this, style);

		selectionGroup = new SelectionGroup(this, style);

		addActor(sceneContainer = new AbstractWidget());

		sceneBackground = new Container();
		sceneBackground.setBackground(style.groupBackground);
		sceneContainer.addActor(sceneBackground);

		sceneContainer.addActor(selectionGroup);

		TouchRepresentation touchRepresentation = new TouchRepresentation(
				style.touch);
		addActor(touchRepresentation);

		addListener(touchRepresentation);
	}

	/**
	 * Sets the group being edited
	 */
	public void setRootGroup(Group rootGroup) {
		if (this.rootGroup != null) {
			this.rootGroup.remove();
		}
		this.rootGroup = rootGroup;
		if (rootGroup != null) {
			sceneBackground.setBounds(
					-style.groupBackground.getLeftWidth(),
					-style.groupBackground.getBottomHeight(),
					rootGroup.getWidth() + style.groupBackground.getLeftWidth()
							+ style.groupBackground.getRightWidth(),
					rootGroup.getHeight()
							+ style.groupBackground.getTopHeight()
							+ style.groupBackground.getBottomHeight());
			sceneContainer.addActorAfter(sceneBackground, rootGroup);
			editedGroup = rootGroup;
			invalidate();
		}
	}

	public void setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
	}

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public boolean isOnlySelection() {
		return onlySelection;
	}

	public void setOnlySelection(boolean onlySelection) {
		this.onlySelection = onlySelection;
	}

	// Invoke by UI (Requires fireContainerUpdated)

	/**
	 * Sets the root group in its initial position, fitting the view
	 */
	public void fit(boolean animated) {
		setZoom(fitZoom, animated);
		panToX(0, animated);
		panToY(0, animated);
	}

	// Invoked programmatically

	public void pan(float deltaX, float deltaY, boolean animated) {
		panToX(sceneContainer.getX() + deltaX, animated);
		panToY(sceneContainer.getY() + deltaY, animated);
	}

	public void panToX(float x, boolean animated) {
		if (animated) {
			sceneContainer.addAction(Actions2.moveToX(x, TIME,
					Interpolation.exp5Out));
		} else {
			sceneContainer.setX(x);
		}
	}

	public void panToY(float y, boolean animated) {
		if (animated) {
			sceneContainer.addAction(Actions2.moveToY(y, TIME,
					Interpolation.exp5Out));
		} else {
			sceneContainer.setY(y);
		}
	}

	public float getZoom() {
		return zoom;
	}

	public void zoomIn() {
		zoom(getWidth() / 2.0f, getHeight() / 2.0f, zoom + 0.25f, true);
	}

	public void zoomOut() {
		zoom(getWidth() / 2.0f, getHeight() / 2.0f, zoom - 0.25f, true);
	}

	/**
	 * Changes the zoom level
	 */
	public void zoom(float centerX, float centerY, float newZoom,
			boolean animate) {

		if (MathUtils.isEqual(zoom, newZoom, 0.001f)) {
			return;
		}

		this.zoom = Math.min(maxZoom, Math.max(minZoom, newZoom));

		Vector2 center = tmp.set(centerX, centerY);
		localToDescendantCoordinates(sceneContainer, center);

		float oldScale = sceneContainer.getScaleX();
		sceneContainer.setScale(zoom, zoom);

		sceneContainer.localToAscendantCoordinates(this, center);

		float newX = sceneContainer.getX() + (centerX - center.x);
		float newY = sceneContainer.getY() + (centerY - center.y);

		if (animate) {
			sceneContainer.setScale(oldScale, oldScale);
			sceneContainer.getActions().clear();
			sceneContainer.addAction(Actions.sequence(Actions.parallel(
					Actions.scaleTo(zoom, zoom, 0.21f, Interpolation.exp5Out),
					Actions.moveTo(newX, newY, 0.21f, Interpolation.exp5Out)),
					Actions.run(containerUpdated)));
		} else {
			sceneContainer.setPosition(newX, newY);
			fireContainerUpdated();
		}
	}

	public void setZoom(float zoom, boolean animate) {
		this.zoom = zoom;
		if (animate) {
			sceneContainer.addAction(Actions.scaleTo(zoom, zoom, 0.21f,
					Interpolation.exp5Out));
		} else {
			sceneContainer.setScale(zoom, zoom);
		}
	}

	@Override
	public void layout() {
		Vector2 fitSize = Scaling.fit.apply(rootGroup.getWidth(),
				rootGroup.getHeight(), getWidth(), getHeight());
		this.fitZoom = fitSize.x / rootGroup.getWidth();
		this.maxZoom = fitZoom * 4;
		this.minZoom = fitZoom * 0.25f;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		batch.setColor(Color.WHITE);
		if (style.background != null) {
			style.background.draw(batch, 0, 0, getWidth(), getHeight());
		}
		super.drawChildren(batch, parentAlpha);
	}

	public AbstractWidget getSceneContainer() {
		return sceneContainer;
	}

	public Array<Actor> getSelection() {
		return selectionGroup.getSelection();
	}

	public void setSelection(Iterable<Actor> selection) {
		boolean isMultiSelection = isMultipleSelection();
		setMultipleSelection(true);
		selectionGroup.clearChildren();
		for (Actor actor : selection) {
			addToSelection(actor);
		}
		setMultipleSelection(isMultiSelection);
	}

	public void addToSelection(Actor actor) {
		selectionGroup.select(actor);
	}

	/**
	 * Clears the current selection
	 */
	public void clearSelection() {
		selectionGroup.clearChildren();
	}

	public void refreshSelectionBox() {
		selectionGroup.refreshSelectionBoxes();
	}

	public Group getRootGroup() {
		return rootGroup;
	}

	public Group getEditedGroup() {
		return editedGroup;
	}

	protected Group newGroup() {
		return new Group();
	}

	/**
	 * Creates a group with the current selection. It is notified through a
	 * grouped event
	 */
	public void createGroupWithSelection() {
		Group group = newGroup();
		for (Actor a : selectionGroup.getSelection()) {
			group.addActor(a);
		}
		EngineUtils.adjustGroup(group);
		fireGrouped(group);
		clearSelection();
		addToSelection(group);
		fireSelection();
	}

	/**
	 * Ungroups all groups in selection
	 */
	public void ungroup() {
		Array<Actor> toUngroup = Pools.obtain(Array.class);
		toUngroup.addAll(getSelection());
		clearSelection();
		for (Actor actor : toUngroup) {
			if (actor instanceof Group) {
				ungroup((Group) actor);
			}
		}
		Pools.free(toUngroup);
	}

	/**
	 * Ungroups the given group in a list of actors. The resulting actors
	 * accumulates the transformation of the group to keep its absolute
	 * coordinates the same. The group is removed.
	 */
	private Array<Actor> ungroup(Group group) {
		Group parent = group.getParent();
		Array<Actor> actors = new Array<Actor>();
		for (Actor actor : group.getChildren()) {
			EngineUtils.computeTransformFor(actor, parent);
			actors.add(actor);
		}

		for (Actor actor : actors) {
			parent.addActor(actor);
		}

		group.remove();
		fireUngroup(parent, group, actors);

		boolean multipleSelection = isMultipleSelection();
		setMultipleSelection(true);
		for (Actor a : actors) {
			addToSelection(a);
		}
		fireSelection();
		setMultipleSelection(multipleSelection);

		return actors;
	}

	/**
	 * Changes the current edited group to the given one.
	 * 
	 * @param group
	 *            must be a direct child of the current edited group. If not,
	 *            nothing happens
	 */
	public void enterGroupEdition(Group group) {
		if (group != editedGroup && group != null
				&& group.getChildren().size > 1
				&& editedGroup.getChildren().contains(group, true)) {
			editedGroup = group;
			for (Actor actor : editedGroup.getParent().getChildren()) {
				// Make non-edited actors transparent
				if (actor != editedGroup) {
					Color c = actor.getColor();
					actor.setColor(c.r, c.g, c.b, c.a * ALPHA_FACTOR);
				}
			}

			for (Actor actor : editedGroup.getChildren()) {
				EngineUtils.computeTransformFor(actor, editedGroup.getParent());
			}
			editedGroup.setPosition(0, 0);
			editedGroup.setRotation(0);
			editedGroup.setScale(1.0f, 1.0f);

			clearSelection();
			fireSelection();
			fireEnteredGroupEdition(editedGroup);
		}
	}

	/**
	 * Edited group is set to the parent of the current edited group
	 */
	public boolean endGroupEdition() {
		// Only can end a group edition of the edited group is not the root
		if (editedGroup != rootGroup) {
			clearSelection();
			Group nextEditedGroup = editedGroup.getParent();
			Group oldGroup = editedGroup;
			/*
			 * The current edited group has changed. It is necessary to simplify
			 * it in case it has less than 2 children, which will make it no
			 * longer a group.
			 */
			Actor simplifiedGroup = simplifyGroup(oldGroup);

			/*
			 * Removed the edited group. Whatever is left of it is in
			 * simplifiedGroup.
			 */
			oldGroup.remove();

			if (simplifiedGroup != null) {
				nextEditedGroup.addActor(simplifiedGroup);
				addToSelection(simplifiedGroup);
				fireTransformed();
			}
			editedGroup = nextEditedGroup;

			// Restore transparency values
			for (Actor actor : editedGroup.getChildren()) {
				if (actor != editedGroup) {
					Color c = actor.getColor();
					actor.setColor(c.r, c.g, c.b, c.a / ALPHA_FACTOR);
				}
			}
			fireSelection();
			fireEndedGroupEdition(nextEditedGroup, oldGroup, simplifiedGroup);
			return true;
		}
		return false;
	}

	/**
	 * @return if the group has 1 children, returns that child with the parent
	 *         transformation applied. If the group has no children,
	 *         {@code null} is returned. Otherwise, the passed group is
	 *         returned.
	 */
	private Actor simplifyGroup(Group group) {
		Actor result;
		if (group.getChildren().size == 0) {
			result = null;
		} else if (group.getChildren().size == 1) {
			result = group.getChildren().first();
			EngineUtils.computeTransformFor(result, group.getParent());
		} else {
			EngineUtils.adjustGroup(group);
			fireGroupSimplified(group);
			result = group;
		}
		return result;
	}

	float[] points = new float[8];

	/**
	 * @return if there was any element in the given coordinates
	 */
	public boolean selectLayer(float x, float y) {
		layersTouched.clear();
		Vector2 tmp = Pools.obtain(Vector2.class);
		Polygon polygon = Pools.obtain(Polygon.class);

		for (Actor actor : editedGroup.getChildren()) {

			int j = 0;
			for (int i = 0; i < 4; i++) {
				tmp.set(i == 0 || i == 3 ? 0 : actor.getWidth(), i > 1 ? 0
						: actor.getHeight());
				actor.localToAscendantCoordinates(this, tmp);
				points[j++] = tmp.x;
				points[j++] = tmp.y;
			}
			polygon.setVertices(points);
			if (polygon.contains(x, y)) {
				layersTouched.add(actor);
			} else {
				for (int i = 0; i < 8; i += 2) {
					if (nearEnough(x, y, points[i], points[i + 1])) {
						layersTouched.add(actor);
						break;
					}
				}
			}
		}
		Pools.free(polygon);
		Pools.free(tmp);
		if (layersTouched.size > 0) {
			showLayerSelector(x, y);
			return true;
		}
		return false;
	}

	protected void showLayerSelector(float x, float y) {
		layerSelector.prepare(layersTouched);
		layerSelector.setPosition(x, y);
		addActor(layerSelector);
		layerSelector.show(null);
	}

	private boolean nearEnough(float x1, float y1, float x2, float y2) {
		return Math.abs(x1 - x2) < cmToXPixels(NEAR_CM)
				&& Math.abs(y1 - y2) < cmToYPixels(NEAR_CM);
	}

	public void fireContainerUpdated() {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.containerUpdated);
		groupEvent.setParent(sceneContainer);
		fire(groupEvent);
		Pools.free(groupEvent);
	}

	private void fireGroupSimplified(Group group) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.transformed);
		groupEvent.setSelection(group.getChildren());
		groupEvent.getSelection().add(group);
		fire(groupEvent);
		Pools.free(groupEvent);
	}

	/**
	 * Fires some actors has been transformed
	 */
	public void fireTransformed() {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.transformed);
		groupEvent.setSelection(selectionGroup.getSelection());
		fire(groupEvent);
		Pools.free(groupEvent);
	}

	private void fireGrouped(Group group) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.grouped);
		groupEvent.setSelection(selectionGroup.getSelection());
		groupEvent.setGroup(group);
		groupEvent.setParent(editedGroup);
		fire(groupEvent);
		Pools.free(groupEvent);
	}

	/**
	 * Fires an ungroup notification
	 * 
	 * @param parent
	 *            the parent for the actors
	 * @param oldGroup
	 *            the old group grouping the actors
	 * @param actors
	 *            the actors ungrouped
	 */
	private void fireUngroup(Group parent, Group oldGroup, Array<Actor> actors) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.ungrouped);
		groupEvent.setParent(parent);
		groupEvent.setGroup(oldGroup);
		groupEvent.setSelection(actors);
		fire(groupEvent);
		Pools.free(groupEvent);
	}

	/**
	 * Notifies current selection has been updated
	 */
	public void fireSelection() {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.selected);
		groupEvent.setSelection(selectionGroup.getSelection());
		fire(groupEvent);
		Pools.free(groupEvent);
	}

	private void fireEnteredGroupEdition(Group group) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.enteredEdition);
		groupEvent.setGroup(group);
		fire(groupEvent);
		Pools.free(groupEvent);
	}

	private void fireEndedGroupEdition(Group parent, Group oldGroup,
			Actor resultingGroup) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.exitedEdition);
		groupEvent.setParent(parent);
		groupEvent.setGroup(oldGroup);
		groupEvent.setSelection(resultingGroup);
		fire(groupEvent);
		Pools.free(groupEvent);
	}

	public static class GroupEditorStyle {

		public Drawable background;

		public Drawable selectedBackground;

		public Drawable groupBackground;

		/**
		 * Background for layer selector
		 */
		public Drawable layersBackground;

		public ButtonStyle layerButtonStyle;

		public Drawable touch;

		public Color pressedColor;

		public Color selectedColor;

		public Color movingColor;

		public Color multiSelectedColor;

		public Color multiMovingColor;

		public Color onlySelectionColor;

		public float alpha;

	}

	public static class GroupEvent extends Event {

		private Type type;

		private Array<Actor> selection = new Array<Actor>();

		private Group group;

		private Group parent;

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public Group getGroup() {
			return group;
		}

		public void setGroup(Group group) {
			this.group = group;
		}

		public Array<Actor> getSelection() {
			return selection;
		}

		public Group getParent() {
			return parent;
		}

		public void setParent(Group parent) {
			this.parent = parent;
		}

		public void setSelection(Array<Actor> selection) {
			this.selection.clear();
			this.selection.addAll(selection);
		}

		public void setSelection(Actor selection) {
			this.selection.clear();
			this.selection.add(selection);
		}

		@Override
		public void reset() {
			super.reset();
			this.selection.clear();
			this.group = null;
		}

		static public enum Type {
			selected, deleted, transformed, grouped, ungrouped, enteredEdition, exitedEdition, containerUpdated, rootChanged
		}
	}

	/**
	 * Base class to listen to {@link GroupEvent}s produced by
	 * {@link GroupEditor}.
	 */
	public static class GroupListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof GroupEvent) {
				GroupEvent groupEvent = (GroupEvent) event;
				switch (groupEvent.getType()) {
				case containerUpdated:
					containerUpdated(groupEvent, groupEvent.getParent());
					break;
				case rootChanged:
					rootChanged(groupEvent, groupEvent.getParent());
					break;
				case selected:
					selectionUpdated(groupEvent, groupEvent.getSelection());
					break;
				case deleted:
					deleted(groupEvent, groupEvent.getParent(),
							groupEvent.getSelection());
					break;
				case transformed:
					transformed(groupEvent, groupEvent.getParent(),
							groupEvent.getSelection());
					break;
				case grouped:
					grouped(groupEvent, groupEvent.getParent(),
							groupEvent.getGroup(), groupEvent.getSelection());
					break;
				case ungrouped:
					ungrouped(groupEvent, groupEvent.getParent(),
							groupEvent.getGroup(), groupEvent.getSelection());
					break;
				case enteredEdition:
					enteredGroupEdition(groupEvent, groupEvent.getGroup());
					break;
				case exitedEdition:
					exitedGroupEdition(groupEvent, groupEvent.getParent(),
							groupEvent.getGroup(), groupEvent.getSelection()
									.first());
				}
				return true;
			}
			return false;
		}

		/**
		 * The container of the group edited has changed (its panning offset or
		 * zoom has changed)
		 * 
		 * @param event
		 *            the event
		 * @param container
		 *            the container
		 */
		public void containerUpdated(GroupEvent event, Group container) {
		}

		/**
		 * The root of the group editor changed
		 * 
		 * @param groupEvent
		 *            the event
		 * @param root
		 *            the new root
		 */
		public void rootChanged(GroupEvent groupEvent, Group root) {
		}

		/**
		 * /** The selection has been updated
		 * 
		 * @param groupEvent
		 *            the event
		 * @param selection
		 *            the elements selected
		 */
		public void selectionUpdated(GroupEvent groupEvent,
				Array<Actor> selection) {
		}

		/**
		 * The selection has been deleted
		 * 
		 * @param groupEvent
		 *            the event
		 * @param parent
		 *            the parent of the deleted actors
		 * @param deleted
		 *            the actors deleted
		 */
		public void deleted(GroupEvent groupEvent, Group parent,
				Array<Actor> deleted) {
		}

		/**
		 * The selection has been transformed
		 * 
		 * @param groupEvent
		 *            the event
		 * @param parent
		 *            the parent of the transformed actors
		 * @param transformed
		 *            the actors transformed
		 */
		public void transformed(GroupEvent groupEvent, Group parent,
				Array<Actor> transformed) {
		}

		/**
		 * A new group has been created
		 * 
		 * @param groupEvent
		 *            the event
		 * @param parent
		 *            the parent of the new group
		 * @param newGroup
		 *            the new group recently created
		 * @param grouped
		 *            all the actors contained by the new group
		 */
		public void grouped(GroupEvent groupEvent, Group parent,
				Group newGroup, Array<Actor> grouped) {
		}

		/**
		 * A group was ungroup
		 * 
		 * @param groupEvent
		 *            the event
		 * @param parent
		 *            the parent of the ungrouped
		 * @param oldGroup
		 *            the group ungrouped
		 * @param ungrouped
		 *            the actors born from the ungrouping
		 */
		public void ungrouped(GroupEvent groupEvent, Group parent,
				Group oldGroup, Array<Actor> ungrouped) {
		}

		/**
		 * A group edition was started
		 * 
		 * @param groupEvent
		 *            the event
		 * @param group
		 *            the group edited
		 */
		public void enteredGroupEdition(GroupEvent groupEvent, Group group) {

		}

		/**
		 * Edition in the group was ended
		 * 
		 * @param groupEvent
		 *            the event
		 * @param parent
		 *            the parent of the edited group
		 * @param oldGroup
		 *            the group edited, before exiting the edition. It could be
		 *            the same as simplifiedGroup, meaning that after exiting
		 *            its edition, the group still have more than one child.
		 *            Check
		 *            {@link es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorDragListener#simplifyGroup(Group)}
		 *            for more details
		 * @param simplifiedGroup
		 *            the group simplified, after exiting the edition. It could
		 *            be the same as oldGroup.
		 */
		public void exitedGroupEdition(GroupEvent groupEvent, Group parent,
				Group oldGroup, Actor simplifiedGroup) {

		}
	}
}
