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
package es.eucm.ead.engine.processors.controls.layouts;

import ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import es.eucm.ead.engine.ComponentLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.MultiComponent;
import es.eucm.ead.engine.components.controls.ControlComponent;
import es.eucm.ead.engine.components.controls.layouts.VerticalLayoutComponent;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.schema.components.controls.Control;
import es.eucm.ead.schema.components.controls.layouts.VerticalLayout;

public class VerticalLayoutProcessor extends ComponentProcessor<VerticalLayout> {

	private ComponentLoader componentLoader;

	public VerticalLayoutProcessor(GameLoop gameLoop,
			ComponentLoader componentLoader) {
		super(gameLoop);
		this.componentLoader = componentLoader;
	}

	@Override
	public Component getComponent(VerticalLayout component) {
		VerticalLayoutComponent verticalLayout = gameLoop
				.createComponent(VerticalLayoutComponent.class);
		for (Control control : component.getControls()) {
			addControls(verticalLayout,
					componentLoader.toEngineComponent(control));
		}

		VerticalGroup group = verticalLayout.getControl();
		switch (component.getAlign()) {
		case LEFT:
			group.left();
			break;
		case CENTER:
			group.center();
			break;
		case RIGHT:
			group.right();
			break;
		}
		group.pack();
		return verticalLayout;
	}

	private void addControls(VerticalLayoutComponent layout, Component component) {
		if (component instanceof MultiComponent) {
			for (Component c : ((MultiComponent) component).getComponents()) {
				addControls(layout, c);
			}
		} else if (component instanceof ControlComponent) {
			layout.addControl(((ControlComponent) component).getControl());
		}
	}
}
