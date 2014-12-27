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
import com.badlogic.gdx.Input.TextInputListener;

import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * <p>
 * Renames the entity in the givencontext
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> name of the context where the
 * entity to rename is</dd>
 * </dl>
 **/
public class Rename extends EditorAction implements TextInputListener {

	private ModelEntity modelEntity;

	private Resource resource;

	public Rename() {
		super(true, false, String.class);
	}

	@Override
	public void perform(Object... args) {
		modelEntity = (ModelEntity) controller.getModel().getSelection()
				.getSingle((String) args[0]);
		resource = null;
		if (modelEntity != null) {
			I18N i18n = controller.getApplicationAssets().getI18N();
			resource = controller.getModel().getResourceFromObject(modelEntity);
			Gdx.input.getTextInput(this, i18n.m("change_title"),
					Q.getTitle(modelEntity), "");
		}
	}

	@Override
	public void input(String text) {
		Documentation doc = Q.getComponent(modelEntity, Documentation.class);
		controller.action(SetField.class, doc, FieldName.NAME, text);
		if (resource != null) {
			resource.setModified(true);
		}
		Gdx.graphics.requestRendering();
	}

	@Override
	public void canceled() {

	}
}
