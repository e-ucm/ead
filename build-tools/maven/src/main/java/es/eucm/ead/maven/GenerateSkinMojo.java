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
package es.eucm.ead.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

/**
 * The plugin generate a libgdx atlas for the project skins.
 * 
 * To use this plugin, type <strong>one</strong> of the following at the command
 * line:
 * 
 * <pre>
 * mvn es.e-ucm.ead:ead-maven-plugin:<version (e.g. 0.0.1-SNAPSHOT)>:generate-skins
 * # or
 * mvn es.e-ucm.ead:ead-maven-plugin:generate-skins
 * # or
 * mvn ead:generate-skins
 * </pre>
 * 
 * </code>
 * 
 * @author Ivan Martinez-Ortiz
 */
@Mojo(name = "generate-skins", requiresProject = false, inheritByDefault = false)
public class GenerateSkinMojo extends AbstractMojo {

	/**
	 * Skin source folder.
	 */
	@Parameter(property = "skins.sourceDir", defaultValue = "${basedir}/assets/skins-raw")
	private File sourceDir;

	/**
	 * Generated skin target folder.
	 */
	@Parameter(property = "skins.outputDir", defaultValue = "${basedir}/assets/skins")
	private File outputDir;

	public void execute() throws MojoExecutionException {

		if (!sourceDir.exists()) {
			throw new MojoExecutionException(
					"[generate-skins] Source directory does not exists: "
							+ sourceDir);
		}

		if (!outputDir.exists()) {
			if (!outputDir.mkdir()) {
				throw new MojoExecutionException(
						"[generate-skins] Cannot create output directory: "
								+ outputDir);
			}
		}

		LwjglFiles files = new LwjglFiles();

		Settings settings = new Settings();

		FileHandle rawRoot = files.internal(sourceDir.getAbsolutePath());
		FileHandle skinsRoot = new FileHandle(files.internal(
				outputDir.getAbsolutePath()).file());

		for (FileHandle folder : rawRoot.list()) {
			if (folder.isDirectory()) {
				FileHandle skinFolder = skinsRoot.child(folder.name());
				if (!skinFolder.exists()) {
					getLog().info(
							"[generate-skins] Generating: " + folder.name());
					skinFolder.mkdirs();
					Settings set = transformTtf2FntIfNeeded(folder, skinFolder,
							settings);
					TexturePacker.process(set, folder.child("images").file()
							.getAbsolutePath(), skinFolder.file()
							.getAbsolutePath(), "skin");
					FileHandle fonts = folder.child("fonts");
					if (fonts.exists())
						fonts.copyTo(skinFolder);
					folder.child("skin.json").copyTo(skinFolder);
				} else {
					getLog().info(
							"[generate-skins] skin already exists, skipping: "
									+ folder.name());
				}
			}
		}
	}

	private Settings transformTtf2FntIfNeeded(FileHandle srcFolder,
			FileHandle skinFolder, Settings settings) {
		FileHandle font = srcFolder.child("font.fnt");
		if (font.exists()) {
			font.copyTo(skinFolder);

			Settings set = new TexturePacker.Settings();
			set.filterMag = TextureFilter.Linear;
			set.filterMin = TextureFilter.MipMapLinearNearest;
			set.pot = true;
			set.maxHeight = 1024;
			set.maxWidth = 1024;
			set.paddingX = 2;
			set.paddingY = 2;
			set.limitMemory = false;
			return set;
		}
		return settings;
	}
}
