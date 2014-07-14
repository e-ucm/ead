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
package es.eucm.ead.editor.control.commands;

import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.ViewEvent;
import es.eucm.ead.editor.view.builders.ViewBuilder;

/**
 * Created by angel on 23/05/14.
 */
public class ViewCommand extends Command {

	private Views views;

	private Class<? extends ViewBuilder> viewClass;

	private Object[] args;

	private Class<? extends ViewBuilder> oldViewClass;

	private Object[] oldArgs;

	public ViewCommand(Views views, Class<? extends ViewBuilder> viewClass,
			Object... args) {
		this.views = views;
		this.viewClass = viewClass;
		this.args = args;
		oldViewClass = views.getCurrentView() == null ? null : views
				.getCurrentView().getClass();
		oldArgs = views.getCurrentArgs();
	}

	public Class<? extends ViewBuilder> getViewClass() {
		return viewClass;
	}

	public Object[] getArgs() {
		return args;
	}

	public Class<? extends ViewBuilder> getOldViewClass() {
		return oldViewClass;
	}

	public Object[] getOldArgs() {
		return oldArgs;
	}

	@Override
	public ModelEvent doCommand() {
		views.setView(viewClass, args);
		return new ViewEvent(views, viewClass, args);
	}

	@Override
	public boolean canUndo() {
		return oldViewClass != null;
	}

	@Override
	public ModelEvent undoCommand() {
		views.setView(oldViewClass, oldArgs);
		return new ViewEvent(views, viewClass, args);
	}

	@Override
	public boolean isTransparent() {
		return true;
	}
}
