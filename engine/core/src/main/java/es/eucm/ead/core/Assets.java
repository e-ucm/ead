package es.eucm.ead.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.core.scene.loaders.SceneLoader;
import es.eucm.ead.core.scene.loaders.TextLoader;
import es.eucm.ead.schema.actors.Scene;

public class Assets extends AssetManager {

	private FileResolver fileResolver;

	private Skin skin;

	private BitmapFont defaultFont = new BitmapFont();

	public Assets(FileResolver fileResolver) {
		super(fileResolver);
		this.fileResolver = fileResolver;
		addAssetLoaders();
		loadSkin("default");
	}

	/**
	 * 
	 * @return returns the current skin for the UI
	 */
	public Skin getSkin() {
		return skin;
	}

	/**
	 * Loads the skin with the given name. It will be necessary to rebuild the
	 * UI to see changes reflected
	 * 
	 * @param skinName
	 *            the skin name
	 */
	public void loadSkin(String skinName) {
		String skinFile = "@skins/" + skinName + "/skin.json";
		load(skinFile, Skin.class);
		finishLoading();
		this.skin = get(skinFile);
	}

	public BitmapFont defaultFont() {
		return defaultFont;
	}

	/**
	 * Add asset loaders to load new assets
	 */
	private void addAssetLoaders() {
		// Scene Loader
		setLoader(Scene.class, new SceneLoader(fileResolver));
		// Text loader
		setLoader(String.class, new TextLoader(fileResolver));
	}
}
