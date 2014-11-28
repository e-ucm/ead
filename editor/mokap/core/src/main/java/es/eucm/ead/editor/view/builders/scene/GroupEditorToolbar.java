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
package es.eucm.ead.editor.view.builders.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.AddLabel;
import es.eucm.ead.editor.control.actions.editor.AddPaintedElement;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.Copy;
import es.eucm.ead.editor.control.actions.editor.Paste;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel.TypePanel;
import es.eucm.ead.editor.control.actions.editor.ShowToast;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.actions.model.AddInteractiveZone;
import es.eucm.ead.editor.control.actions.model.GroupSelection;
import es.eucm.ead.editor.control.actions.model.RemoveSelectionFromScene;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.TakePicture;
import es.eucm.ead.editor.control.actions.model.UngroupSelection;
import es.eucm.ead.editor.control.actions.model.scene.ReorderSelection;
import es.eucm.ead.editor.control.actions.model.scene.transform.MirrorSelection;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.FileView;
import es.eucm.ead.editor.view.builders.scene.SceneEditor.Mode;
import es.eucm.ead.editor.view.builders.scene.draw.BrushStrokes;
import es.eucm.ead.editor.view.builders.scene.draw.BrushStrokes.ModeEvent;
import es.eucm.ead.editor.view.builders.scene.draw.BrushStrokes.ModeListener;
import es.eucm.ead.editor.view.widgets.ContextMenu;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.Switch;
import es.eucm.ead.editor.view.widgets.LabelTextEditor;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.draw.BrushStrokesPicker;
import es.eucm.ead.editor.view.widgets.draw.BrushStrokesPicker.SizeEvent;
import es.eucm.ead.editor.view.widgets.draw.BrushStrokesPicker.SizeListener;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker.ColorEvent;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker.ColorListener;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.entities.ModelEntity;

public class GroupEditorToolbar extends MultiWidget implements ModelView {

	private Controller controller;

	private SceneEditor sceneEditor;

	private BrushStrokes brushStrokes;

	private ClickListener navigationListener = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			sceneEditor.toggleNavigation();
		}
	};

	public GroupEditorToolbar(Controller controller, SceneEditor sceneEditor,
			BrushStrokes brushStrokes) {
		super(controller.getApplicationAssets().getSkin()
				.get(SkinConstants.STYLE_TOOLBAR, MultiWidgetStyle.class));
		this.controller = controller;
		this.sceneEditor = sceneEditor;
		this.brushStrokes = brushStrokes;
		I18N i18N = controller.getApplicationAssets().getI18N();
		Skin skin = controller.getApplicationAssets().getSkin();
		Actor modeSelector = buildModeContextMenu(i18N);
		addWidgets(buildComposeToolbar(i18N, modeSelector),
				buildTransformToolbar(skin, i18N, modeSelector),
				buildDrawToolbar(skin, i18N),
				buildFxToolbar(i18N, modeSelector),
				buildFxSelectionToolbar(i18N, modeSelector),
				buildInteractionToolbar(i18N, modeSelector),
				buildInteractionSelectionToolbar(i18N, modeSelector));

	}

	@Override
	public void prepare() {
	}

	@Override
	public void release() {
	}

	private Actor buildModeContextMenu(I18N i18N) {
		ContextMenu contextMenu = WidgetBuilder.iconLabelContextPanel(
				SkinConstants.IC_COMPOSE, i18N.m("compose"),
				SkinConstants.IC_FX, i18N.m("fx"), SkinConstants.IC_TOUCH,
				i18N.m("interaction"), SkinConstants.IC_PLAY, i18N.m("test"));
		contextMenu.pack();
		contextMenu.setOriginY(contextMenu.getHeight());
		contextMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String name = event.getTarget().getName();
				if (SkinConstants.IC_COMPOSE.equals(name)) {
					sceneEditor.setMode(Mode.COMPOSE);
				} else if (SkinConstants.IC_FX.equals(name)) {
					sceneEditor.setMode(Mode.FX);
				} else if (SkinConstants.IC_PLAY.equals(name)) {
					sceneEditor.setMode(Mode.PLAY);
				} else if (SkinConstants.IC_TOUCH.equals(name)) {
					sceneEditor.setMode(Mode.INTERACTION);
				}
			}
		});
		return contextMenu;
	}

	private IconButton navigationButton() {
		IconButton navigation = WidgetBuilder.toolbarIcon(
				SkinConstants.IC_MENU, null);
		navigation.addListener(navigationListener);
		return navigation;
	}

	private LinearLayout buildComposeToolbar(I18N i18N, Actor modeSelector) {
		LinearLayout compose = new LinearLayout(true);
		compose.setComputeInvisibles(true);
		compose.add(navigationButton());

		IconButton mode = WidgetBuilder.icon(SkinConstants.IC_COMPOSE,
				SkinConstants.STYLE_DROP_DOWN);
		WidgetBuilder.launchContextMenu(mode, modeSelector);

		compose.add(mode);
		compose.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_UNDO,
				i18N.m("undo"), true, Undo.class));
		compose.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_REDO,
				i18N.m("redo"), true, Redo.class));

		compose.addSpace();

		compose.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_PASTE,
				i18N.m("paste"), true, Paste.class));

		compose.add(WidgetBuilder.toolbarIconWithMenu(SkinConstants.IC_ADD,
				buildInsertContextMenu(i18N)));
		return compose;
	}

	private LinearLayout buildTransformToolbar(Skin skin, I18N i18N,
			Actor modeSelector) {
		LinearLayout transform = new LinearLayout(true);
		transform.setComputeInvisibles(true);
		transform.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_CHECK,
				i18N.m("clear.selection"), SetSelection.class,
				Selection.EDITED_GROUP, Selection.SCENE_ELEMENT));
		IconButton mode = WidgetBuilder.icon(SkinConstants.IC_COMPOSE,
				SkinConstants.STYLE_DROP_DOWN);
		WidgetBuilder.launchContextMenu(mode, modeSelector);

		transform.add(mode);
		transform.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_UNDO,
				i18N.m("undo"), true, Undo.class));
		transform.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_REDO,
				i18N.m("redo"), true, Redo.class));

		final Switch multiSelection = new Switch(skin,
				SkinConstants.IC_MULTIPLE_SELECTION,
				SkinConstants.IC_SINGLE_SELECTION);
		multiSelection.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (actor instanceof Switch) {
					sceneEditor.getGroupEditor().setMultipleSelection(
							((Switch) actor).isStateOn());
				}
			}
		});
		WidgetBuilder.actionOnClick(multiSelection, ShowInfoPanel.class,
				TypePanel.MULTIPLE_SELECTION,
				Preferences.HELP_MULTIPLE_SELECTION);

		transform.addSpace();

		transform.add(multiSelection);

		transform.addSpace();

		transform.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_UNGROUP,
				i18N.m("ungroup"), true, UngroupSelection.class));

		final LabelTextEditor textFontPane = new LabelTextEditor(skin,
				controller);
		IconButton edit = WidgetBuilder.toolbarIconWithMenu(
				SkinConstants.IC_EDIT, textFontPane);
		edit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				textFontPane.prepare(sceneEditor);
			}
		});

		final MultiWidget multiButton = WidgetBuilder.multiToolbarIcon(
				edit,
				WidgetBuilder.toolbarIcon(SkinConstants.IC_GROUP,
						i18N.m("group.create"), false, GroupSelection.class));

		controller.getModel().addSelectionListener(new SelectionListener() {

			private float ANIM_TIME = 0.3f;

			@Override
			public void modelChanged(SelectionEvent event) {
				if (controller.getModel().getSelection()
						.get(Selection.SCENE_ELEMENT).length > 1) {
					multiButton.addAction(Actions.fadeIn(ANIM_TIME));
					multiButton.setTouchable(Touchable.enabled);
					multiButton.setSelectedWidget(1);
				} else {
					ModelEntity entity = (ModelEntity) controller.getModel()
							.getSelection().getSingle(Selection.SCENE_ELEMENT);
					if (Q.hasComponent(entity, Label.class)) {
						multiButton.addAction(Actions.fadeIn(ANIM_TIME));
						multiButton.setTouchable(Touchable.enabled);
						multiButton.setSelectedWidget(0);
					} else {
						multiButton.setTouchable(Touchable.disabled);
						multiButton.addAction(Actions.fadeOut(ANIM_TIME));
					}
				}
			}

			@Override
			public boolean listenToContext(String contextId) {
				return contextId.equals(Selection.SCENE_ELEMENT);
			}
		});

		transform.add(multiButton);

		transform.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_TO_BACK,
				i18N.m("to.back"), false, ReorderSelection.class,
				ReorderSelection.Type.TO_BACK));

		transform.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_TO_FRONT,
				i18N.m("to.front"), false, ReorderSelection.class,
				ReorderSelection.Type.TO_FRONT));

		transform.add(WidgetBuilder.toolbarIconWithMenu(SkinConstants.IC_MORE,
				buildTransformContextMenu(i18N)));

		transform.pack();
		textFontPane.setOrigin(textFontPane.getWidth()
				- (transform.getWidth() - multiButton.getX()),
				textFontPane.getHeight());

		return transform;
	}

	private ContextMenu buildInsertContextMenu(I18N i18n) {
		String style = SkinConstants.STYLE_CONTEXT;

		Button picture = WidgetBuilder.button(SkinConstants.IC_CAMERA,
				i18n.m("picture"), style, TakePicture.class);

		Button text = WidgetBuilder.button(SkinConstants.IC_TEXT,
				i18n.m("text"), style, AddLabel.class);

		Button zone = WidgetBuilder.button(SkinConstants.IC_ZONE,
				i18n.m("interactive.zone"), style, AddInteractiveZone.class);
		WidgetBuilder.actionOnClick(zone, ShowInfoPanel.class, TypePanel.ZONES,
				Preferences.HELP_ZONES);

		Button paint = WidgetBuilder.button(SkinConstants.IC_BRUSH,
				i18n.m("drawing"), style);
		paint.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				sceneEditor.setMode(Mode.DRAW);
			}
		});

		Button gallery = WidgetBuilder.button(SkinConstants.IC_CLOUD,
				i18n.m("gallery"), style, ChangeView.class, FileView.class);

		ContextMenu contextMenu = WidgetBuilder.iconLabelContextPanel(gallery,
				picture, text, zone, paint);

		contextMenu.pack();
		contextMenu.setOriginX(contextMenu.getWidth());
		contextMenu.setOriginY(contextMenu.getHeight());
		return contextMenu;
	}

	private ContextMenu buildTransformContextMenu(I18N i18n) {
		String style = SkinConstants.STYLE_CONTEXT;

		Button copy = WidgetBuilder.button(SkinConstants.IC_COPY,
				i18n.m("copy"), style, Copy.class);
		WidgetBuilder.actionOnClick(copy, ShowToast.class, i18n.m("copied"));
		WidgetBuilder.actionOnClick(copy, SetSelection.class,
				Selection.EDITED_GROUP, Selection.SCENE_ELEMENT);

		Button front = WidgetBuilder.button(SkinConstants.IC_BRING_TO_FRONT,
				i18n.m("bring.to.front"), style, ReorderSelection.class,
				ReorderSelection.Type.BRING_TO_FRONT);

		Button back = WidgetBuilder.button(SkinConstants.IC_SEND_TO_BACK,
				i18n.m("send.to.back"), style, ReorderSelection.class,
				ReorderSelection.Type.SEND_TO_BACK);

		Button vertical = WidgetBuilder.button(
				SkinConstants.IC_MIRROR_VERTICAL, i18n.m("mirror.vertical"),
				style, MirrorSelection.class, MirrorSelection.Type.HORIZONTAL);

		Button horizontal = WidgetBuilder.button(
				SkinConstants.IC_MIRROR_HORIZONTAL,
				i18n.m("mirror.horizontal"), style, MirrorSelection.class,
				MirrorSelection.Type.VERTICAL);

		Button delete = WidgetBuilder.button(SkinConstants.IC_DELETE,
				i18n.m("delete"), style, RemoveSelectionFromScene.class);

		ContextMenu contextMenu = WidgetBuilder.iconLabelContextPanel(copy,
				front, back, vertical, horizontal, delete);

		contextMenu.pack();
		contextMenu.setOriginX(contextMenu.getWidth());
		contextMenu.setOriginY(contextMenu.getHeight());
		return contextMenu;
	}

	private LinearLayout buildDrawToolbar(final Skin skin, I18N i18N) {
		LinearLayout draw = new LinearLayout(true);
		IconButton save = WidgetBuilder.toolbarIcon(SkinConstants.IC_CHECK,
				i18N.m("drawing.save"));

		save.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				sceneEditor.setMode(Mode.COMPOSE);
				controller.action(AddPaintedElement.class, brushStrokes);
				brushStrokes.hide(false);
			}
		});

		draw.add(save);

		IconButton mode = WidgetBuilder.icon(SkinConstants.IC_BRUSH,
				SkinConstants.STYLE_DROP_DOWN);
		final Image modeIcon = mode.getIcon();
		WidgetBuilder.launchContextMenu(mode, buildDrawModeContextMenu(i18N));

		draw.add(mode);

		IconButton picker = new IconButton(SkinConstants.IC_CIRCLE, skin,
				SkinConstants.STYLE_DROP_DOWN) {
			@Override
			public void setChecked(boolean isChecked) {
			}
		};
		WidgetBuilder.launchContextMenu(picker,
				buildBrushStrokesColorPicker(skin, picker.getIcon()));
		brushStrokes.addListener(new ModeListener() {
			@Override
			public void modeChanged(ModeEvent event) {
				boolean drawing = event.getMode() == BrushStrokes.Mode.DRAW;
				modeIcon.setDrawable(skin, drawing ? SkinConstants.IC_BRUSH
						: SkinConstants.IC_RUBBER);
			}
		});

		draw.add(picker);

		draw.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_UNDO,
				i18N.m("undo"), Undo.class));
		draw.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_REDO,
				i18N.m("redo"), Redo.class));

		draw.addSpace();

		IconButton close = WidgetBuilder.toolbarIcon(SkinConstants.IC_CLOSE,
				i18N.m("drawing.discard"));

		draw.add(close);

		close.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				sceneEditor.setMode(Mode.COMPOSE);
				brushStrokes.hide(true);
			}
		});

		return draw;
	}

	private Actor buildBrushStrokesColorPicker(Skin skin, final Image pickerIcon) {
		pickerIcon.setOrigin(Align.center);
		BrushStrokesPicker colorPickerPanel = new BrushStrokesPicker(skin);
		colorPickerPanel.pack();
		colorPickerPanel.setOriginY(colorPickerPanel.getHeight());
		colorPickerPanel.addListener(new ColorListener() {
			@Override
			public void colorChanged(ColorEvent event) {
				Color color = event.getColor();
				pickerIcon.setColor(color);
				brushStrokes.setColor(color);
				brushStrokes.setMode(BrushStrokes.Mode.DRAW);
			}
		});
		colorPickerPanel.addListener(new SizeListener() {
			@Override
			public void sizeChanged(SizeEvent event) {
				pickerIcon.setScale(event.getCompletion());
				brushStrokes.setRadius(event.getCompletion());
			}
		});
		colorPickerPanel.setPickedColor(brushStrokes.getInitialColor());
		colorPickerPanel.setSizeValue(brushStrokes.getInitialRadius()
				/ brushStrokes.getMaxRadius());
		return colorPickerPanel;
	}

	private Actor buildDrawModeContextMenu(I18N i18N) {
		ContextMenu contextMenu = WidgetBuilder.iconLabelContextPanel(
				SkinConstants.IC_BRUSH, i18N.m("brush"),
				SkinConstants.IC_RUBBER, i18N.m("erase"));
		contextMenu.pack();
		contextMenu.setOriginY(contextMenu.getHeight());
		contextMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String name = event.getTarget().getName();
				if (SkinConstants.IC_BRUSH.equals(name)) {
					brushStrokes.setMode(BrushStrokes.Mode.DRAW);
				} else if (SkinConstants.IC_RUBBER.equals(name)) {
					brushStrokes.setMode(BrushStrokes.Mode.ERASE);
				}
			}
		});
		return contextMenu;
	}

	private LinearLayout buildFxToolbar(I18N i18N, Actor modeSelector) {
		LinearLayout fx = new LinearLayout(true);
		fx.setComputeInvisibles(true);
		fx.add(navigationButton());

		IconButton mode = WidgetBuilder.icon(SkinConstants.IC_FX,
				SkinConstants.STYLE_DROP_DOWN);
		WidgetBuilder.launchContextMenu(mode, modeSelector);
		fx.add(mode);
		return fx;
	}

	private LinearLayout buildFxSelectionToolbar(I18N i18N, Actor modeSelector) {
		LinearLayout fxSelection = new LinearLayout(true);
		fxSelection.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_CHECK,
				i18N.m("clear.selection"), SetSelection.class,
				Selection.EDITED_GROUP, Selection.SCENE_ELEMENT));
		IconButton mode = WidgetBuilder.icon(SkinConstants.IC_FX,
				SkinConstants.STYLE_DROP_DOWN);
		WidgetBuilder.launchContextMenu(mode, modeSelector);
		fxSelection.add(mode);
		return fxSelection;
	}

	private LinearLayout buildInteractionSelectionToolbar(I18N i18N,
			Actor modeSelector) {
		LinearLayout fxSelection = new LinearLayout(true);
		fxSelection.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_CHECK,
				i18N.m("clear.selection"), SetSelection.class,
				Selection.EDITED_GROUP, Selection.SCENE_ELEMENT));
		IconButton mode = WidgetBuilder.icon(SkinConstants.IC_TOUCH,
				SkinConstants.STYLE_DROP_DOWN);
		WidgetBuilder.launchContextMenu(mode, modeSelector);
		fxSelection.add(mode);
		return fxSelection;
	}

	private LinearLayout buildInteractionToolbar(I18N i18N, Actor modeSelector) {
		LinearLayout interaction = new LinearLayout(true);
		interaction.setComputeInvisibles(true);
		interaction.add(navigationButton());

		IconButton mode = WidgetBuilder.icon(SkinConstants.IC_TOUCH,
				SkinConstants.STYLE_DROP_DOWN);
		WidgetBuilder.launchContextMenu(mode, modeSelector);
		interaction.add(mode);
		return interaction;
	}

}
