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
package es.eucm.ead.editor.processors;

import ashley.core.Component;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

import es.eucm.ead.editor.components.EditableLabelComponent;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.I18nTextComponent;
import es.eucm.ead.engine.components.MultiComponent;
import es.eucm.ead.engine.processors.controls.LabelProcessor;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.controls.Label;

public class EditableLabelProccesor extends LabelProcessor {

	private Controller controller;

	public EditableLabelProccesor(GameLoop engine, GameAssets gameAssets,
			VariablesManager variablesManager, Controller controller) {
		super(engine, gameAssets, variablesManager);
		this.controller = controller;
	}

	@Override
	public Component getComponent(Label component) {
		Skin skin = gameAssets.getSkin();
		EditableLabelComponent button = gameLoop
				.createComponent(EditableLabelComponent.class);
		button.initialize(controller, component);
		button.setVariablesManager(variablesManager);

		TextFieldStyle style = skin.get(component.getStyle(),
				TextFieldStyle.class);
		TextFieldStyle styleCopy = new TextFieldStyle(style);
		button.setStyle(styleCopy);
		button.setText(gameAssets.getI18N().m(component.getText()));

		I18nTextComponent textComponent = gameLoop
				.createComponent(I18nTextComponent.class);
		textComponent.setI18nKey(component.getText());
		textComponent.setTextSetter(button);
		return new MultiComponent(button, textComponent);
	}
}
