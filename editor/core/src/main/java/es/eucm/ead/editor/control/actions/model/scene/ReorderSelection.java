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
package es.eucm.ead.editor.control.actions.model.scene;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.actions.model.Reorder;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.schema.entities.ModelEntity;

import java.util.Comparator;
import java.util.List;

/**
 * Action to reorder the entities in the current selection
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Type}</em> type of the reorder (to
 * front, to back, bring to front, send to back)</dd>
 * </dl>
 */
public class ReorderSelection extends ModelAction implements
		ModelListener<SelectionEvent> {

	public enum Type {
		TO_BACK, TO_FRONT, SEND_TO_BACK, BRING_TO_FRONT
	}

	private Reorder reorder;

	private Array<ModelEntity> orderedSelection;

	private ChildrenComparator childrenComparator = new ChildrenComparator();

	public ReorderSelection() {
		super(true, false, Type.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		reorder = controller.getActions().getAction(Reorder.class);
		orderedSelection = new Array<ModelEntity>();
	}

	public boolean validate(Object... args) {
		if (super.validate(args)
				&& controller.getModel().getEditionContext() instanceof ModelEntity) {
			Array<Object> selection = controller.getModel().getSelection();
			for (Object selected : selection) {
				if (!(selected instanceof ModelEntity)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public CompositeCommand perform(Object... args) {
		Type type = (Type) args[0];

		ModelEntity parent = (ModelEntity) controller.getModel()
				.getEditionContext();

		CompositeCommand compositeCommand = new CompositeCommand();

		orderedSelection.clear();
		for (Object o : controller.getModel().getSelection()) {
			orderedSelection.add((ModelEntity) o);
		}
		childrenComparator.setList(parent.getChildren());
		orderedSelection.sort(childrenComparator);

		int lastIndex = -1;
		if (type == Type.BRING_TO_FRONT || type == Type.TO_FRONT) {
			orderedSelection.reverse();
			lastIndex = parent.getChildren().size();
		}

		for (Object o : orderedSelection) {
			ModelEntity entity = (ModelEntity) o;

			int currentIndex = parent.getChildren().indexOf(entity);
			int index = 0;
			switch (type) {
			case TO_BACK:
				index = Math.max(currentIndex - 1, lastIndex + 1);
				break;
			case TO_FRONT:
				index = Math.min(currentIndex + 1, lastIndex - 1);
				break;
			case SEND_TO_BACK:
				index = Math.max(0, lastIndex + 1);
				break;
			case BRING_TO_FRONT:
				index = Math
						.min(parent.getChildren().size() - 1, lastIndex - 1);
				break;
			}

			Command command = reorder.perform(parent, parent.getChildren(),
					entity, index, false);
			lastIndex = index;

			if (command != null) {
				compositeCommand.addCommand(command);
			}
		}

		return compositeCommand;
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		setEnabled(false);
		if (event.getEditionContext() instanceof ModelEntity) {
			for (Object o : event.getSelection()) {
				if (!(o instanceof ModelEntity)) {
					return;
				}
			}
			setEnabled(true);
		}
	}

	public static class ChildrenComparator implements Comparator<ModelEntity> {

		private List<ModelEntity> list;

		public void setList(List<ModelEntity> list) {
			this.list = list;
		}

		@Override
		public int compare(ModelEntity modelEntity, ModelEntity modelEntity2) {
			return list.indexOf(modelEntity) - list.indexOf(modelEntity2);
		}
	}
}
