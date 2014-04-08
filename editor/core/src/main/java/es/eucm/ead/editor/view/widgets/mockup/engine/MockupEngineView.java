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
package es.eucm.ead.editor.view.widgets.mockup.engine;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.engine.wrappers.EditorGameLoop;
import es.eucm.ead.editor.view.widgets.engine.wrappers.EditorGameView;
import es.eucm.ead.editor.view.widgets.mockup.engine.wrappers.MockupGameLoop;
import es.eucm.ead.editor.view.widgets.mockup.engine.wrappers.MockupGameView;

public class MockupEngineView extends
		es.eucm.ead.editor.view.widgets.engine.EngineView {

	private final Rectangle scissorBounds = new Rectangle();
	private final Rectangle widgetAreaBounds = new Rectangle();

	public MockupEngineView(Controller controller) {
		super(controller);
		super.setFillParent(true);
	}

	@Override
	protected EditorGameLoop createGameLoop(Controller controller,
			EditorGameView sceneView) {
		return new MockupGameLoop(controller, controller.getApplicationAssets()
				.getSkin(), sceneView);
	}

	@Override
	protected EditorGameView createGameView(Controller controller) {
		return new MockupGameView(controller.getModel(),
				controller.getEditorGameAssets(), controller
						.getApplicationAssets().getSkin());
	}

	@Override
	public void layout() {
		super.sceneView.setSize(super.sceneView.getPrefWidth(),
				super.sceneView.getPrefHeight());
		fit();
	}

	@Override
	public void fit() {
		final Vector2 scaling = Scaling.fit.apply(super.sceneView.getWidth(),
				super.sceneView.getHeight(), getWidth(), getHeight());

		final float xScaling = scaling.x / super.sceneView.getWidth();
		final float yScaling = scaling.y / super.sceneView.getHeight();
		super.sceneView.setScale(xScaling, yScaling);

		final float xOffset = (getWidth() - super.sceneView.getWidth()
				* xScaling) / 2;
		final float yOffset = (getHeight() - super.sceneView.getHeight()
				* yScaling) / 2;

		super.sceneView.setPosition(xOffset, yOffset);

		this.widgetAreaBounds.set(xOffset, yOffset, scaling.x, scaling.y);
		super.getStage().calculateScissors(widgetAreaBounds, scissorBounds);
	}

	public MockupGameView getSceneView() {
		return (MockupGameView) super.sceneView;
	}

	@Override
	protected void addTools() {

	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		if (widgetAreaBounds.contains(x, y)) {
			return super.hit(x, y, touchable);
		}
		return null;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		// Enable scissors for widget area and draw the widget.
		if (ScissorStack.pushScissors(scissorBounds)) {
			super.drawChildren(batch, parentAlpha);
			ScissorStack.popScissors();
		}
	}
}
