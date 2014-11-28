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
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor.GroupEvent.Type;
import es.eucm.ead.editor.view.builders.scene.groupeditor.input.EditStateMachine;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

public class GroupEditor extends AbstractWidget {

	public static final float NEAR_CM = 1.0f;

	private GroupEditorStyle style;

	private boolean multipleSelection;

	private Group rootGroup;

	private SelectionGroup selectionGroup;

	protected Array<Actor> layersTouched;

	protected LayerSelector layerSelector;

	private boolean onlySelection;

	public GroupEditor(Skin skin) {
		this(skin.get(GroupEditorStyle.class));
	}

	public GroupEditor(GroupEditorStyle style) {
		this.style = style;
		layersTouched = new Array<Actor>();
		layerSelector = new LayerSelector(this, style);

		selectionGroup = new SelectionGroup(this, style);
		addActor(selectionGroup);

		TouchRepresentation touchRepresentation = new TouchRepresentation(
				style.touch);
		addActor(touchRepresentation);

		addListener(new EditStateMachine(this, selectionGroup));
		addListener(touchRepresentation);
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

	/**
	 * Sets the group being edited
	 */
	public void setRootGroup(Group rootGroup) {
		if (this.rootGroup != null) {
			this.rootGroup.remove();
		}
		this.rootGroup = rootGroup;
		if (rootGroup != null) {
			addActorAt(0, rootGroup);
		}
	}

	public Array<Actor> getSelection() {
		return selectionGroup.getSelection();
	}

	public void setSelection(Iterable<Actor> selection) {
		selectionGroup.clearChildren();
		for (Actor actor : selection) {
			addToSelection(actor);
		}
	}

	void addToSelection(Actor actor) {
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

	float[] points = new float[8];

	public void selectLayer(float x, float y) {
		layersTouched.clear();
		Vector2 tmp = Pools.obtain(Vector2.class);
		Polygon polygon = Pools.obtain(Polygon.class);

		for (Actor actor : rootGroup.getChildren()) {

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
		}
	}

	protected void showLayerSelector(float x, float y) {
		layerSelector.prepare(layersTouched);
		layerSelector.setPosition(x, y);
		addActor(layerSelector);
		layerSelector.show();
	}

	private boolean nearEnough(float x1, float y1, float x2, float y2) {
		return Math.abs(x1 - x2) < cmToXPixels(NEAR_CM)
				&& Math.abs(y1 - y2) < cmToYPixels(NEAR_CM);
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

	public static class GroupEditorStyle {

		public Drawable selectedBackground;

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
