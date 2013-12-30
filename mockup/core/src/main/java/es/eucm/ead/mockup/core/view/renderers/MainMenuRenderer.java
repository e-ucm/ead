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
package es.eucm.ead.mockup.core.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.model.Screens;
import es.eucm.ead.mockup.core.view.ui.CircularGroup;
import es.eucm.ead.mockup.core.view.ui.components.OptionsPane;

public class MainMenuRenderer extends ScreenRenderer {

	public static Group optionsGroup;
	private CircularGroup cg;

	@Override
	public void create() {

		optionsGroup = new Group();
		optionsGroup.setVisible(false);
		optionsGroup.setZIndex(0);

		final OptionsPane p = new OptionsPane(skin, "dialog");
		final Button options = new ImageButton(skin);
		options.setBounds(stagew - 100, stageh - 100, 90, 90);
		options.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.cancel();
				if (!p.isVisible()) {
					mockupController.show(p);
				} else {
					mockupController.hide(p);
				}
			}
		});
		
		Image i = new Image(new Texture(Gdx.files
				.internal("mockup/temp/image.png")));
		i.setTouchable(Touchable.disabled);
		i.setBounds(halfstagew - 100, halfstageh - 100, 200, 200);

		optionsGroup.addActor(i);
		optionsGroup.addActor(p);
		optionsGroup.addActor(options);

		Button t1 = new TextButton("Nuevo Proyecto", skin, "default-thin");
		t1.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				mockupController.changeTo(Screens.PROJECT_MENU);
			}
		});
		Button t2 = new TextButton("Galería de Proyectos", skin);
		Button t3 = new TextButton("Grabar Video", skin);
		Button t4 = new TextButton("Tomar Foto", skin);

		cg = new CircularGroup(halfstageh, 135, 360, true, t1,
				t2, t3, t4);
		cg.setVisible(false);
		cg.setX(halfstagew);
		cg.setY(halfstageh);

		stage.addActor(cg);
		stage.addActor(optionsGroup);
	}

	@Override
	public void show() {
		cg.setVisible(true);
		optionsGroup.setVisible(true);
	}

	@Override
	public void draw() {
		stage.draw();
	}

	@Override
	public void hide() {
		cg.setVisible(false);
		optionsGroup.setVisible(false);
	}
}
