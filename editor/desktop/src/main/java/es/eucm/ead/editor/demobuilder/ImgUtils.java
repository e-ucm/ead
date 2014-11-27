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
package es.eucm.ead.editor.demobuilder;

import com.badlogic.gdx.files.FileHandle;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.process.OutputConsumer;
import org.im4java.process.ProcessStarter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Utility class built on top of <a
 * href="http://im4java.sourceforge.net/">Im4Java</a>, an <a
 * href="http://www.imagemagick.org/">ImageMagick</a> wrapper that is used to
 * help with fast image conversion and resizing.
 * 
 * For this wrapper to work, it is necessary to have ImageMagick installed. Then
 * invoke {@link #imageMagickPath(String)} to specify where it is located,
 * before you start using this class.
 * 
 * Created by jtorrente on 18/11/14.
 */
public class ImgUtils {

	private static final ConvertCmd convertCmd = new ConvertCmd();

	/*
	 * Specifies a set of options to ensure all png images converted by this
	 * tool can be processed by LibGdx. Among other things, it ensures: 1) No
	 * interlacing is used 2) Only 8-bit depth (nothing else supported by
	 * LibGdx) 3) No indexed colors are used (color-type must be always 6)
	 * 
	 * Do not alter these parameters lightly - you may start getting LibGdx
	 * errors
	 */
	private static void fillPNGOptions(IMOperation op) {
		op.define("png:color-type=6");
		op.set("png:color-type", "6");
		op.set("png:interlace-method", "0");
		op.set("png:bit-depth", "8");
		op.colorspace("sRGB");
		op.compress("None");
		op.transparentColor("None");
		op.interlace("None");
		op.format("png");
		op.depth(8);
	}

	/**
	 * This method should be invoked first of all before using
	 * {@link es.eucm.ead.editor.demobuilder.ImgUtils}.
	 * 
	 * It just tells the Im4Java wrapper where ImageMagick is installed on disk.
	 * 
	 * @param path
	 *            The absolute path pointing to the root installation dir of
	 *            ImageMagick. E.g. "C:\Development\ImageMagick". If it is null,
	 *            it does not exist, or it is not a directory, nothing happens
	 *            but the path is not set, and this tool will start throwing
	 *            errors.
	 */
	public static void imageMagickPath(String path) {
		FileHandle dir = new FileHandle(path);
		if (path == null || !dir.exists() || !dir.isDirectory()) {
			System.err
					.println("[ImgUtils] "
							+ path
							+ " is not a valid installation path for ImageMagick. ImgUtils will not work properly. Exceptions may be thrown.");
			return;
		}
		ProcessStarter.setGlobalSearchPath(path);
	}

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
	public static void thumbnail(String originPath, String outputPath,
			int width, int height) {
		IMOperation op = new IMOperation();
		if (originPath.toLowerCase().endsWith(".png")) {
			fillPNGOptions(op);
		}
		op.addImage(originPath);
		op.resize(width, height);
		op.addImage(outputPath);
		try {
			convertCmd.run(op);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[Error] Could not create thumbnail for "
					+ originPath);
		}
	}

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
	public static void convertPNGs(FileHandle fileOrDir) {

		if (fileOrDir.isDirectory()) {
			for (FileHandle child : fileOrDir.list()) {
				convertPNGs(child);
			}
		} else if (fileOrDir.extension().toLowerCase().endsWith("png")) {
			IMOperation op = new IMOperation();
			fillPNGOptions(op);
			op.addImage(fileOrDir.path());
			op.addImage(fileOrDir.path());

			try {
				convertCmd.run(op);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("[Error] Could not convert PNG image "
						+ fileOrDir.name());
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.err.println("[Error] Could not convert PNG image "
						+ fileOrDir.name());
			} catch (IM4JavaException e) {
				e.printStackTrace();
				System.err.println("[Error] Could not convert PNG image "
						+ fileOrDir.name());
			}
		}
	}

	/**
	 * Prints out on console a detailed report of the characteristics and format
	 * of the given image. Useful for debugging weird problematic images.
	 * 
	 * @param imagePath
	 *            The path to the image to analyze
	 */
	public static void showImageProperties(String imagePath) {
		IdentifyCmd identifyCmd = new IdentifyCmd();
		IMOperation op = new IMOperation();
		op.verbose();
		identifyCmd.setOutputConsumer(new OutputConsumer() {
			@Override
			public void consumeOutput(InputStream inputStream)
					throws IOException {
				StringBuilder out = new StringBuilder();
				final Reader in = new InputStreamReader(inputStream, "UTF-8");

				char[] c = new char[1024];
				int length = -1;
				String result = "";
				while ((length = in.read(c)) != -1) {
					out.append(c, 0, length);
				}
				in.close();
				result = out.toString();
				System.err.println(result);
			}
		});
		op.addImage(imagePath);

		try {
			identifyCmd.run(op);
		} catch (Exception e2) {
			// Do nothing
		}

	}
}
