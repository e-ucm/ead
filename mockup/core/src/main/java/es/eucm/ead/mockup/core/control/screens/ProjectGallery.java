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
package es.eucm.ead.mockup.core.control.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.GridPanel;
import es.eucm.ead.mockup.core.view.ui.ToolBar;

public class ProjectGallery extends AbstractScreen {

	private Group navigationGroup;
	private ToolBar toolBar;

	@Override
	public void create() {
		setPreviousScreen(Screens.MAIN_MENU);
		navigationGroup = UIAssets.getNavigationGroup();

		super.root = new Group();
		root.setVisible(false);

		toolBar = new ToolBar(skin);
		//toolBar.setVisible(false);
		toolBar.right();

		String search = "Buscar por nombre";//TODO use i18n!
		TextField searchtf = new TextField("", skin);
		searchtf.setMessageText(search);
		searchtf.setMaxLength(search.length());
		String[] orders = new String[] { "Ordenar por ...", "Ordenar por 2..." };//TODO use i18n!
		SelectBox ordenar = new SelectBox(orders, skin);
		Label nombre = new Label("Galería de proyectos", skin);

		toolBar.add(nombre).expandX().left().padLeft(
				UIAssets.NAVIGATION_BUTTON_WIDTH_HEIGHT);
		toolBar.add(ordenar);
		toolBar.add(searchtf).width(
				skin.getFont("default-font").getBounds(search).width + 50); //FIXME hardcoded fixed value
		//toolBar.debug();

		Texture t = new Texture(Gdx.files.internal("mockup/temp/proyecto.png"));
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		final int COLS = 3, ROWS = 6;
		GridPanel<Actor> gridPanel = new GridPanel<Actor>(skin, ROWS, COLS,
				UIAssets.GALLERY_PROJECT_HEIGHT * .2f);
		gridPanel.defaults().fill().uniform();
		boolean first = true;
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLS; ++j) {
				if (first) {
					first = false;
					gridPanel.addItem(new TextButton("NUEVO", skin), 0, 0)
							.fill();
				} else {
					gridPanel.addItem(new Image(t), i, j);
				}
			}
		}
		//gridPanel.debug();
		ScrollPane scrollPane = new ScrollPane(gridPanel);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setBounds(0, 0, stagew, stageh - toolBar.getHeight());

		gridPanel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (event.getListenerActor() instanceof GridPanel) {
					exitAnimation(Screens.PROJECT_MENU);
				}
			}
		});
		root.addActor(toolBar);
		root.addActor(scrollPane);

		stage.addActor(root);
	}

	@Override
	public void show() {
		super.show();
		root.setVisible(true);
		navigationGroup.setVisible(true);
	}

	@Override
	public void act(float delta) {
		stage.act(delta);
	}

	@Override
	public void draw() {
		stage.draw();
	}

	@Override
	public void hide() {
		root.setVisible(false);
		navigationGroup.setVisible(false);
	}
}
