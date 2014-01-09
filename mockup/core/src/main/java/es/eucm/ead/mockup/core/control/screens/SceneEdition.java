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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.Panel;
import es.eucm.ead.mockup.core.view.ui.ToolBar;
import es.eucm.ead.mockup.core.view.ui.components.PaintComponent;

public class SceneEdition extends AbstractScreen {

	private Group rest;
	private ToolBar toolBar;
	private PaintComponent paint;

	@Override
	public void create() {
		setPreviousScreen(Screens.PROJECT_MENU);

		super.root = new Group();
		root.setVisible(false);

		rest = new Group();

		toolBar = new ToolBar(skin);
		toolBar.setVisible(false);

		Button move = new TextButton("Mover", skin);
		//Button paint = new TextButton("Pintar", skin);
		paint= new PaintComponent(skin);
		Button remove = new TextButton("Borrar", skin);
		Button text = new TextButton("Texto", skin);
		Button inter = new TextButton("Zonas Int.", skin);
		Button add = new TextButton("Añadir", skin);
		Button effect = new TextButton("Efectos", skin);
		Button more = new TextButton("...", skin);
		
		ImageButton frames = new ImageButton(skin);
		frames.setX(AbstractScreen.stagew-frames.getWidth());
		
		toolBar.debug();
		toolBar.add(move);
		toolBar.add(paint.getButton());
		toolBar.add(remove);
		toolBar.add(text);
		toolBar.add(inter);
		toolBar.add(add);
		toolBar.add(effect);
		toolBar.add(more);

		rest.addActor(toolBar);
		rest.addActor(frames);
		root.addActor(paint.getPanel());

		root.addActor(rest);
		stage.addActor(root);
	}

	@Override
	public void show() {
		super.show();
		root.setVisible(true);
		toolBar.show();
		UIAssets.getNavigationGroup().setVisible(true);
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
		UIAssets.getNavigationGroup().setVisible(false);
	}
	
	private Panel paintPanel(){
		Panel paint = new Panel(skin, "default");
		float w = AbstractScreen.stagew * .3f;
		paint.setX(-w);
		paint.setY(AbstractScreen.stageh * .03f);
		paint.setBounds(paint.getX(), paint.getX(), w, AbstractScreen.stageh * .85f);
		paint.setVisible(false);
		paint.setColor(Color.ORANGE);
		paint.setModal(false);
		
		Label cbs1 = new Label("PINTAR", skin);

		Table t = new Table();
		t.add(cbs1);
		
		paint.add(t);
		
		return paint;
	}
}
