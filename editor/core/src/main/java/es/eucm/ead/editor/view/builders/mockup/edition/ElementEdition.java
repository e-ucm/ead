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
package es.eucm.ead.editor.view.builders.mockup.edition;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.edition.EditionComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.EraserComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.PaintComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.SelectComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.TextComponent;
import es.eucm.ead.schema.actors.SceneElement;

/**
 * A view that allows the user to edit {@link SceneElement}s.
 */
public class ElementEdition extends EditionWindow {

	public static final String NAME = "mockup_element_edition";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected void editionComponents(Array<EditionComponent> editionComponents,
			Vector2 viewport, Controller controller, Skin skin) {
		editionComponents.add(new PaintComponent(this, controller, skin));
		editionComponents.add(new EraserComponent(this, controller, skin));
		editionComponents.add(new TextComponent(this, controller, skin));
		editionComponents.add(new SelectComponent(this, controller, skin));
	}
}
