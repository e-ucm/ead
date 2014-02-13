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
package es.eucm.ead.editor.view.widgets.layouts;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

public class ColumnsLayout extends AbstractWidget {

	private Array<Actor> columns;

	private Array<Constraints> constraints;

	public ColumnsLayout() {
		columns = new Array<Actor>();
		constraints = new Array<Constraints>();
	}

	public Constraints column(Actor actor) {
		columns.add(actor);
		addActor(actor);
		Constraints c = new Constraints();
		constraints.add(c);
		return c;
	}

	@Override
	public float getPrefWidth() {
		return super.getChildrenTotalWidth();
	}

	@Override
	public float getPrefHeight(Actor a) {
		return super.getChildrenMaxHeight();
	}

	@Override
	public void layout() {
		float width = getWidth();
		float debtWidth = 0;

		if (width < getPrefWidth()) {
			debtWidth = (getPrefWidth() - width) / columns.size;
		}

		float remainingWidth = width;
		float columnsExpanded = 0;
		for (int i = 0; i < columns.size; i++) {
			Actor column = columns.get(i);
			Constraints c = constraints.get(i);
			if (!c.expand) {
				remainingWidth -= getPrefWidth(column);
			} else {
				columnsExpanded++;
			}
		}

		float expandedWidth = remainingWidth / columnsExpanded;

		float x = 0;
		for (int i = 0; i < columns.size; i++) {
			Actor column = columns.get(i);
			Constraints c = constraints.get(i);
			float columnWidth = (c.expand ? expandedWidth
					: getPrefWidth(column)) - debtWidth;
			column.setBounds(x, 0, columnWidth, getHeight());
			x += columnWidth;
		}
	}

	public class Constraints {
		boolean expand;

		public Constraints column(Actor actor) {
			return ColumnsLayout.this.column(actor);
		}

		public Constraints expand() {
			expand = true;
			return this;
		}
	}

}
