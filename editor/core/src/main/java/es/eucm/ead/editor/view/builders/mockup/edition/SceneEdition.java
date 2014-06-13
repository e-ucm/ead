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
package es.eucm.ead.editor.view.builders.mockup.edition;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.edition.AddElementComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.AddInteractionComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.EditionComponent;
import es.eucm.ead.editor.view.widgets.mockup.scenes.MockupSceneEditor;
import es.eucm.ead.engine.I18N;

/**
 * A view that allows the user to edit scenes
 */
public class SceneEdition extends EditionWindow {

	private Container wrapper;
	private AddElementComponent comp;

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
	}

	@Override
	protected void editionComponents(Array<EditionComponent> editionComponents,
			Vector2 viewport, Controller controller, Skin skin, Table center,
			MockupSceneEditor scaledView) {

		editionComponents.add(new AddInteractionComponent(this, controller,
				skin));

		this.comp = new AddElementComponent(this, controller, skin, center,
				scaledView);
		editionComponents.add(comp);

		ToolBar topToolbar = comp.getToolbar();
		this.wrapper = new Container(topToolbar).fillX().top();
		this.wrapper.setFillParent(true);

		this.getRoot().addActor(this.wrapper);

	}

	@Override
	public Actor getView(Object... args) {

		// This is necessary in case we come from EditionElement screen
		// by pressing back button, in which case the EditContext would be the
		// edited element and the EditScene would be it's parent. In this case
		// we must update EditContext since we're going to edit it.
		Model model = controller.getModel();
		if (!(model.getEditScene() == model.getEditionContext())) {
			controller.action(EditScene.class, model.getEditScene());
		}

		return super.getView(args);
	}

	@Override
	protected String getTitle(I18N i18n) {
		return i18n.m("edition.scene");
	}
}
