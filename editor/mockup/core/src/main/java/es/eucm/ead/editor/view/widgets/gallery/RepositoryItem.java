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

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.RepoAuthor;
import es.eucm.ead.schema.editor.components.RepoElement;
import es.eucm.ead.schema.entities.ModelEntity;

public class RepositoryItem extends GalleryItem implements
		AssetLoadedCallback<Texture> {

	private static final ChangeListener showInfo = new ChangeListener() {

		@Override
		public void changed(ChangeEvent event, Actor actor) {
			RepositoryItem item = (RepositoryItem) event.getListenerActor()
					.getUserObject();
			item.info.show(item);
		}

	};

	private ModelEntity element;
	private RepoElement documentation;
	private ItemInfo info;

	private IconButton infoButton;

	public RepositoryItem(final ItemInfo info, Controller controller,
			ModelEntity element, BaseGallery gallery) {
		super(new Image(), "", 0f, 0f, false, controller.getApplicationAssets()
				.getSkin(), "repository", false, gallery);
		this.element = element;
		this.info = info;
		documentation = Q.getComponent(element, RepoElement.class);

		((Label) name).setText(getDocumentationName());

		infoButton = new IconButton("info80x80", 0f, controller
				.getApplicationAssets().getSkin(), "inverted");
		infoButton.setUserObject(this);
		infoButton.addListener(showInfo);

		addActor(infoButton);

		ApplicationAssets assets = controller.getApplicationAssets();
		assets.get(((MockupController) controller).getRepositoryManager()
				.getCurrentLibraryPath() + documentation.getThumbnail(),
				Texture.class, this);
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		setThumbnail(asset);
		positionInfoButton();
	}

	private void positionInfoButton() {
		Group parent = infoButton.getParent();
		float prefW = infoButton.getPrefWidth();
		float prefH = infoButton.getPrefHeight();
		infoButton.setBounds(parent.getWidth() - prefW, parent.getHeight()
				- prefH, prefW, prefH);
	}

	@Override
	public void layout() {
		super.layout();
		positionInfoButton();
	}

	@Override
	public String getName() {
		return getDocumentationName();
	}

	private String getDocumentationName() {
		if (documentation == null) {
			return " ";
		}
		String name = documentation.getName();
		return name == null ? " " : name;
	}

	public ModelEntity getElement() {
		return element;
	}

	/**
	 * A panel displaying the information contained inside a {@link RepoElement}
	 * .
	 * 
	 */
	public static class ItemInfo extends PositionedHiddenPanel {

		private static final float PAD = 10F;

		private static final float DURATION = .2f;

		private Image thumbnail;
		private Label name, description, author, license;
		private TextButton url;
		private RepositoryItem item;

		public ItemInfo(final Controller controller, Actor rootView) {
			super(controller.getApplicationAssets().getSkin(), Position.CENTER,
					rootView);

			ApplicationAssets applicationAssets = controller
					.getApplicationAssets();
			I18N i18n = applicationAssets.getI18N();
			Skin skin = controller.getApplicationAssets().getSkin();
			setBackground(skin.getDrawable("dialog"));
			thumbnail = new Image();
			thumbnail.setScaling(Scaling.fit);

			final TextButton close = new TextButton(i18n.m("close"), skin,
					"white");

			final TextButton addToScene = new TextButton(i18n.m("addToScene"),
					skin, "to_color");
			addToScene.setColor(Color.GREEN);
			url = new TextButton(i18n.m("webPage"), skin, "white");

			ClickListener listener = new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {
					Actor listenerActor = event.getListenerActor();
					if (listenerActor == url) {
						controller.getPlatform().browseURL(
								url.getUserObject().toString());
					} else if (close == listenerActor) {
						hide();
					} else if (addToScene == listenerActor) {
						item.gallery.itemClicked(item);
					}
				}
			};
			close.addListener(listener);
			addToScene.addListener(listener);
			url.addListener(listener);

			Table table = new Table(skin);
			ScrollPane pane = new ScrollPane(table);
			pane.setFadeScrollBars(true);
			pane.setScrollingDisabled(true, false);
			table.pad(PAD).defaults().space(PAD);
			table.add(i18n.m("name") + ":").colspan(2).left();
			table.row();
			table.add(name = new Label("", skin)).colspan(2);
			table.row();
			table.add(i18n.m("description") + ":").colspan(2).left();
			table.row();
			table.add(description = new Label("", skin)).colspan(2)
					.width(Value.percentWidth(1f, table));
			description.setWrap(true);
			description.setAlignment(Align.center);
			table.row();
			table.add(i18n.m("author") + ":").left();
			table.add(url);
			table.row();
			table.add(author = new Label("", skin)).colspan(2);
			table.row();
			table.add(i18n.m("license") + ":").colspan(2).left();
			table.row();
			table.add(license = new Label("", skin)).colspan(2);
			table.row();
			table.add(addToScene).left();
			table.add(close).right();

			add(thumbnail).maxWidth(Value.percentWidth(.5f, this)).left();
			add(pane).expand().pad(PAD);
			setModal(true);
		}

		public void show(RepositoryItem item) {
			this.item = item;
			name.setText(item.getName());
			RepoElement doc = item.documentation;
			thumbnail.setDrawable(item.image.getDrawable());
			description.setText(doc.getDescription());
			RepoAuthor author = doc.getAuthor();
			this.author.setText(author.getName());
			url.setUserObject(author.getUrl());
			license.setText(doc.getLicense().toString());

			getColor().a = 0f;
			show(fadeIn(DURATION, Interpolation.fade));
		}

		@Override
		public void hide() {
			super.hide(Actions.fadeOut(DURATION, Interpolation.fade));
		}
	}
}
