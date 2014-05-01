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

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.layouts.HorizontalLayout;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TabButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TweenButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TweenButton.TweenType;
import es.eucm.ead.editor.view.widgets.mockup.panels.TabPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Note;

public class MoreElementComponent extends MoreComponent {

	private static final float PAD_TWEEN =0.09f;
	private static final float PAD_TWEEN_ON =0.02f;
	
	private static final String IC_SETTINGS = "ic_elementssettings",
			IC_REMOVE = "ic_remove_tween", IC_MOVE = "ic_move_tween",
			IC_ROTATE = "ic_rotate_tween", IC_SCALE = "ic_scale_tween";

	private final TabPanel<Button, Table> tab;
	private final FlagPanel flagPanel;

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

		final Table tweensTable = new Table(skin);

		// //////////////XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX//////////////////
		final HorizontalLayout list1 = new HorizontalLayout();
		final HorizontalLayout list2 = new HorizontalLayout();
		final HorizontalLayout list3 = new HorizontalLayout();
		
		list1.setDefaultMargin(viewport.x * PAD_TWEEN_ON, 0, viewport.x * PAD_TWEEN_ON, 0);
		list2.setDefaultMargin(viewport.x * PAD_TWEEN_ON, 0, viewport.x * PAD_TWEEN_ON, 0);
		list3.setDefaultMargin(viewport.x * PAD_TWEEN_ON, 0, viewport.x * PAD_TWEEN_ON, 0);
		
		HorizontalLayout listTweens = new HorizontalLayout();
		listTweens.setDefaultMargin(viewport.x * PAD_TWEEN, 0, viewport.x * PAD_TWEEN, 0);

		tweensTable.add(list1).expand().fill();
		tweensTable.row();
		tweensTable.add("------------------").expandX().fillX().center();
		tweensTable.row();
		tweensTable.add(list2).expand().fill();
		tweensTable.row();
		tweensTable.add("------------------").expandX().fillX().center();
		tweensTable.row();
		tweensTable.add(list3).expand().fill();
		tweensTable.row();
		tweensTable.add(listTweens).expandX().fillX();
		
		Label label1 = new Label("Pista-1", skin);
		Label label2 = new Label("Pista-2", skin);
		Label label3 = new Label("Pista-3", skin);

		list1.add(label1);
		list2.add(label2);
		list3.add(label3);

		DragAndDrop dragAndDrop = new DragAndDrop();
		final DragAndDrop dragAndRemove = new DragAndDrop();

		TweenButton tMove = new TweenButton(skin, IC_MOVE, "MOVE", TweenType.MOVE,
				dragAndDrop);
		listTweens.add(tMove);
		TweenButton tRotate = new TweenButton(skin, IC_ROTATE,
				"ROTATE", TweenType.ROTATE, dragAndDrop);
		listTweens.add(tRotate);
		TweenButton tScale = new TweenButton(skin, IC_SCALE, "SCALE", TweenType.SCALE,
				dragAndDrop);
		listTweens.add(tScale);
		TweenButton tRemove = new TweenButton(skin, IC_REMOVE,
				"REMOVE", TweenType.REMOVE, dragAndRemove);
		listTweens.add(tRemove);

		dragAndDrop.addTarget(new Target(list1) {
			public boolean drag(Source source, Payload payload, float x,
					float y, int pointer) {
				return true;
			}

			public void drop(Source source, Payload payload, float x, float y,
					int pointer) {
				final Image sourceImage = new Image(skin, ((TweenButton) source
						.getActor()).getIcon() + "_on");
				final HorizontalLayout aux = (HorizontalLayout) getActor();
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
						System.out.println(getActor());
						aux.removeActor(getActor());
						// int num = list1.getChildren().indexOf(getActor(),
						// true);
						// System.out.println(">>>>>>>>>>>>>>>>>>>>>>!"+num);
						// System.out.println(list1.swapActor(num-1, num));
						// list1.getChildren().removeValue(getActor(), true);
						// list1.invalidate();list1.validate();list1.pack();
					}

				});
				sourceImage.setVisible(true);
			}
		});

		dragAndDrop.addTarget(new Target(list2) {
			public boolean drag(Source source, Payload payload, float x,
					float y, int pointer) {
				return true;
			}

			public void drop(Source source, Payload payload, float x, float y,
					int pointer) {
				final Image sourceImage = new Image(skin, ((TweenButton) source
						.getActor()).getIcon() + "_on");
				final HorizontalLayout aux = (HorizontalLayout) getActor();
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
						System.out.println(getActor());
						aux.removeActor(getActor());
						// int num = list1.getChildren().indexOf(getActor(),
						// true);
						// System.out.println(">>>>>>>>>>>>>>>>>>>>>>!"+num);
						// System.out.println(list1.swapActor(num-1, num));
						// list1.getChildren().removeValue(getActor(), true);
						// list1.invalidate();list1.validate();list1.pack();
					}

				});
				sourceImage.setVisible(true);
			}
		});

		dragAndDrop.addTarget(new Target(list3) {
			public boolean drag(Source source, Payload payload, float x,
					float y, int pointer) {
				return true;
			}

			public void drop(Source source, Payload payload, float x, float y,
					int pointer) {
				final Image sourceImage = new Image(skin, ((TweenButton) source
						.getActor()).getIcon() + "_on");
				final HorizontalLayout aux = (HorizontalLayout) getActor();
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
						System.out.println(getActor());
						aux.removeActor(getActor());
						// int num = list1.getChildren().indexOf(getActor(),
						// true);
						// System.out.println(">>>>>>>>>>>>>>>>>>>>>>!"+num);
						// System.out.println(list1.swapActor(num-1, num));
						// list1.getChildren().removeValue(getActor(), true);
						// list1.invalidate();list1.validate();list1.pack();
					}

				});
				sourceImage.setVisible(true);
			}
		});

		// //////////////XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX/////////////////
		/* Tags */
		final Button tags = new TabButton(i18n.m("general.tag-plural"), skin), contitions = new TabButton(
				i18n.m("general.visibility"), skin), behaviors = new TabButton(
				"Tweens", skin);

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

	@Override
	protected Class<?> getNoteActionClass() {
		return null;
	}

	@Override
	public Array<Actor> getExtras() {
		final Array<Actor> actors = new Array<Actor>(false, 3);
		actors.add(this.tab);
		actors.add(this.flagPanel);
		return actors;
	}

	@Override
	protected Note getNote(Model model) {
		return null;
	}
}
