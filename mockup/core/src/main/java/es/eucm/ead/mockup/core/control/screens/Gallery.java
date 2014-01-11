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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.GridPanel;
import es.eucm.ead.mockup.core.view.ui.Panel;
import es.eucm.ead.mockup.core.view.ui.ToolBar;

public class Gallery extends AbstractScreen {

	private Group rest, navigationGroup;
	private ToolBar toolBar;

	@Override
	public void create() {
		setPreviousScreen(Screens.PROJECT_MENU);
		navigationGroup = UIAssets.getNavigationGroup();

		super.root = new Group();
		root.setVisible(false);

		rest = new Group();

		toolBar = new ToolBar(skin);
		//toolBar.setVisible(false);
		toolBar.right();

		String search = "Buscar por ...";//TODO use i18n!
		TextField searchtf = new TextField("", skin);
		searchtf.setMessageText(search);
		String[] orders = new String[] { "Ordenar por ...", "Ordenar por 2..." };//TODO use i18n!
		SelectBox order = new SelectBox(orders, skin);
		
		/*filter panel*/
		CheckBox cbs = new CheckBox("Escenas", skin);
		CheckBox cbe = new CheckBox("Elementos", skin); 
		CheckBox cbi = new CheckBox("Imágenes", skin);//TODO use i18n!
		Button applyFilter = new TextButton("Filtrar", skin);
		
		CheckBox[] tags = new CheckBox[]{
				new CheckBox("Hospital", skin),
				new CheckBox("Quirófano", skin),
				new CheckBox("Enfermera", skin),
				new CheckBox("Camilla", skin),
				new CheckBox("Almohada", skin),
				new CheckBox("Habitación", skin),
				new CheckBox("Vehículo", skin),
				new CheckBox("Doctor", skin),
				new CheckBox("Paciente", skin),
				new CheckBox("Guantes", skin),
				new CheckBox("Medicamentos", skin),
				new CheckBox("Médico", skin)
		};
		Table tagList = new Table(skin);
		tagList.left();
		tagList.defaults().left();
		for(int i = 0; i < tags.length; ++i){
			tagList.add(tags[i]);
			if(i < tags.length-1)
				tagList.row();
		}
		ScrollPane tagScroll = new ScrollPane(tagList, skin, "opaque");
		
		final Panel filterPanel = new Panel(skin);
		filterPanel.setVisible(false);
		final float panelw = stagew*.6f, panelx = stagew - panelw;
		filterPanel.setBounds(panelx, toolBar.getHeight(), panelw, stageh - toolBar.getHeight()*2f);
		filterPanel.add(cbe).expandX();
		filterPanel.add(cbs).expandX();
		filterPanel.add(cbi).expandX();
		filterPanel.row();
		filterPanel.add(tagScroll).fill().colspan(3).left();		
		filterPanel.row();
		filterPanel.add(applyFilter).colspan(3).expandX();	
				
		Button filterButton = new TextButton("Filtrar por tags", skin);
		ClickListener closeFilterListenerTmp = new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(filterPanel.isVisible()){
					mockupController.hide(filterPanel);
				} else {
					mockupController.show(filterPanel);
				}
			}
		};
		applyFilter.addListener(closeFilterListenerTmp);
		filterButton.addListener(closeFilterListenerTmp);
		/*end of filter panel*/
		
		
		Label nombre = new Label("Galería", skin);

		toolBar.add(nombre).expandX().left().padLeft(
				UIAssets.NAVIGATION_BUTTON_WIDTH_HEIGHT);
		toolBar.add(order);
		toolBar.add(filterButton);
		toolBar.add(searchtf).width(skin.getFont("default-font").getBounds(search).width + 50); //FIXME hardcoded fixed value

		final int COLS = 3, ROWS = 10;
		GridPanel<Actor> gridPanel = new GridPanel<Actor>(skin, ROWS, COLS,
				UIAssets.GALLERY_PROJECT_HEIGHT);
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
		ScrollPane scrollPane = new ScrollPane(gridPanel);
		scrollPane.setBounds(0, toolBar.getHeight(), stagew, stageh - 2
				* toolBar.getHeight());

		ToolBar toolBar2 = new ToolBar(skin);
		toolBar2.setY(0);
		toolBar2.add(new TextButton("Foto", skin)).expandX().left();
		toolBar2.add(new TextButton("Vídeo", skin)).expandX().right();

		rest.addActor(toolBar);
		rest.addActor(toolBar2);
		rest.addActor(scrollPane);
		rest.addActor(filterPanel);

		root.addActor(rest);
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
		Table.drawDebug(stage);
	}

	@Override
	public void hide() {
		root.setVisible(false);
		navigationGroup.setVisible(false);
	}
}
