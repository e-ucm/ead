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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController.BackListener;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.transitions.Transitions;
import es.eucm.ead.editor.view.builders.gallery.ScenesView;
import es.eucm.ead.editor.view.widgets.baseview.BaseView;
import es.eucm.ead.editor.view.widgets.baseview.BaseView.BaseViewStyle;
import es.eucm.ead.editor.view.widgets.editionview.MockupSceneEditor;
import es.eucm.ead.editor.view.widgets.editionview.composition.CompositionToolbar;

public class EditionView implements ViewBuilder, BackListener {

	private BaseView view;

	private MockupSceneEditor sceneEditor;

	private Controller controller;
	private Skin skin;

	private CompositionToolbar composition;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;
		this.skin = controller.getApplicationAssets().getSkin();

		Drawable blank = skin.getDrawable("blank");
		BaseViewStyle style = new BaseViewStyle();
		style.navigationBackground = blank;
		style.navigationBackgroundAlpha = 0.5f;

		view = new BaseView(style);
		view.setFillParent(true);

		sceneEditor = new MockupSceneEditor(controller);

		composition = new CompositionToolbar(controller, sceneEditor);

		view.setToolbar(composition);
		sceneEditor.setFillParent(true);
		view.addActor(sceneEditor);

		sceneEditor.toBack();
	}

	@Override
	public void release(Controller controller) {
		sceneEditor.release();
		composition.getPaintToolbar().hide();
		composition.release();
	}

	@Override
	public Actor getView(Object... args) {
		sceneEditor.prepare();
		composition.showSimple();

		return view;
	}

	@Override
	public void onBackPressed() {
		controller.action(ChangeMockupView.class, ScenesView.class,
				Transitions.getFadeSlideTransition(composition, view, false));
	}
}
