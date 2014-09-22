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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.listeners.TextFieldListener;
import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel.Position;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithFadePanel;
import es.eucm.ead.engine.I18N;

/**
 * A widget used to perform search functionality within the {@link BaseGallery}.
 * 
 */
public class SearchWidget extends IconWithFadePanel {

	private Array<GalleryItem> prevSearchItems;
	private TextField searchTextField;
	private Array<GalleryItem> items;

	public SearchWidget(float padding, float size, Skin skin, I18N i18n,
			Array<GalleryItem> items, final BaseGallery baseGallery) {
		super("search80x80", padding, 0, size, skin, Position.BOTTOM);
		this.items = items;
		this.prevSearchItems = new Array<GalleryItem>(true, 8,
				GalleryItem.class);
		searchTextField = new TextField("", skin);
		searchTextField.setMessageText(i18n.m("gallery.search"));
		searchTextField.setFocusTraversal(false);
		searchTextField.addListener(new TextFieldListener(searchTextField) {

			@Override
			protected void keyTyped(String text) {
				filter();
				baseGallery.sort();
				baseGallery.updateDisplayedElements();
			}

		});
		getPanel().add(searchTextField);
	}

	public String getText() {
		return searchTextField.getText();
	}

	@Override
	protected Action getShowAction() {
		Stage stage = getStage();
		if (stage != null) {
			stage.setKeyboardFocus(searchTextField);
			Gdx.input.setOnscreenKeyboardVisible(true);
		}
		return super.getShowAction();
	}

	@Override
	protected Action getHideAction() {
		Stage stage = getStage();
		if (stage != null) {
			Gdx.input.setOnscreenKeyboardVisible(false);
			stage.setKeyboardFocus(null);
			stage.unfocusAll();
		}
		return super.getHideAction();
	}

	public void filter() {
		String search = this.searchTextField.getText();
		if (search.isEmpty()) {
			if (prevSearchItems.size > 0) {
				items.clear();
				items.addAll(prevSearchItems);
				prevSearchItems.clear();
			}
		} else {
			if (prevSearchItems.size == 0) {
				prevSearchItems.addAll(this.items);
			}
			items.clear();
			Pattern findPattern = Pattern.compile(search,
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = findPattern.matcher("");
			for (GalleryItem entity : this.prevSearchItems) {
				matcher.reset(entity.getName());
				if (matcher.find()) {
					this.items.add(entity);
				}
			}
		}
	}
}
