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

import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.ComponentLoader;
import es.eucm.ead.engine.components.renderers.shape.RectangleRendererComponent;
import es.eucm.ead.schema.components.circuits.chips.RectangleChip;
import es.eucm.ead.schema.data.shape.Rectangle;
import es.eucm.ead.schema.renderers.ShapeRenderer;

public class RectangleChipFactory implements ChipFactory<RectangleChip> {

	private static final ShapeRenderer RECTANGLE_SHAPE;

	static {
		RECTANGLE_SHAPE = new ShapeRenderer();
		Rectangle r = new Rectangle();
		r.setHeight(1);
		r.setWidth(1);
		RECTANGLE_SHAPE.setShape(r);
	}

	private ComponentLoader componentLoader;

	public RectangleChipFactory(ComponentLoader componentLoader) {
		this.componentLoader = componentLoader;
	}

	@Override
	public ChipComponent build(RectangleChip chip) {
		return new RectangleChipComponent(
				componentLoader
						.<RectangleRendererComponent> toEngineComponent(RECTANGLE_SHAPE));
	}

	public static class RectangleChipComponent extends ChipComponent {

		private RectangleRendererComponent rectangle;

		public RectangleChipComponent(RectangleRendererComponent rectangle) {
			this.rectangle = rectangle;
		}

		@Override
		protected void calculateOutputs() {
			rectangle
					.setWidth(this.<Number> getInput("width", 0f).floatValue());
			rectangle.setHeight(this.<Number> getInput("height", 0f)
					.floatValue());
			setOutput("renderer", rectangle);
		}

		@Override
		public void reset() {
			Pools.free(rectangle);
			rectangle = null;
		}
	}
}
