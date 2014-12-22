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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;
import org.w3c.dom.Document;

/**
 * The XML Formatter is a plugin that formats the project's xml files using one
 * formatting option (either spaces or tabs). This helps the development of a
 * project that is being worked on by many different people, with each person
 * using their own preferred formatting style, the files become hard to read.
 * 
 * The plugin contains two arrays in which you can specify which files to
 * include/exclude from the formatting. <strong> By default all XML files are
 * included, except those in the target folder.</strong>
 * 
 * To use this plugin, type <strong>one</strong> of the following at the command
 * line:
 * 
 * <pre>
 * mvn es.e-ucm.ead:ead-maven-plugin:<version (e.g. 0.0.1-SNAPSHOT)>:format-xml
 * # or
 * mvn es.e-ucm.ead:ead-maven-plugin:format-xml
 * # or
 * mvn ead:format-xml
 * </pre>
 * 
 * To format the files using tabs instead of spaces, add this onto the end of
 * one of the above commands. <code>
 * -DxmlFormatter.useTabs="true"</>
 * </code>
 * 
 * @author Ivan Martinez-Ortiz
 * @author Brian Walsh
 * 
 * @see https://code.google.com/p/xml-formatter/
 */
@Mojo(name = "format-xml", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class XMLFormatterMojo extends AbstractMojo {

	/**
	 * Called automatically by each module in the project, including the parent
	 * module. All files will formatted with either <i>spaces</i> or
	 * <i>tabs</i>, and will be written back to it's original location.
	 * 
	 * @throws MojoExecutionException
	 */
	public void execute() throws MojoExecutionException {

		if ((baseDirectory != null) && (getLog().isDebugEnabled())) {
			getLog().debug("[xml formatter] Base Directory:" + baseDirectory);
		}

		if (includes != null) {
			String[] filesToFormat = getIncludedFiles(baseDirectory, includes,
					excludes);

			if (getLog().isDebugEnabled()) {
				getLog().debug(
						"[xml formatter] Format " + filesToFormat.length
								+ " source files in " + baseDirectory);
			}

			for (String include : filesToFormat) {
				try {
					format(new File(baseDirectory + File.separator + include));
				} catch (RuntimeException re) {
					getLog().error(
							"File <"
									+ baseDirectory
									+ File.separator
									+ include
									+ "> failed to parse, skipping and moving on to the next file",
							re);
				}
			}
		}
	}

	/**
	 * A flag used to tell the program to format with either spaces or tabs. By
	 * default, the formatter uses spaces.
	 * 
	 * <ul>
	 * <li><tt>true</tt> - tabs</LI>
	 * <li><tt>false</tt> - spaces</LI>
	 * </ul>
	 * 
	 * <p>
	 * This parameter can be configured using the following at the command line:
	 * -DxmlFormatter.useTabs="true" or using the
	 * 
	 */
	@Parameter(defaultValue = "false", property = "xmlFormatter.useTabs")
	private boolean useTabs;

	/**
	 * The base directory of the project.
	 * 
	 */
	@Parameter(defaultValue = "${basedir}")
	private File baseDirectory;

	/**
	 * A set of file patterns that dictates which files should be included in
	 * the formatting with each file pattern being relative to the base
	 * directory. <i>By default all xml files are included.</i> This parameter
	 * is most easily configured in the parent pom file.
	 */
	@Parameter(alias = "includes")
	private String[] includes = {"pom.xml", "src/**/*.xml"};

	/**
	 * A set of file patterns that allow you to exclude certain files/folders
	 * from the formatting. <i>By default the target folder is excluded from the
	 * formatting.</i> This parameter is most easily configured in the parent
	 * pom file.
	 * 
	 */
	@Parameter(alias = "excludes")
	private String[] excludes = {"target/**"};

	/**
	 * By default we have setup the exclude list to remove the target folders.
	 * Setting any value including an empty array will overide this
	 * functionality. This parameter can be configured in the POM file using the
	 * 'excludes' alias in the configuration option. Note that all files are
	 * relative to the parent POM.
	 * 
	 * @param excludes
	 *            - String array of patterns or filenames to exclude from
	 *            formatting.
	 */
	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

	/**
	 * By default all XML files ending with .xml are included for formatting.
	 * This parameter can be configured in the POM file using the 'includes'
	 * alias in the configuration option. Note that all files are relative to
	 * the parent POM.
	 * 
	 * @param includes
	 *            - Default ["pom.xml", "src&#47;**&#47;*.xml"]. Assigning a new
	 *            value overrides the default settings.
	 */
	public void setIncludes(String[] includes) {
		this.includes = includes;
	}

	/**
	 * Scans the given directory for files to format, and returns them in an
	 * array. The files are only added to the array if they match a pattern in
	 * the <tt>includes</tt> array, and <strong>do not</strong> match any
	 * pattern in the <tt>excludes</tt> array.
	 * 
	 * @param directory
	 *            - Base directory from which we start scanning for files. Note
	 *            that this must be the root directory of the project in order
	 *            to obtain the pom.xml as part of the XML files. This is one
	 *            other differentiator when we were looking for tools, anything
	 *            we found remotely like this did not start at the root
	 *            directory.
	 * @param includes
	 *            - A string array containing patterns that are used to search
	 *            for files that should be formatted.
	 * @param excludes
	 *            - A string array containing patterns that are used to filter
	 *            out files so that they are <strong>not</strong> formatted.
	 * @return - A string array containing all the files that should be
	 *         formatted.
	 */
	public String[] getIncludedFiles(File directory, String[] includes,
			String[] excludes) {

		DirectoryScanner dirScanner = new DirectoryScanner();
		dirScanner.setBasedir(directory);
		dirScanner.setIncludes(includes);
		dirScanner.setExcludes(excludes);
		dirScanner.scan();

		String[] filesToFormat = dirScanner.getIncludedFiles();

		if (getLog().isDebugEnabled()) {

			if (useTabs) {
				getLog().debug("[xml formatter] Formatting with tabs...");
			} else {
				getLog().debug("[xml formatter] Formatting with spaces...");
			}

			getLog().debug("[xml formatter] Files:");
			for (String file : filesToFormat) {
				getLog().debug(
						"[xml formatter] file<" + file
								+ "> is scheduled for formatting");
			}
		}

		return filesToFormat;
	}

	private static final String XERCES2_DOCUMENT_BUILDER_FACTORY_IMPL = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";

	private static final String XALAN2_TRANSFORMER_FACTORY_IMPL = "org.apache.xalan.processor.TransformerFactoryImpl";

	/**
	 * Formats the provided file, writing it back to it's original location.
	 * 
	 * @param formatFile
	 *            - File to be formatted. The output file is the same as the
	 *            input file. Please be sure that you have your files in a
	 *            revision control system (and saved before running this
	 *            plugin).
	 */
	public void format(File formatFile) {

		if (formatFile.exists() && formatFile.isFile()) {

			InputStream inputStream = null;
			Document xml = null;

			try {
				inputStream = new FileInputStream(formatFile);

				if (inputStream == null) {
					getLog().error(
							"[xml formatter] File<" + formatFile
									+ "> could not be opened, skipping");
					return;
				}

				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
						.newInstance(XERCES2_DOCUMENT_BUILDER_FACTORY_IMPL,
								null);
				// Disable entity resolution
				documentBuilderFactory.setFeature(
						"http://xml.org/sax/features/use-entity-resolver2",
						false);
				// Disable validation, we only require well-formed XML
				documentBuilderFactory.setFeature(
						"http://xml.org/sax/features/validation", false);
				// Disable external entity inclusion
				documentBuilderFactory
						.setFeature(
								"http://xml.org/sax/features/external-general-entities",
								false);

				DocumentBuilder documentBuilder = documentBuilderFactory
						.newDocumentBuilder();

				xml = documentBuilder.parse(inputStream);

				getLog().info("Successfully formatted file: " + formatFile);

			} catch (Throwable t) {
				throw new RuntimeException("[xml formatter] Failed to parse..."
						+ t.getMessage(), t);
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable tr) {
						// intentionally hiding exceptions for failures on
						// close....
					}
				}
			}

			FileOutputStream fos = null;
			InputStream stylesheet = null;
			try {

				// Read the stylesheet from the classpath
				stylesheet = getClass().getClassLoader().getResourceAsStream(
						"remove-whitespace.xsl");

				if (stylesheet == null) {
					getLog().error(
							"[xml formatter] Could not find remove-whitespace.xsl");
					return;
				}

				TransformerFactory transformerFactory = TransformerFactory
						.newInstance(XALAN2_TRANSFORMER_FACTORY_IMPL, null);
				Transformer transformer = transformerFactory
						.newTransformer(new StreamSource(stylesheet));
				fos = new FileOutputStream(formatFile);
				StreamResult streamResult = new StreamResult(fos);
				DOMSource domSource = new DOMSource(xml);
				transformer.transform(domSource, streamResult);

			} catch (Throwable t) {
				throw new RuntimeException("[xml formatter] Failed to parse..."
						+ t.getMessage(), t);
			} finally {
				if (stylesheet != null) {
					try {
						stylesheet.close();
					} catch (Throwable tr) {
						// intentionally ignoring exceptions on close
					}
				}

				if (fos != null) {
					try {
						fos.close();
					} catch (Throwable t) {
						// intentionally ignoring exceptions on close
					}
				}
			}

			// Now that we know that the indent is set to four spaces, we can
			// either keep it like that or change them to tabs depending on
			// which 'mode' we are in.

			if (useTabs) {
				indentFile(formatFile);
			}
		} else {
			getLog().debug(
					"[xml formatter] File was not valid:" + formatFile
							+ " skipping");
		}
	}

	/**
	 * Indents the file using tabs, writing it back to its original location.
	 * This method is only called if useTabs is set to true. Note that the input
	 * file is expected to be space-indented (no tabs mixed in) thanks to prior
	 * space-based re-indenting.
	 * 
	 * @param file
	 *            The file to be indented using tabs.
	 */
	private void indentFile(File file) {

		List<String> temp = new ArrayList<String>(); // a temporary list to hold
		// the lines
		BufferedReader reader = null;
		BufferedWriter writer = null;

		// Read the file, and replace the four spaces with tabs.
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = reader.readLine()) != null) {
				temp.add(line.replaceAll("[\\s]{4}", "\t"));
			}

			writer = new BufferedWriter(new FileWriter(file));

			for (String ln : temp) {
				writer.write(ln);
				writer.newLine();
			}
		} catch (Throwable t) {
			throw new RuntimeException("[xml formatter] Failed to read file..."
					+ t.getMessage(), t);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Throwable t) {
					// intentionally ignoring exceptions on close
				}
			}

			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (Throwable t) {
					// intentionally ignoring exceptions on close
				}
			}
		}
	}
}
