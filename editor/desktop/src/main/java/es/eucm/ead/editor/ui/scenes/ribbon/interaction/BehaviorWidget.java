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
package es.eucm.ead.editor.ui.scenes.ribbon.interaction;

import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.ui.scenes.ribbon.interaction.events.EventWidget;
import es.eucm.ead.editor.ui.scenes.ribbon.interaction.events.InitWidget;
import es.eucm.ead.editor.ui.scenes.ribbon.interaction.events.KeyWidget;
import es.eucm.ead.editor.ui.scenes.ribbon.interaction.events.TimerWidget;
import es.eucm.ead.editor.ui.scenes.ribbon.interaction.events.TouchWidget;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.components.behaviors.events.Key;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.components.behaviors.events.Touch;

import java.util.HashMap;
import java.util.Map;

/**
 * Panel containing the representation for a given behavior. At the top, a panel
 * shows the event representation, and at the bottom the effects
 * representations.
 */
public class BehaviorWidget extends LinearLayout implements SelectionListener {

	private Controller controller;

	private Map<Class, Actor> eventWidgets = new HashMap<Class, Actor>();

	public BehaviorWidget(Controller controller) {
		super(false);
		background(controller.getApplicationAssets().getSkin()
				.getDrawable("blank"));
		this.controller = controller;

		eventWidgets.put(Init.class, new InitWidget(controller));
		eventWidgets.put(Touch.class, new TouchWidget(controller));
		eventWidgets.put(Timer.class, new TimerWidget(controller));
		eventWidgets.put(Key.class, new KeyWidget(controller));

		controller.getModel().addSelectionListener(this);
		updateWidget();
	}

	public void updateWidget() {
		reset();
		Behavior behavior = (Behavior) controller.getModel().getSelection()
				.getSingle(Selection.BEHAVIOR);
		if (behavior != null) {
			EventWidget eventWidget = (EventWidget) eventWidgets.get(behavior
					.getEvent().getClass());
			eventWidget.readEvent(behavior.getEvent());
			add(eventWidget).margin(5);
			addSpace();
		}
	}

	private void reset() {
		clearChildren();
	}

	@Override
	public boolean listenToContext(String contextId) {
		return Selection.BEHAVIOR.equals(contextId);
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		switch (event.getType()) {
		case FOCUSED:
			updateWidget();
			break;
		case REMOVED:
			reset();
			break;
		}
	}
}
