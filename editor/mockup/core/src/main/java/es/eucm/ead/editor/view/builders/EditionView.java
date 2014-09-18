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
package es.eucm.ead.editor.view.builders;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.editionview.InfoEdtionPanel;
import es.eucm.ead.editor.view.widgets.editionview.LeftEditionToolbar;
import es.eucm.ead.editor.view.widgets.editionview.MockupSceneEditor;
import es.eucm.ead.editor.view.widgets.editionview.NavigationButton;
import es.eucm.ead.editor.view.widgets.editionview.TopEditionToolbar;
import es.eucm.ead.editor.view.widgets.editionview.draw.PaintToolbar;
import es.eucm.ead.editor.view.widgets.helpmessage.sequence.EditionViewHelp;

public class EditionView implements ViewBuilder {

	private static final float SMALL_PAD = BaseGallery.SMALL_PAD * .6f,
			NORMAL_PAD = SMALL_PAD * 4F, HEIGHT = BaseGallery.TOOLBAR_SIZE,
			ICON_SIZE = BaseGallery.ICON_SIZE, ICON_PAD = BaseGallery.ICON_PAD;
	private static final String TOP_STYLE = "white_top",
			LEFT_STYLE = "white_left";

	private Table view;

	private MockupSceneEditor sceneEditor;

	private PaintToolbar paintToolbar;
	private InfoEdtionPanel infoPanel;

	@Override
	public void initialize(Controller controller) {
		float viewportY = controller.getPlatform().getSize().y;
		float toolbarSize = viewportY * HEIGHT;
		float iconSize = viewportY * ICON_SIZE;
		float iconPad = viewportY * ICON_PAD;

		Skin skin = controller.getApplicationAssets().getSkin();

		view = new Table();
		view.setFillParent(true);

		sceneEditor = new MockupSceneEditor(controller, LEFT_STYLE, TOP_STYLE);

		paintToolbar = new PaintToolbar(iconSize, iconPad, sceneEditor,
				controller);

		final NavigationButton union = new NavigationButton(skin, controller,
				toolbarSize);

		final TopEditionToolbar topBar = new TopEditionToolbar(controller,
				TOP_STYLE, toolbarSize, iconSize, iconPad, paintToolbar,
				SMALL_PAD, NORMAL_PAD);
		final Toolbar leftBar = new LeftEditionToolbar(controller, LEFT_STYLE,
				toolbarSize, iconSize, iconPad, SMALL_PAD, NORMAL_PAD);

		view.add(union);
		view.add(topBar).expandX().fill();
		view.row();
		view.add(leftBar).left().expandY().fill();
		Cell sceneEditorCell = view.add(sceneEditor).expand().fill();
		sceneEditor.toBack();

		infoPanel = new InfoEdtionPanel(controller, skin, sceneEditorCell,
				paintToolbar);

		((MockupViews) controller.getViews())
				.registerHelpMessage(new EditionViewHelp(controller, this,
						topBar, leftBar));
	}

	@Override
	public void release(Controller controller) {
		sceneEditor.release();
		paintToolbar.hide();
	}

	@Override
	public Actor getView(Object... args) {
		infoPanel.show();
		sceneEditor.prepare();
		return view;
	}

}
