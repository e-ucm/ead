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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;

public class Table extends WidgetGroup {

	private Skin skin;

	private Controller controller;

	private Array<CellRow> rows;

	public Table(Controller controller, Skin skin) {
		this.skin = skin;
		this.controller = controller;
		setFillParent(true);
		rows = new Array<CellRow>();
	}

	public CellRow row() {
		return addRow(new LinearLayout(true));
	}

	private CellRow addRow(LinearLayout row) {
		CellRow cellRow = new CellRow(row);
		row.background(skin.getDrawable("grey-bg"));
		rows.add(cellRow);
		addActor(row);
		return cellRow;
	}

	@Override
	public void layout() {
		float expandHeight = getHeight();
		float expandY = 0;
		for (CellRow row : rows) {
			if (!row.expandY) {
				expandHeight -= row.widget.getPrefHeight();
			} else {
				expandY++;
			}
		}

		float yOffset = getHeight();
		for (CellRow row : rows) {
			float rowWidth = getWidth();
			if (row.percentWidth > 0.0f) {
				rowWidth = getWidth() * row.percentWidth;
			}

			float rowHeight = row.expandY ? expandHeight / expandY : row.widget
					.getPrefHeight();

			float xOffset = 0;
			switch (row.horizontalAlign) {
			case Align.right:
				xOffset = getWidth() - rowWidth;
				break;
			case Align.center:
				xOffset = getWidth() / 2 - rowWidth / 2;
				break;
			}

			yOffset -= rowHeight;
			row.widget.setBounds(xOffset, yOffset, rowWidth, rowHeight);
		}

	}

	public class CellRow {

		private WidgetGroup widget;

		private boolean expandY;

		private int horizontalAlign;

		private float percentWidth;

		public CellRow(WidgetGroup widget) {
			this.widget = widget;
			percentWidth = -1.0f;
		}

		public CellRow expandY() {
			this.expandY = true;
			return this;
		}

		public CellRow left() {
			horizontalAlign = Align.left;
			return this;
		}

		public CellRow right() {
			horizontalAlign = Align.right;
			return this;
		}

		public CellRow center() {
			horizontalAlign = Align.center;
			return this;
		}

		public CellRow percentWidth(float percentWidth) {
			this.percentWidth = percentWidth;
			return this;
		}

		public CellRow add(Actor actor) {
			widget.addActor(actor);
			return this;
		}

		public CellRow add(Actor actor, EventListener listener) {
			add(actor);
			actor.addListener(listener);
			return this;
		}

		public CellRow add(Actor actor, String actionName, Object... args) {
			add(actor);
			actor.addListener(new ActionOnClickListener(controller, actionName,
					args));
			return this;
		}

		public CellRow toFront() {
			widget.toFront();
			return this;
		}

		public CellRow toBack() {
			widget.toBack();
			return this;
		}
	}
}
