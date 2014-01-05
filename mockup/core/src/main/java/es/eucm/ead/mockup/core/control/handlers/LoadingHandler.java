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
package es.eucm.ead.mockup.core.control.handlers;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.mockup.core.engine.MockupEngine;
import es.eucm.ead.mockup.core.engine.MockupEventListener;
import es.eucm.ead.mockup.core.model.Screens;
import es.eucm.ead.mockup.core.utils.Constants;
import es.eucm.ead.mockup.core.view.UIAssets;

public class LoadingHandler extends ScreenHandler {

	private boolean engineLoaded;

	@Override
	public void create() {

		am.load(Constants.font_src, BitmapFont.class);
		am.load(Constants.skin_src, Skin.class);
		this.engineLoaded = false;
	}

	@Override
	public void act(float delta) {
		if (am.update()) {
			initStatics();

			mockupController.create();

			mockupController.changeTo(Screens.MAIN_MENU);
			
			UIAssets.addActors();
		}
	}

	private void initStatics() {
		if (font == null) {
			font = am.get(Constants.font_src, BitmapFont.class);
			font.setScale(2f);
			font.getRegion().getTexture().setFilter(TextureFilter.Linear,
					TextureFilter.Linear);
		}
		if (skin == null) {
			skin = am.get(Constants.skin_src, Skin.class);
		}
		if (!engineLoaded) {
			engineLoaded = true;
			MockupEngine engine = new MockupEngine();
			engine.setMockupEventListener(new MockupEventListener());
			engine.create();
		}
		if (stage == null) {
			stage = new Stage(Constants.SCREENW, Constants.SCREENH, true);
			stageh = stage.getHeight();
			stagew = stage.getWidth();
			halfstageh = stageh / 2f;
			halfstagew = stagew / 2f;
		}
		if(!UIAssets.isCreated()){
			UIAssets.create();
		}
	}
}
