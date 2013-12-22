package es.eucm.ead.mockup.core.view.renderers;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import es.eucm.ead.mockup.core.utils.Constants;

public class LoadingRenderer extends ScreenRenderer {

	public static NinePatch loadingBar, loadingProgress;
	private TextureAtlas atlas;
	private float xBar, yBar, wBar, hBar, progress;
	private SpriteBatch sb;

	@Override
	public void create() {

		this.wBar = Constants.HALFSCREENW * 1.5f;
		this.hBar = Constants.HALFSCREENW / 7f;
		this.xBar = Constants.HALFSCREENW - this.wBar / 2f;
		this.yBar = Constants.HALFSCREENH / 2f - hBar / 2f;
		this.atlas = new TextureAtlas("mockup/ninepatch/ninepatch.atlas");
		loadingBar = new NinePatch(atlas.findRegion("2"), 4, 4, 4, 4);
		loadingProgress = new NinePatch(atlas.findRegion("3"), 4, 4, 4, 4);

		this.progress = 0f;
		this.sb = new SpriteBatch(10);
	}

	@Override
	public void draw() {

		this.progress = am.getProgress();
		sb.begin();
		loadingBar.draw(sb, xBar, yBar, wBar, hBar);
		loadingProgress.draw(sb, xBar, yBar, wBar * progress, hBar);
		sb.end();
	}
	
	@Override
	public void hide() {
		sb.dispose();
	}

}
