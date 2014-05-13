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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.RenameScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TabButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TweenButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TweenDragButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TweenDragButton.TweenType;
import es.eucm.ead.editor.view.widgets.mockup.panels.TabPanel;
import es.eucm.ead.editor.view.widgets.mockup.panels.TweenEditionPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.entities.ModelEntity;

public class MoreElementComponent extends MoreComponent {

	private static final float PAD_TWEEN = 0.04f;

	private static final String IC_SETTINGS = "ic_elementssettings",
			IC_REMOVE = "ic_remove_tween", IC_MOVE = "ic_move_tween",
			IC_ROTATE = "ic_rotate_tween", IC_SCALE = "ic_scale_tween",
			IC_ALPHA = "ic_alpha_tween";

	private final TabPanel<Button, Table> tab;
	private final FlagPanel flagPanel;
	private TweenEditionPanel tweensEditionPanel;

	public MoreElementComponent(EditionWindow parent, Controller controller,
			final Skin skin) {
		super(parent, controller, skin);

		final I18N i18n = controller.getApplicationAssets().getI18N();

		final MenuButton actionsButton = new BottomProjectMenuButton(
				this.viewport, i18n.m("edition.tool.advanced"), skin,
				MoreElementComponent.IC_SETTINGS,
				MoreComponent.PREF_BOTTOM_BUTTON_WIDTH,
				MoreComponent.PREF_BOTTOM_BUTTON_HEIGHT, Position.RIGHT);

		this.flagPanel = new FlagPanel(controller, skin);

		final Table contitionsTable = new Table(skin);
		contitionsTable.add(i18n.m("general.edition.visible_if"));
		contitionsTable.row();
		final Table innerTable = new Table();

		final ScrollPane innerScroll = new ScrollPane(innerTable);
		innerScroll.setScrollingDisabled(true, false);

		contitionsTable.add(innerScroll).expand().fill();
		contitionsTable.debug();
		final Button accept = new TextButton(i18n.m("general.accept"), skin);
		accept.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				MoreElementComponent.this.tab.hide();
				return false;
			}
		});

		final Button newCondition = new TextButton(
				i18n.m("general.new_condition"), skin);
		newCondition.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				innerTable
						.add(new ConditionWidget(
								MoreElementComponent.super.viewport, i18n,
								MoreElementComponent.this.flagPanel, skin))
						.expandX().fill();
				return false;
			}
		});

		contitionsTable.row();

		final Table bottom = new Table();
		bottom.add(accept).left().expandX();
		bottom.add(newCondition).right();
		contitionsTable.add(bottom).expandX().fillX();

		// Table of tweens drag'n'drop buttons
		final Table tweensTable = new Table(skin);

		LinearLayout listTweens = new LinearLayout(true);
		listTweens.defaultWidgetsMargin(viewport.x * PAD_TWEEN, 0, viewport.x
				* PAD_TWEEN, 0);

		tweensTable.add(listTweens);
		tweensTable.row();

		DragAndDrop dragAndDrop = new DragAndDrop();
		final DragAndDrop dragAndRemove = new DragAndDrop();

		TweenDragButton tMove = new TweenDragButton(skin, IC_MOVE, i18n.m(
				"general.edition.move").toUpperCase(), TweenType.MOVE,
				dragAndDrop);
		listTweens.add(tMove);
		TweenDragButton tRotate = new TweenDragButton(skin, IC_ROTATE, i18n.m(
				"general.edition.rotate").toUpperCase(), TweenType.ROTATE,
				dragAndDrop);
		listTweens.add(tRotate);
		TweenDragButton tScale = new TweenDragButton(skin, IC_SCALE, i18n.m(
				"general.edition.scale").toUpperCase(), TweenType.SCALE,
				dragAndDrop);
		listTweens.add(tScale);
		TweenDragButton tAlpha = new TweenDragButton(skin, IC_ALPHA, i18n.m(
				"general.edition.alpha").toUpperCase(), TweenType.ALPHA,
				dragAndDrop);
		listTweens.add(tAlpha);
		TweenDragButton tRemove = new TweenDragButton(skin, IC_REMOVE, i18n.m(
				"general.delete").toUpperCase(), TweenType.REMOVE,
				dragAndRemove);
		listTweens.add(tRemove);

		// Table with selected tweens
		final Table tweens = new Table(skin);

		ScrollPane spTweens = new ScrollPane(tweens);
		spTweens.setScrollingDisabled(false, true);

		final LinearLayout list1 = new LinearLayout(true);
		final LinearLayout list2 = new LinearLayout(true);
		final LinearLayout list3 = new LinearLayout(true);

		Image sep1 = new Image(skin.getDrawable("row-separator"));
		Image sep2 = new Image(skin.getDrawable("row-separator"));
		Image sep3 = new Image(skin.getDrawable("row-separator"));

		tweens.add(sep1).expandX().fillX().center();
		tweens.row();
		tweens.add(list1).expand().fill();
		tweens.row();
		tweens.add(sep2).expandX().fillX().center();
		tweens.row();
		tweens.add(list2).expand().fill();
		tweens.row();
		tweens.add(sep3).expandX().fillX().center();
		tweens.row();
		tweens.add(list3).expand().fill();

		Label label1 = new Label(i18n.m("general.edition.tween-track") + "-1",
				skin);
		Label label2 = new Label(i18n.m("general.edition.tween-track") + "-2",
				skin);
		Label label3 = new Label(i18n.m("general.edition.tween-track") + "-3",
				skin);

		list1.add(label1);
		list2.add(label2);
		list3.add(label3);

		tweensEditionPanel = new TweenEditionPanel(skin, i18n);

		final ClickListener clickTweenButton = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TweenButton button = (TweenButton) event.getListenerActor();
				tweensEditionPanel.show(button.getType(), button.getTween());
			}
		};

		dragAndDrop
				.addTarget(listTarget(dragAndRemove, list1, clickTweenButton));
		dragAndDrop
				.addTarget(listTarget(dragAndRemove, list2, clickTweenButton));
		dragAndDrop
				.addTarget(listTarget(dragAndRemove, list3, clickTweenButton));

		tweensTable.add(spTweens).expand().fill();

		/* Tags */
		final Button tags = new TabButton(i18n.m("general.tag-plural"), skin), contitions = new TabButton(
				i18n.m("general.visibility"), skin), behaviors = new TabButton(
				i18n.m("general.edition.tween"), skin);

		final Table tagsTable = new TagPanel(controller, skin);

		final Array<Button> buttons = new Array<Button>(false, 3);
		buttons.add(tags);
		buttons.add(contitions);
		buttons.add(behaviors);

		final Array<Table> tables = new Array<Table>(false, 3);
		tables.add(tagsTable);
		tables.add(contitionsTable);
		tables.add(tweensTable);

		this.tab = new TabPanel<Button, Table>(tables, buttons, .95f, .95f,
				super.viewport, skin);
		this.tab.setVisible(false);

		actionsButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				MoreElementComponent.this.tab.show();
				return false;
			}
		});

		this.row();
		this.add(actionsButton);
	}

	private Target listTarget(final DragAndDrop dragAndRemove,
			final LinearLayout layout, final ClickListener clickTweenButton) {

		return new Target(layout) {
			public boolean drag(Source source, Payload payload, float x,
					float y, int pointer) {
				return true;
			}

			public void drop(Source source, Payload payload, float x, float y,
					int pointer) {
				String icon = ((TweenDragButton) source.getActor()).getIcon();
				TweenType type = ((TweenDragButton) source.getActor())
						.getType();
				final TweenButton sourceImage = new TweenButton(skin, icon
						+ "_on", type, clickTweenButton);
				final LinearLayout aux = (LinearLayout) getActor();
				aux.add(sourceImage);
				dragAndRemove.addTarget(new Target(sourceImage) {

					@Override
					public boolean drag(Source source, Payload payload,
							float x, float y, int pointer) {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					public void drop(Source source, Payload payload, float x,
							float y, int pointer) {
						SnapshotArray<Actor> children = new SnapshotArray<Actor>(
								layout.getChildren());
						int num = children.indexOf(getActor(), true);

						layout.clearChildren();

						for (int i = 0; i < num; i++) {
							layout.add(children.get(i));
						}
						for (int i = num + 1; i < children.size; i++) {
							layout.add(children.get(i));
						}
					}

				});
				sourceImage.setVisible(true);
			}
		};

	}

	@Override
	protected Class<?> getNoteActionClass() {
		return RenameScene.class;
	}

	@Override
	public Array<Actor> getExtras() {
		final Array<Actor> actors = new Array<Actor>(false, 3);
		actors.add(this.tab);
		actors.add(this.flagPanel);
		actors.add(this.tweensEditionPanel);
		return actors;
	}

	@Override
	protected Note getNote(Model model) {
		Object o = model.getSelection().first();
		if (o instanceof ModelEntity) {
			return Model.getComponent((ModelEntity) o, Note.class);
		} else {
			return null;
		}
	}
}
