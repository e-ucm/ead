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

	private static final float PREF_WIDTH = .75f;
	private static final float PREF_HEIGHT = .8f;

	private final List<VariableDef> flags;

	private final GridPanel<FlagButton> inner;

	private final Skin skin;

	/**
	 * The FlagButton that calls this Panel
	 */
	private FlagButton parent;

	private final Vector2 viewport;

	public FlagPanel(Controller controller, final Skin skin) {
		super(skin);

		this.setVisible(false);

		final I18N i18n = controller.getApplicationAssets().getI18N();
		this.viewport = controller.getPlatform().getSize();
		this.flags = controller.getModel().getGame().getVariablesDefinitions();
		this.skin = skin;

		this.inner = new GridPanel<FlagButton>(3, 15);
		this.inner.debug();
		final ScrollPane innerScroll = new ScrollPane(this.inner, skin);
		innerScroll.setScrollingDisabled(true, false);

		final Table bottom = new Table();
		final Button back = new TextButton(i18n.m("general.gallery.back"), skin);
		final Button newFlag = new TextButton(
				i18n.m("general.edition.new_flag"), skin);

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
								skin);
						flagButton.addListener(new ClickListener() {
							@Override
							public void clicked(InputEvent event, float x,
									float y) {
								FlagPanel.this.parent.setVariableDef(newFlag);
								FlagPanel.this.hide();
							}
						});
						FlagPanel.this.inner.addItem(flagButton);
					}

					@Override
					public void canceled() {

					}

				}, i18n.m("general.edition.enter_name_flag"), "");
			}
		});

		bottom.add(back).left().expandX();
		bottom.add(newFlag).right();

		final Label title = new Label("FLAGS", skin);
		title.setAlignment(Align.center);
		this.defaults().fillX();
		this.add(title).top().expandX();
		this.row();
		this.add(innerScroll).expand().top();
		this.row();
		this.add(bottom).expandX();
	}

	public void show() {
		super.show();
		for (final VariableDef variableDef : this.flags) {
			if (variableDef.getType() == VariableDef.Type.BOOLEAN) {
				final FlagButton flagButton = new FlagButton(variableDef,
						this.skin);
				this.inner.addItem(flagButton);

				flagButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						FlagPanel.this.parent.setVariableDef(variableDef);
						FlagPanel.this.hide();
					}
				});
			}
		}
	}

	public void hide() {
		super.hide();
		this.inner.clear();
	}

	public void setParentButton(FlagButton button) {
		this.parent = button;
	}

	@Override
	public float getPrefWidth() {
		return viewport.x * PREF_WIDTH;
	}

	@Override
	public float getPrefHeight() {
		return viewport.y * PREF_HEIGHT;
	}
}
