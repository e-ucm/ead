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
package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.listeners.ChangeNoteFieldListener;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Note;

/**
 * A widget displaying a {@link es.eucm.ead.schema.entities.ModelEntity}, (name,
 * description, image...)
 */
public abstract class DescriptionCard extends Button {

	private static final float PREF_WIDTH = .25F;
	private static final float PREF_HEIGHT = .5F;
	private static final float TITLE_FONT_SCALE = .55f;
	private static final float DESCRIPTION_FONT_SCALE = .5f;
	private static final float DESCRIPTION_PAD_LEFT = 10f;

	private static final int MAX_TITLE_CHARACTERS = 17;
	private static final int MAX_DESCRIPTION_CHARACTERS = 92;

	private final Vector2 viewport;
	private String title, untitled, emptyDescription;
	private Image sceneIcon;

	/**
	 * A widget displaying a {@link es.eucm.ead.schema.entities.ModelEntity},
	 * (name, description, image...)
	 * 
	 * @param viewport
	 * @param i18n
	 * @param type
	 *            Used in case the title or description are null or empty to
	 *            display some alternative text.
	 * @param title
	 *            if is null or empty the type will be used to show an
	 *            informative text instead.
	 * @param description
	 *            if is null or empty the type will be used to show an
	 *            informative text instead.
	 * @param imageName
	 * @param skin
	 */
	public DescriptionCard(Note targetNote, Vector2 viewport, I18N i18n,
			String type, String imageName, Skin skin, Controller controller) {
		super(skin);
		this.viewport = viewport;
		initialize(targetNote, controller, i18n, type, targetNote.getTitle(),
				targetNote.getDescription(), imageName, skin);
	}

	/**
	 * A widget displaying a {@link es.eucm.ead.schema.entities.ModelEntity},
	 * (name, description, image...)
	 * 
	 * @param viewport
	 * @param i18n
	 * @param type
	 *            Used in case the title or description are null or empty to
	 *            display some alternative text.
	 * @param title
	 *            if is null or empty the type will be used to show an
	 *            informative text instead.
	 * @param description
	 *            if is null or empty the type will be used to show an
	 *            informative text instead.
	 * @param imageName
	 * @param skin
	 */
	public DescriptionCard(Note targetNote, Vector2 viewport, I18N i18n,
			String type, String imageName, Skin skin, Controller controller,
			Class<?> action, Object... args) {
		super(skin);
		this.viewport = viewport;
		initialize(targetNote, controller, i18n, type, targetNote.getTitle(),
				targetNote.getDescription(), imageName, skin);
		if (controller != null && action != null) {
			addCaptureListener(new ActionOnClickListener(controller, action,
					args));
		}
	}

	private void initialize(final Object targetNote, Controller controller,
			final I18N i18n, final String type, String titl, String descrip,
			String imageName, Skin skin) {
		TextureRegion image = null;
		if (imageName == null) {
			image = skin.getRegion("icon-blitz");
		} else {
			image = skin.getRegion(imageName);
		}
		sceneIcon = new Image(image);
		sceneIcon.setScaling(Scaling.fit);

		if (titl == null || titl.isEmpty()) {
			this.untitled = titl = type + " " + i18n.m("untitled");
		}

		this.title = shortenBy(titl, MAX_TITLE_CHARACTERS);
		final Label title = new Label(titl, skin);
		title.setFontScale(TITLE_FONT_SCALE);
		title.setWrap(false);
		title.setAlignment(Align.center);

		if (descrip == null || descrip.isEmpty()) {
			this.emptyDescription = descrip = type + " "
					+ i18n.m("emptydescription");
		}

		descrip = shortenBy(descrip, MAX_DESCRIPTION_CHARACTERS);
		final Label description = new Label(descrip, skin);
		description.setFontScale(DESCRIPTION_FONT_SCALE);
		description.setWrap(true);
		description.setAlignment(Align.left);

		final Table titleDescription = new Table();
		titleDescription.add(title).expandX().fillX();
		titleDescription.row();
		titleDescription.add(description).padLeft(DESCRIPTION_PAD_LEFT)
				.expand().fill().left();

		add(titleDescription).expand().fill();
		add(sceneIcon).expandY().fillY().right().maxWidth(getPrefWidth() * .3f);

		if (this instanceof SceneButton || this instanceof ElementButton) {
			final Model model = controller.getModel();
			model.addFieldListener(targetNote, new ChangeNoteFieldListener() {

				@Override
				public void descriptionChanged(FieldEvent event) {
					final Object value = event.getValue();
					final String newValue = value == null ? DescriptionCard.this.emptyDescription
							: value.toString();
					description.setText(newValue.isEmpty() ? DescriptionCard.this.emptyDescription
							: shortenBy(newValue, MAX_DESCRIPTION_CHARACTERS));
				}

				@Override
				public void titleChanged(FieldEvent event) {
					final Object value = event.getValue();
					final String newValue = value == null ? "" : value
							.toString();
					DescriptionCard.this.title = newValue;
					title.setText(newValue.isEmpty() ? DescriptionCard.this.untitled
							: shortenBy(newValue, MAX_TITLE_CHARACTERS));

				}
			});
		}
	}

	public void setIcon(Texture icon) {
		this.sceneIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(
				icon)));
	}

	private String shortenBy(String target, int max) {
		if (target.length() > max) {
			target = (target.substring(0, max) + "...");
		}
		return target;
	}

	@Override
	public float getPrefWidth() {
		return Math.max(super.getPrefWidth(), this.viewport == null ? 0
				: this.viewport.x * PREF_WIDTH);
	}

	@Override
	public float getPrefHeight() {
		return getPrefWidth() * PREF_HEIGHT;
	}

	/**
	 * Used for the necessary comparisons to order the gallery.
	 * 
	 * @return document's title;
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Used for the necessary comparisons to filter the gallery. Default
	 * implementation returns always false.
	 */
	public boolean hasTag(String tag) {
		return false;
	}
}
