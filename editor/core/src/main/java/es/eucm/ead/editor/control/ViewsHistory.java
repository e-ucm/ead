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
package es.eucm.ead.editor.control;

import java.util.Stack;

/**
 * Holds a views history
 */
public class ViewsHistory {

	private Stack<ViewUpdate> previousViews;

	private Stack<ViewUpdate> nextViews;

	public ViewsHistory() {
		previousViews = new Stack<ViewUpdate>();
		nextViews = new Stack<ViewUpdate>();
	}

	/**
	 * The given view with the given arguments was hidden, and should be added
	 * to the view history. The view is queued only if the class and arguments
	 * are different from last view hidden called.
	 */
	public void viewUpdated(Class viewClass, Object... args) {
		nextViews.clear();
		ViewUpdate viewUpdate = new ViewUpdate(viewClass, args);
		if (previousViews.isEmpty() || !viewUpdate.equals(previousViews.peek())) {
			previousViews.push(viewUpdate);
		}
	}

	/**
	 * @return the last view change. Can return {@code null} if there is no view
	 *         to go back
	 */
	public ViewUpdate back() {
		if (previousViews.size() > 1) {
			ViewUpdate viewUpdate = previousViews.pop();
			nextViews.push(viewUpdate);
			return previousViews.peek();
		}
		return null;
	}

	/**
	 * @return the next view, if any. Can return {@code null}
	 */
	public ViewUpdate next() {
		if (!nextViews.isEmpty()) {
			ViewUpdate viewUpdate = nextViews.pop();
			previousViews.push(viewUpdate);
			return viewUpdate;
		}
		return null;
	}

	/**
	 * Represents a change in the view
	 */
	public static class ViewUpdate {
		private Class viewClass;
		private Object[] args;

		public ViewUpdate(Class viewClass, Object[] args) {
			this.viewClass = viewClass;
			this.args = args;
		}

		public Class getViewClass() {
			return viewClass;
		}

		public Object[] getArgs() {
			return args;
		}

		public boolean equals(Object o) {
			if (o instanceof ViewUpdate) {
				ViewUpdate viewUpdate = (ViewUpdate) o;
				if (viewUpdate.viewClass != viewClass
						|| viewUpdate.args.length != args.length) {
					return false;
				}

				for (int i = 0; i < args.length; i++) {
					if (args[i] != viewUpdate.args[i]) {
						return false;
					}
				}
				return true;
			}
			return false;
		}

	}
}
