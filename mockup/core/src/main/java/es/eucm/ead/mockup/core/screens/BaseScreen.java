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
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.mockup.core.Mockup;
import es.eucm.ead.mockup.core.facade.IActionResolver;

public class BaseScreen extends InputAdapter {

	public static final float screenw = 854;
	public static final float screenh = 480;
	public static final float halfscreenw = screenw / 2f;
	public static final float halfscreenh = screenh / 2f;

	public static Mockup mockup;

	/** 
	 * A SpriteBatch is used to draw 2D rectangles that reference a texture (region). 
	 * The class will batch the drawing commands and
	 * optimize them for processing by the GPU.
	 */
	public static SpriteBatch sb;

	/** 
	 * The font consists of 2 files: an image file or {@link TextureRegion} containing the glyphs and a file in
	 * the AngleCode BMFont text format that describes where each glyph is on the image. Currently only a single image of glyphs is
	 * supported.
	 */
	public static BitmapFont font;

	/** 
	 * A Preference instance is a hash map holding different values. 
	 * It is stored alongside your application (SharedPreferences on Android, LocalStorage on GWT, 
	 * on the desktop a Java Preferences file in a ".prefs" directory will be created, and on iOS 
	 * an NSMutableDictonary will be written to the given file). CAUTION: On the desktop platform, 
	 * all libgdx applications share the same ".prefs" directory. To avoid collisions use specific 
	 * names like "com.myname.game1.settings" instead of "settings"
	 * Changes to a preferences instance will be cached in memory until flush() is invoked.
	 */
	//public static Preferences settings;

	/** 
	 * Loads and stores assets like textures, bitmapfonts, tile maps, sounds, music and so on.
	 */
	public static AssetManager am;

	/** 
	 * A camera with orthographic projection.
	 */
	public static OrthographicCamera camera = getCamera();

	/**
	 * The color used to clear the screen
	 */
	public static final Color CLEAR_COLOR = Color.BLACK;

	public static IActionResolver resolver;

	public static Stage stage;

	public static Skin skin;

	protected final String atlas_src = atlas_src_string();
	protected Vector3 touch;
	protected Table root;
	protected InputMultiplexer inputMultiplexer;

	private final String atlas_src_string() {
		String name = getClass().getSimpleName().toLowerCase();
		return "mockup/screens/" + name + "/" + name + ".atlas";
	}

	/**
	 * Updates game's logic.
	 * @param delta
	 */
	public void render(float delta) {
	}

	/**
	 * Everything is drawn here.
	 */
	public void draw() {
	}

	/**
	 * Called when the loading screen finished. 
	 */
	public void create() {
	}

	/**
	 * Called every time we enter this screen.
	 */
	public void show() {
	}

	/**
	 * Called half transition time before we leave the screen.
	 */
	public void hide() {
	}

	/**
	 * Called every time we leave this screen, just before setting new screen.
	 */
	public void onHidden() {
	}

	/**
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 */
	public void pause() {
	}

	/**
	 * Called after pause() when we come back from background.
	 */
	public void resume() {
	}

	/**
	 *  This is to maintain the aspect ratio.
	 *	If the virtual aspect ration does not match with the aspect ratio
	 *	of the hardware screen then the viewport would get scaled to
	 *	meet the size of one dimension and other would not cover full
	 *	dimension.
	 *	If we stretch it to meet the screen aspect ratio then textures will
	 *	get distorted either become fatter or elongated.
	 */
	public static OrthographicCamera getCamera() {

		float viewportWidth = screenw;
		float viewportHeight = screenh;
		float physicalWidth = Gdx.graphics.getWidth();
		float physicalHeight = Gdx.graphics.getHeight();
		float aspect = screenw / screenh;

		if (physicalWidth / physicalHeight >= aspect) {
			// Letterbox left and right.
			viewportWidth = viewportHeight * physicalWidth / physicalHeight;
		} else {
			// Letterbox above and below.
			viewportHeight = viewportWidth * physicalHeight / physicalWidth;
		}

		OrthographicCamera camera = new OrthographicCamera(viewportWidth,
				viewportHeight);
		camera.position.set(screenw / 2, screenh / 2, 0);
		camera.update();
		return camera;
	}

	/**
	 * Prepares the root to be used as an actor.
	 */
	protected void setUpRoot() {
		root = new Table();
		root.setFillParent(true);
		root.setVisible(false);
		stage.addActor(root);
		inputMultiplexer = new InputMultiplexer(stage, this, Engine.stage);
	}

	protected void clearColor() {
		GLCommon gl = Gdx.gl20;
		gl.glClearColor(CLEAR_COLOR.r, CLEAR_COLOR.g, CLEAR_COLOR.b,
				CLEAR_COLOR.a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	public static void disposeStatics() {
		font.dispose();
		font = null;

		am.dispose();
		am = null;

		sb.dispose();
		sb = null;

		stage.dispose();
		stage = null;

		skin = null;
		mockup = null;
		resolver = null;
	}
}
