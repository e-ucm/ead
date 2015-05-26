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
package es.eucm.ead.repobuilder;

import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.utils.gdx.ZipUtils;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Simple test class for the RepoBuilder. Automatically creates the library and
 * gets it exported and extracted to a temp folder ({@code outputFolder}). It
 * checks that subfolders elements/, libraries/ and preview/ are created.
 * 
 * This class also provides methods for checking different aspects. See:
 * 
 * <pre>
 *     <ul>
 *          <li>{@link #checkNumberOfElements(int)}</li>
 *          <li>{@link #checkElement(String)}</li>
 *          <li>{@link #checkContents(FileHandle, String...)}</li>
 *     </ul>
 * </pre>
 * 
 * To use this class for developing a test library, just:
 * 
 * <pre>
 *      <ol>
 *          <li>Create a class that extends {@link RepoLibraryTest}</li>
 *          <li>In this class, create a constructor that receives as argument the name of the zip that contains the resources for the test, or none arguments if you want to use the default test.zip file</li>
 *          <li>Implement method {@link RepoLibraryBuilder#doBuild()} as you would do for any RepoLibrary.</li>
 *          <li>Write your "@Test" methods.</li>
 *      </ol>
 * </pre>
 * 
 * Created by jtorrente on 15/05/2015.
 */
public abstract class RepoLibraryTest extends RepoLibraryBuilder {

	/**
	 * Path to the test image included in test.zip. This file will be accessible
	 * if the default test file is used (when the constructor with no arguments
	 * is used).
	 */
	public static final String DEFAULT_IMG = "image.png";

	/**
	 * Directory where the results of generating the library are placed
	 */
	protected FileHandle outputFolder;

	/**
	 * Array containing any temporary (FileHandle.tempDirectory) directories and
	 * files created during the test that have to be cleaned up upon completion.
	 */
	protected Array<FileHandle> tempFolders;

	/**
	 * Initializes the test with the contents of file "test.zip", which just
	 * contains a 2x2 white png image called "image.png".
	 */
	public RepoLibraryTest() {
		this("test");

	}

	/**
	 * Initializes the test with the contents of the zip file especified by
	 * {@code root}.
	 * 
	 * @param root
	 *            Name of the zip file containing the images and other files
	 *            required for the test. Files root.zip and root.png must be
	 *            available under test/resources.
	 */
	public RepoLibraryTest(String root) {
		super(root, new TestImgUtils());
	}

	@Before
	public void setup() {
		LwjglNativesLoader.load();
		MockApplication.initStatics();

		tempFolders = new Array<FileHandle>();

		setCommonProperty(RepoLibraryBuilder.VERSION, "testversion");
		setCommonProperty(THUMBNAILS, "");
		setCommonProperty(RESOURCES, "");

		outputFolder = FileHandle.tempDirectory("test-repolib");
		outputFolder.mkdirs();
		tempFolders.add(outputFolder);
		export(outputFolder.path());

		// Check element file
		FileHandle elementsSubFolder = outputFolder.child("elements");
		assertTrue(
				"Elements sub-folder does not exist or it's not a directory",
				elementsSubFolder.exists() && elementsSubFolder.isDirectory());

		FileHandle librariesFile = outputFolder.child("libraries");
		assertTrue(
				"Libraries sub-folder does not exist or it's not a directory",
				librariesFile.exists() && librariesFile.isDirectory());

		FileHandle previewFile = outputFolder.child("preview");
		assertTrue("Preview sub-folder does not exist or it's not a directory",
				previewFile.exists() && previewFile.isDirectory());
	}

	/**
	 * Asserts if the number of elements produced in the library is correct
	 * 
	 * @param expectedNumberOfElements
	 *            Expected number of elements
	 */
	public void checkNumberOfElements(int expectedNumberOfElements) {
		FileHandle elementsSubFolder = outputFolder.child("elements");
		assertTrue(
				"Elements sub folder does not exist or it's not a directory",
				elementsSubFolder.exists() && elementsSubFolder.isDirectory());
		assertEquals("The number of expected elements does not match",
				expectedNumberOfElements, elementsSubFolder.list().length);
	}

	/**
	 * Checks that a zip file with the given elementName has been produced and
	 * has a valid structure: thumbnails/ contents.zip descriptor.json
	 * 
	 * Thumbnails folder is checked to contain only valid png thumbnails
	 * 
	 * @param elementName
	 * @return The temp folder where the element file is extracted to, so it can
	 *         be used to make further checks if necessary
	 */
	public FileHandle checkElement(String elementName) {
		if (!elementName.toLowerCase().endsWith(".zip")) {
			elementName += ".zip";
		}
		FileHandle elementsSubFolder = outputFolder.child("elements");
		FileHandle elementZip = elementsSubFolder.child(elementName);
		assertTrue("Element zip file does not exist or it's not a zip file",
				elementZip.exists() && !elementZip.isDirectory());

		// Unzip the file
		FileHandle extractedElementFolder = FileHandle
				.tempDirectory("test-element");
		ZipUtils.unzip(elementZip, extractedElementFolder);
		// Check there are a three things: thumbnails/, contents.zip,
		// descriptor.json
		FileHandle thumbnails = extractedElementFolder.child("thumbnails");
		FileHandle contents = extractedElementFolder.child("contents.zip");
		FileHandle descriptor = extractedElementFolder.child("descriptor.json");
		assertTrue("Thumbnails folder does not exist or it's not valid",
				thumbnails.exists() && thumbnails.isDirectory());
		assertTrue(
				"Contents zip does not exist or it's not valid",
				contents.exists() && !contents.isDirectory()
						&& contents.length() > 0);
		assertTrue(
				"Descriptor does not exist or it's not valid",
				descriptor.exists() && !descriptor.isDirectory()
						&& descriptor.length() > 0);

		// Thumbnails check
		for (FileHandle thumbnail : thumbnails.list()) {
			assertTrue(
					"Not valid thumbnail: " + thumbnail.name(),
					thumbnail.exists()
							&& !thumbnail.isDirectory()
							&& thumbnail.length() > 0
							&& thumbnail.name().toLowerCase().endsWith(".png")
							&& thumbnail.nameWithoutExtension().matches(
									"\\d+x\\d+"));
		}
		tempFolders.add(extractedElementFolder);
		return extractedElementFolder;
	}

	/**
	 * Checks that the contents.zip file of element in {@code elementFolder} has
	 * the entries provided as inputs
	 * 
	 * @param elementFolder
	 *            The folder where the element was extracted to
	 * @param entries
	 *            The list of entries to be checked to be present in the file
	 * @return The FileHandle that points to the json file inside contents.zip
	 */
	public FileHandle checkContents(FileHandle elementFolder, String... entries) {
		FileHandle contentsFolder = FileHandle.tempDirectory("contents-");
		ZipUtils.unzip(elementFolder.child("contents.zip"), contentsFolder);
		assertEquals("Number of expected entries don't match", entries.length,
				ZipUtils.listEntries(elementFolder.child("contents.zip")).size);
		FileHandle jsonEntry = contentsFolder.list("json")[0];
		tempFolders.add(contentsFolder);
		return jsonEntry;
	}

	@After
	public void cleanUp() {
		for (FileHandle fileHandle : tempFolders) {
			if (fileHandle.isDirectory()) {
				fileHandle.deleteDirectory();
			} else {
				fileHandle.delete();
			}
		}
		tempFolders.clear();
	}
}
