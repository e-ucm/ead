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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.widgets.DropDown;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.schema.editor.components.Date;
import es.eucm.ead.schema.entities.ModelEntity;

import java.util.Comparator;

/**
 * Widget used to sort {@link GalleryItem} items in a {@link BaseGallery}.
 */
public class SortWidget extends DropDown {

	private Array<GalleryItem> items;

	public SortWidget(Skin skin, Array<GalleryItem> items,
			final BaseGallery baseGallery) {
		this(skin, items, baseGallery, false);
	}

	public SortWidget(Skin skin, Array<GalleryItem> items,
			final BaseGallery baseGallery, boolean sortByDate) {
		super(skin);
		this.items = items;
		Array<Actor> sortButtons = new Array<Actor>(3);

		if (sortByDate) {
			IconButton dateSort = new IconButton("reorderDate", skin);
			dateSort.setUserObject(new Comparator<GalleryItem>() {
				@Override
				public int compare(GalleryItem o1, GalleryItem o2) {
					if (o1.getUserObject() instanceof ModelEntity
							&& o2.getUserObject() instanceof ModelEntity) {
						ModelEntity entity1 = (ModelEntity) o1.getUserObject();
						ModelEntity entity2 = (ModelEntity) o2.getUserObject();
						String date1 = Q.getComponent(entity1, Date.class)
								.getDate();
						String date2 = Q.getComponent(entity2, Date.class)
								.getDate();
						if (date1 != null && date2 != null) {
							return date1.compareTo(date2);
						} else {
							return 0;
						}
					} else {
						return 0;
					}
				}
			});
			sortButtons.add(dateSort);
		}

		IconButton azSort = new IconButton("reorderAZ80x80", skin);
		azSort.setUserObject(new Comparator<GalleryItem>() {
			@Override
			public int compare(GalleryItem o1, GalleryItem o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortButtons.add(azSort);

		IconButton zaSort = new IconButton("reorderZA80x80", skin);
		zaSort.setUserObject(new Comparator<GalleryItem>() {
			@Override
			public int compare(GalleryItem o1, GalleryItem o2) {
				return o2.getName().compareTo(o1.getName());
			}
		});
		sortButtons.add(zaSort);

		setItems(sortButtons);
		addListener(new DropdownChangeListener() {

			@Override
			public void changed(Actor selected, DropDown listener) {
				sort();
				baseGallery.updateDisplayedElements();
			}
		});
	}

	public void sort() {
		items.sort((Comparator<GalleryItem>) getSelected().getUserObject());
	}
}
