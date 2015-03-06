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
package es.eucm.ead.editor.view.builders.scene.groupeditor;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.engine.gdx.AbstractWidget;

public class TouchRepresentation extends AbstractWidget implements
		EventListener {

	private Image touch1;

	private Image touch2;

	private InputListener inputListener = new InputListener() {

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if (pointer < 2) {
				Image touch = pointer == 0 ? touch1 : touch2;
				touch.clearActions();
				touch.setScale(0, 0);
				touch.setVisible(true);
				touch.addAction(Actions.sequence(Actions.alpha(1.0f), Actions
						.scaleTo(1.0f, 1.0f, 0.25f, Interpolation.exp5Out)));
				touch.setPosition(x - touch.getWidth() / 2.0f,
						y - touch.getHeight() / 2.0f);
			}
			return pointer < 2;
		}

		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			if (pointer < 2) {
				Image touch = pointer == 0 ? touch1 : touch2;
				touch.setPosition(x - touch.getWidth() / 2.0f,
						y - touch.getHeight() / 2.0f);
			}
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			if (pointer < 2) {
				Image touch = pointer == 0 ? touch1 : touch2;
				touch.addAction(Actions.sequence(
						Actions.alpha(0, 0.5f, Interpolation.exp5Out),
						Actions.hide()));
			}
		}
	};

	public TouchRepresentation(Drawable touch) {
		setTouchable(Touchable.disabled);
		addActor(touch1 = new Image(touch));
		addActor(touch2 = new Image(touch));
		touch1.setVisible(false);
		touch1.setTouchable(Touchable.disabled);
		touch1.pack();
		touch1.setOrigin(touch1.getWidth() / 2.0f, touch1.getHeight() / 2.0f);
		touch2.setVisible(false);
		touch2.setTouchable(Touchable.disabled);
		touch2.pack();
		touch2.setOrigin(touch2.getWidth() / 2.0f, touch2.getHeight() / 2.0f);
	}

	@Override
	public boolean handle(Event event) {
		return inputListener.handle(event);
	}
}
