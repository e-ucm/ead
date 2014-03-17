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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.google.gson.stream.JsonWriter;

/**
 * Creates an eAd release update.json
 * 
 * @goal generate-update-json
 * 
 * @phase generate-resources
 */
public class ReleaseMojo extends AbstractMojo {

	private static final String[] TARGET_PLATFORMS = new String[] { "win32", "win64",
			"linux-i386", "linux-amd64", "macosx" };

	/**
	 * The maven project.
	 * 
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	
	/**
	 * Any Object to print out.
	 * 
	 * @parameter name="channel" property="ead.release.channel" default-value="nightly"
	 */
	private String releaseChannel;
	
	/**
	 * Base URL.
	 * 
	 * @parameter name="baseUrl" property="ead.updates.base.url" default-value="http://sourceforge.net/projects/e-adventure/files"
	 */
	private String baseUrl;
	
	/**
	 * Location of the file.
	 * 
	 * @parameter name="outputDir" default-value="${project.build.directory}/updates"
	 * @required
	 */
	private File outputDir;

	
	private static final String PREFIX = "ead2-";
	
	private static final String UPDATE_FILE_NAME ="update.json";
	
	public void execute() throws MojoExecutionException {
		
		if (!outputDir.exists()) {
			if(!outputDir.mkdir()) {
				throw new MojoExecutionException("Cannot create output directory: "+outputDir);
			}
		}
		
		outputDir = new File(outputDir, PREFIX+releaseChannel);
		if (!outputDir.exists()) {
			if(!outputDir.mkdir()) {
				throw new MojoExecutionException("Cannot create output directory: "+outputDir);
			}
		}
		
		File outputFile = new File(outputDir, UPDATE_FILE_NAME);
		OutputStream os = null;
		try {
			String projectVersion = project.getVersion();
			os = new FileOutputStream(outputFile);
			JsonWriter writer = new JsonWriter(new OutputStreamWriter(os));
			writer.beginObject();
			writer.name("version").value(projectVersion);
			writer.name("platforms").beginArray();
			for(String platform : TARGET_PLATFORMS) {
				writer.beginObject();
				writer.name("os").value(platform);
				writer.name("url").value(baseUrl+"/"+PREFIX+releaseChannel+"/"+PREFIX+platform+"-"+projectVersion+".zip/download");
				writer.endObject();
			}
			writer.endArray();
			writer.endObject();
			writer.close();
		} catch (FileNotFoundException e) {
			getLog().error(
					"Can not write to file: " + outputFile.getAbsolutePath(), e);
		} catch (IOException e) {
			getLog().error(
					"Error writing to file: " + outputFile.getAbsolutePath(),
					e);
		}
	}
}
