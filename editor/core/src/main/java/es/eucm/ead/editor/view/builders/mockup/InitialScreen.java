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
package es.eucm.ead.editor.view.builders.mockup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ChangeSkin;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.control.actions.CombinedAction;
import es.eucm.ead.editor.control.actions.NewGame;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.classic.MainBuilder;
import es.eucm.ead.editor.view.widgets.Window;
import es.eucm.ead.editor.view.widgets.mockup.Options;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.engine.I18N;

public class InitialScreen implements ViewBuilder {

	private final FileHandle MOCKUP_PROJECT_FILE = Gdx.files
			.external("/eAdventureMockup/");

	public static final String NAME = "mockup_initial";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {
		Skin skin = controller.getEditorAssets().getSkin();
		I18N i18n = controller.getEditorAssets().getI18N();
		
		final String IC_NEWPROJECT = "ic_newproject", IC_GALLERY = "ic_gallery";
		Button newProjectButton = new MenuButton(
				i18n.m("general.mockup.new-project"), skin, IC_NEWPROJECT,
				controller, CombinedAction.NAME, NewGame.NAME,
				new Object[] { MOCKUP_PROJECT_FILE.path() }, ChangeView.NAME,
				new Object[] { ProjectScreen.NAME });
		Button projectGallery = new MenuButton(
				i18n.m("general.mockup.project-gallery"), skin, IC_GALLERY,
				controller, CombinedAction.NAME, ChangeSkin.NAME,
				new Object[] { "default" }, ChangeView.NAME,
				new Object[] { MainBuilder.NAME });
		
		Options opt = new Options(controller, skin);
		
		Window window = new Window();
		Group root = window.root(new Group());
		
		Table table = new Table();
		table.defaults().expand();
		table.setFillParent(true);
		table.add(newProjectButton);
		table.add(projectGallery);
		
		Table table2 = new Table();
		table2.defaults().expand();
		table2.setFillParent(true);
		table2.add(opt).fill().expand();
		
		root.addActor(table);
		root.addActor(table2);
		
		return window;
	}
}
