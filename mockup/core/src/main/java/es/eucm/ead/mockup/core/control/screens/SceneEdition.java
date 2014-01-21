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

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.ToolBar;
import es.eucm.ead.mockup.core.view.ui.ToolbarButton;
import es.eucm.ead.mockup.core.view.ui.components.AddComponent;
import es.eucm.ead.mockup.core.view.ui.components.DrawComponent;
import es.eucm.ead.mockup.core.view.ui.components.DrawComponent.Type;
import es.eucm.ead.mockup.core.view.ui.components.EffectsComponent;
import es.eucm.ead.mockup.core.view.ui.components.InteractiveComponent;

public class SceneEdition extends AbstractScreen {

	private ToolBar toolBar;
	private DrawComponent paint;
	private DrawComponent delete;
	private DrawComponent text;
	private InteractiveComponent interac;
	private AddComponent add;
	private EffectsComponent effect;
	
	private float TOOLBAR_ICON_HEIGHT;
	private float TOOLBAR_ICON_WIDTH;

	@Override
	public void create() {
		setPreviousScreen(Screens.PROJECT_MENU);
		this.TOOLBAR_ICON_HEIGHT = UIAssets.TOOLBAR_HEIGHT; 
		this.TOOLBAR_ICON_WIDTH = TOOLBAR_ICON_HEIGHT*1.5f; 
		super.root = new Group();
		root.setVisible(false);

		toolBar = new ToolBar(skin);
		toolBar.right();
		//toolBar.setBounds(0, AbstractScreen.stageh * .9f, AbstractScreen.stagew, AbstractScreen.stageh * .1f);

		Button move = new ToolbarButton(skin.getDrawable("ic_move"),"MOVER", skin);

		paint = new DrawComponent("ic_pencil","PINTAR", skin, "Herramienta de pincel",
				Type.BRUSH, 350, 550);
		delete = new DrawComponent("ic_eraser","BORRAR", skin, "Herramienta de goma",
				Type.RUBBER, 350, 250);
		text = new DrawComponent("ic_text", "TEXTO", skin, "Herramienta de escribir",
				Type.TEXT, 350, 550);
		interac = new InteractiveComponent("ic_select", "ZONAS", skin, 
				"Añadir zona interactiva", 250, 390);
		add = new AddComponent("tree_plus", "AÑADIR", skin, "Añadir a la escena:", 250, 390);
		effect = new EffectsComponent( "ic_effects","EFECTOS", skin,  "Añadir efectos de imagen", 300, 400);

		Button more = new ToolbarButton(skin.getDrawable("ic_more"),"OTROS", skin);

		Button frames = new ImageButton(skin);
		frames.setX(AbstractScreen.stagew - frames.getWidth());

		//toolBar.setVisible(false);

		//toolBar.debug();
		toolBar.defaults().size(TOOLBAR_ICON_HEIGHT).width(TOOLBAR_ICON_WIDTH);
		toolBar.add(move);
		toolBar.add(paint.getButton());
		toolBar.add(delete.getButton());
		toolBar.add(text.getButton());
		toolBar.add(interac.getButton());
		toolBar.add(add.getButton());
		toolBar.add(effect.getButton());
		toolBar.add(more);
		/*Does the actors in toolBar update their coordinates*/
		toolBar.invalidate();
		toolBar.validate();

		root.addActor(toolBar);
		root.addActor(frames);

		root.addActor(paint.getPanel());
		delete.actCoordinates();
		root.addActor(delete.getPanel());
		paint.actCoordinates();
		root.addActor(text.getPanel());
		text.actCoordinates();
		root.addActor(interac.getPanel());
		interac.actCoordinates();
		root.addActor(add.getPanel());
		add.actCoordinates();
		root.addActor(effect.getPanel());
		effect.actCoordinates();

		stage.addActor(root);
	}

	@Override
	public void show() {
		super.show();
		root.setVisible(true);
		//toolBar.show();
		UIAssets.getNavigationGroup().setVisible(true);
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
		UIAssets.getNavigationGroup().setVisible(false);
	}
}
