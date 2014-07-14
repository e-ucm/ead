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
package es.eucm.ead.editor.ui.scenes;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

public class SceneEditorToolbar extends LinearLayout {

	public SceneEditorToolbar(final GroupEditor groupEditor, Skin skin) {
		super(false);
		background(skin.getDrawable("bg-black-semitransparent"));

		LinearLayout buttons = new LinearLayout(true);
		add(buttons);

		IconButton select = createToolButton("cursor24x24", skin);
		buttons.add(select);
		select.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				groupEditor.setPanningMode(false);
			}
		});

		IconButton hand = createToolButton("hand24x24", skin);
		buttons.add(hand);
		hand.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				groupEditor.setPanningMode(true);
			}
		});

		IconButton zoomIn = createToolButton("zoomin24x24", skin);
		buttons.add(zoomIn);
		zoomIn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				groupEditor.zoomIn();
			}
		});

		IconButton zoomOut = createToolButton("zoomout24x24", skin);
		buttons.add(zoomOut);
		zoomOut.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				groupEditor.zoomOut();
			}
		});

		IconButton fit = createToolButton("fit24x24", skin);
		fit.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				groupEditor.fit(true);
			}
		});
		buttons.add(fit);
	}

	public IconButton createToolButton(String drawable, Skin skin) {
		return new IconButton(skin.getDrawable(drawable), 5, skin, "white");
	}

}
