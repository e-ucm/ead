/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.view.controllers.options;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.controllers.OptionsController;
import es.eucm.ead.editor.view.widgets.ToggleImagesList;
import es.eucm.ead.editor.view.widgets.ToggleImagesList.ChangeListener;
import es.eucm.ead.editor.view.widgets.options.Option;
import es.eucm.ead.engine.I18N;

public class ToggleImagesController extends
		OptionController<ToggleImagesList, String> {

	public ToggleImagesController(I18N i18N,
			OptionsController optionsController, String field, Option option,
			ToggleImagesList widget) {
		super(i18N, optionsController, field, option, widget);
	}

	@Override
	protected void initialize() {
		widget.addChangeListener(new ChangeListener() {
			@Override
			public void changed(String newValue) {
				change(widget.getSelectedValue());
			}
		});
	}

	@Override
	protected void setWidgetValue(String value) {
		widget.setValue(value);
	}

	public ToggleImagesController button(Drawable drawable, String value) {
		widget.button(drawable, value);
		return this;
	}
}
