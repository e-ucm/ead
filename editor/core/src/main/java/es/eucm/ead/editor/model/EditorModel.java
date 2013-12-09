/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.model;

import com.badlogic.gdx.Gdx;
import es.eucm.ead.schema.game.Game;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Contains a full model of what is being edited. This is a super-set of an
 * AdventureGame, encompassing both engine-related model objects and
 * resources, assets, and strings. Everything is searchable, and dependencies
 * are tracked as objects are changed.
 *
 * @author mfreire
 */
public class EditorModel {

	private static int nextId = 1;

	/**
	 * Quick reference for node retrieval
	 */
	private final TreeMap<Integer, DependencyNode> nodesById = new TreeMap<Integer, DependencyNode>();

	/**
	 * The root of the graph; contains the Game
	 */
	private DependencyNode root;
	/**
	 * Internationalized strings
	 */

	/**
	 * Listeners for model changes
	 */
	private final ArrayList<ModelListener> modelListeners = new ArrayList<ModelListener>();

	/**
	 * Constructor. Does not do much beyond initializing fields.
	 * @param game the game to use as root
	 */
	public EditorModel(Game game) {
		root = new DependencyNode(nextId++, game);
		nodesById.put(root.getId(), root);
	}

	// ----- nodes
	public DependencyNode getNode(int id) {
		return nodesById.get(id);
	}

	/**
	 * Registers a dependencyNode, launching the appropriate event to inform
	 * listeners. The node must not depend upon others (or have all dependencies
	 * already wired-in). This is ideal for editor-windows that are not actually
	 * tied into the current model.
	 * 
	 * @param n the node
	 * @param eventType the event-type
	 */
	public void registerNode(DependencyNode n, String eventType) {
		nodesById.put(n.getId(), n);
		fireModelEvent(new DefaultModelEvent(eventType, this,
				new DependencyNode[] { n }, null));
	}

	/**
	 * Registers a dependencyNode, launching the appropriate event to inform
	 * listeners. The node must not depend upon others (or have all dependencies
	 * already wired-in). This is ideal for editor-windows that are not actually
	 * tied into the current model.
	 * 
	 * @param o the game-object to wrap
	 * @param eventType the event-type
	 */
	public void registerNode(Object o, String eventType) {
		DependencyNode n = new DependencyNode(nextId++, o);
		nodesById.put(n.getId(), n);
		fireModelEvent(new DefaultModelEvent(eventType, this,
				new DependencyNode[] { n }, null));
	}

	/**
	 * Flushes the model.
	 */
	public void clear() {
		nextId = 1;
		nodesById.clear();
		root = null;
	}

	public DependencyNode getRoot() {
		return root;
	}

	public void setRoot(DependencyNode root) {
		this.root = root;
	}

	// -------- model changes (nodes added, changed, removed)

	public void addModelListener(ModelListener modelListener) {
		Gdx.app.log("EditorModel", "--> [+] registered new ModelListener "
				+ modelListener);
		modelListeners.add(modelListener);
	}

	public void removeModelListener(ModelListener modelListener) {
		Gdx.app.log("EditorModel", "--> [-] removed ModelListener "
				+ modelListener);
		modelListeners.remove(modelListener);
	}

	/**
	 * Delivers the modelEvent to all registered listeners. Any changes
	 * described must already have been performed. Intended to be called
	 * by Commands or similar change-encapsulating constructs.
	 * @param event describing the changes.
	 */
	public void fireModelEvent(ModelEvent event) {
		Gdx.app.log("EditorModel", modelListeners.size()
				+ " listeners for model-event: " + event);
		for (ModelListener l : modelListeners) {
			Gdx.app.debug("EditorModel", "--> now delivering to " + l);
			l.modelChanged(event);
		}
	}

	/**
	 * A very simple interface for progress updates
	 */
	public static interface ModelListener {
		/**
		 * Called whenever parts of the model change.
		 * @param event describing the change
		 */
		public void modelChanged(ModelEvent event);
	}
}
