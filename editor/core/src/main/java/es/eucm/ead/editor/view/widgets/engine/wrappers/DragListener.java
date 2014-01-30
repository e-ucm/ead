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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import es.eucm.ead.editor.view.widgets.engine.wrappers.EditorSceneView;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.engine.triggers.TouchSource;
import es.eucm.ead.schema.actors.SceneElement;

public class DragListener extends InputListener {

	private float startX;

	private float startY;

	private float x;

	private float y;

	private int t;

	private boolean changed;

	private SceneElementActor actor;

	private SceneElement element;

	private EditorSceneView sceneView;

	private boolean playing;

	private TouchSource touchSource;

	public DragListener(EditorSceneView sceneView) {
		this.sceneView = sceneView;
		this.playing = false;
		this.touchSource = new TouchSource();
	}

	@Override
	public boolean handle(Event e) {
		return playing ? touchSource.handle(e) : super.handle(e);
	}

	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		if (actor != null) {
			float diffX = event.getStageX() - this.startX;
			float diffY = event.getStageY() - this.startY;
			switch (t) {
			case 0:
				event.getListenerActor().setPosition(
						this.x + diffX / sceneView.getScaleX(),
						this.y + diffY / sceneView.getScaleY());
				break;
			case 1:

				break;
			}
			changed = true;
		}
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		startX = event.getStageX();
		startY = event.getStageY();
		Actor a = event.getListenerActor();
		if (a instanceof SceneElementActor) {
			actor = (SceneElementActor) a;
			element = ((SceneElementActor) a).getSchema();
			this.x = event.getListenerActor().getX();
			this.y = event.getListenerActor().getY();
			if (x < 10 && y < 10) {
				t = 1;
			} else {
				t = 0;
			}
			event.cancel();
			return true;
		} else {
			actor = null;
			element = null;
			return false;
		}
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,
			int button) {
	}

}
