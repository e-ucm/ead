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
package es.eucm.ead.editor.actions.editor;

import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.editor.SetPreference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SetPreferenceTest extends EditorTest {

	@Test
	public void testSetPreference() {
		controller.action(SetPreference.class, "prefInteger", 20);
		controller.action(SetPreference.class, "prefFloat", 55.5f);
		controller.action(SetPreference.class, "prefBoolean", true);
		controller.action(SetPreference.class, "prefString", "prefValue");

		Preferences preferences = controller.getPreferences();

		assertEquals(preferences.getInteger("prefInteger"), 20);
		assertEquals((int) (preferences.getFloat("prefFloat") * 10), 555);
		assertEquals(preferences.getBoolean("prefBoolean"), true);
		assertEquals(preferences.getString("prefString"), "prefValue");
	}

	@Test
	public void testInvalidPreference() {
		controller.action(SetPreference.class, "prefInvalid", new Object());

		Preferences preferences = controller.getPreferences();

		assertEquals(preferences.getString("prefInvalid"), "");
	}

}
