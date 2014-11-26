package es.eucm.ead.repobuilder.libs;

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

import es.eucm.ead.repobuilder.BuildRepoLibs;
import es.eucm.ead.repobuilder.RepoLibraryBuilder;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;

/**
 * Read this class carefully to learn how to create elements for the repo.
 * 
 * Created by Javier Torrente on 23/09/14.
 */
public class Example extends RepoLibraryBuilder {

	public Example() {
		/*
		 * (1) (a) Put all resources (images, thumbnails, etc.) into a zip file.
		 * Then, place this file under the resources/ directory (you will see
		 * there are a zip file and a png file for each library created so far).
		 * You can structure the contents of the zip file the way you want.
		 * However, by default it is assumed your images will live under a
		 * subfolder called "resources" and your thumbnails under "thumbnails".
		 * You can override this behaviour by using properties RESOURCES and
		 * THUMBNAILS (see step 2) (b) Place an image in the same folder and the
		 * same name (but with png extension) that will serve for library
		 * thumbnail.
		 * 
		 * After doing so, you will have added two files with the same name but
		 * different extension (zip/png) to the resources/ directory: resources/
		 * yourlib.zip yourlib.png ... (c) Make sure you pass the name you gave
		 * to those files (yourlib) to the constructor in the super(String)
		 * instruction. In the example below, replace "example" with "yourlib"
		 */
		super("example");
	}

	@Override
	protected void doBuild() {
		/*
		 * (2) Setup common properties. These are used to save you time. For
		 * example, when you want some tags to be applied to all elements, you
		 * can declare the property TAGS with a comma-separated list of tags
		 * that are to be applied to all elements in the library. After that,
		 * you still can apply specific tags to each element.
		 */
		// Switching the AUTO_IDS property on will make each element that is
		// created to be given an auto-incremental id based on the library id
		// (which is specified through property LIB_NAME).
		setCommonProperty(AUTO_IDS, "true");
		setCommonProperty(LIB_NAME, "example-lib");
		/*
		 * Set up the publisher for this library. Although any string can be
		 * provided, we recommend one of the following: "mokap": for all
		 * elements created by our team or the eucm group "openclipart": for
		 * elements retrieved from openclipart.org "freepik": for elements
		 * retrieved or adapted from freepik "vectorcharacters": for elements
		 * retrieved or adapted from vectorcharacters.net
		 */
		setCommonProperty(PUBLISHER, "mokap");

		// MAX_WIDTH and MAX_HEIGHT properties tell the utility to make sure all
		// elements are no larger than this size. If they include images bigger
		// than that, the images are not scaled down; instead, a transformation
		// is applied to the element so it shows no bigger than desired on
		// screen. This way, mokap users can scale up the elements if desired
		// and still get good results.
		setCommonProperty(MAX_WIDTH, "1024");
		setCommonProperty(MAX_HEIGHT, "1024");
		// Common tags for all elements to be created
		setCommonProperty(TAGS, "eUCM,example,mokap");
		// Common author name and license for all elements
		setCommonProperty(AUTHOR_NAME, "Javier Torrente");
		setCommonProperty(LICENSE, DefaultLicenses.License.CC_BY.toString());
		// Override the location of your resources and thumbnails inside the
		// zipped file, if necessary
		setCommonProperty(RESOURCES, "");
		setCommonProperty(THUMBNAILS, "");

		/*
		 * (3) Start creating the elements of your library. Use the "repoEntity"
		 * method for that. See examples below
		 */

		/*
		 * (3.a) Create a static element that just has an image. Use that
		 * specific image to create the thumbnail
		 */
		String imageA = "exampleA.png";
		repoEntity("Element name A in English", "Element name A in Spanish",
				"Description in English", "Description in Spanish", imageA,
				imageA);

		/*
		 * (3.b) Create a static element that just has an image. Use a different
		 * image for the thumbnail
		 */
		String imageB = "exampleB.png";
		String thumbnailB = "thumnail_exampleB.png";
		repoEntity("Element name B in English", "Element name B in Spanish",
				"Description in English", "Description in Spanish", thumbnailB,
				imageB);

		/*
		 * (3.c) Create a static element that just has image, thumbnail, and
		 * belongs to the "background" category. You can see a list of available
		 * categories in
		 * es.eucm.ead.schema.editor.components.repo.RepoCategories
		 */
		String imageC = "exampleC.png";
		String thumbnailC = "thumnail_exampleC.png";
		repoEntity("Element name C in English", "Element name C in Spanish",
				"Description in English", "Description in Spanish", thumbnailC,
				"scenes-backgrounds", imageC);
		/*
		 * After creating the element, you can setup additional tags, author,
		 * etc. using other methods provided. These methods are applied to the
		 * last repoEntity created. They also return a pointer to this same
		 * object (not the result of any operation), to facilitate chaining
		 * calls.
		 * 
		 * For example, the next instructions alter the author of the element
		 * (both name and url), add an additional tag in English and Spanish,
		 * add a category, and change the license
		 */
		authorName("jtv").authorUrl("http://www.mokap.es")
				.tag("example tag", "tag de ejemplo")
				.category(RepoCategories.ELEMENTS)
				.license(DefaultLicenses.License.LEARNING_ONLY.toString());

		/*
		 * (4) Creating animated characters is just as simple. You only need to
		 * take this into consideration: - When invoking repoEntity(), pass null
		 * as image. Keep passing a valid thumbnail. - Use the frameState()
		 * instruction to add new frame animations to the element
		 */
		repoEntity("Animated element with two animations",
				"Elmento animado con dos animaciones", "", "",
				"thumbnail_animated.png", null).tagFullyAnimatedCharacter()
				.category(RepoCategories.ELEMENTS_CHARACTERS);

		/*
		 * The frameState() method receives as inputs the duration of each frame
		 * (how long it is to be shown on screen, in seconds) and a list of
		 * strings (as many as desired). These strings are both tags to mark the
		 * animation (e.g. to specify it is for "walking") and the paths to the
		 * frames. Tags should go first and frame paths next. You specify how
		 * many tags are at the beginning of the list with the first param.
		 * 
		 * You can add as many frameStates() as needed, although for now only
		 * one will be used in mokap. However, if you add more than one
		 * frameState, add the tag DEFAULT to the one you want mokap to pick.
		 */
		float frame_duration = 0.8F;
		int numberOfTags = 2;
		frameState(numberOfTags, frame_duration, WALK, RIGHT,
				"animated_frame_01.png", "animated_frame_02.png");

		/*
		 * (5) After creating the elements, just add an entry for library
		 * metadata
		 */
		repoLib("Library name in English", "Library name in Spanish",
				"Library description", "Descripción de biblioteca", null);

		/*
		 * (6) After this, you are all set! To generate the library, use the
		 * BuildRepoLibs tool. This tool needs you to pass as argument (1) the
		 * installation path of imageMagick, (2) the output directory where the
		 * library should be stored and (3) THe library or libraries to
		 * generate.
		 * 
		 * See example in main method below. This will generate several things
		 * as outputs. First, it will create a subfolder called "elements" and
		 * it will put there a zip file containing all binaries and json for
		 * each specific element you creatd. Second, it will also create a
		 * subfolder called "libraries" and it will put there a zip file with
		 * the contents of the library created (just the json for the library
		 * and its thumbnails, nothing else). Finally, it creates a subfolder
		 * "preview" with a zip file. That zip file contains a sample mokap that
		 * lets you preview how your elements will look like on the app.
		 * 
		 * When the generation process completes, the previewer will launch.
		 * Click on "Select directory to scan" and select any file inside the
		 * "preview/" folder. Then hit the button below "previe". This will open
		 * a black screen where the first element is shown. Go back and forth
		 * around elements by clicking the little white circles included.
		 */
	}

	public static void main(String[] args) {
		String[] argsForBuilder = new String[] { "-out", "C:/test/", "-libs",
				Example.class.getName(), "-imagemagick",
				"C:\\DEVELOPMENT\\ImageMagick-6.9.0-Q16" };
		BuildRepoLibs.main(argsForBuilder);
	}
}
