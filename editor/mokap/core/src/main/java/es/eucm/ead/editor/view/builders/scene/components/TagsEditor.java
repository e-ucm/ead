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
package es.eucm.ead.editor.view.builders.scene.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.CreateTag;
import es.eucm.ead.editor.control.actions.model.generic.AddToArray;
import es.eucm.ead.editor.control.actions.model.generic.RemoveFromArray;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.TagsList;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ComponentIds;

public class TagsEditor extends ComponentEditor<Tags> implements
		Input.TextInputListener {

	private static final float PAD = 10;

	private ClickListener selectTag;

	private TagsList tagsList;

	private Tags tagComponent;

	private TextButton createTag;

	private LinearLayout tagsLayout;

	private int countTags;

	public TagsEditor(Controller cont) {
		super(SkinConstants.IC_TAG, cont.getApplicationAssets().getI18N()
				.m("tags"), ComponentIds.TAG, cont);
	}

	@Override
	protected void buildContent() {
		countTags = 0;

		tagsLayout = new LinearLayout(false);
		tagsList = Q.getComponent(controller.getModel().getGame(),
				TagsList.class);

		selectTag = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TextButton current = ((TextButton) event.getListenerActor());
				TextButton.TextButtonStyle style = current.getStyle();
				TextButton.TextButtonStyle context = skin.get(
						SkinConstants.STYLE_TEMPLATE,
						TextButton.TextButtonStyle.class);
				if (style == context) {
					selectTag(current);
				} else {
					current.setStyle(context);
					controller.action(RemoveFromArray.class, tagComponent,
							tagComponent.getTags(), current.getText()
									.toString());
					countTags--;
				}
			}
		};

		createTag = new TextButton(i18N.m("add_tag"), skin);
		createTag.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(CreateTag.class, TagsEditor.this);
			}
		});
		list.add(createTag);
		list.add(tagsLayout);
	}

	@Override
	public String getTooltip() {
		return controller.getApplicationAssets().getI18N().m("tags");
	}

	@Override
	protected void read(ModelEntity entity, Tags component) {
		tagsLayout.clearChildren();

		countTags = 0;
		tagComponent = component;

		tagsList.getTags().sort();
		for (String tag : tagsList.getTags()) {
			TextButton tagButton = new TextButton(tag, skin);
			tagButton.addListener(selectTag);
			tagsLayout.add(tagButton).marginTop(PAD);
			if (tagComponent.getTags().contains(tag, false)) {
				tagButton.setStyle(skin.get(SkinConstants.STYLE_ORANGE,
						TextButton.TextButtonStyle.class));
				countTags++;
			} else {
				tagButton.setStyle(skin.get(SkinConstants.STYLE_TEMPLATE,
						TextButton.TextButtonStyle.class));
			}
		}

	}

	private void selectTag(TextButton tagButton) {
		tagButton.setStyle(skin.get(SkinConstants.STYLE_ORANGE,
				TextButton.TextButtonStyle.class));
		controller.action(AddToArray.class, tagComponent,
				tagComponent.getTags(), tagButton.getText().toString());
		countTags++;
	}

	@Override
	public void release() {
		if (countTags == 0) {
			ModelEntity modelEntity = (ModelEntity) controller.getModel()
					.getSelection().getSingle(Selection.SCENE_ELEMENT);
			if (modelEntity != null) {
				controller.action(RemoveFromArray.class, modelEntity,
						modelEntity.getComponents(), tagComponent);
			}
		}
		super.release();
	}

	@Override
	protected Tags buildNewComponent() {
		tagComponent = new Tags();
		return tagComponent;
	}

	@Override
	public void input(String text) {
		text = text.trim();

		TextButton tagButton = new TextButton(text, skin);
		tagButton.addListener(selectTag);
		tagsLayout.add(0, tagButton).marginTop(PAD);

		selectTag(tagButton);
	}

	@Override
	public void canceled() {

	}
}
