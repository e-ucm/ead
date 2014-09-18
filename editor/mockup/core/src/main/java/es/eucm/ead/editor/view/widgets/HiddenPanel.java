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
package es.eucm.ead.editor.view.widgets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Panel is a generic lightweight container with {@link #show()} and
 * {@link #hide()} methods. Sizes and positions children using table constraint.
 */
public class HiddenPanel extends Table {

	private static final Vector2 temp = new Vector2();

	private static final InputListener handleHit = new InputListener() {

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			HiddenPanel listenerActor = (HiddenPanel) event.getListenerActor();

			Stage stage = listenerActor.getStage();
			if (stage == null) {
				return false;
			}

			listenerActor.defaultHit = true;
			Actor hit = listenerActor.getStage().hit(event.getStageX(),
					event.getStageY(), true);
			listenerActor.defaultHit = false;
			boolean resendTouch = hit == null
					|| !hit.isDescendantOf(listenerActor);
			if (resendTouch) {
				listenerActor.hide();
				stage.stageToScreenCoordinates(temp.set(event.getStageX(),
						event.getStageY()));
				stage.touchDown((int) temp.x, (int) temp.y, event.getPointer(),
						event.getButton());
			}
			return false;
		}
	};

	private boolean defaultHit;

	public HiddenPanel(Skin skin) {
		this(skin, (Drawable) null);
	}

	public HiddenPanel(Skin skin, String drawable) {
		this(skin, skin.getDrawable(drawable));
	}

	public HiddenPanel(Skin skin, Drawable drawableBackground) {
		super(skin);
		setBackground(drawableBackground);
		initialize(skin);
	}

	protected void initialize(Skin skin) {
		this.defaultHit = false;
		addCaptureListener(handleHit);
	}

	public void show(Stage stage) {
		show(stage, null);
	}

	public void show(Stage stage, Action action) {
		defaultHit = false;
		stage.addActor(this);
		setTouchable(Touchable.enabled);
		clearActions();
		if (action != null) {
			addAction(action);
		}
		validate();
	}

	public void hide() {
		hide(null);
	}

	public void hide(Action action) {
		defaultHit = true;
		setTouchable(Touchable.disabled);

		if (action != null) {
			addAction(sequence(action, Actions.removeActor()));
		} else {
			addAction(Actions.removeActor());
		}
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		if (!defaultHit) {
			final Actor hit = super.hit(x, y, touchable);
			if (hit == null && isTouchable()) {
				return this;
			}
			return hit;
		} else {
			return super.hit(x, y, touchable);
		}
	}
}