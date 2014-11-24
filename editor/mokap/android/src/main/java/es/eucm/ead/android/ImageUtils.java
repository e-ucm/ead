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
package es.eucm.ead.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;

public final class ImageUtils {

	private static final String TAG = "ImageUtils";

	private ImageUtils() {

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
	public static boolean decodeFile(FileHandle file, int targetWidth,
			int targetHeight, FileHandle result) {
		boolean success = false;
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
			success = true;

			Gdx.app.log(TAG, "New image saved! " + file.path());
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
				tempBmap = null;
			}
			StreamUtils.closeQuietly(decodedFis);
			StreamUtils.closeQuietly(scaledFis);
			StreamUtils.closeQuietly(savedFos);
		}
		return success;
	}
}
