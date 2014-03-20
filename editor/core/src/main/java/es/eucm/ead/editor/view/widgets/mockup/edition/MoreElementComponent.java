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

	public MoreElementComponent(EditionWindow parent, Controller controller,
			Skin skin) {
		super(parent, controller, skin);

		final MenuButton actionsButton = new BottomProjectMenuButton(viewport,
				i18n.m("edition.tool.advanced"), skin, IC_SETTINGS,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		this.viewport = controller.getPlatform().getSize();

		setVisible(false);
		setModal(true);

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

	public Actor getExtras() {
	protected Note getNote(Model model) {
		return null;
	}
}
