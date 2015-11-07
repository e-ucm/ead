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
package es.eucm.ead.engine.systems.gamestatepersistence;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import es.eucm.ead.engine.components.PersistentGameStateComponent;
import es.eucm.ead.engine.systems.gamestatepersistence.SerializableGameState;
import es.eucm.ead.engine.systems.gamestatepersistence.SerializableVariable;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.engine.components.PersistentVariable;

import java.util.HashSet;

/**
 * Deals with all aspects of game state persistence. This includes: 1)
 * Processing the first entity loaded into the system that has a
 * {@link PersistentGameStateComponent}, which holds the settings for the game
 * in terms of state persistence. This system will process only one entity, as
 * {@link PersistentGameStateComponent} is expected to appear only in the main
 * game entity.
 * 
 * 2) The actual storage (see {@link #save()}) and retrieval (see
 * {@link #read()}) of the game state. These two methods are invoked when the
 * app is paused and created, respectively.
 * 
 * Created by jtorrente on 29/10/2015.
 */
public class PersistentGameStateSystem extends IteratingSystem {

	/**
	 * Name of the file in the app's internal (private) space that will store
	 * persistent variables
	 */
	public static final String SAVED_VARS_FILENAME = "saved_vars.json";
	public static final String LOG_TAG = "GAME STATE PERSISTENCE";

	private VariablesManager variablesManager;

	private HashSet<String> persistentVariables;

	private Json json; // Used to save persistent variables
	private PersistentGameStateComponent persistentGameStateComponent; // Stores
																		// the
																		// configuration
																		// of
																		// the
																		// system.

	public PersistentGameStateSystem(VariablesManager varManager) {
		super(Family.all(PersistentGameStateComponent.class).get());
		persistentVariables = new HashSet<String>();
		json = new Json();
		this.variablesManager = varManager;
		persistentGameStateComponent = null;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
		variablesManager = null;
		persistentVariables.clear();
		persistentGameStateComponent = null;
		json = null;
	}

	@Override
	protected void processEntity(Entity entity, float v) {
		persistentGameStateComponent = entity
				.getComponent(PersistentGameStateComponent.class);
		for (PersistentVariable pv : persistentGameStateComponent
				.getPersistentVariables()) {
			makeVariablePersistent(pv.getVariable(), pv.getInitValue());
		}
		setProcessing(false);
	}

	/**
	 * @return The FileHandle pointing to the local file (app's private storage
	 *         space on Android) that will store the value of persistent
	 *         variables.
	 */
	private FileHandle getFileForSavedState() {
		return Gdx.files.local(SAVED_VARS_FILENAME);
	}

	/**
	 * Annotates the global variable with name {@code varName} as a persistent
	 * variable. From this point onwards, the variable's state will be preserved
	 * across different executions of the application. If the variable has not
	 * been registered yet, it will be registered using the expression
	 * {@code initialValueExp} to determine its initial value. If the variable
	 * has already been registered, {@code initialValueExp} is ignored, and the
	 * variable is just marked as persistent.
	 * 
	 * @param varName
	 *            The name of the variable to make persistent
	 * @param initialValueExp
	 *            Expression to determine its initial value. Ignored if the
	 *            variable is already registered in the global context.
	 */
	private void makeVariablePersistent(String varName, String initialValueExp) {
		// Register if needed (the variable has not been read from disk)
		if (!variablesManager.isVariableDefinedAs(varName, true)) {
			// if (!globalContext.hasVariable(varName)) {
			variablesManager.setVarToExpression(varName, initialValueExp, true);
		}
		// Add to persistent vars
		persistentVariables.add(varName);
	}

	/**
	 * Saves persistent game state to disk in location determined by
	 * {@link #getFileForSavedState()} using json format.
	 */
	public void save() {
		// If persistent state settings have not been processed yet, do nothing
		// (system is disabled)
		if (persistentGameStateComponent == null) {
			return;
		}

		try {
			SerializableGameState gameState = new SerializableGameState();
			for (String varName : persistentVariables) {
				if (!variablesManager.isVariableDefinedAs(varName, true)
						|| variablesManager.getValue(varName) == null) {
					Gdx.app.debug(
							LOG_TAG,
							"Variable "
									+ varName
									+ " cannot be saved because it is not defined, it is not global, or it is null");
					continue;
				}
				gameState.addPersistentVariable(varName,
						variablesManager.getValue(varName));
			}
			json.toJson(gameState, null, getFileForSavedState());
		} catch (Exception e) {
			Gdx.app.error(LOG_TAG,
					"Persistent game state could not be saved to file "
							+ getFileForSavedState().file().getAbsolutePath(),
					e);
		}
	}

	/**
	 * Reads persistent game state from disk. This includes persistent
	 * variables, which are read and their value updated accordingly. If
	 * variables read from disk have not been yet initialized in global context,
	 * it is done so.
	 */
	public void read() {
		try {
			SerializableGameState gameState = json.fromJson(
					SerializableGameState.class, null, getFileForSavedState());
			for (String varNameRead : gameState.getPersistentVariables()
					.keySet()) {
				SerializableVariable varRead = gameState
						.getPersistentVariables().get(varNameRead);
				variablesManager.registerVar(varNameRead, varRead.getValue(),
						true);
				persistentVariables.add(varNameRead);
			}
		} catch (Exception e) {
			Gdx.app.error(LOG_TAG,
					"Persistent game state could not be read from file "
							+ getFileForSavedState().file().getAbsolutePath()
							+ ". File exists? "
							+ getFileForSavedState().exists());
		}
	}

	/**
	 * Deletes any data related to game state persistence from disk. USE WITH
	 * CAUTION!
	 */
	public void deletePersistentState() {
		getFileForSavedState().delete();
	}
}
