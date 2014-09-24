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
package es.eucm.ead.editor.view.builders.gallery;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.engine.I18N;

public class CreditsView implements ViewBuilder {

	private Stack window;

	private static final String IC_GO_BACK = "back80x80";
	private static final String GITHUB = "https://github.com/e-ucm/ead";

	private Controller controller;

	private Skin skin;
	private I18N i18n;

	private Label drotaru;
	private Label gorco;
	private Label anserran;
	private Label jtorrente;
	private Label imartinezortiz;
	private Label manuelfreire;
	private Label angeldelblanco;
	private Label balta;
	private Label license;

	private Label github;

	@Override
	public void initialize(final Controller controller) {
		skin = controller.getApplicationAssets().getSkin();
		this.i18n = controller.getApplicationAssets().getI18N();
		this.controller = controller;

		float littlePad = Gdx.graphics.getHeight() * 0.01f;
		float normalPad = Gdx.graphics.getHeight() * 0.03f;
		float bigPad = Gdx.graphics.getWidth() * 0.1f;

		window = new Stack();
		window.setFillParent(true);

		IconButton back = new IconButton(IC_GO_BACK, 0, skin, "inverted") {
			@Override
			public void layout() {
				super.layout();
				setBounds(0f, getParent().getHeight() - getPrefHeight(),
						getPrefWidth(), getPrefHeight());
			}
		};
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((MockupViews) controller.getViews()).onBackPressed();
			}
		});

		initializeMembers();

		Table rightTable = new Table();
		rightTable.align(Align.top);

		Table leftTable = new Table();
		leftTable.align(Align.top);

		Table topTable = new Table();
		leftTable.align(Align.top);

		Table bottomTable = new Table();
		leftTable.align(Align.top);

		Table mainTable = new Table();
		mainTable.align(Align.top);

		topTable.add(new Image(skin.getDrawable("gameMockupEditor"))).top()
				.padBottom(littlePad);
		topTable.row();
		topTable.add(new Label(i18n.m("about.eAdventureProduct"), skin))
				.padBottom(littlePad);
		topTable.row();
		topTable.add(new Image(skin.getDrawable("eAdventure"))).padBottom(
				normalPad);

		// Main developers
		leftTable.add(new Label(i18n.m("about.developers") + ":", skin))
				.padBottom(littlePad);
		leftTable.row();
		leftTable.add(drotaru).padBottom(littlePad);
		leftTable.row();
		leftTable.add(gorco).padBottom(normalPad);
		leftTable.row();

		// Project Managers
		leftTable.add(new Label(i18n.m("about.projectManager") + ":", skin))
				.padBottom(littlePad);
		leftTable.row();
		leftTable.add(balta).padBottom(littlePad);
		leftTable.row();
		leftTable.add(jtorrente).padBottom(normalPad);
		leftTable.row();

		// Technical Manager
		rightTable.add(new Label(i18n.m("about.technicalManager") + ":", skin))
				.padBottom(littlePad);
		rightTable.row();
		rightTable.add(anserran).padBottom(normalPad);
		rightTable.row();

		// Contributors
		rightTable.add(new Label(i18n.m("about.contributors") + ":", skin))
				.padBottom(littlePad);
		rightTable.row();
		rightTable.add(manuelfreire).padBottom(littlePad);
		rightTable.row();
		rightTable.add(imartinezortiz).padBottom(littlePad);
		rightTable.row();
		rightTable.add(angeldelblanco).padBottom(normalPad);
		rightTable.row();

		// Thanks
		bottomTable.add(new Label(i18n.m("about.thanks") + ":", skin))
				.padBottom(littlePad);
		bottomTable.row();
		bottomTable.add(
				new Label("Borja Manero Iglesias, Pablo Moreno-Ger,", skin,
						"credits")).padBottom(littlePad);
		bottomTable.row();
		bottomTable.add(
				new Label("Miguel Collado Segura, David Serrano Arce, ", skin,
						"credits")).padBottom(littlePad);
		bottomTable.row();
		bottomTable.add(
				new Label(
						"Daniel Alejandro Nowndsztern, Frank Elvis Canchari. ",
						skin, "credits")).padBottom(normalPad);
		bottomTable.row();
		bottomTable.add(license).padBottom(littlePad);
		bottomTable.row();
		bottomTable.add(new Label(i18n.m("about.github"), skin)).padBottom(
				littlePad);
		bottomTable.row();
		bottomTable.add(github).padBottom(littlePad);

		mainTable.add(topTable).expandX().fill().colspan(2);
		mainTable.row();
		mainTable.add(leftTable).expandX().fill().left().uniform()
				.padRight(bigPad);
		mainTable.add(rightTable).expandX().fill().right().uniform()
				.padLeft(bigPad);
		mainTable.row();
		mainTable.add(bottomTable).expandX().fill().colspan(2);

		ScrollPane scroll = new ScrollPane(mainTable);
		Table aux = new Table();
		aux.setFillParent(true);
		aux.add(scroll);
		window.add(aux);
		window.add(back);
	}

	private void initializeMembers() {
		drotaru = new Label("Dan Cristian Rotaru", skin, "credits_link");

		gorco = new Label("Antonio Calvo Morata", skin, "credits_link");

		anserran = new Label("Ángel Serrano Laguna", skin, "credits_link");

		jtorrente = new Label("Javier Torrente", skin, "credits_link");

		manuelfreire = new Label("Manuel Freire Morán", skin, "credits_link");

		imartinezortiz = new Label("Iván Martínez Ortiz", skin, "credits_link");

		angeldelblanco = new Label("Ángel del Blanco Aguado", skin,
				"credits_link");

		balta = new Label("Baltasar Fernández Manjón", skin, "credits_link");

		github = new Label(GITHUB, skin, "credits_link");

		license = new Label(i18n.m("about.license"), skin);

		ClickListener listener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == drotaru) {
					controller.getPlatform().browseURL(
							"https://github.com/RotaruDan");
				} else if (listenerActor == gorco) {
					controller.getPlatform().browseURL(
							"https://github.com/gorco");
				} else if (listenerActor == anserran) {
					controller.getPlatform().browseURL(
							"https://github.com/anserran");
				} else if (listenerActor == jtorrente) {
					controller.getPlatform().browseURL(
							"https://github.com/jtorrente");
				} else if (listenerActor == manuelfreire) {
					controller.getPlatform().browseURL(
							"https://github.com/manuel-freire");
				} else if (listenerActor == imartinezortiz) {
					controller.getPlatform().browseURL(
							"https://github.com/imartinezortiz");
				} else if (listenerActor == angeldelblanco) {
					controller.getPlatform().browseURL(
							"https://github.com/angeldelblanco");
				} else if (listenerActor == balta) {
					controller.getPlatform().browseURL(
							"http://www.e-ucm.es/es/people/balta/");
				} else if (listenerActor == github) {
					controller.getPlatform().browseURL(
							"https://github.com/e-ucm/ead");
				} else if (listenerActor == license) {
					controller.getPlatform().browseURL(
							"http://www.gnu.org/licenses/lgpl.html");
				}
			}
		};

		drotaru.addListener(listener);
		gorco.addListener(listener);
		anserran.addListener(listener);
		jtorrente.addListener(listener);
		manuelfreire.addListener(listener);
		imartinezortiz.addListener(listener);
		angeldelblanco.addListener(listener);
		balta.addListener(listener);
		github.addListener(listener);
		license.addListener(listener);
	}

	@Override
	public void release(Controller controller) {

	}

	@Override
	public Actor getView(Object... args) {
		return window;
	}

}
