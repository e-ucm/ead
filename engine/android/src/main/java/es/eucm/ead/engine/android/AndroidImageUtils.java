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

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import es.eucm.ead.engine.assets.GameAssets.ImageUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.IntBuffer;

public class AndroidImageUtils implements ImageUtils {

	private static final String TAG = "ImageUtils";

	private int maxSize = -1;

	public AndroidImageUtils() {
		// We do this to make sure it is called in a opengl thread
		maxSize();
	}

	@Override
	public boolean imageSize(FileHandle fileHandle, Vector2 size) {
		BitmapFactory.Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fileHandle.file().getAbsolutePath(), options);
		size.set(options.outWidth, options.outHeight);
		return true;
	}

	private int maxSize() {
		if (maxSize == -1) {
			IntBuffer intBuffer = BufferUtils.newIntBuffer(16);
			Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, intBuffer);
			maxSize = Math
					.min(intBuffer.get(0),
							Math.max(Gdx.graphics.getHeight(),
									Gdx.graphics.getWidth()));
		}
		return maxSize;
	}

	@Override
	public boolean validSize(Vector2 size) {
		return size.x < maxSize() && size.y < maxSize();
	}

	@Override
	public float scale(FileHandle src, FileHandle target) {
		return decodeFile(src, maxSize(), maxSize(), target);
	}

	/**
	 * Decodes an image and scales it to reduce memory consumption if its width
	 * or height is higher than the provided {@code targetWidth} or
	 * {@code targetHeight} respectively. The scaling is done without loading
	 * the image into video memory. Also, the scaling keeps the aspect ratio of
	 * the source {@code file}.
	 * 
	 * @param file
	 *            the source file to be decoded and possibly scaled.
	 * @param targetWidth
	 *            the maximum width of the resulting scaled file.
	 * @param targetHeight
	 *            the maximum height of the resulting scaled file.
	 * @param result
	 *            the destination of the scaled image. If null the source file
	 *            will be overridden. If no scaling is needed the source
	 *            {@code file} is copied to {@code result}.
	 * @return true if the scaling has been successful, false otherwise.
	 */
	public float decodeFile(FileHandle file, int targetWidth, int targetHeight,
			FileHandle result) {
		if (result == null) {
			result = file;
		}
		InputStream decodedFis = null;
		InputStream scaledFis = null;
		OutputStream savedFos = null;
		Bitmap tempBmap = null;
		try {

			// Decode image size
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			decodedFis = file.read();
			BitmapFactory.decodeStream(decodedFis, null, bmOptions);

			// Find the correct scale value.
			// It should be the power of 2.
			int scale = 1;
			int tmpWidth = bmOptions.outWidth;
			int tmpHeight = bmOptions.outHeight;
			while (tmpWidth > targetWidth || tmpHeight > targetHeight) {
				tmpWidth = (int) (tmpWidth * .5f);
				tmpHeight = (int) (tmpHeight * .5f);
				scale *= 2;
			}

			if (scale != 1) {
				// Decode with inSampleSize
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scale;
				scaledFis = file.read();
				tempBmap = BitmapFactory.decodeStream(scaledFis, null,
						bmOptions);

				savedFos = result.write(false);
				String extension = result.extension();
				tempBmap.compress(
						(extension.equalsIgnoreCase("jpg") || extension
								.equalsIgnoreCase("jpeg")) ? CompressFormat.JPEG
								: CompressFormat.PNG, 90, savedFos);
				savedFos.flush();
				Gdx.app.log(TAG, "Scaling image, scalingFactor:  " + scale);
			} else if (file != result) {
				file.copyTo(result);
			}
			Gdx.app.log(TAG, "New image saved! " + file.path());
			return scale;
		} catch (FileNotFoundException fnfex) {
			Gdx.app.error(TAG, "File not found! ", fnfex);
		} catch (IOException ioex) {
			Gdx.app.error(TAG, "I/O error! ", ioex);
		} catch (GdxRuntimeException grex) {
			Gdx.app.error(TAG, "The file handle represents a directory, "
					+ "doesn't exist, or could not be read! ", grex);
		} finally {
			if (tempBmap != null) {
				tempBmap.recycle();
			}
			StreamUtils.closeQuietly(decodedFis);
			StreamUtils.closeQuietly(scaledFis);
			StreamUtils.closeQuietly(savedFos);
		}
		return -1;
	}
}
