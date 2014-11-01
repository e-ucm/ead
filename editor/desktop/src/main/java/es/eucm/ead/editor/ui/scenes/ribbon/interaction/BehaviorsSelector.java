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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.widgets.WidgetsUtils;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.Event;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.components.behaviors.events.Key;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Contains a list with the behavior of the current selected entity
 */
public class BehaviorsSelector extends LinearLayout {

	private Controller controller;

	private Skin skin;

	private Model model;

	private BehaviorsListener behaviorsListener = new BehaviorsListener();

	public BehaviorsSelector(Controller controller) {
		super(true);
		this.controller = controller;
		skin = controller.getApplicationAssets().getSkin();
		model = controller.getModel();
		model.addSelectionListener(new BehaviorsSelectionListener());
	}

	private void readBehaviors() {
		reset();
		ModelEntity modelEntity = (ModelEntity) controller.getModel()
				.getSelection().getSingle(Selection.SCENE_ELEMENT);
		if (modelEntity != null) {
			model.addListListener(modelEntity.getComponents(),
					behaviorsListener);
			for (ModelComponent component : modelEntity.getComponents()) {
				if (component instanceof Behavior) {
					addBehavior((Behavior) component);
				}
			}
		}
	}

	private void reset() {
		clearChildren();
		model.removeListenerFromAllTargets(behaviorsListener);
	}

	private void addBehavior(Behavior behavior) {
		Event event = behavior.getEvent();
		Actor actor = null;
		if (event instanceof Touch) {
			actor = add(WidgetsUtils.createIcon("touch48x48", skin)).getActor();
		} else if (event instanceof Timer) {
			actor = add(WidgetsUtils.createIcon("timer48x48", skin)).getActor();
		} else if (event instanceof Init) {
			actor = add(WidgetsUtils.createIcon("init48x48", skin)).getActor();
		} else if (event instanceof Key) {
			actor = add(WidgetsUtils.createIcon("key48x48", skin)).getActor();
		}

		if (actor != null) {
			actor.setUserObject(behavior);
			actor.addListener(new ActionOnClickListener(controller,
					SetSelection.class, Selection.SCENE_ELEMENT,
					Selection.BEHAVIOR, behavior));
		}
	}

	private void removeBehavior(Behavior behavior) {
		for (Actor actor : getChildren()) {
			if (actor.getUserObject() == behavior) {
				actor.remove();
				break;
			}
		}
	}

	public class BehaviorsSelectionListener implements SelectionListener {

		@Override
		public boolean listenToContext(String contextId) {
			return Selection.SCENE_ELEMENT.equals(contextId);
		}

		@Override
		public void modelChanged(SelectionEvent event) {
			switch (event.getType()) {
			case ADDED:
			case FOCUSED:
				readBehaviors();
				break;
			case REMOVED:
				reset();
				break;
			}
		}
	}

	public class BehaviorsListener implements ModelListener<ListEvent> {

		@Override
		public void modelChanged(ListEvent event) {
			Object object = event.getElement();
			if (object instanceof Behavior) {
				switch (event.getType()) {
				case ADDED:
					addBehavior((Behavior) object);
					break;
				case REMOVED:
					removeBehavior((Behavior) object);
					break;
				}
			}
		}
	}
}
