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

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.buttons.FlagButton;
import es.eucm.ead.editor.view.widgets.mockup.panels.GridPanel;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.VariableDef;

/**
 * A panel with all boolean {@link VariableDef} and button to create a new flag
 * 
 */
public class FlagPanel extends HiddenPanel {

	private List<VariableDef> flags;

	private GridPanel<TextButton> inner;

	private Skin skin;

	/**
	 * The FlagButton that calls this Panel
	 */
	private FlagButton parent;

	private Vector2 viewport;

	public FlagPanel(Controller controller, final Skin skin) {
		super(skin);

		this.setVisible(false);

		final I18N i18n = controller.getApplicationAssets().getI18N();
		this.viewport = controller.getPlatform().getSize();
		this.flags = controller.getModel().getGame().getVariablesDefinitions();
		this.skin = skin;

		this.inner = new GridPanel<TextButton>(4, 20);
		ScrollPane sp = new ScrollPane(inner, skin);

		for (final VariableDef i : flags) {
			if (i.getType() == VariableDef.Type.BOOLEAN) {
				final FlagButton flagButton = new FlagButton(i, this.viewport,
						skin);
				inner.addItem(flagButton);
				flagButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						parent.setVariableDef(i);
						FlagPanel.this.hide();
					}
				});
			}
		}

		Table bottom = new Table(skin);
		Button back = new TextButton(i18n.m("general.gallery.back"), skin);
		Button newFlag = new TextButton(i18n.m("general.edition.new_flag"),
				skin);

		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				FlagPanel.this.hide();
			}
		});

		newFlag.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.input.getTextInput(new TextInputListener() {

					@Override
					public void input(String text) {
						final VariableDef newFlag = new VariableDef();
						final String name = text;
						newFlag.setName(name);
						newFlag.setType(VariableDef.Type.BOOLEAN);
						newFlag.setInitialValue("false");

						FlagPanel.this.flags.add(newFlag);
						final FlagButton flagButton = new FlagButton(newFlag,
								FlagPanel.this.viewport, skin);
						flagButton.addListener(new ClickListener() {
							@Override
							public void clicked(InputEvent event, float x,
									float y) {
								parent.setVariableDef(newFlag);
								FlagPanel.this.hide();
							}
						});
						inner.addItem(flagButton);
					}

					@Override
					public void canceled() {
						// TODO Auto-generated method stub

					}

				}, i18n.m("general.edition.enter_name_flag"), "");
			}
		});

		bottom.add(back).left();
		bottom.add("").expandX();
		bottom.add(newFlag).right();

		Label title = new Label("FLAGS", skin);
		title.setAlignment(Align.center);
		this.add(title).top().expandX().fillX();
		this.row();
		this.add(sp).expand().fill();
		this.row();
		this.add(bottom).expandX().fillX();
	}

	public void show() {
		for (final VariableDef i : flags) {
			if (i.getType() == VariableDef.Type.BOOLEAN) {
				final FlagButton flagButton = new FlagButton(i, this.viewport,
						skin);
				inner.addItem(flagButton);

				flagButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						parent.setVariableDef(i);
					}
				});
			}
		}
		this.setVisible(true);
	}

	public void hide() {
		this.setVisible(false);
		inner.clear();
	}

	public void setParentButton(FlagButton button) {
		this.parent = button;
	}

	@Override
	public float getPrefWidth() {
		return viewport.x * .7f;
	}

	@Override
	public float getPrefHeight() {
		return viewport.y * .8f;
	}
}
