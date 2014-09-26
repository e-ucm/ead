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
package es.eucm.ead.editor.view.widgets.groupeditor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class GesturesListener extends InputListener {

	private static final float MIN_SCALE_DIFF = 100;

	private static final float MAX_ROTATION_MARGIN = 20;

	private static final float ROTATION_MULTIPLIER = 2f;

	private static final float MIN_ROTATION_MARGIN = 10;

	private GroupEditorDragListener dragListener;

	private Handles handles;

	private Vector2 finger1 = new Vector2();
	private Vector2 finger2 = new Vector2();

	private int fingers = 0;
	private boolean cancel = false;
	private Vector2 startingVector = new Vector2();

	private Vector2 currentVector = new Vector2();

	private Actor influencedActor;

	private float startingRotation;

	private Vector2 startingSize = new Vector2();

	private Vector2 size = new Vector2();

	public GesturesListener(GroupEditorDragListener dragListener) {
		this.dragListener = dragListener;
		handles = dragListener.getModifier().getHandles();
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		if (pointer < 2 && fingers < 2) {
			setFinger(x, y, pointer);
			fingers++;
			if (fingers == 2) {
				cancel = true;
				startingVector.set(finger1).sub(finger2);
				influencedActor = handles.getInfluencedActor();
				if (influencedActor != null) {
					startingRotation = influencedActor.getRotation();
					size.set(influencedActor.getWidth(),
							influencedActor.getHeight());
					startingSize.set(influencedActor.getScaleX() * size.x,
							influencedActor.getScaleY() * size.y);
				}
			}
		}
		return pointer < 2;
	}

	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		setFinger(x, y, pointer);
		if (pointer < 2 && fingers == 2 && influencedActor != null) {
			event.cancel();
			currentVector.set(finger1).sub(finger2);
			float diffAngle = startingVector.angle(currentVector)
					* ROTATION_MULTIPLIER;
			float diffModule = currentVector.len() - startingVector.len();

			influencedActor.setRotation(round(diffModule, startingRotation
					+ diffAngle));
			float sizeX = startingSize.x + diffModule;
			float sizeY = startingSize.y + diffModule;
			influencedActor.setScale(sizeX / size.x, sizeY / size.y);

			handles.readActorTransformation();
		}
	}

	private float round(float diffModule, float angle) {
		int rest = Math.round(angle + 360) % 90;
		return rest < (diffModule < MIN_SCALE_DIFF ? MIN_ROTATION_MARGIN
				: MAX_ROTATION_MARGIN) ? Math.round(angle - rest) : angle;
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,
			int button) {
		if (pointer < 2 && fingers > 0) {
			fingers--;
			if (cancel) {
				event.cancel();
				if (fingers == 0) {
					dragListener.fireTransformed();
					cancel = false;
				}
			}
		}
	}

	private void setFinger(float x, float y, int pointer) {
		switch (pointer) {
		case 0:
			finger1.set(x, y);
			break;
		case 1:
			finger2.set(x, y);
			break;
		}
	}
}
