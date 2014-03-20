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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.RenameMetadataObject;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.editor.view.widgets.mockup.panels.TabsPanel;

public class MoreElementComponent extends MoreComponent {

	private static final String IC_SETTINGS = "ic_editactions";

	private final TabsPanel<Button, Table> tab;
	private final Table inner;
	private FlagPanel flagPanel;

	public MoreElementComponent(EditionWindow parent, Controller controller,
			final Skin skin) {
		super(parent, controller, skin);

		final I18N i18n = controller.getApplicationAssets().getI18N();

		final MenuButton actionsButton = new BottomProjectMenuButton(viewport,
				i18n.m("edition.tool.advanced"), skin, IC_SETTINGS,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		this.viewport = controller.getPlatform().getSize();

		this.flagPanel = new FlagPanel(controller, skin);

		setVisible(false);
		setModal(true);

		TabButton general = new TabButton(i18n.m("general.visibility"), skin);
		TabButton actions = new TabButton(i18n.m("general.actions"), skin);

		final Table botGeneral = new Table(skin);
		botGeneral.add(i18n.m("general.edition.visible_if"));
		botGeneral.row();
		this.inner = new Table();

		ScrollPane sp = new ScrollPane(inner, skin);

		botGeneral.add(sp).expand().fill();
		botGeneral.debug();
		Button accept = new TextButton(i18n.m("general.accept"), skin);
		accept.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				tab.hide();
				return true;
			}
		});

		Button newCon = new TextButton(i18n.m("general.new_condition"), skin);
		newCon.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				inner.add(new ConditionWidget(viewport, i18n, flagPanel, skin))
						.expandX();
				return true;
			}
		});

		botGeneral.row();

		Table bottom = new Table(skin);
		bottom.add(accept).left();
		bottom.add("").expandX();
		bottom.add(newCon).right();
		botGeneral.add(bottom).expandX().fillX();

		final Table botActions = new Table();
		botActions.add(new TextButton("Prueba2", skin)); // FIXME Do the panel

		Array<Button> buttons = new Array<Button>();
		buttons.add(general);
		buttons.add(actions);

		Array<Table> tables = new Array<Table>();
		tables.add(botGeneral);
		tables.add(botActions);

		this.tab = new TabsPanel<Button, Table>(tables, buttons, .8f, .9f,
				viewport, skin);
		this.tab.setVisible(false);

		actionsButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				tab.show();
				return true;
			}
		});

		this.row();
		this.add(actionsButton);
	}

	@Override
	protected Class<? extends RenameMetadataObject> getNameActionClass() {
		return null;
	}

	public Array<Actor> getExtras() {
	protected Note getNote(Model model) {
		return null;
		Array<Actor> actors = new Array<Actor>();
		actors.add(tab);
		actors.add(flagPanel);
		return actors;
	}
}
