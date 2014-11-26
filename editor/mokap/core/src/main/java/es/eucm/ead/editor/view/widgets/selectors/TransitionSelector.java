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
package es.eucm.ead.editor.view.widgets.selectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.GalleryStyle;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.effects.GoScene.Transition;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

public class TransitionSelector extends LinearLayout implements
		Selector<String> {

	private SelectorListener<String> selectorListener;

	private SelectTransitionsGallery transitionSelector;

	private Controller controller;

	public TransitionSelector(Controller controller) {
		super(false);
		this.controller = controller;
		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18N = controller.getApplicationAssets().getI18N();
		background(skin.getDrawable(SkinConstants.DRAWABLE_BLANK));

		MultiWidget toolbar = new MultiWidget(skin, SkinConstants.STYLE_TOOLBAR);
		LinearLayout buttons = new LinearLayout(true);
		toolbar.addWidgets(buttons);

		Actor cancel = WidgetBuilder.toolbarIcon(SkinConstants.IC_GO,
				i18N.m("cancel"));
		buttons.add(cancel);
		cancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				selectorListener.cancelled();
			}
		});

		buttons.add(new Label(i18N.m("select.transition"), skin,
				SkinConstants.STYLE_TOOLBAR));
		buttons.addSpace();

		add(toolbar).expandX();
		add(
				transitionSelector = new SelectTransitionsGallery(Gdx.graphics
						.getHeight() / 1.6f, 2, controller
						.getEditorGameAssets(), skin, i18N)).expand(true, true);
	}

	@Override
	public void prepare(SelectorListener<String> selectorListener,
			Object... args) {
		this.selectorListener = selectorListener;
		transitionSelector.gallery.uncheckAll();
		transitionSelector.prepare(args);
		if (args.length > 0 && args[0] != null) {
			String transitionname = (String) args[0];
			Actor actor = findActor(transitionname);
			if (actor instanceof Cell) {
				((Cell) actor).checked(true);
			}
		}
	}

	public class SelectTransitionsGallery extends AbstractWidget {

		protected Skin skin;

		protected I18N i18N;

		protected Array<TransitionDrawable> pendingCurrentTextures = new Array<TransitionDrawable>();
		protected Array<TransitionDrawable> pendingNextTextures = new Array<TransitionDrawable>();

		protected Gallery gallery;

		public SelectTransitionsGallery(float rowHeight, int columns,
				Assets assets, Skin skin, I18N i18N) {
			this(rowHeight, columns, assets, skin, i18N, skin
					.get(GalleryStyle.class));
		}

		public SelectTransitionsGallery(float rowHeight, int columns,
				Assets assets, Skin skin, I18N i18N, String galleryStyle) {
			this(rowHeight, columns, assets, skin, i18N, skin.get(galleryStyle,
					GalleryStyle.class));
		}

		public SelectTransitionsGallery(float rowHeight, int columns,
				Assets assets, Skin skin, I18N i18N, GalleryStyle galleryStyle) {
			addActor(gallery = new Gallery(rowHeight, columns, galleryStyle));
			gallery.setFillParent(true);
			this.skin = skin;
			this.i18N = i18N;
		}

		public void addTransitionPreview(String id, String title) {
			TransitionDrawable thumbnail = new TransitionDrawable();
			thumbnail.setTransition(Transition.fromValue(id), 2f);
			Image image = new Image(thumbnail) {
				@Override
				public void draw(Batch batch, float parentAlpha) {
					if (clipBegin(getX(), getY(), getWidth(), getHeight())) {
						super.draw(batch, parentAlpha);
						batch.flush();
						clipEnd();
					}
				}
			};
			pendingCurrentTextures.add(thumbnail);
			pendingNextTextures.add(thumbnail);

			title = title == null || "".equals(title) ? i18N.m("untitled")
					: title;
			Tile tile = WidgetBuilder.tile(image, title);
			tile.setName(id);
			prepareGalleryItem(tile, id);
			Cell cell = gallery.add(tile);
			cell.setName(id);
		}

		protected void prepareGalleryItem(Actor actor, final String id) {
			actor.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					gallery.uncheckAll();
					Cell cell = (Cell) event.getListenerActor().getParent();
					cell.checked(true);
					selectorListener.selected(id);
				}
			});
		}

		public void prepare(Object... args) {

			Model model = controller.getModel();
			ModelEntity selectedScene = (ModelEntity) model.getSelection()
					.getSingle(Selection.SCENE);

			String nextSceneId = (String) args[1];
			ModelEntity nextScene = (ModelEntity) model.getResourceObject(
					nextSceneId, ResourceCategory.SCENE);

			if (pendingCurrentTextures.size == 0) {
				Transition[] values = Transition.values();
				for (int i = 0; i < values.length; ++i) {
					Transition transition = values[i];
					String name = transition.toString();
					addTransitionPreview(name, i18N.m(name));
				}
			}

			Q.getThumbnailTexture(selectedScene,
					new AssetLoadedCallback<Texture>() {

						@Override
						public void loaded(String fileName, Texture asset) {
							for (TransitionDrawable drawable : pendingCurrentTextures) {
								drawable.setCurrentTexture(asset);
							}

						}
					});

			Q.getThumbnailTexture(nextScene,
					new AssetLoadedCallback<Texture>() {

						@Override
						public void loaded(String fileName, Texture asset) {
							for (TransitionDrawable drawable : pendingNextTextures) {
								drawable.setNextTexture(asset);
							}
						}
					});

		}

		@Override
		public void clear() {
			gallery.clearChildren();
			pendingCurrentTextures.clear();
		}
	}
}
