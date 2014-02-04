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
package es.eucm.ead.editor.view.widgets.engine.wrappers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.SceneElementEvent;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.schema.actors.SceneElement;

public class EditorSceneElement extends SceneElementActor implements
		ModelListener<SceneElementEvent> {

	private Drawable border;

	private EditorGameLoop editorGameLoop;

	@Override
	public void setGameLoop(GameLoop gameLoop) {
		super.setGameLoop(gameLoop);
		editorGameLoop = (EditorGameLoop) gameLoop;
		addListener(editorGameLoop.getDragListener());
		editorGameLoop.getController().addModelListener(
				SceneElementEvent.class, this);
	}

	@Override
	public void initialize(SceneElement schemaObject) {
		super.initialize(schemaObject);
		Skin skin = editorGameLoop.getSkin();
		border = skin.getDrawable("rose-border");
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		super.drawChildren(batch, parentAlpha);
		if (!editorGameLoop.isPlaying()) {
			border.draw(batch, 0, 0, getWidth(), getHeight());
		}
	}

	@Override
	public void act(float delta) {
		super.act(editorGameLoop.isPlaying() ? delta : 0);
	}

	@Override
	public void modelChanged(SceneElementEvent event) {
		if (this.getSchema() == event.getSceneElement()) {

			switch (event.getType()) {
			case MOVE:
				setX(element.getTransformation().getX());
				setY(element.getTransformation().getY());
				break;
			case REMOVED:
				this.remove();
				break;
			}
		}
	}
}
