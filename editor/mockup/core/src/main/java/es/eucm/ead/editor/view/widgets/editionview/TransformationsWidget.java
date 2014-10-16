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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.scene.ReorderSelection;
import es.eucm.ead.editor.control.actions.model.scene.transform.MirrorSelection;
import es.eucm.ead.editor.control.actions.model.scene.transform.MoveSelection;
import es.eucm.ead.editor.control.actions.model.scene.transform.RotateSelection;
import es.eucm.ead.editor.control.actions.model.scene.transform.ScaleSelection;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithScalePanel;

public class TransformationsWidget extends IconWithScalePanel {

	private static final int COLS = 4;
	private static final float PAD = .01F;

	public TransformationsWidget(final Controller controller, float space) {
		super("undo80x80", space, COLS, controller.getApplicationAssets()
				.getSkin());
		float pad = Gdx.graphics.getWidth() * PAD;
		panel.pad(pad).defaults().space(pad);
		Skin skin = controller.getApplicationAssets().getSkin();

		final IconButton moveLeft = new IconButton("play", "debugPlay", 0f,
				skin, "inverted");
		final IconButton moveRight = new IconButton("share", "share80x80", 0f,
				skin, "inverted");

		final IconButton moveUp = new IconButton("undo", "undo80x80", 0f, skin);
		final IconButton moveDown = new IconButton("redo", "redo80x80", 0f,
				skin);

		final IconButton rotateClockwise = new IconButton("paste",
				"paste80x80", 0f, skin);
		final IconButton rotateCounterClockwise = new IconButton("camera",
				"camera80x80", 0f, skin);
		final IconButton flipHorizontal = new IconButton("repository",
				"repository80x80", 0f, skin);
		final IconButton flipVertical = new IconButton("gallery",
				"android_gallery80x80", 0f, skin);

		final IconButton scaleUp = new IconButton("paint", "paint80x80", 0f,
				skin);
		final IconButton scaleDown = new IconButton("text", "text80x80", 0f,
				skin);

		final IconButton toFront = new IconButton("zone", "interactive80x80",
				0f, skin);
		final IconButton toBack = new IconButton("exit", "gateway80x80", 0f,
				skin);

		ChangeListener buttonsListener = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == moveLeft) {
					controller.action(MoveSelection.class,
							MoveSelection.Type.LEFT);
				} else if (listenerActor == moveRight) {
					controller.action(MoveSelection.class,
							MoveSelection.Type.RIGHT);
				} else if (listenerActor == moveUp) {
					controller.action(MoveSelection.class,
							MoveSelection.Type.UP);
				} else if (listenerActor == moveDown) {
					controller.action(MoveSelection.class,
							MoveSelection.Type.DOWN);
				} else if (listenerActor == rotateClockwise) {
					controller.action(RotateSelection.class,
							RotateSelection.Type.CLOCKWISE);
				} else if (listenerActor == rotateCounterClockwise) {
					controller.action(RotateSelection.class,
							RotateSelection.Type.COUNTER_CLOCKWISE);
				} else if (listenerActor == flipHorizontal) {
					controller.action(MirrorSelection.class,
							MirrorSelection.Type.VERTICAL);
				} else if (listenerActor == flipVertical) {
					controller.action(MirrorSelection.class,
							MirrorSelection.Type.HORIZONTAL);
				} else if (listenerActor == scaleUp) {
					controller.action(ScaleSelection.class,
							ScaleSelection.Type.INCREASE);
				} else if (listenerActor == scaleDown) {
					controller.action(ScaleSelection.class,
							ScaleSelection.Type.DECREASE);
				} else if (listenerActor == toFront) {
					controller.action(ReorderSelection.class,
							ReorderSelection.Type.BRING_TO_FRONT);
				} else if (listenerActor == toBack) {
					controller.action(ReorderSelection.class,
							ReorderSelection.Type.SEND_TO_BACK);
				}
			}
		};

		moveLeft.addListener(buttonsListener);
		moveRight.addListener(buttonsListener);
		moveUp.addListener(buttonsListener);
		moveDown.addListener(buttonsListener);
		rotateClockwise.addListener(buttonsListener);
		rotateCounterClockwise.addListener(buttonsListener);
		flipHorizontal.addListener(buttonsListener);
		flipVertical.addListener(buttonsListener);
		scaleUp.addListener(buttonsListener);
		scaleDown.addListener(buttonsListener);
		toFront.addListener(buttonsListener);
		toBack.addListener(buttonsListener);

		panel.add(moveLeft);
		panel.add(moveRight);
		panel.add(moveUp);
		panel.add(moveDown);
		panel.add(rotateClockwise);
		panel.add(rotateCounterClockwise);
		panel.add(flipHorizontal);
		panel.add(flipVertical);
		panel.add(scaleUp);
		panel.add(scaleDown);
		panel.add(toFront);
		panel.add(toBack);

	}

}
