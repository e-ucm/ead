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
package es.eucm.ead.editor.view.widgets.drag;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

/**
 * A widget containing a drag and drop scene. Provides operations to visualize
 * the drag scene, like panning or zoom. Sets some default keyboard shortcuts:
 * <ul>
 * <li><strong>SPACE and mouse drag</strong>: pans the scene</li>
 * <li><strong>+/-</strong>: zoom in/zoom out</li>
 * <li><strong>1</strong>: fits the scene inside container</li>
 * </ul>
 */
public class DragAndDropContainer extends AbstractWidget {

	public static final float ROTATION_STEP = 15.0f;

	private DragAndDropStyle style;

	private DragAndDropScene scene;

	public DragAndDropContainer(Skin skin) {
		setRequestKeyboardFocus(true);
		style = new DragAndDropStyle();
		style.background = skin.getDrawable("blank");
		scene = new DragAndDropScene(new ShapeRenderer());
		addActor(scene);
		addListener(new ContainerListener());
	}

	/**
	 * Sets scene size
	 */
	public void setSceneSize(float width, float height) {
		scene.setSize(width, height);
		fit();
	}

	/**
	 * Fits the scene in the current container size
	 */
	public void fit() {
		float scaleX = getWidth() / scene.getWidth();
		float scaleY = getHeight() / scene.getHeight();
		float scale = Math.min(scaleX, scaleY);
		float offsetX = (getWidth() - scene.getWidth() * scale) / 2.0f;
		float offsetY = (getHeight() - scene.getHeight() * scale) / 2.0f;
		scene.setPosition(offsetX, offsetY);
		scene.setScale(scale);
	}

	/**
	 * Adds the given scale to the current scale
	 */
	public void scale(float scale) {
		scene.setScale(scene.getScaleX() + scale);
	}

	/**
	 * Adds an actor to scene
	 */
	public void addActorToScene(Actor actor) {
		scene.addActor(actor);
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		style.background.draw(batch, 0, 0, getWidth(), getHeight());
		super.drawChildren(batch, parentAlpha);
	}

	/**
	 * Listener to trigger common behaviors of the container like panning or
	 * scaling
	 */
	private class ContainerListener extends DragListener {

		private boolean spacePressed = false;

		private boolean moving = false;

		@Override
		public void dragStart(InputEvent event, float x, float y, int pointer) {
			moving = spacePressed;
		}

		@Override
		public void drag(InputEvent event, float x, float y, int pointer) {
			if (moving) {
				scene.setPosition(scene.getX() - getDeltaX(), scene.getY()
						- getDeltaY());
			}
		}

		@Override
		public boolean keyDown(InputEvent event, int keycode) {
			switch (keycode) {
			case Keys.SPACE:
				spacePressed = true;
				return true;
			case Keys.NUM_1:
				fit();
				return true;
			case Keys.MINUS:
				scale(-0.1f);
				return true;
			case Keys.PLUS:
				scale(0.1f);
				return true;
			case Keys.CONTROL_LEFT:
				scene.getModifier().setRotationStep(ROTATION_STEP);
				return true;
			}
			return false;
		}

		@Override
		public boolean keyUp(InputEvent event, int keycode) {
			switch (keycode) {
			case Keys.SPACE:
				spacePressed = false;
				return true;
			case Keys.CONTROL_LEFT:
				scene.getModifier().setRotationStep(1.0f);
				return true;
			}
			return false;
		}

	}

	public static class DragAndDropStyle {

		public Drawable background;

	}
}
