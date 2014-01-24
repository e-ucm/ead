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
package es.eucm.ead.editor.view.options;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import es.eucm.ead.editor.control.CommandManager;
import es.eucm.ead.editor.model.EditorModel.ModelListener;
import es.eucm.ead.editor.model.ModelEvent;

import java.util.ArrayList;
import java.util.List;

public class OptionsPanel implements ModelListener {

	/**
	 * Available layout policies for the panel
	 */
	public static enum LayoutPolicy {
		/**
		 * A policy where each element is placed following the next, minimizing
		 * the size of the panel
		 */
		Flow,
		/**
		 * A policy where options are placed next to each other, even if of
		 * different sizes
		 */
		HorizontalBlocks,
		/**
		 * A policy where options are placed on top of each other, even if of
		 * different sizes
		 */
		VerticalBlocks,
		/**
		 * A policy where options are stacked on top of each other, each with
		 * the same height
		 */
		VerticalEquallySpaced
	}

	private List<Option> options;

	private LayoutPolicy layoutPolicy;

	private LayoutBuilder builder;

	public Table getControl(CommandManager manager, Skin skin) {
		Table table = new Table();
		for (Option e : getOptions()) {
			builder.addRow(table, e, manager, skin);
		}
		return table;
	}

	public OptionsPanel(LayoutPolicy layoutPolicy) {
		options = new ArrayList<Option>();
		this.layoutPolicy = layoutPolicy;
		switch (layoutPolicy) {
		case VerticalBlocks: {
			builder = new VerticalBlocksBuilder();
			break;
		}
		default:
			throw new IllegalArgumentException("No builder for " + layoutPolicy);
		}
	}

	public List<Option> getOptions() {
		return options;
	}

	public OptionsPanel add(Option element) {
		options.add(element);
		return this;
	}

	public LayoutPolicy getLayoutPolicy() {
		return layoutPolicy;
	}

	public void modelChanged(ModelEvent event) {
		for (Option ie : options) {
			ie.modelChanged(event);
		}
	}

	// ----- layout builders here -----

	public interface LayoutBuilder {
		void addRow(Table table, Option element, CommandManager manager,
				Skin skin);
	}

	public class VerticalBlocksBuilder implements LayoutBuilder {

		private float pad = 10;

		@Override
		public void addRow(Table table, Option option, CommandManager manager,
				Skin skin) {
			Label titleLabel = new Label(option.getTitle(), skin);
			table.row();
			table.add(titleLabel).left().pad(pad);
			table.add(option.getControl(manager, skin)).left().pad(pad);
		}

	}
}
