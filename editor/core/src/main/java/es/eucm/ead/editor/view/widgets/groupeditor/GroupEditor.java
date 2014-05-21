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
package es.eucm.ead.editor.view.widgets.groupeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * A widget where all children are movable, rotatable and scalable and allows
 * group/ungroup. Default shortcuts:
 * <ul>
 * <li>SPACE: Panning</li>
 * <li>DEL: Deletes current selection</li>
 * <li>Ctrl + G: Groups selection</li>
 * <li>Alt + G: Ungroups selection</li>
 * <li>+/-: Zoom in, zoom out</li>
 * <li>1: fits the current scene in the widget</li>
 * </ul>
 */
public class GroupEditor extends AbstractWidget {

	private static final Color SELECTION_COLOR = new Color(0.8f, 0.8f, 0.8f,
			0.5f);

	private ShapeRenderer shapeRenderer;

	private Drawable background;

	private GroupEditorDragListener groupEditorDragListener;

	private Rectangle selection = new Rectangle();

	public GroupEditor(ShapeRenderer shapeRenderer) {
		this.shapeRenderer = shapeRenderer;
		setRequestKeyboardFocus(true);
		addListener(groupEditorDragListener = new GroupEditorDragListener(this,
				shapeRenderer));
	}

	/**
	 * Sets the root group that is going be edited. All its children will be
	 * movable, rotatable and scalable
	 */
	public void setRootGroup(Group group) {
		clearChildren();
		addActor(group);
		groupEditorDragListener.setRootGroup(group);
		// The group has been automatically adjusted. Model entities must update
		// their positions
		readPositions(group);
	}

	private void readPositions(Actor actor) {
		ModelEntity entity = Model.getModelEntity(actor);
		if (entity != null) {
			entity.setX(actor.getX());
			entity.setY(actor.getY());

			if (actor instanceof Group) {
				for (Actor child : ((Group) actor).getChildren()) {
					readPositions(child);
				}
			}
		}
	}

	public void setPanning(boolean panning) {
		groupEditorDragListener.setPanningMode(panning);
	}

	public void zoomIn() {
		groupEditorDragListener
				.scale(1.f / GroupEditorDragListener.SCALE_FACTOR);
	}

	public void zoomOut() {
		groupEditorDragListener.scale(GroupEditorDragListener.SCALE_FACTOR);
	}

	public void fit() {
		groupEditorDragListener.fit();
	}

	public void adjustGroup(Group group) {
		groupEditorDragListener.adjustGroup(group);
	}

	/**
	 * Sets the background for the widget
	 */
	public void setBackground(Drawable background) {
		this.background = background;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		if (background != null) {
			background.draw(batch, 0, 0, getWidth(), getHeight());
		}
		super.drawChildren(batch, parentAlpha);
		if (selection.getWidth() != 0 && selection.getHeight() != 0) {
			drawSelectionRectangle(batch);
		}
	}

	private void drawSelectionRectangle(Batch batch) {
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(SELECTION_COLOR);
		shapeRenderer.rect(selection.x, selection.y, selection.width,
				selection.height);
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}

	/**
	 * Re-reads selection and refresh the selection. Must be call whenever the
	 * selection is modified externally.
	 */
	public void refresh() {
		groupEditorDragListener.refresh();
	}

	/**
	 * Sets the origin of the selection
	 */
	public void setSelectionStart(float x, float y) {
		selection.setPosition(x, y);
	}

	/**
	 * Sets the end point of the selection
	 */
	public void setSelectionEnd(float x, float y) {
		selection.setSize(x - selection.x, y - selection.y);
	}

	/**
	 * Resets the selection rectangle
	 */
	public void endSelection() {
		selection.set(0, 0, 0, 0);
	}

	/**
	 * @return the current selection rectangle
	 */
	public Rectangle getSelection() {
		return selection;
	}

	/**
	 * When a group is created inside the widget, this method is invoked, and
	 * the new group returned will be used as the grouping root for the selected
	 * elements. Can be overridden for those who need a specific implementation
	 * of group.
	 * 
	 * @return a group to be the root of created groups
	 */
	public Group newGroup() {
		return new Group();
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
				case selected:
					selectionUpdated(groupEvent, groupEvent.getSelection());
					break;
				case deleted:
					deleted(groupEvent, groupEvent.getParent(),
							groupEvent.getSelection());
					break;
				case transformed:
					transformed(groupEvent, groupEvent.getSelection());
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
		 * The selection has been updated
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
		 * @param transformed
		 *            the actors transformed
		 */
		public void transformed(GroupEvent groupEvent, Array<Actor> transformed) {
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
		 *            Check {@link GroupEditorDragListener#simplifyGroup(Group)}
		 *            for more details
		 * @param simplifiedGroup
		 *            the group simplified, after exiting the edition. It could
		 *            be the same as oldGroup.
		 */
		public void exitedGroupEdition(GroupEvent groupEvent, Group parent,
				Group oldGroup, Actor simplifiedGroup) {

		}
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
			selected, deleted, transformed, grouped, ungrouped, enteredEdition, exitedEdition
		}
	}

}
