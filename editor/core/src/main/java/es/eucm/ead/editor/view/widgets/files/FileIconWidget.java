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
package es.eucm.ead.editor.view.widgets.files;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import es.eucm.ead.engine.gdx.AbstractWidget;

/**
 * Widget to represent a file, with an icon and label (normally, the file name)
 */
public class FileIconWidget extends AbstractWidget {

	private FileIconWidgetStyle style;

	private Image icon;

	private Label label;

	private String fileName;

	private boolean isFolder;

	private boolean over;

	private boolean selected;

	/**
	 * Creates a file icon
	 * 
	 * @param fileName
	 *            the file name for the icon. NOTE: this parameter only expects
	 *            the file name, no the complete path
	 * @param isFolder
	 *            if this icon represents a folder
	 * @param style
	 *            the style for the icon
	 */
	public FileIconWidget(String fileName, boolean isFolder,
			FileIconWidgetStyle style) {
		this.style = style;
		this.fileName = fileName;
		this.isFolder = isFolder;
		icon = new Image(style.icon, Scaling.none);
		LabelStyle labelStyle = new LabelStyle(style.font, style.fontColor);
		label = new Label(fileName, labelStyle);
		label.setWrap(true);
		addActor(icon);
		addActor(label);
		addListener(new InputListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer,
					Actor fromActor) {
				over = true;
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer,
					Actor toActor) {
				over = false;
			}
		});
	}

	/**
	 * @return the file name associated to this widget
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return if this icon represents a folder
	 */
	public boolean isFolder() {
		return isFolder;
	}

	/**
	 * Sets this icon selected (the {@link FileIconWidgetStyle#selected} will be
	 * drawn)
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		super.drawChildren(batch, parentAlpha);
		if (selected && style.selected != null) {
			style.selected.draw(batch, 0, 0, getWidth(), getHeight());
		}

		if (over && style.over != null) {
			style.over.draw(batch, 0, 0, getWidth(), getHeight());
		}
	}

	@Override
	public float getPrefWidth() {
		return icon.getPrefWidth() + style.padding * 2;
	}

	@Override
	public float getPrefHeight() {
		return getChildrenTotalHeight() + style.padding * 2;
	}

	@Override
	public void layout() {
		float iconY = getHeight() - style.padding - icon.getPrefHeight();
		icon.setBounds(style.padding, iconY, icon.getPrefWidth(),
				icon.getPrefHeight());

		float labelHeight = label.getPrefHeight();
		label.setBounds(style.padding, iconY - labelHeight, getWidth()
				- style.padding * 2, labelHeight);
	}

	public static class FileIconWidgetStyle {
		/**
		 * Drawable for the file icon
		 */
		public Drawable icon;

		/**
		 * Font for the label
		 */
		public BitmapFont font;

		/**
		 * Color for the label
		 */
		public Color fontColor;

		/**
		 * Drawable to mark that the widget is selected
		 */
		public Drawable selected;

		/**
		 * Drawable to mark that the mouse is over the widget
		 */
		public Drawable over;

		/**
		 * Padding of the content around the containing box
		 */
		public float padding = 5.0f;

		public FileIconWidgetStyle(Drawable icon, BitmapFont font,
				Color fontColor, Drawable selected, Drawable over) {
			this.icon = icon;
			this.font = font;
			this.fontColor = fontColor;
			this.selected = selected;
			this.over = over;

		}
	}

}
