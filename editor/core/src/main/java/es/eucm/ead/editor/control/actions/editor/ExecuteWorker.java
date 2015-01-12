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
package es.eucm.ead.editor.control.actions.editor;

import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;

/**
 * <p>
 * Starts a worker
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Class}</em> Worker class</dd>
 * <dd><strong>args[1]</strong> <em>Optional {@link Boolean}</em> If the
 * previous workers with the same class should be cancelled (true) or this
 * worker should be appended among the others</dd>
 * <dd><strong>args[1 or 2]</strong> <em>{@link WorkerListener}</em> A listener
 * for the worker</dd>
 * <dd><strong>args[2 or 3]...</strong> <em>{@link Object}</em> Arguments passed
 * to the worker</dd>
 * </dl>
 */
public class ExecuteWorker extends EditorAction {

	@Override
	public boolean validate(Object... args) {
		return args.length > 1
				&& args[0] instanceof Class
				&& (args[1] instanceof WorkerListener || (args[1] instanceof Boolean && args[2] instanceof WorkerListener));
	}

	@Override
	public void perform(Object... args) {
		boolean cancelOthers = true;
		int index = 2;
		int listenerIndex = 1;
		if (args.length > 2) {
			if (args[1] instanceof Boolean) {
				index = 3;
				listenerIndex = 2;
				cancelOthers = (Boolean) args[1];
			}
		}
		Object[] workerArguments = new Object[args.length - index];
		System.arraycopy(args, index, workerArguments, 0,
				workerArguments.length);
		controller.getWorkerExecutor().execute((Class) args[0],
				(WorkerListener) args[listenerIndex], cancelOthers,
				workerArguments);
	}
}
