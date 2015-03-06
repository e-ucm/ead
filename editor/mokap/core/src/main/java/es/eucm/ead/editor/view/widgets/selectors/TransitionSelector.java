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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearGallery;
import es.eucm.ead.editor.view.widgets.layouts.LinearGallery.FocusEvent;
import es.eucm.ead.editor.view.widgets.layouts.LinearGallery.FocusListener;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.effects.GoScene.Transition;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

public class TransitionSelector extends LinearLayout implements
		Selector<String>, BackListener {

	private SelectorListener<String> selectorListener;

	private SelectTransitionsGallery transitionSelector;

	private SelectBox<String> box;

	private Controller controller;

	public TransitionSelector(Controller controller) {
		super(false);
		this.controller = controller;
		Skin skin = controller.getApplicationAssets().getSkin();
		final I18N i18N = controller.getApplicationAssets().getI18N();
		background(skin.getDrawable(SkinConstants.DRAWABLE_BLANK));

		MultiWidget toolbar = new MultiWidget(skin, SkinConstants.STYLE_TOOLBAR);
		LinearLayout buttons = new LinearLayout(true);
		toolbar.addWidgets(buttons);

		Actor accept = WidgetBuilder.toolbarIcon(SkinConstants.IC_CHECK,
				i18N.m("accept"));
		buttons.add(accept);
		accept.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor actor = transitionSelector.gallery.getCurrentPage();
				selectorListener.selected(actor.getName());
			}
		});

		buttons.addSpace();
		buttons.add(box = new SelectBox<String>(skin,
				SkinConstants.STYLE_TOOLBAR));

		add(toolbar).expandX();
		add(
				transitionSelector = new SelectTransitionsGallery(controller
						.getEditorGameAssets(), skin, i18N)).expand(true, true);
		transitionSelector.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor currentPage = transitionSelector.gallery.getCurrentPage();
				box.setSelected(i18N.m(currentPage.getName()));
			}
		});
		transitionSelector.addListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent event) {
				for (TransitionDrawable drawable : transitionSelector.pendingCurrentTextures) {
					drawable.setUpdate(false);
				}

				Image image = (Image) ((Container) event.getActor()).getActor();
				((TransitionDrawable) image.getDrawable()).setUpdate(true);
			}
		});
		box.getSelection().setProgrammaticChangeEvents(false);
	}

	@Override
	public void prepare(SelectorListener<String> selectorListener,
			Object... args) {
		this.selectorListener = selectorListener;
		transitionSelector.prepare(args);
	}

	public class SelectTransitionsGallery extends AbstractWidget {

		private Skin skin;

		private I18N i18N;

		private Array<TransitionDrawable> pendingCurrentTextures = new Array<TransitionDrawable>();
		private Array<TransitionDrawable> pendingNextTextures = new Array<TransitionDrawable>();

		private LinearGallery gallery;

		private UpdateSelected updateSelected = new UpdateSelected();

		private AssetLoadedCallback<Texture> currentTexturesCallback = new AssetLoadedCallback<Texture>() {

			@Override
			public void loaded(String fileName, Texture asset) {
				for (TransitionDrawable drawable : pendingCurrentTextures) {
					drawable.setCurrentTexture(asset);
				}
			}

			@Override
			public void error(String fileName, Class type, Throwable exception) {

			}
		};

		private AssetLoadedCallback<Texture> nextTexturesCallback = new AssetLoadedCallback<Texture>() {

			@Override
			public void loaded(String fileName, Texture asset) {
				for (TransitionDrawable drawable : pendingNextTextures) {
					drawable.setNextTexture(asset);
				}
			}

			@Override
			public void error(String fileName, Class type, Throwable exception) {
			}
		};

		public SelectTransitionsGallery(Assets assets, Skin skin, I18N i18N) {
			addActor(gallery = new LinearGallery(skin,
					SkinConstants.DRAWABLE_BLANK));
			gallery.setFillParent(true);
			this.skin = skin;
			this.i18N = i18N;
		}

		public void addTransitionPreview(String id) {
			TransitionDrawable thumbnail = new TransitionDrawable();
			thumbnail.setTransition(Transition.fromValue(id), 2f);
			Image image = new Image(thumbnail);
			pendingCurrentTextures.add(thumbnail);
			pendingNextTextures.add(thumbnail);
			Container<Image> container = new Container<Image>(image);
			container.setClip(true);
			container.fill();
			container.background(skin.getDrawable(SkinConstants.DRAWABLE_PAGE));
			container.setName(id);
			gallery.add(container);
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
				String[] items = new String[values.length];
				for (int i = 0; i < values.length; ++i) {
					Transition transition = values[i];
					String name = transition.toString();
					addTransitionPreview(name);
					items[i] = i18N.m(name);
				}

				box.setItems(items);
				box.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						SelectBox box = (SelectBox) actor;
						gallery.scrollToPage(box.getSelectedIndex());
					}
				});
			}

			updateSelected.index = box.getItems().indexOf(
					i18N.m((String) args[0]), false);
			Gdx.app.postRunnable(updateSelected);

			Q.getThumbnailTexture(selectedScene, currentTexturesCallback);

			Q.getThumbnailTexture(nextScene, nextTexturesCallback);

		}

		@Override
		public void clear() {
			gallery.clearChildren();
			pendingCurrentTextures.clear();
		}
	}

	@Override
	public boolean onBackPressed() {
		selectorListener.cancelled();
		return true;
	}

	private class UpdateSelected implements Runnable {
		private int index;

		@Override
		public void run() {
			transitionSelector.gallery.scrollToPage(index);
		}
	}
}
