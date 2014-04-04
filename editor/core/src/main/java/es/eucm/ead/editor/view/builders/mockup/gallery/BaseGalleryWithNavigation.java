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
package es.eucm.ead.editor.view.builders.mockup.gallery;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.ChangeInitialScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.DescriptionCard;
import es.eucm.ead.editor.view.widgets.mockup.buttons.SceneButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.game.GameData;
import es.eucm.ead.schema.entities.ModelEntity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Abstract class. This implementation of {@link BaseGallery} also has a
 * navigation button with its {@link HiddenPanel panel}, a filter button with
 * its {@link HiddenPanel panel} and a bottom tool bar with one or two buttons
 * (Take picture and Take photo).
 * 
 * This is the base implementation for the galleries that will display scenes,
 * elements or both.
 */
public abstract class BaseGalleryWithNavigation<T extends DescriptionCard>
		extends BaseGallery<T> {

	private static final String IC_CHANGE = "ic_delete";// Change for other icon

	private Table tagList;
	private Navigation navigation;
	private String selectedSceneId;
	private HiddenPanel filterPanel;
	private Array<T> prevTagElements;
	private EventListener tagCheckBoxListener;
	private Array<String> totalTags, selectedTags;
	private Comparator<String> filterTagsComparator;
	private ToolbarButton initialSceneButton;

	@Override
	public Actor build(Controller controller) {
		final I18N i18n = controller.getApplicationAssets().getI18N();
		final Skin skin = controller.getApplicationAssets().getSkin();
		final Vector2 viewport = controller.getPlatform().getSize();

		this.navigation = new Navigation(viewport, controller, skin);
		final Table rootWindow = (Table) super.build(controller);
		final WidgetGroup bottom = bottomWidget(viewport, i18n, skin,
				controller);

		if (bottom != null) {
			rootWindow.row();
			rootWindow.add(bottom).expandX().fill();
		}
		addActorToHide(bottom);
		this.prevTagElements = new Array<T>(false, 10, DescriptionCard.class);
		return rootWindow;
	}

	@Override
	protected Button topLeftButton(Vector2 viewport, Skin skin,
			Controller controller) {
		return this.navigation.getButton();
	}

	@Override
	protected WidgetGroup topWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		final Table top = (Table) super.topWidget(viewport, i18n, skin,
				controller);

		final Button filterButton = new TextButton(
				i18n.m("general.gallery.filter"), skin);
		filterButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (BaseGalleryWithNavigation.this.filterPanel.isVisible()) {
					BaseGalleryWithNavigation.this.resetElements();
					BaseGalleryWithNavigation.this.filterPanel.hide();
				} else {
					BaseGalleryWithNavigation.this.filterPanel.show();
				}
				return false;
			}
		});

		top.add(filterButton);

		return top;
	}

	@Override
	protected WidgetGroup centerWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		final Table centerWidget = (Table) super.centerWidget(viewport, i18n,
				skin, controller);

		this.filterPanel = new HiddenPanel(skin);
		this.filterPanel.setStageBackground(null);
		this.filterPanel.setModal(false);
		this.filterPanel.setVisible(false);

		this.totalTags = new Array<String>(false, 5, String.class);
		this.selectedTags = new Array<String>(false, 5, String.class);
		this.filterTagsComparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		};
		this.tagCheckBoxListener = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (actor instanceof CheckBox) {
					final CheckBox tagCheckBox = (CheckBox) actor;
					if (tagCheckBox.isChecked()) {
						BaseGalleryWithNavigation.this.selectedTags
								.add(tagCheckBox.getText().toString());
					} else {
						BaseGalleryWithNavigation.this.selectedTags
								.removeValue(tagCheckBox.getText().toString(),
										false);
					}
					BaseGalleryWithNavigation.this.updateDisplayedElements();
				}
			}
		};

		this.tagList = new Table(skin);
		this.tagList.left();
		this.tagList.defaults().left();
		final ScrollPane tagScroll = new ScrollPane(this.tagList, skin,
				"opaque");
		tagScroll.setScrollingDisabled(true, false);

		this.filterPanel.add(tagScroll).fill().left();

		final Container wrapper = new Container(this.filterPanel);
		wrapper.setFillParent(true);
		wrapper.right().top();
		centerWidget.addActor(wrapper);

		final Container navWrapper = new Container(this.navigation.getPanel());
		navWrapper.setFillParent(true);
		navWrapper.top().left().fillY();
		centerWidget.addActor(navWrapper);
		return centerWidget;
	}

	@Override
	public void initialize(Controller controller) {
		if (updateFilterTags(this.totalTags, controller)) {
			updateFilterPanel(controller.getApplicationAssets().getSkin(),
					controller.getApplicationAssets().getI18N());
		}
		super.initialize(controller);
	}

	/**
	 * Updates the right filter panel with the new values.
	 * 
	 * @param skin
	 * @param i18n
	 */
	private void updateFilterPanel(Skin skin, I18N i18n) {
		this.tagList.clearChildren();
		final int totalTagsSize = this.totalTags.size;

		if (totalTagsSize == 0) {
			final Label emptyLabel = new Label(
					i18n.m("general.gallery.empty-tags"), skin);
			this.tagList.add(emptyLabel).pad(20f);
			return;
		}

		Arrays.sort(this.totalTags.items, 0, totalTagsSize,
				this.filterTagsComparator);

		final int lastRow = totalTagsSize - 1;
		for (int i = 0; i < totalTagsSize; ++i) {
			final CheckBox tagCheckBox = new CheckBox(this.totalTags.get(i),
					skin);
			tagCheckBox.addListener(this.tagCheckBoxListener);
			this.tagList.add(tagCheckBox).padRight(55f);
			if (i < lastRow)
				this.tagList.row();
		}
	}

	@Override
	protected void filter() {
		super.filter();
		filterByTags();
	}

	/**
	 * Filters the elements depending on the current
	 * {@link BaseGalleryWithNavigation#selectedTags selected tags}.
	 */
	private void filterByTags() {
		if (this.selectedTags.size == 0)
			return;
		this.prevTagElements.clear();
		this.prevTagElements.addAll(this.elements);

		this.elements.clear();
		for (final String tag : this.selectedTags) {
			for (final T element : this.prevTagElements) {
				if (elementHasTag(element, tag)
						&& !this.elements.contains(element, false)) {
					this.elements.add(element);
				}
			}
		}
	}

	/**
	 * This method should return true if the element has associated the tag.
	 * 
	 * @param element
	 * @param tag
	 * @return
	 */
	protected abstract boolean elementHasTag(T element, String tag);

	/**
	 * This method constructs the bottom tool bar. This tool bar usually has one
	 * or two buttons (Take picture and Take video).
	 * 
	 * @param viewport
	 * @param i18n
	 * @param skin
	 * @param controller
	 * @return The widget that will be placed at the bottom of the view.
	 */
	protected WidgetGroup bottomWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		final ToolBar botBar = new ToolBar(viewport, skin, 0.04f);

		final Button bottomLeftButton = bottomLeftButton(viewport, i18n, skin,
				controller);
		if (bottomLeftButton != null) {
			botBar.add(bottomLeftButton).left().expandX();
		}
		final Button bottomRightButton = bottomRightButton(viewport, i18n,
				skin, controller);
		if (bottomRightButton != null) {
			botBar.add(bottomRightButton).right();
		}

		return botBar;
	}

	@Override
	protected void addSortingsAndComparators(Array<String> shortings,
			ObjectMap<String, Comparator<T>> comparators, I18N i18n) {
		// Do nothing since we won't have additional sorting methods in
		// ElementGallery, SceneGallery or Gallery
	}

	@Override
	protected void addExtrasToTopToolbar(ToolBar topToolbar, Vector2 viewport,
			Skin skin, I18N i18n, final Controller controller) {
		initialSceneButton = new ToolbarButton(viewport, IC_CHANGE,
				i18n.m("general.make-initial"), skin);
		initialSceneButton.setVisible(false);
		initialSceneButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ChangeInitialScene.class, selectedSceneId);
			}
		});
		topToolbar.add(initialSceneButton);
	}

	@Override
	protected void entitySelected(T actor, int entitiesCount,
			Controller controller) {
		if (entitiesCount == 1 && actor instanceof SceneButton) {
			this.selectedSceneId = ((SceneButton) actor).getKey();
			GameData gameData = Model.getComponent(controller.getModel()
					.getGame(), GameData.class);
			if (!this.selectedSceneId.equals(gameData))
				initialSceneButton.setVisible(true);
		} else {
			initialSceneButton.setVisible(false);
		}
	}

	/**
	 * This method should return the button that will be placed at left in the
	 * bottom tool bar.
	 * 
	 * @param viewport
	 * @param i18n
	 * @param skin
	 * @param controller
	 * @return
	 */
	protected abstract Button bottomLeftButton(Vector2 viewport, I18N i18n,
			Skin skin, Controller controller);

	/**
	 * This method should return the button that will be placed at right in the
	 * bottom tool bar.
	 * 
	 * @param viewport
	 * @param i18n
	 * @param skin
	 * @param controller
	 * @return
	 */
	protected abstract Button bottomRightButton(Vector2 viewport, I18N i18n,
			Skin skin, Controller controller);

	/**
	 * This method should add all the available filter tags to the array. If
	 * returns true, an UI update will be performed over the tags filter panel
	 * with the new tags. Default implementation iterates through every
	 * {@link String tag} of every
	 * {@link es.eucm.ead.schema.entities.ModelEntity} of every scene in the
	 * model and adds it to the array. Default implementation always returns
	 * true if a new tag was added.
	 * 
	 * @param tags
	 * @param controller
	 * @return true if the Array of tags changed, false otherwise
	 */
	protected boolean updateFilterTags(Array<String> tags, Controller controller) {
		boolean needsUIupdate = false;
		Map<String, ModelEntity> map = controller.getModel().getScenes();
		for (Entry<String, ModelEntity> entry : map.entrySet()) {
			List<ModelEntity> sceneChildren = entry.getValue().getChildren();
			int totalChildren = sceneChildren.size();
			for (int i = 0; i < totalChildren; ++i) {
				ModelEntity currentChildren = sceneChildren.get(i);
				for (ModelComponent component : currentChildren.getComponents()) {
					if (component instanceof Tags) {
						List<String> childrenTags = ((Tags) component)
								.getTags();
						int totalChildrenTags = childrenTags.size();
						for (int j = 0; j < totalChildrenTags; ++j) {
							String currentTag = childrenTags.get(j);
							if (!tags.contains(currentTag, false)) {
								tags.add(currentTag);
								needsUIupdate = true;
							}
						}
					}
				}
			}
		}
		return needsUIupdate || tags.size == 0;
	}
}
