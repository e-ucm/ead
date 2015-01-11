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
package es.eucm.ead.engine.android;

import android.graphics.BitmapFactory;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import es.eucm.ead.engine.EngineApplicationListener;
import es.eucm.ead.engine.assets.GameAssets;

import java.io.InputStream;

/**
 * Simple Application Listener for standalone Android mokaps. It basically
 * creates a basic imageUtils in the create() method instead of passing it as
 * argument. It is done this way to give Libgdx time to initialize graphics and
 * openGl stuff. AndroidImageUtils is not used since it seems to slow down
 * standalone games.
 * 
 * Created by jtorrente on 1/01/15.
 */
public class EngineAndroidApplicationListener extends EngineApplicationListener {
	public EngineAndroidApplicationListener() {
		super(null);
	}

	@Override
	public void create() {
		imageUtils = new GameAssets.ImageUtils() {
			@Override
			public boolean imageSize(FileHandle fileHandle, Vector2 size) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				InputStream decodedFis = fileHandle.read();
				BitmapFactory.decodeStream(decodedFis, null, options);
				int imageHeight = options.outHeight;
				int imageWidth = options.outWidth;
				size.set(imageWidth, imageHeight);
				return true;
			}

			@Override
			public boolean validSize(Vector2 size) {
				return true;
			}

			@Override
			public float scale(FileHandle src, FileHandle target) {
				src.copyTo(target);
				return 1.0f;
			}
		};
		super.create();
	}
}
