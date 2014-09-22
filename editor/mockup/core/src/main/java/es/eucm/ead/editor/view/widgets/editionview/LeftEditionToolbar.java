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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.ChangeVariablePanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.DeparturePanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.ShowTextPanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.SoundPanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.TouchabilityPanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.TweensPanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.VisibilityPanel;

public class LeftEditionToolbar extends Toolbar {

	private float width;

	public LeftEditionToolbar(Controller controller, String style, float width,
			float iconSize, float iconPad, float smallPad, float normalPad) {
		super(controller.getApplicationAssets().getSkin(), style);

		this.width = width;

		align(Align.center);
		DeparturePanel departurePanel = new DeparturePanel(iconPad, iconSize,
				controller);
		ChangeVariablePanel changeVariablePanel = new ChangeVariablePanel(
				iconPad, iconSize, controller);
		VisibilityPanel visibilityPanel = new VisibilityPanel(iconPad,
				iconSize, controller);
		TweensPanel tweensPanel = new TweensPanel(iconPad, iconSize, controller);
		TouchabilityPanel touchabilityPanel = new TouchabilityPanel(iconPad,
				iconSize, controller);
		SoundPanel sound = new SoundPanel(iconPad, iconSize, controller);
		ShowTextPanel conversations = new ShowTextPanel(iconPad, iconSize,
				controller);

		defaults().padBottom(smallPad).fill().expandX();

		addInNewRow(departurePanel);
		addInNewRow(changeVariablePanel);
		addInNewRow(sound);
		addInNewRow(conversations).padBottom(normalPad);
		addInNewRow(visibilityPanel);
		addInNewRow(touchabilityPanel);
		addInNewRow(tweensPanel);
	}

	@Override
	public float getPrefWidth() {
		return width;
	}

}