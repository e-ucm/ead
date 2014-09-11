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
package es.eucm.ead.editor.view.builders.gallery;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.GridPanel;
import es.eucm.ead.editor.view.widgets.Notification;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.gallery.GalleryItem;
import es.eucm.ead.editor.view.widgets.gallery.SearchWidget;
import es.eucm.ead.editor.view.widgets.gallery.SortWidget;
import es.eucm.ead.editor.view.widgets.helpmessage.sequence.HelpSequence;
import es.eucm.ead.editor.view.widgets.iconwithpanel.Settings;
import es.eucm.ead.engine.I18N;

public abstract class BaseGallery implements ViewBuilder {

	private static final int DEFAULT_COLUMNS = 5;

	public static final float ICON_SIZE = 0.06f, TOOLBAR_SIZE = 0.075f,
			ICON_PAD = 0.0075f, SMALL_PAD = 20f, DEFAULT_ENTYTY_SPACING = 20f,
			MIN_ITEM_HEIGHT = 165F, UNDO_POPUP_TIMEOUT = 5F, PLAY_PAD = 100F;

	private static final ClickListener newButtonListener = new ClickListener() {

		public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event,
				float x, float y) {
			BaseGallery gallery = (BaseGallery) event.getListenerActor()
					.getUserObject();
			gallery.newItem();
		};
	};

	protected int getColumns() {
		return DEFAULT_COLUMNS;
	}

	private static final ClickListener undoListener = new ClickListener() {

		public void clicked(InputEvent event, float x, float y) {
			BaseGallery gallery = (BaseGallery) event.getListenerActor()
					.getUserObject();
			Array<Undoable> undosPending = gallery.undosPending;

			if (undosPending.size != 0) {
				undosPending.pop().undo();
				gallery.updateDisplayedElements();
			}

			// Dismiss dialog or change text
			Notification notif = gallery.undoNotification;
			if (undosPending.size != 0) {
				gallery.changePopupText();
				notif.show(gallery.topBar.getStage(), UNDO_POPUP_TIMEOUT);
			} else {
				if (notif.hasParent()) {
					notif.hide();
				}
			}
		};
	};

	protected Notification undoNotification;
	protected Array<Undoable> undosPending;
	protected GridPanel<Actor> galleryGrid;
	protected Array<GalleryItem> items;
	private SearchWidget searchWidget;
	protected Controller controller;
	private SortWidget sortWidget;
	protected Button newButton;
	protected Toolbar topBar;
	protected float iconSize;
	protected float iconPad;
	private TextButton undo;
	protected Table view;
	protected Skin skin;
	protected I18N i18n;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;
		ApplicationAssets applicationAssets = controller.getApplicationAssets();
		this.skin = applicationAssets.getSkin();
		i18n = applicationAssets.getI18N();

		undoNotification = new Notification(skin) {
			public void hide() {
				super.hide();
				discardUndo();
			};
		}.modal(false);
		undo = new TextButton(i18n.m("undo"), skin, "white");
		undo.setUserObject(this);
		undo.addListener(undoListener);
		undoNotification.add(undo);
		this.items = new Array<GalleryItem>(true, 8, GalleryItem.class);
		undosPending = new Array<Undoable>(4);

		float viewportHeight = controller.getPlatform().getSize().y;
		iconSize = viewportHeight * ICON_SIZE;
		iconPad = viewportHeight * ICON_PAD;
		float toolbarSize = viewportHeight * TOOLBAR_SIZE;

		view = new Table();
		view.align(Align.top);
		view.setFillParent(true);

		topBar = new Toolbar(skin, "white_top");

		Actor backButton = createBackButton();
		if (backButton != null) {
			topBar.add(backButton).left().fill().size(toolbarSize);
		}

		Actor play = createPlayButton();
		if (play != null) {
			topBar.add(play).left().padLeft(PLAY_PAD).fill();
		}

		Actor share = createShareButton();
		if (share != null) {
			topBar.add(share).left().padLeft(SMALL_PAD).fill();
		}

		Actor toolbarText = createToolbarText();
		topBar.add(toolbarText).expand();

		searchWidget = createSearchWidget();
		topBar.add(searchWidget).padRight(SMALL_PAD).fill();

		sortWidget = addReorderWidget();
		topBar.add(sortWidget).padRight(SMALL_PAD).fill();

		Actor settings = createSettings(controller);
		if (settings != null) {
			topBar.add(settings).padRight(SMALL_PAD).fill();
		}

		createnNewButton();

		galleryGrid = new GridPanel<Actor>(getColumns(), DEFAULT_ENTYTY_SPACING);
		ScrollPane galleryPane = new ScrollPane(galleryGrid);
		galleryPane.setScrollingDisabled(true, false);

		view.add(topBar).expandX().fill().height(toolbarSize);
		view.row();
		view.add(galleryPane).expand().fillX().top();

		HelpSequence helpSequence = getHelpSequence(controller);
		if (helpSequence != null) {
			((MockupViews) controller.getViews())
					.registerHelpMessage(helpSequence);
		}
	}

	private void createnNewButton() {
		String newButtonIcon = getNewButtonIcon();
		if (newButtonIcon != null) {
			newButton = new Button(skin.get(ButtonStyle.class));
			Image image = new Image(skin.getDrawable(newButtonIcon));
			image.setScaling(Scaling.fit);
			newButton.add(image);
			newButton.setUserObject(this);
			newButton.addListener(newButtonListener);
		}
	}

	/**
	 * Invoked when {@link #newButton} was clicked.
	 */
	protected void newItem() {

	}

	/**
	 * Invoked when a {@link GalleryItem} was clicked.
	 */
	public void itemClicked(GalleryItem item) {

	}

	/**
	 * Invoked when an item was deleted.
	 * 
	 * @param item
	 */
	public void deleteItem(final GalleryItem item) {
		Cell<?> cell = galleryGrid.getCell(item);
		if (cell != null) {
			item.remove();
			final int position = items.indexOf(item, true);
			items.removeValue(item, true);
			updateDisplayedElements();
			undosPending.add(new Undoable() {

				@Override
				public void undo() {
					items.insert(position, item);
				}

				@Override
				public String getTitle() {
					return item.getName();
				}

				@Override
				public void discard() {
					item.deleteItem();
				}
			});
			changePopupText();
			undoNotification.show(topBar.getStage(), UNDO_POPUP_TIMEOUT);
		}
	}

	/**
	 * Discard all stored undos and hide the undo popup dialog.
	 */
	public void discardUndo() {
		for (Undoable undoable : undosPending) {
			undoable.discard();
		}
		undosPending.clear();
	}

	protected SortWidget addReorderWidget() {
		SortWidget sortWidget = new SortWidget(skin, iconPad, iconSize, items,
				this);
		return sortWidget;
	}

	protected SearchWidget createSearchWidget() {
		SearchWidget searchWidget = new SearchWidget(iconPad, iconSize, skin,
				i18n, items, this);
		searchWidget.getPanel().addTouchableActor(topBar);
		return searchWidget;
	}

	protected void loadItems(Array<GalleryItem> items) {

	}

	protected Actor createSettings(Controller controller) {
		Settings settings = new Settings(controller, iconPad, iconSize);
		settings.getPanel().addTouchableActor(topBar);
		return settings;
	}

	protected Actor createShareButton() {
		return null;
	}

	protected abstract Actor createPlayButton();

	protected abstract Actor createBackButton();

	protected abstract Actor createToolbarText();

	protected abstract String getNewButtonIcon();

	protected abstract HelpSequence getHelpSequence(Controller controller);

	@Override
	public void release(Controller controller) {
		discardUndo();
		undoNotification.hide();
	}

	@Override
	public Actor getView(Object... args) {
		loadItems(items);
		searchWidget.filter();
		sort();
		updateDisplayedElements();
		return view;
	}

	public void sort() {
		sortWidget.sort();
	}

	/**
	 * Updates the displayed elements depending of the sorting order and search
	 * value.
	 */
	public void updateDisplayedElements() {
		this.galleryGrid.clear();
		if (this.newButton != null) {
			this.galleryGrid.addItem(this.newButton).minHeight(MIN_ITEM_HEIGHT);
		}
		for (GalleryItem element : items) {
			galleryGrid.addItem(element);
		}
		galleryGrid.invalidateHierarchy();
	}

	public abstract static class Undoable {

		public abstract void undo();

		public String getTitle() {
			return null;
		}

		public void discard() {
		}

	}

	/**
	 * Changes the text of the undo popup. If more then one item can be undone,
	 * the number of deleted items will be shown. If only one deletion can be
	 * undone, the title of this deletion (or a default string in case the title
	 * is {@code null}) will be shown.
	 */
	private void changePopupText() {
		String msg = null;
		if (undosPending.size > 1) {
			msg = i18n.m("gallery.deletedElements", undosPending.size);
		} else if (undosPending.size == 1) {
			String title = undosPending.peek().getTitle();
			if (title == null || title.isEmpty()) {
				msg = "1 " + i18n.m("gallery.deletedElement");
			} else {
				msg = i18n.m("gallery.deletedElement") + ": " + title;
			}
		}
		undoNotification.clearChildren();
		undoNotification.text(msg);
		undoNotification.add(undo);
	}

	public I18N getI18n() {
		return i18n;
	}
}
