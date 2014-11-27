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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.draw.BrushStrokesPicker.BrushStrokesPickerStyle;
import es.eucm.ead.editor.view.widgets.draw.ColorPickerPanel;
import es.eucm.ead.editor.view.widgets.draw.ColorPickerPanel.ColorPickerPanelStyle;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker.ColorEvent;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker.ColorListener;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public class TextFontColorPicker extends ContextMenu {

	private ColorPickerPanel colorPicker;

	private Label label;

	private String text;

	protected I18N i18n;

	private SelectBox<String> selectSize;

	private SelectBox<String> selectTypo;

	protected LinearLayout top;

	public TextFontColorPicker(Skin skin, I18N i18n) {
		this(skin, skin.get(TextFontColorPickerStyle.class), i18n);
	}

	public TextFontColorPicker(Skin skin, TextFontColorPickerStyle style,
			I18N i18n) {
		setBackground(style.background);

		this.i18n = i18n;

		final IconButton editLabel = new IconButton(SkinConstants.IC_EDIT, skin);
		text = " ";
		label = new Label(text, skin, SkinConstants.STYLE_TOOLBAR);
		label.setEllipsis(true);
		Table textContainer = new Table();
		Color color = skin.getColor(SkinConstants.COLOR_SEMI_BLACK);
		textContainer.setBackground(skin
				.getDrawable(SkinConstants.DRAWABLE_TEXT_FIELD));
		textContainer.setColor(color);
		textContainer.add(label).expandX().fillX().width(0);

		colorPicker = new ColorPickerPanel(skin, style.colorPickerStyle);
		colorPicker.addListener(new ColorListener() {
			@Override
			public void colorChanged(ColorEvent event) {
				label.setColor(event.getColor());
			}
		});

		float pad = WidgetBuilder.dpToPixels(16);

		top = new LinearLayout(true);
		top.add(editLabel).margin(0, 0, pad * 0.5f, 0);
		top.add(textContainer).expandX();

		selectTypo = new SelectBox<String>(skin);
		Array<String> typo = new Array<String>();
		typo.add("roboto");
		typo.add("comfortaa");
		typo.add("rabanera");
		selectTypo.setItems(typo);

		selectSize = new SelectBox<String>(skin);
		Array<String> size = new Array<String>();
		size.add(i18n.m("small"));
		size.add(i18n.m("big"));
		selectSize.setItems(size);

		LinearLayout fontOptions = new LinearLayout(true);
		fontOptions.add(selectTypo);
		fontOptions.add(selectSize);

		add(top).expandX().fillX().pad(0, pad, 0, pad);
		row();
		add(fontOptions);
		row();
		add(colorPicker);
	}

	@Override
	public void show() {
		super.show();
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

	public String getStyle() {
		return selectTypo.getSelected() + "-" + getTextSize();
	}

	public void updateSelectedStyle(String style) {
		String[] keys = style.split("-");
		selectTypo.setSelected(keys[0]);
		if (keys[1].equalsIgnoreCase("small"))
			selectSize.setSelectedIndex(0);
		else if (keys[1].equalsIgnoreCase("big")) {
			selectSize.setSelectedIndex(1);
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

	static public class TextFontColorPickerStyle {

		public ColorPickerPanelStyle colorPickerStyle;

		/** Optional */
		public Drawable background;

		public TextFontColorPickerStyle() {
		}

		public TextFontColorPickerStyle(BrushStrokesPickerStyle style) {
			this.colorPickerStyle = style.colorPickerStyle;
			this.background = style.background;
		}

		public TextFontColorPickerStyle(ColorPickerPanelStyle colorPickerStyle,
				Drawable background) {
			this.colorPickerStyle = colorPickerStyle;
			this.background = background;
		}
	}
}
