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
package es.eucm.ead.editor.view.controllers.options;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import es.eucm.ead.editor.view.controllers.OptionsController;
import es.eucm.ead.editor.view.widgets.options.Option;
import es.eucm.ead.engine.I18N;

import java.util.Map;

/**
 * Created by angel on 20/03/14.
 */
public class SelectOptionController extends OptionController<SelectBox, Object> {

	private Map<String, Object> values;

	public SelectOptionController(I18N i18N,
			OptionsController optionsController, String field, Option option,
			SelectBox widget, Map<String, Object> values) {
		super(i18N, optionsController, field, option, widget);
		this.values = values;
	}

	@Override
	protected void initialize() {
		widget.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				change(values.get(widget.getSelected().toString()));
			}
		});
	}

	@Override
	public void setWidgetValue(Object value) {
		widget.setSelected(value.toString());
	}
}
