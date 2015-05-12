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
import es.eucm.ead.schema.renderers.SpineAnimation;

/**
 * Created by angel on 29/11/14.
 */
public class SpineDemo extends ExecutableDemoBuilder {

	public SpineDemo() {
		super("spine");
	}

	@Override
	public String getDescription() {
		return "Simple demo that demonstrates how spine animations work.\nMore details can be found at:\nhttp://es.esotericsoftware.com/spine-using-runtimes";
	}

	@Override
	protected void doBuild() {
		ModelEntity scene = singleSceneGame(null, 1200, 800).getLastScene();
		scene.getChildren().add(spineEntity("spineboy", "idle", 1.0F, 150, 0));
		scene.getChildren().add(
				spineEntity("r5p2/skeleton", "stand", 0.3F, 300, 200));
		scene.getChildren().add(
				spineEntity("r1v1/skeleton", "default", 0.35F, 530, 0));
		scene.getChildren().add(
				spineEntity("whiterobot/skeleton", "default", 0.5F, 770, 0));
		scene.getChildren().add(
				spineEntity("c6b4/skeleton", "default", 0.5F, 1000, 200));
	}

	protected ModelEntity spineEntity(String uri, String initialState,
			float scale, float x, float y) {
		ModelEntity animation = new ModelEntity();
		SpineAnimation spineAnimation = new SpineAnimation();
		spineAnimation.setUri(uri);
		spineAnimation.setInitialState(initialState);
		animation.setX(x);
		animation.setScaleX(scale);
		animation.setScaleY(scale);
		animation.getComponents().add(spineAnimation);
		return animation;
	}
}
