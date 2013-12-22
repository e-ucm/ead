package es.eucm.ead.mockup.core.control.handlers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.mockup.core.mockupengine.MockupEngine;
import es.eucm.ead.mockup.core.mockupengine.MockupEventListener;
import es.eucm.ead.mockup.core.model.Screens;
import es.eucm.ead.mockup.core.utils.Constants;
import es.eucm.ead.mockup.core.view.renderers.ScreenRenderer;

public class LoadingHandler extends ScreenHandler {


	private boolean engineLoaded;
	private AssetManager am;

	public LoadingHandler(AssetManager am) {
		this.am = am;
	}

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

			mockupController.changeTo(Screens.MENU);
		} 
	}

	private void initStatics() {
		if (ScreenRenderer.font == null) {
			ScreenRenderer.font = am.get(Constants.font_src, BitmapFont.class);
			ScreenRenderer.font.setScale(2f);
			ScreenRenderer.font.getRegion().getTexture().setFilter(TextureFilter.Linear,
					TextureFilter.Linear);
		}
		if (ScreenRenderer.skin == null) {
			ScreenRenderer.skin = am.get(Constants.skin_src, Skin.class);
		}
		if (!engineLoaded) {
			engineLoaded = true;
			MockupEngine engine = new MockupEngine();
			engine.setMockupEventListener(new MockupEventListener());
			engine.create();
		}
		if (ScreenHandler.stage == null) {
			ScreenHandler.stage = new Stage(Constants.SCREENW, Constants.SCREENH, true);
			ScreenRenderer.stage = ScreenHandler.stage;
		}
	}
}
