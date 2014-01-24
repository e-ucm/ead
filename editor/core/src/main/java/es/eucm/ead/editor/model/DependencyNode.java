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

/**
 * The editor uses these nodes to encapsulate actual model objects, be they
 * Resources or EAdElements. The nodes are expected to be collected into a large
 * model graph, and must have a model-wide unique id.
 * 
 * @author mfreire
 */
public class DependencyNode implements Comparable<DependencyNode> {
	private String id;
	protected Object content;
	private DependencyNode manager;

	public DependencyNode(String id, Object content) {
		this.id = id;
		this.content = content;
	}

	public void setManager(DependencyNode manager) {
		this.manager = manager;
	}

	public boolean isManaged() {
		return manager != null;
	}

	public DependencyNode getManager() {
		return manager;
	}

	public String getLinkText() {
		return "" + id;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || (getClass() != other.getClass())) {
			return false;
		}
		return ((DependencyNode) other).id == id;
	}

	@Override
	public int hashCode() {
		return 23 * this.id.hashCode() + 5;
	}

	/**
	 * Compares this node to another one, using IDs as a sorting key
	 * 
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(DependencyNode other) {
		return id.compareTo(other.id);
	}
}
