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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.controls.ImageButtonComponent;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.schema.components.controls.ImageButton;

public class ImageButtonProcessor extends ComponentProcessor<ImageButton> {

	public GameAssets gameAssets;

	public ImageButtonProcessor(GameLoop engine, GameAssets gameAssets) {
		super(engine);
		this.gameAssets = gameAssets;
	}

	@Override
	public Component getComponent(ImageButton component) {
		// Create the component
		final ImageButtonComponent button = gameLoop
				.createComponent(ImageButtonComponent.class);

		// Load basic skin for the image component
		Skin skin = gameAssets.getSkin();
		final ImageButtonStyle imageButtonStyle = new ImageButtonStyle(
				skin.get(component.getStyle(), ImageButtonStyle.class));

		// If the imageUp is defined, load it and add it to style
		if (component.getImageUp() != null) {
			gameAssets.get(component.getImageUp(), Texture.class,
					new Assets.AssetLoadedCallback<Texture>() {
						@Override
						public void loaded(String fileName, Texture asset) {
							imageButtonStyle.imageUp = new TextureRegionDrawable(
									new TextureRegion(asset));
							button.set(imageButtonStyle);
						}
					});
		}

		// If the imageDown is defined, load it and add it to style
		if (component.getImageDown() != null) {
			gameAssets.get(component.getImageDown(), Texture.class,
					new Assets.AssetLoadedCallback<Texture>() {
						@Override
						public void loaded(String fileName, Texture asset) {
							imageButtonStyle.imageDown = new TextureRegionDrawable(
									new TextureRegion(asset));
							button.set(imageButtonStyle);
						}
					});
		}

		// Set component and return it
		button.set(imageButtonStyle);
		return button;
	}
}
