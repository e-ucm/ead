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
/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.ead.mockup.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.mockup.core.MockupEngine;

public class Loading extends BaseScreen {

	public static NinePatch loadingBar, loadingProgress;
	private TextureAtlas atlas;
	private float xBar, yBar, wBar, hBar, progress;
	private boolean engineLoaded;

	private final String font_src = "mockup/font/impact38bold.fnt";
	private final String skin_src = "mockup/skin/holo-dark-xhdpi.json";

	@Override
	public void create() {
		Gdx.input.setInputProcessor(null);

		am.load(font_src, BitmapFont.class);
		am.load(skin_src, Skin.class);

		this.wBar = halfscreenw * 1.5f;
		this.hBar = halfscreenw / 7f;
		this.xBar = halfscreenw - this.wBar / 2f;
		this.yBar = halfscreenh / 2f - hBar / 2f;
		this.atlas = new TextureAtlas("mockup/ninepatch/ninepatch.atlas");
		loadingBar = new NinePatch(atlas.findRegion("2"), 4, 4, 4, 4);
		loadingProgress = new NinePatch(atlas.findRegion("3"), 4, 4, 4, 4);

		am.load(mockup.menu.atlas_src, TextureAtlas.class);

		this.progress = 0f;
		this.engineLoaded = false;
	}

	@Override
	public void render(float delta) {
		clearColor();

		if (am.update()) {
			initStatics();

			mockup.menu.create();

			mockup.setScreen(mockup.menu);
		} else {
			this.progress = am.getProgress();
		}
	}

	private void initStatics() {
		if (font == null) {
			font = am.get(font_src, BitmapFont.class);
			font.setScale(2f);
			font.getRegion().getTexture().setFilter(TextureFilter.Linear,
					TextureFilter.Linear);
		}
		if (skin == null) {
			skin = am.get(skin_src, Skin.class);
		}
		if (!engineLoaded) {
			engineLoaded = true;
			MockupEngine engine = new MockupEngine();
			engine.create();
		}
		if (stage == null) {
			stage = new Stage(screenw, screenh, true);
		}
	}

	@Override
	public void draw() {
		sb.begin();
		loadingBar.draw(sb, xBar, yBar, wBar, hBar);
		loadingProgress.draw(sb, xBar, yBar, wBar * progress, hBar);
		sb.end();
	}

	public void dispose() {
		this.atlas.dispose();
	}

}
