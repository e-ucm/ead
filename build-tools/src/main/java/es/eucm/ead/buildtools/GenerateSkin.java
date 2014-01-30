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
package es.eucm.ead.buildtools;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class GenerateSkin {

	/**
	 * Location of raw skins
	 */
	public static final String RAW_LOCATION = "assets/skins-raw";

	/**
	 * Location for processed skins
	 */
	public static final String SKINS_LOCATION = "assets/skins";

	public static void main(String[] args) {
		Files files = new LwjglFiles();

		Settings settings = new Settings();

		FileHandle rawRoot = files.internal(RAW_LOCATION);
		FileHandle skinsRoot = new FileHandle(files.internal(SKINS_LOCATION)
				.file());

		for (FileHandle folder : rawRoot.list()) {
			if (folder.isDirectory()) {
				FileHandle skinFolder = skinsRoot.child(folder.name());
				skinFolder.mkdirs();
				TexturePacker.process(settings, folder.child("images").file()
						.getAbsolutePath(),
						skinFolder.file().getAbsolutePath(), "skin");
				folder.child("fonts").copyTo(skinFolder);
				folder.child("skin.json").copyTo(skinFolder);
			}
		}
	}
}
