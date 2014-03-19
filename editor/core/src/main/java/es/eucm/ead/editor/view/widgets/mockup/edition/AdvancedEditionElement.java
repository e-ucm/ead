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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.view.widgets.mockup.panels.TabsPanel;

public class AdvancedEditionElement extends TabsPanel<Button, Table> {

	private static final float PREF_WIDTH = 0.8f;
	private static final float PREF_HEIGHT = 0.9f;
	private final Vector2 viewport;
	
	public AdvancedEditionElement(Vector2 viewport, Skin skin) {
		super(skin);

		setVisible(false);
		setModal(true);
		this.viewport = viewport;
		Label generalLabel = new Label("General", skin);
		generalLabel.setAlignment(Align.center);
		final Button general = new Button(skin, "toggle");
		general.add(generalLabel).expandX();

		Label actionsLabel = new Label("Acciones", skin);
		actionsLabel.setAlignment(Align.center);
		final Button actions = new Button(skin, "toggle");
		actions.add(actionsLabel).expandX();

		final Table botGeneral = new Table();
		botGeneral.add(new TextButton("Prueba", skin));

		final Table botActions = new Table();
		botActions.add(new TextButton("Prueba2", skin));

		getTabTable().defaults().expandX().fill();
		addBinding(general, botGeneral);
		addBinding(actions, botActions);
		setCurrentTab(general);

		// getParent().addActor(flagPanel);
		// flagPanel.toFront();
	}

	@Override
	public float getPrefWidth() {
		// TODO Auto-generated method stub
		return viewport.x*PREF_WIDTH;
	}
	
	@Override
	public float getPrefHeight() {
		// TODO Auto-generated method stub
		return viewport.y*PREF_HEIGHT;
	}
}
