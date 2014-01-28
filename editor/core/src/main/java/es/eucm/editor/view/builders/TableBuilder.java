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
package es.eucm.editor.view.builders;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.XmlReader.Element;

import es.eucm.editor.view.ViewBuilder;
import es.eucm.editor.view.ViewFactory;
import es.eucm.ead.editor.view.widgets.Row;
import es.eucm.ead.editor.view.widgets.Table;
import es.eucm.ead.editor.view.widgets.Table.CellRow;

public class TableBuilder extends ViewBuilder {

	public TableBuilder(ViewFactory viewFactory) {
		super(viewFactory);
	}

	@Override
	public Actor buildView(Element element, Skin skin) {
		Table table = new Table();
		for (Element child : element.getChildrenByName("row")) {
			Row row = new Row();
			CellRow cellRow = table.addRow(row);
			setClasses(cellRow, row, child);
			setSize(cellRow, child);
			for (int i = 0; i < child.getChildCount(); i++) {
				Element rowChild = child.getChild(i);
				row.addActor(viewFactory.build(rowChild, skin));
			}
		}
		return table;
	}

	private void setClasses(CellRow cellRow, Row row, Element element) {
		try {
			String[] classes = element.get("class").split(" ");
			for (String clazz : classes) {
				if ("expandY".equals(clazz)) {
					cellRow.expandY();
				} else if ("left".equals(clazz)) {
					cellRow.left();
				} else if ("right".equals(clazz)) {
					cellRow.right();
				} else if ("center".equals(clazz)) {
					cellRow.center();
				} else if ("uniform".equals(clazz)) {
					row.uniform();
				}
			}
		} catch (GdxRuntimeException e) {

		}
	}

	private void setSize(CellRow cellRow, Element element) {
		try {
			cellRow.percentWidth(Float.parseFloat(element.get("width")));
		} catch (Exception e) {

		}
	}
}
