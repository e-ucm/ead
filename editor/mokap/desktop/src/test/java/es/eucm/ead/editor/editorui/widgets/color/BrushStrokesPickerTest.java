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
package es.eucm.ead.editor.editorui.widgets.color;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.editorui.UITest;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.draw.BrushStrokesPicker;
import es.eucm.ead.editor.view.widgets.draw.BrushStrokesPicker.SizeEvent;
import es.eucm.ead.editor.view.widgets.draw.BrushStrokesPicker.SizeListener;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public class BrushStrokesPickerTest extends UITest {

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {

		LinearLayout container = new LinearLayout(false).background(skin
				.getDrawable(SkinConstants.DRAWABLE_LIGHT_GRAY_BG));
		IconButton iconButton = WidgetBuilder.toolbarIcon(SkinConstants.IC_ADD,
				null);
		container.add(iconButton);

		float initialValue = .7f;
		final Label size = new Label("Size: " + initialValue, skin);

		container.add(size);

		final BrushStrokesPicker contextMenu = new BrushStrokesPicker(skin,
				controller.getPreferences());
		contextMenu.addListener(new SizeListener() {
			@Override
			public void sizeChanged(SizeEvent event) {
				size.setText("Size: " + event.getCompletion());
			}
		});
		contextMenu.setSizeValue(initialValue);
		contextMenu.setPickedColor(Color.BLUE);
		contextMenu.pack();

		container.addSpace();

		iconButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.getViews().showModal(contextMenu, 0, 0);
			}
		});

		return container;
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 600;
		config.height = 500;
		config.title = "BrushStrokesPicker";
		new LwjglApplication(new BrushStrokesPickerTest(), config);
	}

}