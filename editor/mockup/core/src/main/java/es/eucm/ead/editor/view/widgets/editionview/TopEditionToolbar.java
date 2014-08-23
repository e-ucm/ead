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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Toolbar;

public class TopEditionToolbar extends Toolbar {

	private static final float BIG_PAD = 160, NORMAL_PAD = 40, SMALL_PAD = 20;

	private float height;

	public TopEditionToolbar(Controller controller, String style, float height,
			float iconSize, float PAD) {
		super(controller.getApplicationAssets().getSkin(), style);

		Skin skin = controller.getApplicationAssets().getSkin();

		this.height = height;

		align(Align.right);

		// TODO change for widgets
		IconButton play = new IconButton("play80x80", 0, skin);

		IconButton undo = new IconButton("undo80x80", 0, skin);
		IconButton redo = new IconButton("redo80x80", 0, skin);

		IconButton camera = new IconButton("camera80x80", 0, skin);
		IconButton repository = new IconButton("repository80x80", 0, skin);
		IconButton android = new IconButton("android_gallery80x80", 0, skin);

		IconButton paint = new IconButton("paint80x80", 0, skin);
		IconButton text = new IconButton("text80x80", 0, skin);

		IconButton zones = new IconButton("interactive80x80", 0, skin);
		IconButton gate = new IconButton("gateway80x80", 0, skin);

		IconButton others = new IconButton("others80x80", 0, skin);

		Image logo = new Image(skin, "eAdventure");
		add(logo).size(iconSize * 4.5f, iconSize).padRight(NORMAL_PAD * 2);

		add(play).size(iconSize).padRight(BIG_PAD);

		add(undo).size(iconSize).pad(SMALL_PAD);
		add(redo).size(iconSize).padRight(BIG_PAD);

		add(camera).size(iconSize).padRight(SMALL_PAD);
		add(repository).size(iconSize).padRight(SMALL_PAD);
		add(android).size(iconSize).padRight(NORMAL_PAD);

		add(paint).size(iconSize).padRight(SMALL_PAD);
		add(text).size(iconSize).padRight(NORMAL_PAD);

		add(zones).size(iconSize).padRight(SMALL_PAD);
		add(gate).size(iconSize).padRight(NORMAL_PAD);

		add(others).size(iconSize).padRight(SMALL_PAD);
	}

	@Override
	public float getPrefHeight() {
		return height;
	}
}