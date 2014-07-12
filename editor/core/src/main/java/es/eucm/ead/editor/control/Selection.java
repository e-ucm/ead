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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * Keeps track of all the elements selected in the editor, in different
 * contexts. Context are hierarchical, so every time a selection is set, the
 * parent context must be known.
 */
public class Selection {

	private static final SnapshotArray<Object> NO_SELECTION = new SnapshotArray<Object>();

	public static String SCENE = "scene";

	public static String EDITED_GROUP = "editedGroup";

	public static String SCENE_ELEMENT = "sceneElement";

	public static String BEHAVIOR = "behavior";

	public static final String FRAMES = "frames";

	public static final String FRAME = "frame";

	private int pointer = -1;

	private Array<Context> contexts;

	public Selection() {
		contexts = new Array<Context>();
	}

	/**
	 * Sets the root context and its selection. Equivalent to calling
	 * {@link #set(String, String, Object...)} with null as
	 * {@code parentContextId}
	 * 
	 * @return the existing contexts that has been removed to set the new
	 *         context
	 */
	public Array<Context> setRoot(String contextId, Object... selection) {
		return set(null, contextId, selection);
	}

	/**
	 * <p>
	 * Sets the selection of the given context, and sets the focus to this
	 * context (i.e., {@link #getCurrent()} will return the selection for this
	 * context). This method removes all those contexts that are below from it.
	 * For example, if we have the following context structure:
	 * </p>
	 * 
	 * <pre>
	 * scene + editedGroup + sceneElement
	 * </pre>
	 * 
	 * <p>
	 * and we set the "scene" context to a new scene, "editedGroup" and
	 * "sceneElement" contexts are removed and returned by the method.
	 * <p>
	 * <p>
	 * There is a special case in where children context are not removed: when
	 * the selection stays the same (e.g., we set a scene and the "scene"
	 * context already has that scene as selection), children context are not
	 * removed. The only change the method produce then is that the selection
	 * focus passes to the "scene" context.
	 * </p>
	 * 
	 * @param parentContextId
	 *            the id of the parent context
	 * @param contextId
	 *            the id of the context to be changed
	 * @param selection
	 *            the selection for the context
	 * @return the existing contexts that has been removed to set the new
	 *         context
	 */
	public Array<Context> set(String parentContextId, String contextId,
			Object... selection) {

		if (parentContextId == null) {
			Array<Context> contextsRemoved = contexts;
			contexts = new Array<Context>();
			contexts.add(new Context(null, contextId, selection));
			this.pointer = 0;
			return contextsRemoved;
		}

		Array<Context> contextsRemoved = new Array<Context>();

		int index = getIndex(contextId);
		Context context;
		if (index == -1) {
			int parentIndex = getIndex(parentContextId);
			context = Pools.obtain(Context.class);
			context.setId(contextId);
			context.setParentId(parentContextId);
			if (parentIndex != -1 && parentIndex < contexts.size - 1) {
				for (int i = parentIndex + 1; i < contexts.size; i++) {
					contextsRemoved.add(contexts.get(i));
				}
				contexts.removeRange(parentIndex + 1, contexts.size - 1);
			}
			contexts.add(context);
			index = contexts.size - 1;
		} else {
			context = contexts.get(index);
		}

		if (context.isDifferentSelection(selection)) {
			context.setSelection(selection);
			if (index + 1 < contexts.size) {
				for (int i = index + 1; i < contexts.size; i++) {
					contextsRemoved.add(contexts.get(i));
				}
				contexts.removeRange(index + 1, contexts.size - 1);
			}
		}
		this.pointer = index;
		return contextsRemoved;
	}

	private int getIndex(String id) {
		int j = 0;
		for (Context context : contexts) {
			if (context.getId().equals(id)) {
				return j;
			}
			j++;
		}
		return -1;
	}

	/**
	 * @return current edition context
	 */
	public Context getCurrentContext() {
		return pointer == -1 ? null : contexts.get(pointer);
	}

	/**
	 * @return array of the objects selected in the focused context. Never
	 *         returns {@code null}. This array should not be modified
	 */
	public SnapshotArray<Object> getCurrent() {
		if (pointer < 0 || pointer > contexts.size - 1) {
			return NO_SELECTION;
		} else {
			return contexts.get(pointer).getSelection();
		}
	}

	/**
	 * @return the selection of an specific context. It the context is not set,
	 *         never returns {@code null}, but an empty array. Returned array
	 *         should not be modified.
	 */
	public SnapshotArray<Object> get(String contextId) {
		int index = getIndex(contextId);
		return index == -1 ? NO_SELECTION : contexts.get(getIndex(contextId))
				.getSelection();
	}

	/**
	 * @return the first element of the selection of a context. Can be
	 *         {@code null} if not selection is present in the given context
	 */
	public Object getSingle(String contextId) {
		SnapshotArray<Object> selection = get(contextId);
		return selection == null || selection.size == 0 ? null : selection
				.first();
	}

	public Array<Context> getContexts() {
		return contexts;
	}

	public Context getContext(String contextId) {
		int index = getIndex(contextId);
		return index == -1 ? null : contexts.get(index);
	}

	public Context remove(String contextId) {
		int index = getIndex(contextId);
		if (index == pointer) {
			pointer--;
		}
		return contexts.removeIndex(index);
	}

	public static class Context implements Poolable {

		private String parentId;

		private String id;

		private SnapshotArray<Object> selection = new SnapshotArray<Object>();

		public Context() {
			// Used by pools
		}

		public Context(String parentId, String id, Object... selection) {
			this.parentId = parentId;
			this.id = id;
			this.selection.addAll(selection);
		}

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}

		public void setSelection(SnapshotArray<Object> selection) {
			this.selection = selection;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setSelection(Object... selection) {
			this.selection.clear();
			this.selection.addAll(selection);
		}

		public SnapshotArray<Object> getSelection() {
			return selection;
		}

		public boolean isDifferentSelection(Object... selection) {
			if (this.selection.size == selection.length) {
				for (Object selected : selection) {
					if (!this.selection.contains(selected, true)) {
						return true;
					}
				}
				return false;
			}
			return true;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Context that = (Context) o;

			if (id != null ? !id.equals(that.id) : that.id != null)
				return false;
			if (parentId != null ? !parentId.equals(that.parentId)
					: that.parentId != null)
				return false;
			if (selection != null ? !selection.equals(that.selection)
					: that.selection != null)
				return false;

			return true;
		}

		@Override
		public void reset() {
			id = null;
			parentId = null;
			selection.clear();
		}
	}
}
