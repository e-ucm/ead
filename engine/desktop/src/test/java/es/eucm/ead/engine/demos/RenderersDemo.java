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

import es.eucm.ead.engine.demobuilder.ExecutableDemoBuilder;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Created by angel on 29/11/14.
 */
public class RenderersDemo extends ExecutableDemoBuilder {

	public RenderersDemo() {
		super("cooldemo");
	}

	@Override
	public String getName() {
		return "Renderers Demo";
	}

	@Override
	public String getDescription() {
		return "Demo with renderers";
	}

	@Override
	public boolean debug() {
		return true;
	}

	@Override
	protected void doBuild() {
		game(800, 600).scene("map.png");
		initVar("state", "bfalse");

		entity(getLastScene(), 100, 100).frames(0.1f, "bee.png", "bee_fly.png")
				.tags("rotate");

		ModelEntity chameleons = entity(getLastScene(), 0, 390)
				.color(1.0f, 1.0f, 1.0f, 0.75f).tags("rotate").origin(100, 100)
				.getLastEntity();
		entity(chameleons, "chameleon.png", 0, 0).color(1.0f, 0.25f, 0.25f,
				1.0f);
		entity(chameleons, "chameleon.png", 100, 0).color(0.25f, 1.0f, 1.0f,
				1.0f);
		entity(chameleons, "chameleon.png", 0, 100).color(1.0f, 1.f, 1.f, 0.5f);
		entity(chameleons, "chameleon.png", 100, 100).color(1.0f, 1.0f, 0.25f,
				1.0f);

		entity(getLastScene(), 600, 500).states()
				.state(makeFrames(0.1f, "p1_walk", ".png", 1, 7, 2), "walk")
				.state(createImage("p1_stand.png"), "stand")
				.tags("group", "states");

		libraryEntity("tree").image("../../../tree.png").tags("rotate")
				.origin(150, 150);

		entity(getLastScene(), 300, 0).reference("tree");

		entity(getLastScene(), 200, 50).circle(50).tags("rotate")
				.color(0.234f, 0.5674f, 0.2571f, 0.8f).origin(50, 50);

		ModelEntity group = entity(getLastScene(), 200, 400).rotation(15f)
				.getLastEntity();
		entity(group, 10, 10).reference("tree").scale(0.5f).rotation(45f);
		entity(group, -20, 30).image("chameleon.png").tags("rotate");

		entity(getLastScene(), 0, 0).emptyRectangle(800, 600).touchBehavior(
				makeAddComponent(makeEntitiesWithTagExp("rotate"),
						makeRotateTween(360.0f, 1.0f)));
		touchBehavior(makeChangeVar("state", "(not $state)"));
		touchBehavior(makeIfElse("$state",
				makeChangeState(makeEntitiesWithTagExp("states"), "stand"),
				makeChangeState(makeEntitiesWithTagExp("states"), "walk")));
	}

}
