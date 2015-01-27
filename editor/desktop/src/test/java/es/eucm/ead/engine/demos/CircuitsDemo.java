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
package es.eucm.ead.engine.demos;

import es.eucm.ead.editor.demobuilder.EditorDemoBuilder;
import es.eucm.ead.editor.utils.CircuitBuilder;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.schema.components.circuits.Circuit;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.TemplateRenderer;

public class CircuitsDemo extends EditorDemoBuilder {

	public CircuitsDemo() {
		super("shaders");
	}

	@Override
	public String getName() {
		return "Circuits";
	}

	@Override
	public String getDescription() {
		return "Circuits in action.";
	}

	@Override
	protected void doBuild() {
		ModelEntity scene = singleSceneGame(null, 800, 600).getLastScene();
		initVar("x", "f100");
		initVar("time", "f6.0");

		CircuitBuilder builder = new CircuitBuilder();
		Circuit circuit = builder.newCircuit()
				.inputs("width", "height", "time").outputs("sky").entity("bg")
				.rectangle("bgRectangle")
				.shader("skycolors", "daycicle.fragment")
				.wire("width", "bgRectangle.width")
				.wire("height", "bgRectangle.height")
				.wire("bgRectangle.renderer", "bg.renderer")
				.wire("time", "skycolors.time").wire("bg.entity", "sky")
				.wire("skycolors.shader", "bg.shader").build();

		gameAssets.toJson(circuit, null,
				gameAssets.resolve("circuits/sky.json"));

		TemplateRenderer template = new TemplateRenderer();
		template.setUri("circuits/sky.json");

		template.getInputs().add(
				param("width", "$" + VarsContext.RESERVED_VIEWPORT_WIDTH_VAR));
		template.getInputs()
				.add(param("height", "$"
						+ VarsContext.RESERVED_VIEWPORT_HEIGHT_VAR));
		template.getInputs().add(param("time", "$time"));
		template.setOutput("sky");

		infiniteTimer(scene, 0.1f, makeChangeVar("time", "( + $time f0.005)"));

		scene.getComponents().add(template);
	}
}
