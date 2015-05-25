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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.generic.AddToArray;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.TagsList;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;

public class CreateTag extends EditorAction implements Input.TextInputListener {

	private static final String IDENTIFIER_EXPRESSION = "^[^\\d\\W]\\w*\\Z";

	private Input.TextInputListener resultListener;

	private TagsList tagsList;

	private I18N i18N;

	public CreateTag() {
		super(true, false, Input.TextInputListener.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		i18N = controller.getApplicationAssets().getI18N();
	}

	@Override
	public void perform(Object... args) {
		resultListener = (Input.TextInputListener) args[0];
		ModelEntity game = (ModelEntity) controller.getModel()
				.getResource(ModelStructure.GAME_FILE).getObject();
		tagsList = Q.getComponent(game, TagsList.class);

		Gdx.input.getTextInput(this, i18N.m("add_tag"), newTag(), "");
	}

	private void retry(String message, String tag) {
		Gdx.input.getTextInput(this, message, tag, "");
	}

	private String newTag() {
		String tag = "tag";
		int i = 1;
		while (tagsList.getTags().contains(tag, false)) {
			tag = "tag" + ++i;
		}
		return tag;
	}

	@Override
	public void input(String text) {
		text = text.trim();
		if (tagsList.getTags().contains(text, false)) {
			retry(i18N.m("duplicated_tag"), text);
		} else if (!text.equals("")) {
			controller.action(AddToArray.class, tagsList, tagsList.getTags(),
					text);
			controller.getModel().getResource(ModelStructure.GAME_FILE)
					.setModified(true);
			resultListener.input(text);
		} else {
			retry(i18N.m("invalid_tag"), text);
		}
	}

	@Override
	public void canceled() {
		resultListener.canceled();
	}
}
