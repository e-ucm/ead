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
package es.eucm.ead.editor.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.eucm.ead.editor.control.actions.model.ChangeLabelText;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schemax.FieldName;

public class ChangeLabelTextTest extends ActionTest {

	String newText = "THE_NEW_TEXT";

	private boolean somethingChanged;

	@Test
	public void testChangeText() {
		final Label label = new Label();
		label.setText("THE OLD TEXT");

		controller.getModel().addFieldListener(label, new FieldListener() {

			@Override
			public void modelChanged(FieldEvent event) {
				ChangeLabelTextTest.this.somethingChanged = true;
			}

			@Override
			public boolean listenToField(String fieldName) {
				return fieldName.equals(FieldName.TEXT);
			}
		});

		this.somethingChanged = false;
		controller.action(ChangeLabelText.class, label, newText);

		assertTrue(this.somethingChanged);
		assertEquals(label.getText(), newText);
	}
}
