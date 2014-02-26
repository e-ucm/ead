/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.actors.hud;

import com.badlogic.gdx.scenes.scene2d.Actor;

import es.eucm.ead.engine.actors.ActorEngineObject;
import es.eucm.ead.schema.actors.hud.Hud;
import es.eucm.ead.schema.actors.hud.HudElement;

public class HudEngineObject extends ActorEngineObject<Hud> {

	@Override
	public void initialize(Hud schemaObject) {
		for (HudElement hudElement : schemaObject.getChildren()) {
			Actor a = (Actor) gameLoop.getAssets().getEngineObject(
					hudElement.getSceneElement());
			addActor(a);
		}
	}

	@Override
	public void layout() {
		for (int i = 0; i < element.getChildren().size(); i++) {
			Actor actor = getChildren().get(i);
			HudElement hudElement = element.getChildren().get(i);
			float x = 0f;
			float y = 0f;
			if (hudElement.getHorizontalAlign() != null) {
				switch (hudElement.getHorizontalAlign()) {
				case LEFT:
					x = 0f;
					break;
				case CENTER:
					x = (getWidth() - actor.getWidth()) / 2;
					break;
				case RIGHT:
					x = getWidth() - actor.getWidth();
					break;
				}
			}

			if (hudElement.getVerticalAlign() != null) {
				switch (hudElement.getVerticalAlign()) {
				case TOP:
					y = getHeight() - actor.getHeight();
					break;
				case MIDDLE:
					y = (getHeight() - actor.getHeight()) / 2.0f;
					break;
				case BOTTOM:
					y = 0f;
					break;
				}
			}

			actor.setPosition(x + actor.getOriginX(), y + actor.getOriginY());
		}
	}
}
