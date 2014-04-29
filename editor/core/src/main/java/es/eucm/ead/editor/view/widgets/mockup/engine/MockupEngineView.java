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
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.widgets.mockup.edition.draw.BrushStrokes;
import es.eucm.ead.schema.components.game.GameData;
import es.eucm.ead.schema.entities.ModelEntity;

public class MockupEngineView extends
		es.eucm.ead.editor.view.widgets.engine.EngineView {

	private final Rectangle scissorBounds = new Rectangle();
	private final Rectangle widgetAreaBounds = new Rectangle();
	private BrushStrokes brushStrokes;

	public MockupEngineView(Controller controller) {
		super(controller);
		super.setFillParent(true);
	}

	public void layout() {
		if (game != null) {
			GameData gameData = Model.getComponent(game, GameData.class);
			sceneView.setSize(gameData.getWidth(), gameData.getHeight());
			fit();
		}
	}

	public WidgetGroup getSceneview() {
		return sceneView;
	}

	@Override
	public void fit() {
		final Vector2 scaling = Scaling.fit.apply(super.sceneView.getWidth(),
				super.sceneView.getHeight(), getWidth(), getHeight());

		final float xScaling = scaling.x / super.sceneView.getWidth();
		final float yScaling = scaling.y / super.sceneView.getHeight();
		super.sceneView.setScale(xScaling, yScaling);

		final float xOffset = (getWidth() - scaling.x) / 2;
		final float yOffset = (getHeight() - scaling.y) / 2;

		super.sceneView.setPosition(xOffset, yOffset);
		this.widgetAreaBounds.set(xOffset, yOffset, scaling.x, scaling.y);
		this.brushStrokes.setBounds(0, 0, scaling.x, scaling.y);
		super.getStage().calculateScissors(widgetAreaBounds, scissorBounds);
	}

	public void setGame(ModelEntity game) {
		super.game = game;
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

	public void setBrushStrokes(BrushStrokes brushStrokes) {
		this.brushStrokes = brushStrokes;
		sceneView.addActor(brushStrokes);
	}
}
