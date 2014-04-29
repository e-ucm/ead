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
package es.eucm.ead.editor.view.widgets.scenetree;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import es.eucm.ead.editor.view.widgets.scenetree.SceneTree.Node;

/**
 * Base class to listen to scene tree changes
 */
public abstract class SceneTreeListener implements EventListener {

	@Override
	public boolean handle(Event event) {
		if (event instanceof SceneTreeEvent) {
			SceneTreeEvent sceneTreeEvent = (SceneTreeEvent) event;
			Node node = sceneTreeEvent.getNode();
			switch (sceneTreeEvent.getType()) {
			case ADDED:
				return nodeAdded(sceneTreeEvent, node);
			case UPDATED:
				return nodeUpdated(sceneTreeEvent, node);
			case REMOVED:
				return nodeRemoved(sceneTreeEvent, node);
			}
		}
		return false;
	}

	/**
	 * A node was added
	 * 
	 * @param event
	 *            the original event
	 * @param node
	 *            the node added
	 * @return if the event was handled
	 */
	public boolean nodeAdded(SceneTreeEvent event, Node node) {
		return false;
	}

	/**
	 * A node was removed
	 * 
	 * @param event
	 *            the original event
	 * @param node
	 *            the node removed
	 * @return if the event was handled
	 */
	public boolean nodeRemoved(SceneTreeEvent event, Node node) {
		return false;
	}

	/**
	 * Something in the node changed (e.g., its label)
	 * 
	 * @param event
	 *            the original event
	 * @param node
	 *            the node updated
	 * @return if the event was handled
	 */
	public boolean nodeUpdated(SceneTreeEvent event, Node node) {
		return false;
	}

	/**
	 * Represents events in a scene tree
	 */
	public static class SceneTreeEvent extends Event {

		public enum Type {
			REMOVED, UPDATED, ADDED
		}

		private Node node;

		private Type type;

		/**
		 * @return node involved in the event
		 */
		public Node getNode() {
			return node;
		}

		/**
		 * @return type of the event
		 */
		public Type getType() {
			return type;
		}

		/**
		 * @param node
		 *            node involved in the event
		 */
		public void setNode(Node node) {
			this.node = node;
		}

		/**
		 * @param type
		 *            type of the event
		 */
		public void setType(Type type) {
			this.type = type;
		}
	}
}
