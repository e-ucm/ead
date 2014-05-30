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
package es.eucm.ead.engine.processors.controls;

import ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.I18nTextComponent;
import es.eucm.ead.engine.components.MultiComponent;
import es.eucm.ead.engine.components.controls.TextButtonComponent;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.schema.components.controls.TextButton;

public class TextButtonProcessor extends ComponentProcessor<TextButton> {

	public GameAssets gameAssets;

	public TextButtonProcessor(GameLoop engine, GameAssets gameAssets) {
		super(engine);
		this.gameAssets = gameAssets;
	}

	@Override
	public Component getComponent(TextButton component) {
		Skin skin = gameAssets.getSkin();
		TextButtonComponent button = engine
				.createComponent(TextButtonComponent.class);
		button.set(
				gameAssets.getI18N().m(component.getText()),
				new TextButtonStyle(skin.get(component.getStyle(),
						TextButtonStyle.class)));

		I18nTextComponent textComponent = engine
				.createComponent(I18nTextComponent.class);
		textComponent.setI18nKey(component.getText());
		textComponent.setTextSetter(button);
		return new MultiComponent(button, textComponent);
	}
}
