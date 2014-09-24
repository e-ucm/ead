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
package es.eucm.ead.editor.view.builders.gallery.repository.info;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel;
import es.eucm.ead.editor.view.widgets.gallery.repository.InfoGalleryItem;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.RepoAuthor;
import es.eucm.ead.schema.editor.components.RepoElement;
import es.eucm.ead.schema.editor.components.RepoLibrary;

/**
 * A panel displaying the information contained inside a {@link RepoElement} or
 * a {@link RepoLibrary}.
 * 
 */
public class ItemInfo<T extends InfoGalleryItem> extends PositionedHiddenPanel {

	private static final float PAD = 10F;

	private static final float DURATION = .2f;

	private Image thumbnail;
	private Label name, description, author, license, tags;
	private Button url;
	private T item;
	protected TextButton actionButton;

	protected Controller controller;

	public ItemInfo(final Controller controller, Actor rootView) {
		super(controller.getApplicationAssets().getSkin(), Position.CENTER,
				rootView);

		this.controller = controller;
		ApplicationAssets applicationAssets = controller.getApplicationAssets();
		I18N i18n = applicationAssets.getI18N();
		Skin skin = controller.getApplicationAssets().getSkin();
		setBackground(skin.getDrawable("dialog"));
		thumbnail = new Image();
		thumbnail.setScaling(Scaling.fit);

		final TextButton close = new TextButton(i18n.m("close"), skin, "white");

		actionButton = new TextButton(getActionButtonString(i18n), skin,
				"to_color");
		actionButton.setColor(Color.GREEN);
		url = new TextButton(i18n.m("webPage"), skin, "white");

		ChangeListener listener = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == url) {
					controller.getPlatform().browseURL(
							url.getUserObject().toString());
				} else if (close == listenerActor) {
					hide();
				} else if (actionButton == listenerActor) {
					actionButtonClicked(item);
				}
			}
		};
		close.addListener(listener);
		actionButton.addListener(listener);
		url.addListener(listener);

		Table table = new Table(skin);
		Value wrapLabel = Value.percentWidth(.8f, table);
		ScrollPane pane = new ScrollPane(table);
		pane.setFadeScrollBars(true);
		pane.setScrollingDisabled(true, false);
		table.pad(PAD).defaults().space(PAD);
		table.add(i18n.m("name") + ":").colspan(2).left();
		table.row();
		table.add(name = new Label("", skin)).colspan(2).width(wrapLabel);
		name.setWrap(true);
		name.setAlignment(Align.center);
		table.row();
		table.add(i18n.m("description") + ":").colspan(2).left();
		table.row();
		table.add(description = new Label("", skin)).colspan(2)
				.width(wrapLabel);
		description.setWrap(true);
		description.setAlignment(Align.center);
		table.row();
		table.add(i18n.m("author") + ":").left();
		table.add(url).right();
		table.row();
		table.add(author = new Label("", skin)).colspan(2).width(wrapLabel);
		author.setWrap(true);
		author.setAlignment(Align.center);
		table.row();
		table.add(i18n.m("license") + ":").colspan(2).left();
		table.row();
		table.add(license = new Label("", skin)).colspan(2).width(wrapLabel);
		license.setWrap(true);
		license.setAlignment(Align.center);
		table.row();
		table.add(i18n.m("tags") + ":").colspan(2).left();
		table.row();
		table.add(tags = new Label("", skin)).colspan(2).width(wrapLabel);
		tags.setWrap(true);
		tags.setAlignment(Align.center);
		table.row();
		buildWidgets(table, i18n, skin);
		table.add(actionButton).left();
		table.add(close).right();

		add(thumbnail).maxWidth(Value.percentWidth(.5f, this)).left();
		add(pane).expand().fill();
		setModal(true);
	}

	protected String getActionButtonString(I18N i18n) {
		return "";
	}

	protected void actionButtonClicked(T item) {
		item.getGallery().itemClicked(item);
	}

	protected void buildWidgets(Table table, I18N i18n, Skin skin) {

	}

	public void show(T item) {
		this.item = item;
		thumbnail.setDrawable(item.getImage().getDrawable());
		name.setText(item.getName());
		description.setText(item.getDescription());
		RepoAuthor author = item.getAuthor();
		this.author.setText(author.getName());
		String urlString = author.getUrl();
		if (urlString != null) {
			url.setUserObject(urlString);
			url.setDisabled(false);
		} else {
			url.setDisabled(true);
		}
		license.setText(item.getLicense());
		tags.setText(item.getTags());

		getColor().a = 0f;
		show(fadeIn(DURATION, Interpolation.fade));
	}

	public void setThumbnail(Drawable drawable, T item) {
		if (this.item == item) {
			thumbnail.setDrawable(drawable);
		}
	}

	@Override
	public void hide() {
		super.hide(Actions.fadeOut(DURATION, Interpolation.fade));
	}
}
