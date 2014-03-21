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
package es.eucm.ead.editor.view.widgets.mockup.edition;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.view.widgets.mockup.buttons.FlagButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;

public class ConditionWidget extends Table {

	private static final float DEFAULT_SPACE = 15f;

	private final SelectBox<String> state;
	private String name;

	public ConditionWidget(Vector2 viewport, I18N i18n,
			final FlagPanel flagPanel, Skin skin) {
		super();

		this.name = "FLAG";

		final FlagButton flag = new FlagButton(this.name, skin);
		flag.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				flagPanel.show();
				flagPanel.setParentButton(flag);
			}
		});

		this.state = new SelectBox<String>(skin);

		final String[] states = { i18n.m("general.inactive"),
				i18n.m("general.active") };
		this.state.setItems(states);

		final Button delete = new ToolbarButton(viewport,
				skin.getDrawable("ic_delete"), i18n.m("general.delete"), skin);
		delete.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ConditionWidget.this.remove();
			}
		});

		this.defaults().space(DEFAULT_SPACE);
		this.add(flag).fill();
		this.add(this.state).expandX().fill();
		this.add(delete);
	}

	public String getStateSelected() {
		return this.state.getSelected();
	}

	public String getFlag() {
		return this.name;
	}
}
