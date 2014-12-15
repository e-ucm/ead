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
package es.eucm.ead.editor.view.widgets.draw;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.view.widgets.ContextMenu;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker.SlideColorPickerStyle;

/**
 * A panel with a {@link SlideColorPicker} that remembers the recent colors that
 * have been picked.
 * 
 * @author Rotaru Dan Cristian
 * 
 */
public class ColorPickerPanel extends ContextMenu {

	private static final ClickListener colorClicked = new ClickListener() {

		public void clicked(InputEvent event, float x, float y) {
			Actor listenerActor = event.getTarget();
			ColorPickerPanel colorPicker = ((ColorPickerPanel) listenerActor
					.getUserObject());
			Color color = listenerActor.getColor();
			colorPicker.picker.updateSlidersTo(color);
		};
	};

	private Table colors;
	protected SlideColorPicker picker;

	public ColorPickerPanel(Skin skin) {
		this(skin, skin.get(ColorPickerPanelStyle.class));
	}

	public ColorPickerPanel(Skin skin, String styleName) {
		this(skin, skin.get(styleName, ColorPickerPanelStyle.class));
	}

	public ColorPickerPanel(Skin skin,
			ColorPickerPanelStyle colorPickerPanelStyle) {
		setBackground(colorPickerPanelStyle.background);
		String recentColorDrawable = colorPickerPanelStyle.recentColorIcon;
		String buttonStyleName = colorPickerPanelStyle.recentColorStyle;

		int maxColors = colorPickerPanelStyle.maxColors;
		int columns = colorPickerPanelStyle.columns;

		colors = new Table();
		for (int i = 0; i < maxColors; ++i) {
			IconButton image = new IconButton(recentColorDrawable, skin,
					buttonStyleName) {
				@Override
				public void setChecked(boolean isChecked) {
				}

				public void setColor(Color color) {
					super.setColor(color);
					getIcon().setColor(color);
				};
			};
			image.setUserObject(this);

			colors.add(image);
			if (i % columns == columns - 1) {
				colors.row();
			}
		}
		colors.addListener(colorClicked);
		picker = new SlideColorPicker(colorPickerPanelStyle);

		add(colors);
		row();
		add(picker).fill().expand();
	}

	public void setPickedColor(Color color) {
		picker.setPickedColor(color);
	}

	public void show(Views views) {
		initResources();
		super.show(views);
	}

	public void initResources() {
		picker.initialize();
	}

	@Override
	public void hide(Runnable runnable) {
		setUpPickedColor();
		SequenceAction hideAction = getHideAction(runnable);
		hideAction.addAction(Actions.run(getReleaseResources()));
		addAction(hideAction);
	}

	private Runnable releaseResources = new Runnable() {

		@Override
		public void run() {
			picker.release();
		}
	};

	public Runnable getReleaseResources() {
		return releaseResources;
	}

	public void setUpPickedColor() {
		if (!hasPickedColor()) {
			Array<Cell> colorCells = this.colors.getCells();
			for (int i = colorCells.size - 2; i >= 0; --i) {
				Actor actor = colorCells.get(i).getActor();
				colorCells.get(i + 1).getActor().setColor(actor.getColor());
			}
			colors.getCells().first().getActor()
					.setColor(picker.getPickedColor());
		}
	}

	private boolean hasPickedColor() {
		for (Cell cell : colors.getCells()) {
			Actor actor = cell.getActor();
			if (actor.getColor().equals(picker.getPickedColor())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hideAlways() {
		return false;
	}

	/**
	 * The style for a {@link ColorPickerPanel}.
	 * 
	 * @author Rotaru Dan Cristian
	 */
	static public class ColorPickerPanelStyle extends SlideColorPickerStyle {

		public String recentColorIcon;

		public String recentColorStyle;

		public int maxColors, columns;

		/** Optional */
		public Drawable background;

		public ColorPickerPanelStyle() {
		}

		public ColorPickerPanelStyle(ColorPickerPanelStyle style) {
			super(style);
			this.recentColorIcon = style.recentColorIcon;
		}

		public ColorPickerPanelStyle(SlideColorPickerStyle style,
				String recentColorIcon) {
			super(style);
			this.recentColorIcon = recentColorIcon;
		}
	}
}
