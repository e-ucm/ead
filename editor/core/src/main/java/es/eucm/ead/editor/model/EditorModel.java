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
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Contains a full model of what is being edited. This is a super-set of an
 * AdventureGame, encompassing both engine-related model objects and resources,
 * assets, and strings. Everything is searchable, and dependencies are tracked
 * as objects are changed.
 * 
 * @author mfreire
 */
public class EditorModel {

	public static final String gameId = "game.json";
	public static final String sceneIdPrefix = "scenes/";
	public static final String actorIdPrefix = "actors/";

	/**
	 * Quick reference for node retrieval, Ids are filesystem paths.
	 */
	private final TreeMap<String, DependencyNode> nodesById = new TreeMap<String, DependencyNode>();

	/**
	 * Listeners for model changes
	 */
	private final ArrayList<ModelListener> modelListeners = new ArrayList<ModelListener>();

	/**
	 * Constructor.
	 */
	public EditorModel() {
	}

	// ----- nodes
	public DependencyNode getNode(String id) {
		return nodesById.get(id);
	}

	/**
	 * Registers a dependencyNode, launching the appropriate event to inform
	 * listeners. The node must not depend upon others (or have all dependencies
	 * already wired-in). This is ideal for editor-windows that are not actually
	 * tied into the current model.
	 * 
	 * @param n
	 *            the node
	 */
	public void registerNode(DependencyNode n) {
		nodesById.put(n.getId(), n);
		fireModelEvent(new ModelEvent(this, new DependencyNode[] { n }, null));
	}

	/**
	 * Flushes the model.
	 */
	public void clear() {
		nodesById.clear();
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
	 * described must already have been performed. Intended to be called by
	 * Commands or similar change-encapsulating constructs.
	 * 
	 * @param event
	 *            describing the changes.
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
	public interface ModelListener {
		/**
		 * Called whenever parts of the model change.
		 * 
		 * @param event
		 *            describing the change
		 */
		void modelChanged(ModelEvent event);
	}
}
