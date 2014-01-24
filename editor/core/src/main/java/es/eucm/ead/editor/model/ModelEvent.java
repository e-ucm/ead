/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * An implementation of ModelEvent, intended for bundling several ModelEvents
 * together
 * 
 * @author mfreire
 */
public class ModelEvent {

	private static DependencyNode[] emptyArray = new DependencyNode[0];

	private final Object cause;
	private final ArrayList<DependencyNode> added = new ArrayList<DependencyNode>();
	private final ArrayList<DependencyNode> changed = new ArrayList<DependencyNode>();
	private final ArrayList<DependencyNode> removed = new ArrayList<DependencyNode>();

	public ModelEvent(Object cause) {
		this.cause = cause;
	}

	public ModelEvent(Object cause, DependencyNode[] added,
			DependencyNode[] removed, DependencyNode... changed) {
		this.cause = cause;
		this.added.addAll(Arrays.asList(added == null ? emptyArray : added));
		this.removed.addAll(Arrays.asList(removed == null ? emptyArray
				: removed));
		this.changed.addAll(Arrays.asList(changed == null ? emptyArray
				: changed));
		sort();
	}

	private void accumulate(ArrayList<DependencyNode> target,
			ArrayList<DependencyNode> source) {
		for (DependencyNode n : source) {
			if (!target.contains(n)) {
				target.add(n);
			}
		}
	}

	/**
	 * Must be called after the last merge
	 */
	private void sort() {
		Collections.sort(added);
		Collections.sort(changed);
		Collections.sort(removed);
	}

	public void merge(ModelEvent me) {
		accumulate(added, me.getAdded());
		accumulate(removed, me.getRemoved());
		accumulate(changed, me.getChanged());
		sort();
	}

	public ArrayList<DependencyNode> getAdded() {
		return added;
	}

	public ArrayList<DependencyNode> getRemoved() {
		return removed;
	}

	public ArrayList<DependencyNode> getChanged() {
		return changed;
	}

	public Object getCause() {
		return cause;
	}

	@Override
	public String toString() {
		return "ModelChange{cause=" + cause + ", added=" + getAdded()
				+ ", changed=" + getChanged() + ", removed=" + getRemoved()
				+ '}';
	}

	public static void appendIds(StringBuilder sb,
			ArrayList<DependencyNode> nodes) {
		sb.append('[');
		for (DependencyNode dn : nodes) {
			sb.append(dn.getId()).append(' ');
		}
		sb.append(']');
	}

	/**
	 * Utility method to show an event in full glory
	 * 
	 * @return pretty-printed IDs from this event
	 */
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append(getCause());
		sb.append(" change=");
		appendIds(sb, getChanged());
		sb.append(" add=");
		appendIds(sb, getAdded());
		sb.append(" remove=");
		appendIds(sb, getRemoved());
		return sb.toString();
	}

	/**
	 * Utility method to check whether a value is in an array. Note: uses binary
	 * search to greatly speed this up if there are many values. Added, changed
	 * and removed must always be sorted.
	 * 
	 * @param haystack
	 * @param needles
	 * @return true if contained
	 */
	public static boolean contains(ArrayList<DependencyNode> haystack,
			DependencyNode... needles) {
		if (needles == null) {
			return false;
		}
		for (DependencyNode needle : needles) {
			if (Collections.binarySearch(haystack, needle) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Utility method to check whether a value is changed in an event.
	 * 
	 * @param values
	 * @return
	 */
	public boolean changes(DependencyNode... values) {
		return contains(changed, values);
	}
}
