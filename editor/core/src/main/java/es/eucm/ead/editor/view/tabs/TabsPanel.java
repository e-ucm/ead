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
package es.eucm.ead.editor.view.tabs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.gdx.AbstractWidget;

/**
 * A panel containing some tabs
 */
public class TabsPanel extends AbstractWidget {

	private Array<TabWidget> tabs;

	protected Skin skin;

	private Drawable background;

	private TabWidget selectedTab;

	public TabsPanel(Skin skin) {
		this.tabs = new Array<TabWidget>();
		this.skin = skin;
	}

	/**
	 * Sets the background for the panel
	 */
	public void setBackground(Drawable background) {
		this.background = background;
	}

	/**
	 * Adds a new tab to the panel
	 * 
	 * @param title
	 *            the title for the tab
	 * @return the created tab
	 */
	public TabWidget addTab(String title) {
		TabWidget tabWidget = new TabWidget(skin);
		tabWidget.setTitle(title);
		tabs.add(tabWidget);
		addActor(tabWidget);
		tabWidget.toBack();
		// First tab added selected by default
		if (getChildren().size == 1) {
			setSelectedTab(tabWidget);
		}
		return tabWidget;
	}

	/**
	 * Sets the given tab as selected and unselects the current selected
	 */
	public void setSelectedTab(TabWidget selectedTab) {
		if (tabs.contains(selectedTab, true)) {
			this.selectedTab = selectedTab;
			for (TabWidget tab : tabs) {
				tab.setSelected(tab == selectedTab);
			}
		}
	}

	/**
	 * @return the current selected tab
	 */
	public TabWidget getSelectedTab() {
		return selectedTab;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		if (background != null) {
			background.draw(batch, 0, 0, getWidth(), getHeight());
		}
		super.drawChildren(batch, parentAlpha);
	}

	@Override
	public float getPrefWidth() {
		return getChildrenMaxWidth();
	}

	@Override
	public float getPrefHeight() {
		return getChildrenMaxHeight();
	}

	@Override
	public void layout() {
		float titleMargin = 0;
		float titleHeight = 0;
		for (TabWidget tab : tabs) {
			tab.setBounds(0, 0, getWidth(), getHeight());
			titleHeight = Math.max(titleHeight, tab.getTitlePrefHeight());
			tab.setTitleMargin(titleMargin);
			titleMargin += tab.getTitlePrefWidth();
		}

		for (TabWidget tab : tabs) {
			tab.setTitleHeight(titleHeight);
		}
	}
}
