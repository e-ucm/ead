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
package es.eucm.ead.editor.view.generic;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import es.eucm.ead.editor.control.CommandManager;
import es.eucm.ead.editor.model.ModelEvent;
import java.util.ArrayList;
import java.util.List;

public class OptionPanel implements Option {

	/**
	 * Available layout policies for the panel
	 */
	public static enum LayoutPolicy {
		/**
		 * A policy where each element is placed following the next, minimizing the size of the panel
		 */
		Flow,
		/**
		 * A policy where elements are placed next to each other, even if of different sizes
		 */
		HorizontalBlocks,
		/**
		 * A policy where elements are placed on top of each other, even if of different sizes
		 */
		VerticalBlocks,
		/**
		 * A policy where elements are stacked on top of each other, each with the same height
		 */
		VerticalEquallySpaced
	}

	private List<Option> elements;

	private String title;

	private LayoutPolicy layoutPolicy;

	private Table inner;
	private Table outer;

	private int insets;
	private Skin skin;

	private LayoutBuilder builder;

	@Override
	public WidgetGroup getControl(CommandManager manager, Skin skin) {
		this.skin = skin;
		inner = new Table();
		outer = new Table();
		ScrollPane scroll = new ScrollPane(inner);
		outer.add(scroll).fill().expand();
		outer.row();
		builder.start();
		for (Option e : getElements()) {
			builder.add(e, manager);
		}
		builder.finish();
		return outer;
	}

	public OptionPanel(String title, LayoutPolicy layoutPolicy, int insets) {
		elements = new ArrayList<Option>();
		this.title = title;
		this.layoutPolicy = layoutPolicy;
		this.insets = insets;
		switch (layoutPolicy) {
		case VerticalBlocks: {
			builder = new VerticalBlocksBuilder();
			break;
		}
		default:
			throw new IllegalArgumentException("No builder for " + layoutPolicy);
		}
	}

	public List<Option> getElements() {
		return elements;
	}

	public String getTitle() {
		return title;
	}

	public OptionPanel add(Option element) {
		elements.add(element);
		return this;
	}

	public LayoutPolicy getLayoutPolicy() {
		return layoutPolicy;
	}

	public void modelChanged(ModelEvent event) {
		for (Option ie : elements) {
			ie.modelChanged(event);
		}
	}

	public String getToolTipText() {
		return null;
	}

	// ----- layout builders here -----

	public interface LayoutBuilder {
		void start();

		void add(Option element, CommandManager manager);

		void finish();
	}

	public class VerticalBlocksBuilder implements LayoutBuilder {

		@Override
		public void start() {
		}

		@Override
		public void add(Option element, CommandManager manager) {
			Label titleLabel = new Label(element.getTitle(), skin);
			inner.add(titleLabel).expand(false, false);
			inner.add(element.getControl(manager, skin)).expand(false, false);
			inner.row();
		}

		@Override
		public void finish() {
		}
	}
}
