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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.vividsolutions.jts.geom.Geometry;
import es.eucm.ead.editor.utils.GeometryUtils;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.schema.data.Dimension;
import es.eucm.ead.schema.data.shape.Polygon;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Contains several utility methods for DemoBuilder.
 * 
 * Created by Javier Torrente on 6/07/14.
 */
public class BuilderUtils {

	/**
	 * Copies all files from source folder to target folder, recursively
	 */
	static void copyAllTo(FileHandle source, FileHandle target) {
		for (FileHandle fileHandle : source.list()) {
			if (fileHandle.isDirectory()) {
				FileHandle childTarget = target.child(fileHandle.name());
				childTarget.mkdirs();
				copyAllTo(fileHandle, childTarget);
			} else {
				fileHandle.copyTo(target);
			}
		}
	}

	/**
	 * Determines the width and height of an image without loading it from disk.
	 */
	static Dimension getImageDimension(GameAssets gameAssets, String imageUri) {
		InputStream inputStream = gameAssets.resolve(imageUri).read();
		ImageInputStream in = null;
		try {
			in = ImageIO.createImageInputStream(inputStream);
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				try {
					reader.setInput(in);
					Dimension dimension = new Dimension();
					dimension.setWidth(reader.getWidth(0));
					dimension.setHeight(reader.getHeight(0));
					return dimension;
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					reader.dispose();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	/**
	 * Creates a model collider for the given image
	 */
	static Array<Polygon> createSchemaCollider(GameAssets gameAssets,
			String imageUri) {
		Array<Polygon> collider = new Array<Polygon>();
		Pixmap pixmap = new Pixmap(gameAssets.resolve(imageUri));
		Array<Geometry> geometryArray = GeometryUtils
				.findBorders(pixmap, .1, 2);
		for (Geometry geometry : geometryArray) {
			collider.add(GeometryUtils.jtsToSchemaPolygon(geometry));
		}
		pixmap.dispose();
		return collider;
	}

	static String collidersToString(GameAssets gameAssets, FileHandle folder) {
		String colliders = "";
		for (FileHandle child : folder.list("png")) {
			String collider = "\"collider\": [\n";
			for (Polygon polygon : createSchemaCollider(gameAssets,
					child.path())) {
				collider += "{ \"points\":[\n";
				for (float f : polygon.getPoints()) {
					collider += f + ", ";
				}
				collider = collider.substring(0, collider.length() - 2);
				collider += "\n]},\n";
			}
			collider = collider.substring(0, collider.length() - 2);
			collider += "\n]\n";

			colliders += "****************************";
			colliders += child.name();
			colliders += "****************************";
			colliders += collider;
		}
		return colliders;
	}

	/**
	 * Unzips the given zipFile to the given outputFolder
	 */
	static void unZipIt(FileHandle zipFile, FileHandle outputFolder) {

		byte[] buffer = new byte[1024];

		try {

			// Create temp folder structure if does not exist
			if (!outputFolder.exists()) {
				outputFolder.mkdirs();
			}

			// Get the zip file content
			ZipInputStream zis = new ZipInputStream(zipFile.read());
			// Get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				FileHandle newFile = outputFolder.child(fileName);
				if (ze.isDirectory()) {
					newFile.mkdirs();
				}

				OutputStream fos = newFile.write(false);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
