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
package es.eucm.ead.editor.view.widgets.mockup.panels;

import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.esotericsoftware.tablelayout.Cell;

public class TabsPanel<T extends Button, C extends Table> extends HiddenPanel {

	private Table tabs;
	private Stack body;

	private float PREF_WIDTH;
	private float PREF_HEIGHT;
	private final Vector2 viewport;

	/**
	 * Used to keep one tab at any time.
	 */
	private ButtonGroup buttonGroup;

	/**
	 * Keeps track of the current bindings between Tabs and Containers.
	 */
	private ArrayMap<T, C> tabBind;

	private T currentTab;
	private C currentContainer;

	/**
	 * Create a {@link TabsPanel}
	 * 
	 * @param skin
	 *            the skin to use for style
	 * */
	public TabsPanel(Skin skin) {
		super(skin);
		init();
		this.viewport = null;
	}

	/**
	 * Create a {@link TabsPanel} with n buttons associated with an n tables.
	 * The first button in Array of buttons is associated with first table in
	 * Array of tables.
	 * 
	 * @param buttons
	 * @param tables
	 * @param skin
	 * @param prefW
	 * @param prefH
	 * @param viewport
	 */
	public TabsPanel(Array<C> buttons, Array<T> tables, float prefW,
			float prefH, Vector2 viewport, Skin skin) {
		super(skin);

		if (buttons.size != tables.size) {
			throw new IllegalArgumentException(
					"The number of buttons and tables do not match");
		}

		init();

		this.getTabTable().defaults().expandX().fill();

		Iterator<T> tb = tables.iterator();

		for (C i : buttons) {
			this.addBinding(tb.next(), i);
		}

		this.setCurrentTab(tables.first());

		this.viewport = viewport;
		this.PREF_WIDTH = prefW;
		this.PREF_HEIGHT = prefH;
	}

	private void init() {
		this.tabBind = new ArrayMap<T, C>();
		this.buttonGroup = new ButtonGroup();
		this.tabs = new Table();

		this.body = new Stack();

		super.add(tabs).expandX().fillX();
		this.row();
		super.add(body).expand().fill();
	}

	public Table getTabTable() {
		return tabs;
	}

	/**
	 * Add the specified {@link T tab} bound to the specified {@link C
	 * container}.
	 * 
	 * @param tab
	 *            the {@link T tab} to add
	 * @param container
	 *            the {@link C container} to add
	 * 
	 * @return Returns the Tab's cell in the upper table.
	 */
	public Cell<?> addBinding(T tab, C container) {
		tabBind.put(tab, container);
		buttonGroup.add(tab);
		tab.addListener(changeTabListener);

		body.add(container);

		return tabs.add(tab);
	}

	/**
	 * Remove the specified {@link T tab}.
	 * 
	 * @param tab
	 *            the {@link T tab} to remove
	 */
	public void removeTab(T tab) {
		T _tab = null;
		int _index = tabBind.indexOfKey(tab);
		if (_index + 1 < tabBind.size)
			_tab = tabBind.getKeyAt(_index + 1);
		else if (_index - 1 >= 0)
			_tab = tabBind.getKeyAt(_index - 1);

		buttonGroup.remove(tab);
		tabBind.getValueAt(_index).remove();
		tabBind.removeIndex(_index);
		tab.remove();

		setCurrentTab(_tab);
	}

	private void hideAllContainer() {
		for (C _cont : tabBind.values())
			_cont.setVisible(false);
	}

	/**
	 * Get all {@link T tabs}s of this {@link TabPane}.
	 * 
	 * @return all {@link T tabs}
	 */
	public ArrayMap<T, C> getTabs() {
		return tabBind;
	}

	/**
	 * Get the selected {@link C container}.
	 * 
	 * @return the selected {@link C container}
	 */
	public C getCurrentContainer() {
		return currentContainer;
	}

	/**
	 * Get the selected {@link T tab}.
	 * 
	 * @return the selected {@link T tab}
	 */
	public T getCurrentTab() {
		return currentTab;
	}

	/**
	 * Set the selected {@link T tab}.
	 * 
	 * @param currentTab
	 *            the {@link T tab} to set selected
	 */
	public void setCurrentTab(T currentTab) {
		this.currentTab = currentTab;

		if (currentTab == null)
			return;

		currentContainer = tabBind.get(currentTab);

		hideAllContainer();
		tabBind.get(currentTab).setVisible(true);
		currentTab.setChecked(true);
	}

	/**
	 * Set the selected {@ink T tab}.
	 * 
	 * @param index
	 *            the index of the {@link T tab}
	 */
	public void setCurrentTab(int index) {
		setCurrentTab(tabBind.getKeyAt(index));
	}

	/**
	 * Get the {@link C container} at the specified index. Start from 0.
	 * 
	 * @param index
	 *            position of the {@link T tab} of the {@link C container}
	 * @return the {@link C container} at the specified index
	 */
	public C getTab(int index) {
		return tabBind.getValueAt(index);
	}

	/**
	 * Get the first {@link C container} of the specified type.
	 * 
	 * @param clazz
	 *            the type of the searched {@link C container}
	 * @return the first found {@link C container} of the searched type or null
	 *         if nothing is found
	 */
	public <A extends C> A getTab(Class<A> clazz) {
		for (C _container : tabBind.values()) {
			try {
				A _cast = clazz.cast(_container);

				return _cast;
			} catch (ClassCastException e) {

			}
		}
		return null;
	}

	/**
	 * Used to change between tabs.
	 */
	private final ClickListener changeTabListener = new ClickListener() {
		@SuppressWarnings("unchecked")
		public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event,
				float x, float y) {
			setCurrentTab((T) event.getListenerActor());
		}
	};

	@Override
	public float getPrefWidth() {
		if (PREF_HEIGHT != 0 && PREF_WIDTH != 0) {
			return viewport.x * PREF_WIDTH;
		} else {
			return super.getPrefWidth();
		}
	}

	@Override
	public float getPrefHeight() {
		if (PREF_HEIGHT != 0 && PREF_WIDTH != 0) {
			return viewport.y * PREF_WIDTH;
		} else {
			return super.getPrefHeight();
		}
	}
}
