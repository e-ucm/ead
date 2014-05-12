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
package es.eucm.ead.engine.systems;

import ashley.core.Entity;
import ashley.core.Family;
import ashley.systems.IteratingSystem;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.systems.behaviors.TimersSystem;
import es.eucm.ead.engine.systems.behaviors.TouchSystem;
import es.eucm.ead.engine.systems.variables.VariablesManager;

/**
 * Convenient system that provides functionality for evaluating conditions. Any
 * system needing to (1) iterate through entities and (2) check if one or more
 * conditions are met would profit from extending this class.
 * 
 * Known subclasses: {@link VisibilitySystem}, {@link EffectsSystem},
 * {@link TimersSystem}, {@link TouchSystem}.
 * 
 * Created by Javier Torrente on 19/04/14.
 */
public abstract class ConditionalSystem extends IteratingSystem {

	// Engine for creating new components
	protected GameLoop engine;

	// For evaluating expressions
	protected VariablesManager variablesManager;

	public ConditionalSystem(GameLoop engine, VariablesManager variablesManager,
			Family family) {
		super(family);
		this.engine = engine;
		this.variablesManager = variablesManager;
	}

	/**
	 * Evaluates the given condition using the underlying
	 * {@link es.eucm.ead.engine.systems.variables.VariablesManager}. If for whatever reason this system triggers an
	 * exception, or if the {@code expression} provided for evaluation is
	 * {@code null}, it returns {@link #getDefaultValueForCondition()}.
	 * 
	 * @param expression
	 *            The condition to be evaluated
	 * @return The results of evaluating {@code expression} or
	 *         {@link #getDefaultValueForCondition()} if it is {@code null} or
	 *         if {@link es.eucm.ead.engine.systems.variables.VariablesManager} throws an exception.
	 */
	protected boolean evaluateCondition(String expression) {
		try {
			boolean conditionResult = variablesManager.evaluateCondition(
					expression, getDefaultValueForCondition());
			return conditionResult;
		} catch (IllegalArgumentException e) {
			return getDefaultValueForCondition();
		}
	}

	/**
	 * Default value for expressions that are undefined or which cannot be
	 * evaluated (e.g. due to syntax errors or because the VariablesManager was
	 * not registered). {@code true} by default. Subclasses may want to redefine
	 * this method to return {@code false} if they want to avoid the execution
	 * of code that depends on expressions when these are not present, for
	 * example.
	 */
	protected boolean getDefaultValueForCondition() {
		return true;
	}

	@Override
	// This method is overriden because its convenient to setup a local context
	// in VarsSystem with the entity owner.
	// Subclasses should implement the doProcessEntity() method instead
	public void processEntity(Entity entity, float deltaTime) {
		variablesManager.push().localOwnerVar(entity);
		doProcessEntity(entity, deltaTime);
		variablesManager.pop();
	}

	/**
	 * Does the actual entity processing. See
	 * {@link IteratingSystem#processEntity(Entity, float)} for more details
	 */
	public abstract void doProcessEntity(Entity entity, float deltaTime);
}
