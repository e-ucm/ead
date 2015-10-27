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
package es.eucm.ead.repobuilder.libs.sound;

import es.eucm.ead.repobuilder.RepoLibraryBuilder;
import es.eucm.ead.repobuilder.RepoTags;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;

/**
 * Created by eucm on 27/05/15. An abstract class used to create sound and music
 * libraries.
 */
public abstract class SoundLibrary extends RepoLibraryBuilder {

	private static final String[] PROPERTIES = new String[] { LIB_NAME,
			PUBLISHER, CATEGORIES, TAGS, AUTHOR_NAME, AUTHOR_URL, LICENSE };

	public SoundLibrary(String root) {
		super(root);
	}

	@Override
	protected void doBuild() {
		setCommonProperty(AUTO_IDS, "true");
		String[] commonProperties = getCommonProperties();
		for (int i = 0; i < PROPERTIES.length; ++i) {
			setCommonProperty(PROPERTIES[i], commonProperties[i]);
		}

		String[] descriptors = getSoundDescriptors();
		for (int i = 0; i < descriptors.length; i += 2) {
			String[] engInfo = descriptors[i].split("#");
			String[] esInfo = descriptors[i + 1].split("#");
			String nameEn = engInfo[0];
			repoSound(nameEn, esInfo[0], engInfo[1], esInfo[1],
					isBackgroundMusic(nameEn), toSoundFile(nameEn));
		}

		String[] libDescriptor = getRepoLibDescriptor();
		repoLib(libDescriptor[0], libDescriptor[1], libDescriptor[2],
				libDescriptor[3], null);
	}

	/**
	 * Common properties
	 * 
	 * @return args[0] = LIB_NAME, args[1] = PUBLISHER, args[2] = CATEGORIES,
	 *         args[3] = TAGS, args[4] = AUTHOR_NAME, args[5] = AUTHOR_URL,
	 *         args[6] = LICENSE
	 */
	public abstract String[] getCommonProperties();

	/**
	 * 
	 * @param nameEn
	 *            the NAME_EN parameter of the song provided by the
	 *            #getCommonProperties() method
	 * @return if the repoSound shoudl be backgroundMusic or not
	 */
	public abstract boolean isBackgroundMusic(String nameEn);

	/**
	 * 
	 * @param nameEn
	 *            the NAME_EN parameter of the song provided by the
	 *            #getCommonProperties() method
	 * @return the name of the actual file asociated to that name, i. e.
	 *         NAME_EN.mp3
	 */
	public abstract String toSoundFile(String nameEn);

	/**
	 * 
	 * @return for each sound in ROOT.zip two arguments with the following
	 *         format { args[0] = "NAME_EN#DESC_EN", args[1] =
	 *         "NAME_ES#DESC_ES". }
	 */
	public abstract String[] getSoundDescriptors();

	/**
	 * Library descriptor
	 * 
	 * @return args[0] = String nameEn, args[1] = String nameEs, args[2] =
	 *         String descriptionEn, args[3] = String descriptionEs
	 */
	public abstract String[] getRepoLibDescriptor();

}
