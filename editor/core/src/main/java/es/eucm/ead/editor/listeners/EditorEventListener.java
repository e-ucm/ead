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
package es.eucm.ead.editor.listeners;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import es.eucm.ead.editor.Editor;
import es.eucm.ead.editor.EditorStage;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.engine.triggers.TouchSource;
import es.eucm.ead.schema.actions.Spin;
import es.eucm.ead.schema.actions.Transform;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.behaviors.Touch.Type;
import es.eucm.ead.schema.components.Transformation;

public class EditorEventListener extends InputListener {

	private float startX;

	private float startY;

	private float x;

	private float y;

	private int t;

	private boolean changed;

	private EditorStage stage;

	private SceneElementActor actor;

	private SceneElement element;

	private TouchSource engineListener;

	public EditorEventListener(EditorStage stage) {
		this.engineListener = new TouchSource();
		this.stage = stage;
		stage.addListener(this);
	}

	@Override
	public boolean handle(Event e) {
		if (stage.isPlaying()) {
			return engineListener.handle(e);
		} else {
			return super.handle(e);
		}
	}

	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		if (actor != null) {
			float diffX = event.getStageX() - this.startX;
			float diffY = event.getStageY() - this.startY;
			switch (t) {
			case 0:
				event.getListenerActor().setPosition(this.x + diffX,
						this.y + diffY);
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
		super.touchUp(event, x, y, pointer, button);
		if (changed && element != null && actor != null) {
			Transformation t = element.getTransformation();
			if (t == null) {
				t = Editor.factory.newInstance(Transformation.class);
				actor.getSchema().setTransformation(t);
			}
			t.setX(actor.getX());
			t.setY(actor.getY());
			t.setRotation(actor.getRotation());
			t.setScaleX(actor.getScaleX());
			t.setScaleY(actor.getScaleY());
			Color c = actor.getColor();
			es.eucm.ead.schema.components.Color c2 = new es.eucm.ead.schema.components.Color();
			c2.setA(c.a);
			c2.setB(c.b);
			c2.setR(c.r);
			c2.setG(c.g);
			t.setColor(c2);
			changed = false;
		}
	}

	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		switch (keycode) {
		case Keys.DEL:
		case Keys.FORWARD_DEL:
			if (actor != null) {
				Editor.gameController.removeSceneElement(actor);
			}
			break;
		case Keys.C:
			if (element != null) {
				Transformation transformation = new Transformation();
				es.eucm.ead.schema.components.Color c2 = new es.eucm.ead.schema.components.Color();
				c2.setB(0.0f);
				c2.setG(0.0f);
				transformation.setColor(c2);
				transformation.setScaleY(0.f);
				transformation.setScaleX(0.f);
				Transform t = new Transform();
				t.setDuration(0.5f);
				t.setTransformation(transformation);
				t.setRelative(true);
				Behavior b = new Behavior();
				es.eucm.ead.schema.behaviors.Touch touch = new es.eucm.ead.schema.behaviors.Touch();
				touch.setType(Type.ENTER);
				b.setTrigger(touch);
				b.setAction(t);
				element.getBehaviors().add(b);
				transformation = new Transformation();
				c2 = new es.eucm.ead.schema.components.Color();
				c2.setB(1.0f);
				c2.setG(1.0f);
				transformation.setColor(c2);
				transformation.setScaleY(0.f);
				transformation.setScaleX(0.f);
				t = new Transform();
				t.setDuration(0.5f);
				t.setTransformation(transformation);
				t.setRelative(true);
				b = new Behavior();
				touch = new es.eucm.ead.schema.behaviors.Touch();
				touch.setType(Type.EXIT);
				b.setTrigger(touch);
				b.setAction(t);
				element.getBehaviors().add(b);
				actor.setSchema(element);
			}
			break;
		case Keys.R:
			if (element != null) {
				Spin spin = new Spin();
				spin.setForever(true);
				spin.setDuration(1.0f);
				spin.setSpins(2);
				Behavior b = new Behavior();
				es.eucm.ead.schema.behaviors.Touch touch = new es.eucm.ead.schema.behaviors.Touch();
				touch.setType(Type.PRESS);
				b.setTrigger(touch);
				b.setAction(spin);
				element.getBehaviors().add(b);
				actor.setSchema(element);
			}
			break;
		case Keys.T:
			if (element != null) {
				Transformation transformation = new Transformation();
				es.eucm.ead.schema.components.Color c2 = new es.eucm.ead.schema.components.Color();
				c2.setB(0.0f);
				c2.setG(0.0f);
				transformation.setColor(c2);
				transformation.setScaleY(0.f);
				transformation.setScaleX(0.f);
				Transform t = new Transform();
				t.setDuration(5.0f);
				t.setTransformation(transformation);
				t.setRelative(true);
				element.getActions().add(t);
				actor.setSchema(element);
			}
			break;
		}
		return super.keyDown(event, keycode);
	}

	public SceneElement getElement() {
		return element;
	}
}
