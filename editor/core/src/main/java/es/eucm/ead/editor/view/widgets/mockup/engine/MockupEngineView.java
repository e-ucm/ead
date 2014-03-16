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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.engine.wrappers.EditorGameLoop;
import es.eucm.ead.editor.view.widgets.engine.wrappers.EditorGameView;
import es.eucm.ead.editor.view.widgets.mockup.engine.wrappers.MockupGameLoop;

public class MockupEngineView extends
		es.eucm.ead.editor.view.widgets.engine.EngineView {

	private final Rectangle scissorBounds = new Rectangle();
	private final Rectangle widgetAreaBounds = new Rectangle();

	public MockupEngineView(Controller controller) {
		super(controller);
	}

	@Override
	protected EditorGameLoop createGameLoop(Controller controller,
			EditorGameView sceneView) {
		return new MockupGameLoop(controller, controller.getEditorAssets()
				.getSkin(), sceneView);
	}

	@Override
	public void layout() {
		super.layout();
		this.widgetAreaBounds.set(getX(), getY(), getWidth(), getHeight());
		super.getStage().calculateScissors(widgetAreaBounds, scissorBounds);
	}

	@Override
	public void fit() {
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
