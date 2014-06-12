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
package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.edition.MoreElementComponent;
import es.eucm.ead.editor.view.widgets.mockup.panels.behaviours.BehavioursEdition;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.Event;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.effects.GoTo;
import es.eucm.ead.schema.effects.RemoveEntity;

/**
 * Button to represent behavior.
 * 
 */
public class BehaviorButton extends Button {

	private final String IC_TIMER = MoreElementComponent.IC_TIMER,
			IC_TOUCH = MoreElementComponent.IC_TOUCH,
			IC_TRASH = MoreElementComponent.IC_TRASH;

	private final float PAD_ICON = 20f;

	private Behavior behaviour;

	private Event event;

	private Button trash;

	private Label time;

	private Label repeats;

	private Label effect;

	private I18N i18n;

	public BehaviorButton(Skin skin, Vector2 viewport, Controller controller,
			final BehavioursEdition behavioursEdition, Behavior behaviour) {
		super(skin);

		this.i18n = controller.getApplicationAssets().getI18N();

		this.behaviour = behaviour;
		this.event = behaviour.getEvent();

		this.time = new Label("", skin);
		this.time.setFontScale(0.5f);

		this.repeats = new Label("", skin);
		this.repeats.setFontScale(0.5f);

		this.effect = new Label("", skin);

		this.trash = new ToolbarButton(viewport, skin.getDrawable(IC_TRASH),
				i18n.m("general.delete"), skin);
		this.trash.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				remove();
			}
		});

		final Image icon;

		if (event instanceof Touch) {
			icon = new Image(skin.getRegion(IC_TOUCH));
			initialize(behaviour, (Touch) event);
		} else {// this.behaviour instanceof Timer
			icon = new Image(skin.getRegion(IC_TIMER));
			initialize(behaviour, (Timer) event);
		}
		icon.setScaling(Scaling.fit);

		this.add(icon).left().padRight(PAD_ICON).padLeft(PAD_ICON);
		this.add(effect).left().expandX();
		this.add(trash).right().padRight(PAD_ICON);
		this.row();
		this.add(time);
		this.add(repeats);

		this.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				behavioursEdition.show(BehaviorButton.this);
			}
		});
	}

	public void initialize(Behavior behavior, Timer timer) {
		if (behavior.getEffects().size() != 0) {
			this.effect.setText(i18n.m("edition.effect") + ": "
					+ effectToString(behavior.getEffects().get(0)));
		} else {
			this.effect.setText(i18n.m("edition.effect") + ": "
					+ effectToString(null));
		}

		this.time.setText(i18n.m("general.time") + " : " + timer.getTime());

		String times = "" + timer.getRepeat();
		if (timer.getRepeat() == 0) {
			times = "1";
		} else if (timer.getRepeat() < 0) {
			times = i18n.m("general.always");
		}
		this.repeats.setText(i18n.m("general.repeats") + " : " + times);
	}

	public void initialize(Behavior behavior, Touch touch) {
		if (behavior.getEffects().size() != 0) {
			this.effect.setText(i18n.m("edition.effect") + ": "
					+ effectToString(behavior.getEffects().get(0)));
		} else {
			this.effect.setText(i18n.m("edition.effect") + ": "
					+ effectToString(null));
		}
	}

	public Behavior getBehaviour() {
		return behaviour;
	}

	public Event getEvent() {
		return event;
	}

	public void setEffectDesc(String effect) {
		this.effect.setText(i18n.m("edition.effect") + ": " + effect);
		if (event instanceof Timer) {
			Timer timer = (Timer) this.event;
			this.time.setText(i18n.m("general.time") + timer.getTime());

			String times = "" + timer.getRepeat();
			if (timer.getRepeat() == 0) {
				times = "1";
			} else if (timer.getRepeat() < 0) {
				times = i18n.m("general.always");
			}
			this.repeats.setText(i18n.m("general.repeats") + " : " + times);
		}
	}

	public String effectToString(Effect effect) {
		if (effect == null) {
			return i18n.m("general.effect.without");
		} else if (effect instanceof ChangeVar) {
			return i18n.m("general.effects.change-var");
		} else if (effect instanceof GoTo) {
			return i18n.m("general.effects.go-to");
		} else if (effect instanceof GoScene) {
			return i18n.m("general.effects.go-scene");
		} else if (effect instanceof RemoveEntity) {
			return i18n.m("general.effects.remove-entity");
		} else {// effect instanceof EndGame
			return i18n.m("general.effects.end-game");
		}
	}
}
