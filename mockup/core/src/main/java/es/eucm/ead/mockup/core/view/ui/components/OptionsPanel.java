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
package es.eucm.ead.mockup.core.view.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.mockup.core.control.screens.AbstractScreen;
import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.Panel;

public class OptionsPanel extends Panel {

	private float x, y;

	public OptionsPanel(Skin skin) {
		super(skin, "default");
	}

	public OptionsPanel(Skin skin, String styleName) {
		super(skin, styleName);
		this.x = AbstractScreen.stagew * .6f;
		this.y = UIAssets.TOOLBAR_HEIGHT;
		setBounds(AbstractScreen.stagew, y, (AbstractScreen.stagew - UIAssets.NAVIGATION_BUTTON_WIDTH_HEIGHT) - x,
				AbstractScreen.stageh - y*2);
		setVisible(false);
		setColor(Color.ORANGE);
		setModal(true);

		Label cbs1 = new Label("SKINS", skin);
		CheckBox cbs2 = new CheckBox("Skin predefinido", skin, "default-radio");
		CheckBox cbs3 = new CheckBox("Skin divertido", skin);
		CheckBox cbs4 = new CheckBox("Skin serio", skin);
		CheckBox cbs5 = new CheckBox("Skin profesional", skin);
		Label cbs6 = new Label("- - - - - - - - - - - - -", skin);
		Label cbs7 = new Label("IDIOMAS", skin);
		CheckBox cbs8 = new CheckBox("ES/Español", skin);
		CheckBox cbs9 = new CheckBox("EN/Inglés", skin);
		CheckBox cbs10 = new CheckBox("FR/Francés", skin);
		ImageButton ib = new ImageButton(skin);

		Table t = new Table();
		ScrollPane sp = new ScrollPane(t, skin);
		sp.setupFadeScrollBars(0f, 0f);
		sp.setScrollingDisabled(true, false);
		t.add(ib);
		t.row();
		t.add(cbs1);
		t.row();
		t.add(cbs2).left();
		t.row();
		t.add(cbs3).left();
		t.row();
		t.add(cbs4).left();
		t.row();
		t.add(cbs5).left();
		t.row();
		t.add(cbs6);
		t.row();
		t.add(cbs7);
		t.row();
		t.add(cbs8).left();
		t.row();
		t.add(cbs9).left();
		t.row();
		t.add(cbs10).left();
		add(sp);
	}

	@Override
	public void show() {
		super.show();
		addAction(Actions.moveTo(x, y, fadeDuration));
	}

	@Override
	public void hide() {
		super.hide();
		addAction(Actions.moveTo(AbstractScreen.stagew, y, fadeDuration));
	}
}
