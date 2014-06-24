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
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.ui.WidgetsUtils;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.Event;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.entities.ModelEntity;

public class BehaviorsWidget extends LinearLayout {

	private Skin skin;

	private Model model;

	private BehaviorsListener behaviorsListener = new BehaviorsListener();

	public BehaviorsWidget(Controller controller) {
		super(true);
		skin = controller.getApplicationAssets().getSkin();
		model = controller.getModel();
		model.addSelectionListener(new SelectionListener());
	}

	private void readSelection(Array<Object> selection) {
		if (selection.size == 1 && selection.first() instanceof ModelEntity) {
			ModelEntity sceneElement = (ModelEntity) selection.first();
			readBehaviors(sceneElement);
		} else {
			clearChildren();
		}
	}

	private void readBehaviors(ModelEntity modelEntity) {
		clearChildren();
		model.removeListenerFromAllTargets(behaviorsListener);
		model.addListListener(modelEntity.getComponents(), behaviorsListener);
		for (ModelComponent component : modelEntity.getComponents()) {
			if (component instanceof Behavior) {
				addBehavior((Behavior) component);
			}
		}
	}

	private void addBehavior(Behavior behavior) {
		Event event = behavior.getEvent();
		Actor actor = null;
		if (event instanceof Touch) {
			actor = add(WidgetsUtils.createIcon("touch48x48", skin)).getActor();
		} else if (event instanceof Timer) {
			actor = add(WidgetsUtils.createIcon("timer48x48", skin)).getActor();
		}

		if (actor != null) {
			actor.setUserObject(behavior);
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

	public class SelectionListener implements ModelListener<SelectionEvent> {

		@Override
		public void modelChanged(SelectionEvent event) {
			readSelection(event.getSelection());
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
