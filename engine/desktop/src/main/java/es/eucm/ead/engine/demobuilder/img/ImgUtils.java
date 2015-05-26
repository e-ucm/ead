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
package es.eucm.ead.engine.demobuilder.img;

import com.badlogic.gdx.files.FileHandle;

public interface ImgUtils {

	/**
	 * Creates a thumbnail from the source image specified to the specified
	 * location with the given width and height. Width and height are used as
	 * provided, so aspect ratio in resulting image may vary from original.
	 * 
	 * @param originPath
	 *            Path to the source image to make a thumbnail for. Can be a
	 *            relative or absolute path, as long as it can be resolved.
	 *            E.g.: "big_image.png", "C:/images/big_image.png"
	 * @param outputPath
	 *            Path to the location to store the thumbnail. Can be a relative
	 *            or absolute path, as long as it can be resolved. E.g.:
	 *            "thumbnail_image.png", "C:/images/thumbnail_image.png"
	 * @param width
	 *            The width of the output image, in pixels
	 * @param height
	 *            The height of the output image, in pixels.
	 */
	void thumbnail(String originPath, String outputPath, int width, int height);

	/**
	 * Converts a PNG image (or a set of images) to ensure they can be processed
	 * by LibGdx. This ensures no interlacing, indexed colors or 16 bits are
	 * used.
	 * 
	 * @param fileOrDir
	 *            The png file to convert. If it is a directory, this method
	 *            will scan it recursively and convert all png images found in
	 *            it.
	 */
	void convertPNGs(FileHandle fileOrDir);

	/**
	 * Prints out on console a detailed report of the characteristics and format
	 * of the given image. Useful for debugging weird problematic images.
	 * 
	 * @param imagePath
	 *            The path to the image to analyze
	 */
	void showImageProperties(String imagePath);
}
