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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.Gdx;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.editor.workers.LoadScenes;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.LoadEvent.Type;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schemax.ModelStructure;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Open the project in the given path
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> Path of the project</dd>
 * </dl>
 */
public class OpenProject extends EditorAction implements
		AssetLoadedCallback<Object> {

	public OpenProject() {
		super(true, false, new Class[] {}, new Class[] { String.class });
	}

	@Override
	public boolean validate(Object... args) {
		return super.validate(args)
				&& controller.getApplicationAssets()
						.resolve(getProjectPath(args)).exists();
	}

	@Override
	public void perform(Object... args) {
		String path = getProjectPath(args);
		controller.getEditorGameAssets().setLoadingPath(path);
		controller.getModel().reset();
		controller.getEditorGameAssets().get(ModelStructure.GAME_FILE,
				Object.class, this);
	}

	private String getProjectPath(Object... args) {
		return (String) (args.length == 1 ? args[0] : controller.getModel()
				.getSelection().getSingle(Selection.RESOURCE));
	}

	@Override
	public void loaded(String fileName, Object asset) {
		controller.getModel().putResource(ModelStructure.GAME_FILE,
				ResourceCategory.GAME, asset);
		controller.getModel().notify(
				new LoadEvent(Type.LOADED, controller.getModel()));
		controller.getPreferences().putString(Preferences.LAST_OPENED_GAME,
				controller.getEditorGameAssets().getLoadingPath());
		controller.getPreferences().flush();
		controller.action(SetSelection.class, null, Selection.RESOURCE,
				ModelStructure.GAME_FILE);
		controller.action(SetSelection.class, Selection.RESOURCE,
				Selection.MOKAP, asset);
		controller.action(SetSelection.class, Selection.MOKAP,
				Selection.MOKAP_RESOURCE);
		controller.action(LoadScenes.class);
	}

	@Override
	public void error(String fileName, Class type, Throwable exception) {
		Gdx.app.error("OpenProject", "Impossible to open game", exception);
	}
}
