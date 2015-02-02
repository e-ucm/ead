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
package es.eucm.ead.editor;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.exporter.ExporterApplication;
import es.eucm.ead.editor.exporter.ExporterFiles;
import es.eucm.ead.engine.utils.ZipUtils;

/**
 * Demonstrates how to build standalone mokap APKs.
 * 
 * Created by jtorrente on 3/01/15.
 */
public class ExportAsApkTest {
	public static void main(String[] args) {
		System.out
				.println("** Do not forget to install maven and setup MAVEN_HOME env variable before running this test!! **");
		ExporterFiles files = new ExporterFiles();
		FileHandle projectDir = FileHandle.tempDirectory("test");
		projectDir.mkdirs();
		ZipUtils.unzip(files.internal("export/got.zip"), projectDir);
		FileHandle thumbnail = FileHandle.tempDirectory("test")
				.child("got.png");
		thumbnail.parent().mkdirs();
		files.internal("export/got.png").copyTo(thumbnail);
		FileHandle target = FileHandle.tempDirectory("got").child("got.apk");
		target.parent().mkdirs();

		ExporterApplication
				.exportAsApk(projectDir.path(), null, null, null, null,
						"Game Of Thrones", thumbnail.path(), false,
						target.path());
	}
}
