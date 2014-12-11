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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.AddPaintedElement;
import es.eucm.ead.editor.control.actions.editor.Copy;
import es.eucm.ead.editor.control.actions.editor.Paste;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.ShowContextMenu;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel.TypePanel;
import es.eucm.ead.editor.control.actions.editor.ShowToast;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.actions.model.RemoveSelectionFromScene;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.scene.ReorderSelection;
import es.eucm.ead.editor.control.actions.model.scene.transform.MirrorSelection;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.SceneEditor.Mode;
import es.eucm.ead.editor.view.builders.scene.draw.BrushStrokes;
import es.eucm.ead.editor.view.builders.scene.draw.BrushStrokes.ModeEvent;
import es.eucm.ead.editor.view.builders.scene.draw.BrushStrokes.ModeListener;
import es.eucm.ead.editor.view.listeners.VisibleListener;
import es.eucm.ead.editor.view.widgets.ContextMenu;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.LabelTextEditor;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.Switch;
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

	private float ANIM_TIME = 0.3f;

	private Controller controller;

	private SceneEditor sceneEditor;

	private BrushStrokes brushStrokes;

	private MultiWidget editButton;

	private VisibleListener ungroupVisible;

	private VisibleListener editVisible;

	private ClickListener navigationListener = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			if (sceneEditor.getGroupEditor().getRootGroup() == sceneEditor
					.getGroupEditor().getEditedGroup()) {
				sceneEditor.toggleNavigation();
			} else {
				sceneEditor.getGroupEditor().endGroupEdition();
			}
		}
	};

	private ToolbarSelectionListener selectionListener = new ToolbarSelectionListener();

	public GroupEditorToolbar(Controller controller, SceneEditor sceneEditor,
			BrushStrokes brushStrokes) {
		super(controller.getApplicationAssets().getSkin()
				.get(SkinConstants.STYLE_TOOLBAR, MultiWidgetStyle.class));
		this.controller = controller;
		this.sceneEditor = sceneEditor;
		this.brushStrokes = brushStrokes;
		I18N i18N = controller.getApplicationAssets().getI18N();
		Skin skin = controller.getApplicationAssets().getSkin();
		addWidgets(buildComposeToolbar(i18N),
				buildTransformToolbar(skin, i18N), buildDrawToolbar(skin, i18N));

	}

	@Override
	public void prepare() {
		controller.getModel().addSelectionListener(selectionListener);
		readSelection();
	}

	@Override
	public void release() {
		controller.getModel().removeSelectionListener(selectionListener);
	}

	private IconButton navigationButton() {
		IconButton navigation = WidgetBuilder.toolbarIcon(
				SkinConstants.IC_MENU, null);
		navigation.addListener(navigationListener);
		return navigation;
	}

	private LinearLayout buildComposeToolbar(I18N i18N) {
		LinearLayout compose = new LinearLayout(true);
		compose.setComputeInvisibles(true);
		compose.add(navigationButton());

		compose.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_UNDO,
				i18N.m("undo"), true, Undo.class));
		compose.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_REDO,
				i18N.m("redo"), true, Redo.class));

		compose.addSpace();

		compose.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_PASTE,
				i18N.m("paste"), true, Paste.class));
		return compose;
	}

	private LinearLayout buildTransformToolbar(Skin skin, I18N i18N) {
		LinearLayout transform = new LinearLayout(true);
		transform.setComputeInvisibles(true);
		transform.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_CHECK,
				i18N.m("clear.selection"), SetSelection.class,
				Selection.EDITED_GROUP, Selection.SCENE_ELEMENT));

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

		Actor ungroupButton = WidgetBuilder.toolbarIcon(
				SkinConstants.IC_UNGROUP, i18N.m("ungroup"));

		ungroupButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				sceneEditor.getGroupEditor().ungroup();
			}
		});

		transform.add(ungroupButton);

		ungroupVisible = new VisibleListener(ungroupButton);

		final LabelTextEditor textFontPane = new LabelTextEditor(skin,
				controller);
		final IconButton edit = WidgetBuilder.toolbarIcon(
				SkinConstants.IC_EDIT, i18N.m("edit"));

		edit.addCaptureListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ModelEntity sceneElement = (ModelEntity) controller.getModel()
						.getSelection().getSingle(Selection.SCENE_ELEMENT);
				if (Q.hasComponent(sceneElement, Label.class)) {
					controller
							.action(ShowContextMenu.class, edit, textFontPane);
					textFontPane.prepare(sceneEditor);
				} else if (Q.isGroup(sceneElement)) {
					event.stop();
					Actor actor = sceneEditor.getGroupEditor().findActor(
							sceneElement);
					if (actor instanceof Group) {
						sceneEditor.getGroupEditor().enterGroupEdition(
								(Group) actor);
					}
				}
			}
		});

		IconButton createGroup = WidgetBuilder.toolbarIcon(
				SkinConstants.IC_GROUP, i18N.m("group.create"));

		createGroup.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				sceneEditor.getGroupEditor().createGroupWithSelection();
			}
		});

		editButton = WidgetBuilder.multiToolbarIcon(edit, createGroup);

		editVisible = new VisibleListener(editButton);

		transform.add(editButton);
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
				- (transform.getWidth() - editButton.getX()),
				textFontPane.getHeight());

		return transform;
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

	private void readSelection() {
		if (controller.getModel().getSelection().get(Selection.SCENE_ELEMENT).length > 1) {
			ungroupVisible.setVisible(false);
			editVisible.setVisible(true);
			editButton.setSelectedWidget(1);
		} else {
			ModelEntity entity = (ModelEntity) controller.getModel()
					.getSelection().getSingle(Selection.SCENE_ELEMENT);
			ungroupVisible.setVisible(entity != null
					&& entity.getChildren().size > 0);
			editButton.setSelectedWidget(0);
			editVisible.setVisible(entity != null
					&& (Q.hasComponent(entity, Label.class) || entity
							.getChildren().size > 1));
		}
	}

	public class ToolbarSelectionListener implements SelectionListener {

		@Override
		public void modelChanged(SelectionEvent event) {
			readSelection();
		}

		@Override
		public boolean listenToContext(String contextId) {
			return contextId.equals(Selection.SCENE_ELEMENT);
		}
	}

}
