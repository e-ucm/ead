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
package es.eucm.ead.mockup.core.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class LoadingRenderer extends ScreenRenderer {

	private NinePatch loadingBar, loadingProgress;
	private TextureAtlas atlas;
	private float xBar, yBar, wBar, hBar;
	private SpriteBatch sb;

	@Override
	public void create() {

		float hh = Gdx.graphics.getHeight() / 2f, hw = Gdx.graphics.getWidth() / 2f;
		this.wBar = hw * 1.5f;
		this.hBar = hw / 7f;
		this.xBar = hw - this.wBar / 2f;
		this.yBar = hh / 2f - hBar / 2f;
		this.atlas = new TextureAtlas("mockup/ninepatch/ninepatch.atlas");
		loadingBar = new NinePatch(atlas.findRegion("2"), 4, 4, 4, 4);
		loadingProgress = new NinePatch(atlas.findRegion("3"), 4, 4, 4, 4);

		this.sb = new SpriteBatch(10);
	}

	@Override
	public void draw() {

		sb.begin();
		loadingBar.draw(sb, xBar, yBar, wBar, hBar);
		loadingProgress.draw(sb, xBar, yBar, wBar * am.getProgress(), hBar);
		sb.end();
	}

	@Override
	public void hide() {
		sb.dispose();
		loadingBar.getTexture().dispose();
		loadingProgress.getTexture().dispose();
	}

}
