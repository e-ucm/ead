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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;

public class EffectsComponent extends EditionComponent {

	private static final String IC_EFFECTS = "ic_effects";
	private static final String IC_SETTINGS = "ic_settings";

	private static final int PAD_RIGHT = 30;

	public EffectsComponent(EditionWindow parent, Controller controller,
			Skin skin) {
		super(parent, controller, skin);

		final Label label = new Label(i18n.m("edition.tool.effects"), skin,
				"default-thin-opaque");
		label.setWrap(false);
		label.setAlignment(Align.center);
		label.setFontScale(0.7f);

		final Table table = new Table().debug();

		// TODO Load the real postprocessor tools
		CheckBox cb1 = new CheckBox("Color diluido", skin);
		CheckBox cb2 = new CheckBox("Pincel seco", skin);
		CheckBox cb3 = new CheckBox("Neón", skin);
		CheckBox cb4 = new CheckBox("Bordes", skin);
		CheckBox cb5 = new CheckBox("Sombreado", skin);
		CheckBox cb6 = new CheckBox("Ondas marinas", skin);
		CheckBox cb7 = new CheckBox("Efecto 7", skin);

		// Load options of postprocess tools
		final Drawable settingsDrawable = skin.getDrawable(IC_SETTINGS);
		Button prop1 = new IconButton(viewport, settingsDrawable);
		Button prop2 = new IconButton(viewport, settingsDrawable);
		Button prop3 = new IconButton(viewport, settingsDrawable);
		Button prop4 = new IconButton(viewport, settingsDrawable);
		Button prop5 = new IconButton(viewport, settingsDrawable);
		Button prop6 = new IconButton(viewport, settingsDrawable);
		Button prop7 = new IconButton(viewport, settingsDrawable);

		new ButtonGroup(prop1, prop2, prop3, prop4, prop5, prop6, prop7);

		table.add(cb1).left();
		table.add(prop1).right().padRight(PAD_RIGHT);
		table.row();
		table.add(cb2).left();
		table.add(prop2).right().padRight(PAD_RIGHT);
		table.row();
		table.add(cb3).left();
		table.add(prop3).right().padRight(PAD_RIGHT);
		table.row();
		table.add(cb4).left();
		table.add(prop4).right().padRight(PAD_RIGHT);
		table.row();
		table.add(cb5).left();
		table.add(prop5).right().padRight(PAD_RIGHT);
		table.row();
		table.add(cb6).left();
		table.add(prop6).right().padRight(PAD_RIGHT);
		table.row();
		table.add(cb7).left();
		table.add(prop7).right().padRight(PAD_RIGHT);

		final ScrollPane effectsScroll = new ScrollPane(table);
		effectsScroll.setupFadeScrollBars(0f, 0f);
		effectsScroll.setScrollingDisabled(true, false);

		this.add(label).fillX().expandX();
		this.row();
		this.add(effectsScroll);

	}

	@Override
	protected Button createButton(Vector2 viewport, Skin skin, I18N i18n) {
		return new ToolbarButton(viewport, skin.getDrawable(IC_EFFECTS),
				i18n.m("edition.effects"), skin);
	}

}
