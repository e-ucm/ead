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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.schema.components.VariableDef;

public class FlagPanel extends HiddenPanel {

	private List<VariableDef> flags;

	private Table inner;

	private Skin skin;

	private int added = 0;

	private TextButton parent;

	public FlagPanel(Controller controller, final Skin skin) {
		super(skin);

		this.setVisible(false);

		this.flags = controller.getModel().getGame().getVariablesDefinitions();
		this.skin = skin;

		inner = new Table(skin);
		ScrollPane sp = new ScrollPane(inner, skin);

		for (final VariableDef i : flags) {
			if (i.getType() == VariableDef.Type.BOOLEAN) {
				final TextButton flagButton = new TextButton(i.getName(), skin);
				inner.add(flagButton);
				flagButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						parent.setText(i.getName());
						FlagPanel.this.hide();
					}
				});
				FlagPanel.this.added++;
				if (FlagPanel.this.added % 4 == 0) {
					inner.row();
				}
			}
		}

		Table bottom = new Table(skin);
		Button back = new TextButton("Atrás", skin);
		Button newFlag = new TextButton("Nuevo flag", skin);

		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				FlagPanel.this.hide();
			}
		});

		newFlag.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				VariableDef nuevo = new VariableDef();
				final String name = "nuevo"; // FIXME The user enters text
				nuevo.setName(name);
				nuevo.setType(VariableDef.Type.BOOLEAN);

				FlagPanel.this.flags.add(nuevo);

				final TextButton flagButton = new TextButton(name, skin);
				flagButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						parent.setText(name);
						FlagPanel.this.hide();
					}
				});

				inner.add(flagButton);

				FlagPanel.this.added++;
				if (FlagPanel.this.added % 4 == 0) {
					inner.row();
				}
			}
		});

		bottom.add(back).left();
		bottom.add("").expandX();
		bottom.add(newFlag).right();

		this.add(sp);
		this.row();
		this.add(bottom);
	}

	public void show() {
		for (final VariableDef i : flags) {
			if (i.getType() == VariableDef.Type.BOOLEAN) {
				final TextButton flagButton = new TextButton(i.getName(), skin);
				inner.add(flagButton);

				flagButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						parent.setText(i.getName());
						FlagPanel.this.hide();
					}
				});
				FlagPanel.this.added++;
				if (FlagPanel.this.added % 4 == 0) {
					inner.row();
				}
			}
		}
		this.setVisible(true);
	}

	public void hide() {
		this.setVisible(false);
		inner.reset();
		added = 0;
	}

	public void setParentButton(TextButton button) {
		this.parent = button;
	}

}
