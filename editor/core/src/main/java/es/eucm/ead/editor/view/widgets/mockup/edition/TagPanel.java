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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.editor.view.widgets.mockup.panels.GridPanel;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A panel with all the {@link String tags} and a button to create a new
 * {@link String tag}.
 * 
 */
public class TagPanel extends Table {

	private static final String IC_DELETE = "ic_delete";

	private static final float PREF_WIDTH = .9f;
	private static final float PREF_HEIGHT = .9f;
	private static final int MAX_TAG_CARACTERS = 20;

	private final Skin skin;
	private final I18N i18n;
	private final Vector2 viewport;
	private final Controller controller;
	private final Array<String> addedTags;
	private final GridPanel<Button> innerTagGrid;

	public TagPanel(final Controller controller, final Skin skin) {
		super();

		this.skin = skin;
		this.controller = controller;
		this.addedTags = new Array<String>(false, 16, String.class);
		this.setVisible(false);

		this.i18n = controller.getApplicationAssets().getI18N();
		this.viewport = controller.getPlatform().getSize();

		this.innerTagGrid = new GridPanel<Button>(2, 15);
		this.innerTagGrid.debug();
		final ScrollPane innerScroll = new ScrollPane(this.innerTagGrid);
		innerScroll.setScrollingDisabled(true, false);

		final Table bottom = new Table();
		final Button newTag = new TextButton(
				this.i18n.m("general.edition.new_tag"), skin);
		final Button back = new TextButton(this.i18n.m("general.gallery.back"),
				skin);

		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((HiddenPanel) getParent().getParent()).hide();
			}
		});

		newTag.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.input.getTextInput(new TextInputListener() {

					@Override
					public void input(String text) {
						if (text == null || text.isEmpty())
							return;

						if (text.length() > TagPanel.MAX_TAG_CARACTERS) {
							text = text
									.substring(0, TagPanel.MAX_TAG_CARACTERS);
						}

						if (TagPanel.this.addedTags.contains(text, false))
							return;

						final Button tagButton = getTagButton(text);
						TagPanel.this.addedTags.add(text);
						TagPanel.this.innerTagGrid.addItem(tagButton);
						// TODO change to an action (AddTagAction) when
						// getEditElement() is available
						// For now it's just for testing purposes
						ModelEntity element = (ModelEntity) controller
								.getModel().getEditScene().getChildren().get(0);

						Tags tags = null;
						for (ModelComponent c : element.getComponents()) {
							if (c instanceof Tags) {
								tags = (Tags) c;
							}
						}

						if (tags == null) {
							tags = new Tags();
							element.getComponents().add(tags);
						}

						tags.getTags().add(text);
					}

					@Override
					public void canceled() {

					}

				}, TagPanel.this.i18n.m("general.edition.enter_name_tag"), "");
			}
		});

		bottom.add(back).left().expandX();
		bottom.add(newTag).right();

		this.defaults().fillX();
		this.add(innerScroll).expand().top();
		this.row();
		this.add(bottom).expandX();
	}

	public void seVisible(boolean visible) {
		if (visible) {
			updateUItags();
		} else {
			this.innerTagGrid.clear();
			this.addedTags.clear();
		}
	}

	private void updateUItags() {
		Map<String, Object> scenes = this.controller.getModel().getResources(
				ResourceCategory.SCENE);
		for (Object scene : scenes.values()) {
			List<ModelEntity> children = ((ModelEntity) scene).getChildren();
			for (ModelEntity element : children) {
				List<String> tags = null;
				for (ModelComponent c : element.getComponents()) {
					if (c instanceof Tags) {
						tags = ((Tags) c).getTags();
					}
				}
				if (tags != null) {
					for (String tag : tags) {
						if (!this.addedTags.contains(tag, false)) {
							this.addedTags.add(tag);
						}
					}
				}
			}
		}
		Arrays.sort(this.addedTags.items, 0, this.addedTags.size);
		for (final String tag : this.addedTags) {
			this.innerTagGrid.addItem(getTagButton(tag));
		}
	}

	private Button getTagButton(final String text) {
		final Button tagButton = new Button(this.skin);
		final Button delete = new ToolbarButton(TagPanel.this.viewport,
				this.skin.getDrawable(IC_DELETE),
				this.i18n.m("general.delete"), this.skin);
		delete.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// TODO change to an action (RemoveTagAction) when
				// getEditElement() is available
				// For now it's just for testing purposes
				List<String> tags = null;
				for (ModelComponent c : controller.getModel().getEditScene()
						.getChildren().get(0).getComponents()) {
					if (c instanceof Tags) {
						tags = ((Tags) c).getTags();
					}
				}
				if (tags != null && tags.remove(text)) {
					TagPanel.this.innerTagGrid.clear();
					TagPanel.this.addedTags.clear();
					updateUItags();
				}
			}
		});

		tagButton.add(text).expandX();
		tagButton.add(delete).right();
		return tagButton;
	}

	@Override
	public float getPrefWidth() {
		return this.viewport.x * TagPanel.PREF_WIDTH;
	}

	@Override
	public float getPrefHeight() {
		return this.viewport.y * TagPanel.PREF_HEIGHT;
	}
}
