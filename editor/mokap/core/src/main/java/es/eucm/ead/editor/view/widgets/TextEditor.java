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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.draw.BrushStrokesPicker.BrushStrokesPickerStyle;
import es.eucm.ead.editor.view.widgets.draw.ColorPickerPanel;
import es.eucm.ead.editor.view.widgets.draw.ColorPickerPanel.ColorPickerPanelStyle;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker.ColorEvent;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker.ColorListener;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public class TextEditor extends ContextMenu {

	protected Controller controller;
	private ColorPickerPanel colorPicker;

	private Label label;

	private String text;

	protected I18N i18n;

	private SelectBox<String> selectSize;

	private SelectBox<String> selectTypo;

	protected Table top;

	public TextEditor(Controller control) {
		this.controller = control;

		ApplicationAssets applicationAssets = controller.getApplicationAssets();
		Skin skin = applicationAssets.getSkin();
		I18N i18n = applicationAssets.getI18N();
		Preferences prefs = controller.getPreferences();

		TextEditorStyle style = skin.get(TextEditorStyle.class);

		float pad = WidgetBuilder.dpToPixels(8);
		pad(pad);

		setBackground(style.background);

		this.i18n = i18n;

		final IconButton editLabel = new IconButton(SkinConstants.IC_EDIT, skin);
		text = " ";
		label = new Label(text, skin, SkinConstants.STYLE_TOOLBAR);
		label.setEllipsis(true);
		Container<Label> textContainer = new Container<Label>();
		Color color = skin.getColor(SkinConstants.COLOR_SEMI_BLACK);
		textContainer.setBackground(skin
				.getDrawable(SkinConstants.DRAWABLE_TEXT_FIELD));
		textContainer.setColor(color);
		textContainer.setActor(label);
		textContainer.fillX().width(0);

		colorPicker = new ColorPickerPanel(skin, style.colorPickerStyle, prefs);
		colorPicker.addListener(new ColorListener() {
			@Override
			public void colorChanged(ColorEvent event) {
				label.setColor(event.getColor());
			}
		});

		top = new Table();
		top.add(editLabel).pad(pad);
		top.add(textContainer).pad(pad).expandX().fillX();

		selectTypo = new SelectBox<String>(skin);
		selectTypo.getSelection().setProgrammaticChangeEvents(false);
		Array<String> typo = new Array<String>();
		typo.add("roboto");
		typo.add("comfortaa");
		typo.add("rabanera");
		selectTypo.setItems(typo);

		selectSize = new SelectBox<String>(skin);
		selectSize.getSelection().setProgrammaticChangeEvents(false);
		Array<String> size = new Array<String>();
		size.add(i18n.m("small"));
		size.add(i18n.m("big"));
		selectSize.setItems(size);

		LinearLayout fontOptions = new LinearLayout(true);
		fontOptions.add(selectTypo).marginRight(pad);
		fontOptions.add(selectSize).marginRight(pad);

		add(top).expandX().fillX();
		row();
		add(fontOptions).padBottom(pad);
		row();
		add(colorPicker).padBottom(pad);
		colorPicker.completeRowsIfPossible(this);
	}

	@Override
	public void show(Views views) {
		super.show(views);
		colorPicker.initResources();
	}

	@Override
	public void hide(Runnable runnable) {
		colorPicker.setUpPickedColor();
		SequenceAction hideAction = getHideAction(runnable);
		hideAction.addAction(Actions.run(colorPicker.getReleaseResources()));
		addAction(hideAction);
	}

	@Override
	public boolean hideAlways() {
		return false;
	}

	private String getTextSize() {
		if (selectSize.getSelected().equals(i18n.m("small"))) {
			return "small";
		} else if (selectSize.getSelected().equals(i18n.m("big"))) {
			return "big";
		}

		return null;
	}

	public String getStyleName() {
		return selectTypo.getSelected() + "-" + getTextSize();
	}

	public void updateSelectedStyle(String style) {
		String[] keys = style.split("-");
		if (keys.length == 1) {
			Skin skin = controller.getEditorGameAssets().getSkin();
			Label.LabelStyle labelStyle = skin.get(style,
					Label.LabelStyle.class);
			String fontString = skin.find(labelStyle.font);
			if (fontString == null || fontString.isEmpty()) {
				selectTypo.setSelectedIndex(0);
				selectSize.setSelectedIndex(0);
			} else {
				Array<String> items = selectSize.getItems();
				for (int i = 0; i < items.size; i++) {
					if (fontString.contains(items.get(i))) {
						selectSize.setSelectedIndex(i);
						break;
					}
				}
				items = selectTypo.getItems();
				for (int i = 0; i < items.size; i++) {
					if (fontString.contains(items.get(i))) {
						selectTypo.setSelectedIndex(i);
						break;
					}
				}
			}
		} else {
			selectTypo.setSelected(keys[0]);
			if (keys[1].equalsIgnoreCase("small"))
				selectSize.setSelectedIndex(0);
			else if (keys[1].equalsIgnoreCase("big")) {
				selectSize.setSelectedIndex(1);
			}
		}
	}

	public void updateText(String text) {
		updatePaneText(text, label.getColor());
	}

	public void updatePaneText(String text, Color color) {
		this.text = text;
		String[] lines = text.split("\n");
		if (lines.length > 1) {
			lines[0] += "...";
		}
		label.setText(lines[0]);
		label.setColor(color);
		colorPicker.setPickedColor(color);
	}

	static public class TextEditorStyle {

		public ColorPickerPanelStyle colorPickerStyle;

		/** Optional */
		public Drawable background;

		public TextEditorStyle() {
		}

		public TextEditorStyle(BrushStrokesPickerStyle style) {
			this.colorPickerStyle = style.colorPickerStyle;
			this.background = style.background;
		}

		public TextEditorStyle(ColorPickerPanelStyle colorPickerStyle,
				Drawable background) {
			this.colorPickerStyle = colorPickerStyle;
			this.background = background;
		}
	}
}
