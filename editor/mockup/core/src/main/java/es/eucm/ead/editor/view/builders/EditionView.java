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
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController.BackListener;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.transitions.Transitions;
import es.eucm.ead.editor.view.builders.gallery.ScenesView;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.editionview.InfoEditionPanel;
import es.eucm.ead.editor.view.widgets.editionview.LeftEditionToolbar;
import es.eucm.ead.editor.view.widgets.editionview.MockupSceneEditor;
import es.eucm.ead.editor.view.widgets.editionview.NavigationButton;
import es.eucm.ead.editor.view.widgets.editionview.TopEditionToolbar;
import es.eucm.ead.editor.view.widgets.editionview.draw.PaintToolbar;
import es.eucm.ead.editor.view.widgets.helpmessage.sequence.EditionViewHelp;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

public class EditionView implements ViewBuilder, BackListener {

	private static final String TOP_STYLE = "white_top",
			LEFT_STYLE = "white_left";

	private Table view;

	private MockupSceneEditor sceneEditor;

	private PaintToolbar paintToolbar;
	private InfoEditionPanel infoPanel;
	private Controller controller;

	private Table top;

	private Table bottom;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;

		Skin skin = controller.getApplicationAssets().getSkin();

		view = new Table();
		view.setFillParent(true);

		top = new Table();
		bottom = new Table();

		sceneEditor = new MockupSceneEditor(controller, LEFT_STYLE, TOP_STYLE);

		paintToolbar = new PaintToolbar(sceneEditor, controller);

		final NavigationButton union = new NavigationButton(skin, controller,
				top, bottom);

		final TopEditionToolbar topBar = new TopEditionToolbar(controller,
				TOP_STYLE, paintToolbar, 0f, 0f, sceneEditor, top, bottom);
		final Actor leftBar = new LeftEditionToolbar(controller, LEFT_STYLE,
				0f, 0f);

		top.add(union).fill();
		top.add(topBar).expandX().fill();
		bottom.add(leftBar).left().expandY().fill();
		Cell sceneEditorCell = bottom.add(sceneEditor).expand().fill();

		view.add(top).expandX().fill();
		view.row();
		view.add(bottom).expand().fill();
		sceneEditor.toBack();
		bottom.toBack();

		infoPanel = new InfoEditionPanel(controller, skin, sceneEditorCell,
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

	@Override
	public void onBackPressed() {
		controller.action(ChangeMockupView.class, ScenesView.class,
				Transitions.getFadeSlideTransition(top, bottom, false));
	}
}
