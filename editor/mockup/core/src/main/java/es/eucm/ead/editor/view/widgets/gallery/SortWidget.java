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
package es.eucm.ead.editor.view.widgets.gallery;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.widgets.DropDown;
import es.eucm.ead.editor.view.widgets.IconButton;

/**
 * Widget used to sort {@link GalleryItem} items in a {@link BaseGallery}.
 * 
 */
public class SortWidget extends DropDown {

	private Array<GalleryItem> items;
	private IconButton az;

	private Comparator<GalleryItem> comparatorAZ = new Comparator<GalleryItem>() {
		@Override
		public int compare(GalleryItem o1, GalleryItem o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};
	private Comparator<GalleryItem> comparatorZA = new Comparator<GalleryItem>() {
		@Override
		public int compare(GalleryItem o1, GalleryItem o2) {
			return o2.getName().compareTo(o1.getName());
		}
	};

	public SortWidget(Skin skin, Array<GalleryItem> items,
			final BaseGallery baseGallery) {
		super(skin);
		this.items = items;
		az = new IconButton("reorderAZ80x80", skin);
		IconButton za = new IconButton("reorderZA80x80", skin);
		Array<Actor> array = new Array<Actor>();
		array.add(az);
		array.add(za);
		setItems(array);
		addListener(new ChangeListener() {

			private Actor currentSelected;

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor selected = getSelected();
				if (currentSelected != selected) {
					currentSelected = selected;
					sort();
					baseGallery.updateDisplayedElements();
				}
			}
		});
	}

	public void sort() {
		Arrays.sort(items.items, 0, items.size,
				az == getSelected() ? comparatorAZ : comparatorZA);
	}

}
