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
package es.eucm.ead.editor.view.builders.scene.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.GalleryStyle;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

public abstract class GroupContext extends AbstractWidget {

	public static final int COLUMNS = 4;

	private final LinearLayout iconsContainer;

	protected LinearLayout iconsList;

	protected Gallery gallery;

	private int columns;

	public GroupContext(Skin skin) {
		this(skin.get(GroupContextStyle.class));
	}

	public GroupContext(GroupContextStyle style) {
		iconsList = new LinearLayout(false);
		iconsContainer = new LinearLayout(false, style.iconsBackground);
		iconsContainer.add(iconsList).expandX();
		iconsContainer.addSpace();
		iconsContainer.setTouchable(Touchable.disabled);
		addActor(iconsContainer);
		addActor(gallery = new Gallery(1, COLUMNS, style.galleryStyle));
		gallery.setCancelTouchFocus(false);
	}

	@Override
	public void layout() {
		float width = WidgetBuilder.dpToPixels(16);
		setBounds(iconsContainer, -width, 0, width, getHeight());
		gallery.setRows(getHeight() / WidgetBuilder.dpToPixels(48));
		gallery.setColumns(columns);
		setBounds(gallery, 0, 0, getWidth(), getHeight());
	}

	protected void addIcon(String id, Drawable drawable) {
		Image image = new Image();
		image.setName(id);
		image.setDrawable(drawable);
		Container<Image> imageContainer = new Container<Image>()
				.size(WidgetBuilder.dpToPixels(12));
		imageContainer.setActor(image);
		iconsList.add(imageContainer).centerX();
	}

	protected void clearIcons() {
		iconsList.clear();
	}

	@Override
	public float getPrefWidth() {
		float availableWidth = Gdx.graphics.getWidth() / 2.0f;

		float margins = WidgetBuilder.dpToPixels(16) * 2;
		float prefWidth = WidgetBuilder.dpToPixels(COLUMNS
				* WidgetBuilder.UNIT_SIZE)
				+ margins;

		if (prefWidth < availableWidth) {
			this.columns = COLUMNS;
			return prefWidth;
		} else {
			availableWidth -= margins;
			this.columns = (int) Math.floor(availableWidth
					/ WidgetBuilder.UNIT_SIZE);
			return WidgetBuilder.dpToPixels(columns * WidgetBuilder.UNIT_SIZE)
					+ margins;
		}
	}

	public static class GroupContextStyle {

		public Drawable iconsBackground;

		public Drawable background;

		public GalleryStyle galleryStyle;

	}

}
