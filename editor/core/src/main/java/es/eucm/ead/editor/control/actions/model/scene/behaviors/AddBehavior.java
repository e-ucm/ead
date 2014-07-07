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
package es.eucm.ead.editor.control.actions.model.scene.behaviors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.Event;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Adds an empty behavior to the selected model entity, with the specified event
 * as trigger
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Class}</em> the class of the event
 * that triggers the behavior. It should extends {@link Event}</dd>
 * </dl>
 * {@link #perform(Object...)} will return {@code null} if no entity is
 * associated with the actor
 */
public class AddBehavior extends ModelAction implements SelectionListener {

	public AddBehavior() {
		super(true, false, Class.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		controller.getModel().addSelectionListener(this);
	}

	@Override
	public boolean validate(Object... args) {
		if (super.validate(args)) {
			Class eventClass = (Class) args[0];
			return ClassReflection.isAssignableFrom(Event.class, eventClass);
		}
		return false;
	}

	@Override
	public Command perform(Object... args) {
		ModelEntity modelEntity = (ModelEntity) controller.getModel()
				.getSelection().getCurrent().first();
		Class eventClass = (Class) args[0];
		try {
			Event event = (Event) ClassReflection.newInstance(eventClass);
			Behavior behavior = new Behavior();
			behavior.setEvent(event);
			return new AddToListCommand(modelEntity,
					modelEntity.getComponents(), behavior);
		} catch (ReflectionException e) {
			Gdx.app.error("AddBehavior", "Impossible to create event "
					+ eventClass, e);
			return null;
		}
	}

	@Override
	public boolean listenToContext(String contextId) {
		return true;
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		setEnabled(controller.getModel().getSelection()
				.getSingle(Selection.SCENE_ENTITY) != null);
	}
}
