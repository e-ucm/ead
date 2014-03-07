/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.view.builders.mockup.gallery;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import es.eucm.ead.editor.assets.EditorAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.DescriptionCard;
import es.eucm.ead.editor.view.widgets.mockup.panels.GalleryEntity;
import es.eucm.ead.editor.view.widgets.mockup.panels.GalleryGrid;
import es.eucm.ead.editor.view.widgets.mockup.panels.GalleryGrid.SelectListener;
import es.eucm.ead.engine.I18N;

/**
 * Abstract class. A layout that holds a top tool bar and a gallery grid in the
 * center..
 */
public abstract class BaseGallery<T extends DescriptionCard> implements
		ViewBuilder {

	private ObjectMap<String, Comparator<T>> comparators;
	private GalleryGrid<Actor> galleryTable;
	private SelectBox<String> orderingBox;
	private Actor firstPositionActor;
	private String currentOrdering;
	private Array<T> prevElements;
	private Array<T> elements;
	private Table rootWindow;
	private TextField searchField;

	@Override
	public String getName() {
		return null;
	}

	/**
	 * Make the Gallery view with three WidgetsGroup that return the bottom,
	 * center and topWidget functions. If any WidgetGroup is null, this is not
	 * added.
	 * */
	@Override
	public Actor build(Controller controller) {
		I18N i18n = controller.getEditorAssets().getI18N();
		Skin skin = controller.getEditorAssets().getSkin();
		final Vector2 viewport = controller.getPlatform().getSize();

		this.rootWindow = new Table().debug();
		this.rootWindow.setFillParent(true);

		WidgetGroup top = topWidget(viewport, i18n, skin, controller);
		WidgetGroup center = centerWidget(viewport, i18n, skin, controller);

		if (top != null) {
			this.rootWindow.add(top).expandX().fill();
		}
		if (center != null) {
			this.rootWindow.row();
			this.rootWindow.add(center).center().fill().expand();
		}
		addActorToHide(top);
		return this.rootWindow;
	}

	/**
	 * This adds an actor that will be hidden when we enter selection mode.
	 * Convenience method that shouldn't be overridden.
	 * 
	 * @param actorToHide
	 */
	protected void addActorToHide(Actor actorToHide) {
		this.galleryTable.addActorToHide(actorToHide);
	}

	/**
	 * This method constructs the top tool bar. You shouldn't override this
	 * method unless you know what you're doing.
	 * 
	 * @param viewport
	 * @param i18n
	 * @param skin
	 * @param controller
	 * @return
	 */
	protected WidgetGroup topWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {

		String search = i18n.m("general.gallery.search");
		this.searchField = new TextField("", skin);
		this.searchField.setMessageText(search);
		this.searchField.setMaxLength(search.length());
		this.searchField.setTextFieldListener(new TextFieldListener() {

			@Override
			public void keyTyped(TextField textField, char key) {

				updateDisplayedElements();

				if (key == '\n' || key == '\r') {
					resetElements();
				}
			}
		});
		final String none = i18n.m("general.gallery.sort"), nameaz = i18n
				.m("general.gallery.nameAZ"), nameza = i18n
				.m("general.gallery.nameZA");
		this.comparators = new ObjectMap<String, Comparator<T>>(8);
		this.comparators.put(nameaz, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return o1.getTitle().compareTo(o2.getTitle());
			}
		});
		this.comparators.put(nameza, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return o2.getTitle().compareTo(o1.getTitle());
			}
		});

		final Array<String> orders = new Array<String>(false, 4, String.class);
		orders.add(none);
		orders.add(nameaz);
		orders.add(nameza);

		addSortingsAndComparators(orders, this.comparators, i18n);

		this.orderingBox = new SelectBox<String>(skin);
		this.orderingBox.setItems(orders);
		this.orderingBox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				final String selectedOrdering = orderingBox.getSelected();
				if (currentOrdering.equals(selectedOrdering))
					return;
				currentOrdering = selectedOrdering;
				updateDisplayedElements();
			}
		});
		this.currentOrdering = this.orderingBox.getSelected();
		this.elements = new Array<T>(false, 10, GalleryEntity.class);
		this.prevElements = new Array<T>(false, 10, GalleryEntity.class);

		ToolBar topBar = new ToolBar(viewport, skin);
		topBar.debug();
		topBar.add(topLeftButton(viewport, skin, controller)).left().expandX();
		topBar.right();
		topBar.add(this.searchField, this.orderingBox);

		return topBar;
	}

	/**
	 * Add here additional sorting methods as {@link String}s to the
	 * {@link Array} and their corresponding {@link Comparator}s to the
	 * {@link ObjectMap}.
	 * 
	 * @param orders
	 *            add here the additional {@link String}s
	 * @param comparators
	 *            add here {@link Comparator}s for every new shorting added
	 * @param i18n
	 */
	protected abstract void addSortingsAndComparators(Array<String> shortings,
			ObjectMap<String, Comparator<T>> comparators, I18N i18n);

	/**
	 * This method should never return null.
	 * 
	 * @param skin
	 * @return The button that will be placed at left in the top tool bar.
	 */
	protected abstract Button topLeftButton(Vector2 viewport, Skin skin,
			Controller controller);

	/**
	 * This method constructs the central widget which is a {@link GalleryGrid}.
	 * You shouldn't override this method unless you know what you're doing.
	 * 
	 * @param viewport
	 * @param i18n
	 * @param skin
	 * @param controller
	 * @return
	 */
	protected WidgetGroup centerWidget(Vector2 viewport, final I18N i18n,
			Skin skin, final Controller controller) {

		Table centerWidget = new Table().debug();

		this.galleryTable = new GalleryGrid<Actor>(skin, 3, viewport,
				this.rootWindow, controller) {
			@Override
			@SuppressWarnings("unchecked")
			protected void entityClicked(InputEvent event, Actor targetActor) {
				BaseGallery.this.entityClicked(event, (T) targetActor,
						controller, i18n);
			}
		};
		this.galleryTable.debug();

		this.firstPositionActor = getFirstPositionActor(viewport, i18n, skin,
				controller);

		ScrollPane sp = new ScrollPane(this.galleryTable);
		sp.setScrollingDisabled(true, false);

		centerWidget.add(sp).expand().fillX().top();

		return centerWidget;
	}

	/**
	 * Should return the button that will be placed at the first row and first
	 * column of the {@link GalleryGrid galleryTable}. If it's null, no actor
	 * will be added.
	 * 
	 * @return the actor or null.
	 */
	protected abstract Actor getFirstPositionActor(Vector2 viewport, I18N i18n,
			Skin skin, Controller controller);

	/**
	 * Adds the updated elements via {@link Array elements}, if returns false,
	 * nothing will be updated.
	 * 
	 * @param controller
	 * @param elements
	 *            may not be empty, if needs to be updated should be cleared
	 *            first.
	 * @param viewport
	 * @param i18n
	 * @param skin
	 * @return true if gallery elements should be updated, false otherwise
	 */
	protected abstract boolean updateGalleryElements(Controller controller,
			Array<T> elements, Vector2 viewport, I18N i18n, Skin skin);

	@Override
	public void initialize(Controller controller) {
		final EditorAssets editorAssets = controller.getEditorAssets();
		final Skin skin = editorAssets.getSkin();
		final I18N i18n = editorAssets.getI18N();
		final Vector2 viewport = controller.getPlatform().getSize();
		if (updateGalleryElements(controller, this.elements, viewport, i18n,
				skin)) {
			updateDisplayedElements();
		}
	}

	/**
	 * Shorts the {@link GalleryGrid galleryTable}, if the first button returned
	 * by getAddToGalleryButton() method is null the whole {@link GalleryGrid
	 * galleryTable} will be shorted, else the first element won't change its
	 * position within the gallery.
	 */
	private void shortGalleryElements() {
		final String selectedOrder = this.orderingBox.getSelected();
		final Comparator<T> comparator = this.comparators.get(selectedOrder);
		if (comparator != null) {
			Arrays.sort(this.elements.items, 0, this.elements.size, comparator);
		}
		for (T element : this.elements) {
			this.galleryTable.addItem(element);
		}
	}

	/**
	 * Clears the {@link GalleryTable} and adds the first actor if is not null.
	 */
	private void restartGalleryTable() {
		this.galleryTable.clear();
		if (this.firstPositionActor != null) {
			this.galleryTable.addItem(this.firstPositionActor);
		}
	}

	/**
	 * Filters the elements depending on the current value of the search
	 * {@link TextField text field}.
	 */
	private void filterBySearch() {
		if (this.prevElements.size == 0) {
			this.prevElements.addAll(this.elements);
		}

		this.elements.clear();
		final String search = this.searchField.getText();
		final Pattern findPattern = Pattern.compile(search,
				Pattern.CASE_INSENSITIVE);
		final Matcher matcher = findPattern.matcher("");
		for (T entity : this.prevElements) {
			matcher.reset(entity.getTitle());
			if (matcher.find()) {
				this.elements.add(entity);
			}
		}
	}

	/**
	 * Resets the elements from the elements array to their previous state.
	 */
	private void resetElements() {
		if (this.prevElements.size != 0) {
			this.elements.clear();
			this.elements.addAll(this.prevElements);
			this.prevElements.clear();
		}
	}

	/**
	 * Updates the displayed elements depending of the sorting order and search
	 * value.
	 */
	private void updateDisplayedElements() {
		filterBySearch();
		restartGalleryTable();
		shortGalleryElements();
	}

	/**
	 * Invoked only if target Actor implements {@link SelectListener}. Called
	 * when the target Actor was clicked if the {@link GalleryGrid} isn't in
	 * Selection Mode. Convenience method that should be overridden if needed.
	 */
	protected abstract void entityClicked(InputEvent event, T target,
			Controller controller, I18N i18n);

	@Override
	public void release(Controller controller) {
		resetElements();
	}
}
