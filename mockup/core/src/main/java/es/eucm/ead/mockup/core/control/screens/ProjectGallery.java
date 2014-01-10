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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.GridPanel;
import es.eucm.ead.mockup.core.view.ui.ToolBar;

public class ProjectGallery extends AbstractScreen {

	private Group rest, navigationGroup;
	private ToolBar toolBar;

	@Override
	public void create() {
		setPreviousScreen(Screens.MAIN_MENU);
		navigationGroup = UIAssets.getNavigationGroup();

		super.root = new Group();
		root.setVisible(false);

		rest = new Group();

		toolBar = new ToolBar(skin);
		toolBar.setVisible(false);
		toolBar.right();

		String search = "Buscar por nombre";//TODO use i18n!
		TextField buscar = new TextField(search, skin); 
		String[] orders = new String[]{"Ordenar por ...", "Ordenar por 2..."};//TODO use i18n!
		SelectBox ordenar = new SelectBox(orders, skin);
		Label nombre = new Label("Galería de proyectos", skin);

		toolBar.add(nombre).expandX().left().padLeft(UIAssets.NAVIGATION_BUTTON_WIDTH_HEIGHT);
		toolBar.add(ordenar);
		toolBar.add(buscar).width(400f); //FIXME fixed values could give problems if we change wirtual width/height.
		toolBar.debug();
		
		final int COLS = 3, ROWS = 10 ;
		GridPanel<Actor> gridPanel = new GridPanel<Actor>(skin, ROWS, COLS, UIAssets.GALLERY_PROJECT_HEIGHT);
		gridPanel.addItem(new TextButton("AÑADIR", skin), 0, 0);
		gridPanel.addItem(new TextButton("proyecto 1", skin), 0, 1);
		ImageButton i1 = new ImageButton(skin);
		gridPanel.addItem(i1, 0, 2);
		gridPanel.addItem(new ImageButton(skin), 1, 2);
		gridPanel.addItem(new ImageButton(skin), 1, 1);
		gridPanel.addItem(new ImageButton(skin), 2, 2);
		gridPanel.addItem(new ImageButton(skin), 3, 1);
		gridPanel.addItem(new ImageButton(skin), 6, 2);
		gridPanel.addItem(new ImageButton(skin), 7, 1);
		gridPanel.addItem(new ImageButton(skin), 8, 2);
		gridPanel.addItem(new ImageButton(skin), 9, 1);
		gridPanel.debug();
		ScrollPane scrollPane = new ScrollPane(gridPanel);
		scrollPane.setBounds(0, 0, stagew, stageh - toolBar.getHeight());

		rest.addActor(toolBar);
		rest.addActor(scrollPane);

		root.addActor(rest);
		stage.addActor(root);
	}

	@Override
	public void show() {
		super.show();
		root.setVisible(true);
		toolBar.show();
		navigationGroup.setVisible(true);
	}

	@Override
	public void act(float delta) {
		stage.act(delta);
	}

	@Override
	public void draw() {
		stage.draw();
		Table.drawDebug(stage);
	}

	@Override
	public void hide() {
		root.setVisible(false);
		navigationGroup.setVisible(false);
	}
}
