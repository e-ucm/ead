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
package es.eucm.ead.repobuilder.libs;

import es.eucm.ead.engine.demobuilder.ExecutableDemoBuilder;
import es.eucm.ead.repobuilder.RepoLibraryBuilder;
import es.eucm.ead.repobuilder.RepoTags;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Created by jtorrente on 06/05/2015.
 */
public abstract class PlaygroundLibrary extends RepoLibraryBuilder {

	public PlaygroundLibrary(String root) {
		super(root);
	}

	protected abstract void doBuildImpl();

	protected abstract String libName();

	protected abstract String libDescriptionEn();

	protected abstract String libDescriptionEs();

	protected abstract String mainPublisher();

	protected abstract RepoTags mainSource();

	protected abstract String additionalTags();

	protected abstract String authorName();

	protected abstract String authorUrl();

	protected String buildDescriptionEn(String descriptionEn) {
		return descriptionEn
				+ "\n\nCharacter designed by "
				+ mainPublisher()
				+ " and animated by the Mokap team using Esoteric Software's Spine.";
	}

	protected String buildDescriptionEs(String descriptionEs) {
		return descriptionEs
				+ "\n\nPersonaje diseñado por "
				+ mainPublisher()
				+ " y animado por el equipo de Mokap utilizando Spine de Esoteric Software.";
	}

	@Override
	protected void doBuild() {
		// All images are placed under the root of the zip file robots.zip
		setCommonProperty(RESOURCES, "");
		setCommonProperty(THUMBNAILS, "");

		// Disable auto-generation of IDs
		setCommonProperty(AUTO_IDS, "false");
		setCommonProperty(LIB_NAME, libName());
		setCommonProperty(PUBLISHER, mainPublisher() + " - Mokap team");
		setCommonProperty(CATEGORIES,
				RepoCategories.ELEMENTS_CHARACTERS.toString());

		// Common tags for all elements
		String mainTags = RepoTags.appendTags(mainSource(),
				RepoTags.CHARACTERISTIC_ANIMATED, RepoTags.TYPE_CHARACTER);
		// Additional tags
		mainTags = RepoTags.appendTags(mainTags, additionalTags());
		setCommonProperty(TAGS, mainTags);

		setCommonProperty(AUTHOR_NAME, authorName());
		setCommonProperty(AUTHOR_URL, authorUrl());
		setCommonProperty(LICENSE,
				DefaultLicenses.License.LINK_AUTHOR.toString());

		setCommonProperty(MAX_HEIGHT, "650");
		setCommonProperty(MAX_WIDTH, "650");

		// Create the elements
		doBuildImpl();

		// Library
		repoLib(libName(), libName(), libDescriptionEn(), libDescriptionEs(),
				null);
	}

	/**
	 * Creates an element with a frame animation
	 * 
	 * @param id
	 *            The id of the element (e.g. C4G2). This is used both as id and
	 *            as prefix for frame images, which are expected to follow the
	 *            pattern id_XXX.png, where XXX is the frame number from 0 to
	 *            nFrames-1
	 * @param descriptionEn
	 *            The English version of the description
	 * @param descriptionEs
	 *            The Spanish version of the description
	 * @param nFrames
	 *            The total number of frames
	 * @param frameMS
	 *            The duration of each frame, in milliseconds
	 */
	protected void elementWithFrames(String id, String descriptionEn,
			String descriptionEs, int nFrames, float frameMS) {
		descriptionEn = buildDescriptionEn(descriptionEn);
		descriptionEs = buildDescriptionEs(descriptionEs);
		float frameDuration = frameMS / 1000F;
		String firstFrame = id + "__000.png";
		repoEntity(id, id, descriptionEn, descriptionEs, firstFrame, null,
				firstFrame);
		for (int i = 1; i < nFrames; i++) {
			frame(id + "__" + String.format("%03d", i) + ".png", frameDuration);
		}
	}

	protected void elementWithFrames(String id, int nFrames,
			String descriptionEn, String descriptionEs) {
		elementWithFrames(id, descriptionEn, descriptionEs, nFrames, 35.0F);
	}

	protected void elementWithFrames(String id, int nFrames) {
		elementWithFrames(id, nFrames, "", "");
	}

	protected void elementWithSpineAnimation(String id, String initialState,
			float x, float scale, String descriptionEn, String descriptionEs) {
		descriptionEn = buildDescriptionEn(descriptionEn);
		descriptionEs = buildDescriptionEs(descriptionEs);
		repoEntity(id, id, descriptionEn, descriptionEs, id + "/thumbnail.png",
				null);
		spine(id + "/skeleton", initialState);
		getLastEntity().setX(x);
		getLastEntity().setScaleX(scale);
		getLastEntity().setScaleY(scale);
		adjustEntity(getLastEntity());
	}

	protected <T extends ExecutableDemoBuilder> void landscape(
			Class<T> landscapeClazz, String nameEn, String nameEs,
			String descriptionEn, String descriptionEs, String thumbnail) {
		ExecutableDemoBuilder builder = null;
		try {
			builder = landscapeClazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		builder.prepare();
		builder.build();

		ModelEntity entity = builder.getLastScene().getChildren().first();
		entity.setScaleX(0.7F);
		entity.setScaleY(0.7F);
		String categories = RepoCategories.SCENES_BACKGROUNDS.toString();

		lastEntity = entity;
		repoEntities.add(entity);
		repoEntity(nameEn, nameEs, descriptionEn, descriptionEs, thumbnail,
				categories, entity);
		tag(RepoTags.TYPE_BACKGROUND);
		adjustEntity(entity);
	}
}
