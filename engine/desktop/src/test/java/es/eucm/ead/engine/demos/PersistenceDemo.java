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
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.engine.components.PersistentGameState;
import es.eucm.ead.schema.engine.components.PersistentVariable;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Created by jtorrente on 30/10/2015.
 */
public class PersistenceDemo extends ExecutableDemoBuilder {
	public static final String VAR_NAME = "selectedStar";
	public static final String VAR_NAME2 = VAR_NAME + "2";
	public static final int GAME_HEIGHT = 800;
	public static final int GAME_WIDTH = 1280;

	public PersistenceDemo() {
		super("persistence");
	}

	@Override
	public String getName() {
		return "Game State Persistence";
	}

	@Override
	public String getDescription() {
		return "Shows a scene with four stars. Each star sets two variables, "
				+ VAR_NAME
				+ " and "
				+ VAR_NAME2
				+ ", with a different value. However, only "
				+ VAR_NAME
				+ " is persistent, so when the application is restarted its value is preserved while the value of "
				+ VAR_NAME2 + " is lost";
	}

	@Override
	protected void doBuild() {
		game(GAME_WIDTH, GAME_HEIGHT);
		scene("mountains.jpg");
		doBuildScene();
	}

	protected void doBuildScene() {
		PersistentGameState pgs = new PersistentGameState();
		PersistentVariable persistentVariable = new PersistentVariable();
		persistentVariable.setVariable(VAR_NAME);
		persistentVariable.setInitValue("i0");
		pgs.getPersistentVariables().add(persistentVariable);
		getLastScene().getComponents().add(pgs);
		ModelEntity stars = entity(0, 0).initBehavior()
				.changeVar(VAR_NAME2, "i0", ChangeVar.Context.GLOBAL)
				.getLastEntity();
		entity(stars, "star1.png", 100, 100).touchBehavior()
				.changeVar(VAR_NAME, "i1", ChangeVar.Context.GLOBAL)
				.changeVar(VAR_NAME2, "i1", ChangeVar.Context.GLOBAL);
		entity(stars, "star2.png", 300, 100).touchBehavior()
				.changeVar(VAR_NAME, "i2", ChangeVar.Context.GLOBAL)
				.changeVar(VAR_NAME2, "i2", ChangeVar.Context.GLOBAL);
		entity(stars, "star3.png", 500, 100).touchBehavior()
				.changeVar(VAR_NAME, "i3", ChangeVar.Context.GLOBAL)
				.changeVar(VAR_NAME2, "i3", ChangeVar.Context.GLOBAL);
		entity(stars, "star4.png", 700, 100).touchBehavior()
				.changeVar(VAR_NAME, "i4", ChangeVar.Context.GLOBAL)
				.changeVar(VAR_NAME2, "i4", ChangeVar.Context.GLOBAL);

		Label label = new Label();
		label.setColor(makeColor(0F, 0F, 0F, 1F));
		label.setText(VAR_NAME + " (persistent): #$" + VAR_NAME + "#");

		Label label2 = new Label();
		label2.setColor(makeColor(0F, 0F, 0F, 1F));
		label2.setText(VAR_NAME2 + " (volatile): #$" + VAR_NAME2 + "#");

		entity(getLastScene(), 100, GAME_HEIGHT - 100).scale(0.4F)
				.getLastEntity().getComponents().add(label);
		entity(getLastScene(), 100, GAME_HEIGHT - 200).scale(0.4F)
				.getLastEntity().getComponents().add(label2);
	}
}
