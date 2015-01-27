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
package es.eucm.ead.engine.processors.templates;

import ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import es.eucm.ead.engine.ComponentLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.processors.templates.circuits.ChipFactory;
import es.eucm.ead.engine.processors.templates.circuits.CircuitComponent;
import es.eucm.ead.engine.processors.templates.circuits.TemplateRendererComponent;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.circuits.Chip;
import es.eucm.ead.schema.components.circuits.Circuit;
import es.eucm.ead.schema.components.circuits.Generator;
import es.eucm.ead.schema.components.circuits.Wire;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.renderers.TemplateRenderer;

public class TemplateRendererProcessor extends
		ComponentProcessor<TemplateRenderer> implements
		AssetLoadedCallback<Object> {

	private final VariablesManager variableManager;

	private ObjectMap<Class, ChipFactory> chipFactories;

	private GameAssets gameAssets;

	private ObjectMap<String, TemplateRendererComponent> pending = new ObjectMap<String, TemplateRendererComponent>();

	public TemplateRendererProcessor(GameLoop gameLoop, GameAssets gameAssets,
			VariablesManager variablesManager, ComponentLoader componentLoader) {
		super(gameLoop);
		this.gameAssets = gameAssets;
		this.variableManager = variablesManager;
		chipFactories = new ObjectMap<Class, ChipFactory>();
	}

	@Override
	public Component getComponent(TemplateRenderer template) {
		TemplateRendererComponent templateComponent = gameLoop
				.createComponent(TemplateRendererComponent.class);
		templateComponent.setVariablesManager(variableManager);
		templateComponent.setRendererOutput(template.getOutput());
		for (Parameter parameter : template.getInputs()) {
			templateComponent.setInputExpression(parameter.getName(),
					parameter.getValue());
		}

		pending.put(template.getUri(), templateComponent);

		gameAssets.get(template.getUri(), Object.class, this);
		return templateComponent;
	}

	@Override
	public void loaded(String fileName, Object asset) {
		Circuit circuit = (Circuit) asset;
		TemplateRendererComponent templateRendererComponent = pending
				.remove(fileName);

		CircuitComponent circuitComponent = new CircuitComponent();
		for (Chip chip : circuit.getChips()) {
			try {
				circuitComponent.putChip(chip.getName(),
						chipFactories.get(chip.getClass()).build(chip));
			} catch (Exception e) {
				Gdx.app.error("CircuitProcessor", "Error creating circuit", e);
			}
		}

		for (Generator generator : circuit.getGenerators()) {
			circuitComponent.putGenerator(generator.getName(),
					generator.getValue());
		}

		for (Wire wire : circuit.getWires()) {
			circuitComponent.addWire(wire.getOutput(), wire.getInputs());
		}
		templateRendererComponent.setCircuit(circuitComponent);
		templateRendererComponent.start();
	}
}
