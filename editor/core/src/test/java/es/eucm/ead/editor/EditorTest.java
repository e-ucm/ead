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

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.badlogic.gdx.scenes.scene2d.Group;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.platform.MockPlatform;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;

/**
 * This abstract class is meant to be the "parent" test for all editor tests. It
 * initializes a basic platform, controller and model that are made available to
 * any test extending this class. EditorTest also deals with disposal of any
 * resources initialized by platform.
 */
public abstract class EditorTest {

	/**
	 * Mock editor controller. For testing. It is initialized statically before
	 * the class is actually loaded, so it is available for any test method
	 * implemented in the child test class.
	 */
	protected Controller controller;

	/**
	 * Mock platform. For testing. It is initialized statically before the class
	 * is actually loaded, so it is available for any test method implemented in
	 * the child test class.
	 */
	protected MockPlatform platform;

	/**
	 * Mock model. For testing. It is initialized statically before the class is
	 * actually loaded, so it is available for any test method implemented in
	 * the child test class.
	 */
	protected Model model;

	@BeforeClass
	public static void setUpEditorTest() {
		MockApplication.initStatics();
	}

	@Before
	public void setUp() {
		platform = new MockPlatform();
		controller = new Controller(platform, new MockFiles(), new Group(),
				new Group());
		model = controller.getModel();
	}

	@After
	public void tearDownClass() {
		platform.removeTempFiles();
	}
}
