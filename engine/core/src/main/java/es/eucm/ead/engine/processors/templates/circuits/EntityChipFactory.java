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

import es.eucm.ead.engine.components.ShaderComponent;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.entities.actors.RendererActor;
import es.eucm.ead.schema.components.circuits.chips.EntityChip;

public class EntityChipFactory implements ChipFactory<EntityChip> {

	@Override
	public ChipComponent build(EntityChip chip) {
		return new EntityChipComponent();
	}

	public static class EntityChipComponent extends ChipComponent {

		private RendererActor rendererActor = new RendererActor();

		@Override
		protected void calculateOutputs() {
			rendererActor.setShader(this.<ShaderComponent> getInput("shader",
					null));
			rendererActor.setRenderer(this.<RendererComponent> getInput(
					"renderer", null));
			rendererActor.setPosition(getInput("x", 0f), getInput("y", 0f));
			rendererActor.setScale(getInput("scaleX", 1f),
					getInput("scaleY", 1f));
			rendererActor.setRotation(getInput("rotation", 0f));
			rendererActor.setOrigin(getInput("originX", 0f),
					getInput("originY", 0f));
			setOutput("entity", rendererActor);
		}

		@Override
		public void reset() {
		}
	}
}
