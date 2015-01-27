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
package es.eucm.ead.engine.processors.templates.circuits;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool.Poolable;

import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.entities.actors.RendererActor;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VariablesManager.VariableListener;

public class TemplateRendererComponent extends RendererComponent implements
		Poolable, VariableListener {

	private RendererActor rendererActor;

	private VariablesManager variablesManager;

	private Array<String> variables = new Array<String>();

	private ObjectMap<String, String> inputsExpressions = new ObjectMap<String, String>();

	private CircuitComponent circuit;

	private String rendererOutput;

	public void setRendererOutput(String rendererOutput) {
		this.rendererOutput = rendererOutput;
	}

	public void setCircuit(CircuitComponent circuit) {
		this.circuit = circuit;
	}

	public void setVariablesManager(VariablesManager variablesManager) {
		this.variablesManager = variablesManager;
		variablesManager.addListener(this);
	}

	public void setInputExpression(String input, String expression) {
		inputsExpressions.put(input, expression);
		variablesManager.readVariables(expression, variables);
	}

	/**
	 * Reads the circuit output and sets up the renderer
	 */
	public void start() {
		for (Entry<String, String> entry : inputsExpressions.entries()) {
			circuit.setInput(entry.key,
					variablesManager.evaluateExpression(entry.value));
		}
		rendererActor = circuit.getOutput(rendererOutput);
	}

	@Override
	public void draw(Batch batch) {
		if (rendererActor != null) {
			rendererActor.draw(batch, 1.0f);
		}
	}

	@Override
	public float getWidth() {
		return rendererActor == null ? 0 : rendererActor.getWidth();
	}

	@Override
	public float getHeight() {
		return rendererActor == null ? 0 : rendererActor.getHeight();
	}

	@Override
	public Array<Polygon> getCollider() {
		return null;
	}

	@Override
	public void reset() {
		variablesManager.removeListener(this);
		variablesManager = null;
		variables.clear();
		inputsExpressions.clear();
	}

	@Override
	public boolean listensTo(String variableName) {
		return variables.contains(variableName, false);
	}

	@Override
	public void variableChanged(String variableName, Object value) {
		for (Entry<String, String> entry : inputsExpressions.entries()) {
			if (entry.value.contains(variableName)) {
				circuit.setInput(entry.key,
						variablesManager.evaluateExpression(entry.value));
			}
		}
	}
}
