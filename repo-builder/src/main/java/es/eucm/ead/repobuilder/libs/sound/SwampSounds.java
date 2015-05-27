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

import es.eucm.ead.repobuilder.RepoTags;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;

/**
 * Created by Cristian Rotaru on 27/05/15.
 */
public class SwampSounds extends SoundLibrary {

	private String ambientDescEn = "A swamp atmospheric sound effect.";
	private String ambientDescEs = "Un sonido ambiental del pantano.";

	private String beastDescEn = "A beast sound effect.";
	private String beastDescEs = "Un efecto de sonido de una bestia.";

	private String bubblingDescEn = "A bubbling sound effect.";
	private String bubblingDescEs = "El efecto de sonido de unas burbujas.";

	private String bugDescEn = "A bug sound effect.";
	private String bugDescEs = "El efecto de sonido de un bicho.";

	private String cicadaDescEn = "A cicada sound effect.";
	private String cicadaDescEs = "El efecto de sonido de una cigarra.";

	private String cricketDescEn = "A cricket sound effect.";
	private String cricketDescEs = "El efecto de sonido de un grillo.";

	private String flyDescEn = "A fly sound effect.";
	private String flyDescEs = "El efecto de sonido de una mosca.";

	public SwampSounds() {
		super("swampSounds");
	}

	@Override
	public String[] getCommonProperties() {
		return new String[] {
				"Swamp Environment Sounds",
				"OpenGameArt.org",
				RepoCategories.SOUNDS_EFFECTS.toString(),
				RepoTags.appendTags("", "sound", "sonido", "sound effect",
						"efecto de sonido", "atmosphere", "atmósfera", "beast",
						"bestia", "bug", "bicho"), "LokiF",
				"http://opengameart.org/users/lokif",
				DefaultLicenses.License.PUBLIC_DOMAIN.toString() };
	}

	@Override
	public boolean isBackgroundMusic(String nameEn) {
		return false;
	}

	@Override
	public String toSoundFile(String nameEn) {
		return nameEn.toLowerCase().replace(" ", "_") + ".ogg";
	}

	@Override
	public String[] getSoundDescriptors() {
		return new String[] { "Atmosphere 1#" + ambientDescEn,
				"Ambiente 1#" + ambientDescEs,

				"Atmosphere 2#" + ambientDescEn, "Ambiente 2#" + ambientDescEs,

				"Atmosphere 3#" + ambientDescEn, "Ambiente 3#" + ambientDescEs,

				"Beast 1#" + beastDescEn, "Bestia 1#" + beastDescEs,

				"Beast 2#" + beastDescEn, "Bestia 2#" + beastDescEs,

				"Bubbling 1#" + bubblingDescEn, "Burbujas 1#" + bubblingDescEs,

				"Bubbling 2#" + bubblingDescEn, "Burbujas 2#" + bubblingDescEs,

				"Bubbling 3#" + bubblingDescEn, "Burbujas 3#" + bubblingDescEs,

				"Bubbling 4#" + bubblingDescEn, "Burbujas 4#" + bubblingDescEs,

				"Bug 1#" + bugDescEn, "Bicho 1#" + bugDescEs,

				"Bug 2#" + bugDescEn, "Bicho 2#" + bugDescEs,

				"Cicada 1#" + cicadaDescEn, "Cigarra 1#" + cicadaDescEs,

				"Cicada 2#" + cicadaDescEn, "Cigarra 2#" + cicadaDescEs,

				"Cricket 1#" + cricketDescEn, "Grillo 1#" + cricketDescEs,

				"Cricket 2#" + cricketDescEn, "Grillo 2#" + cricketDescEs,

				"Fly 1#" + flyDescEn, "Mosca 1#" + flyDescEs,

				"Fly 2#" + flyDescEn, "Mosca 2#" + flyDescEs,

				"Fly 3#" + flyDescEn, "Mosca 3#" + flyDescEs };
	}

	@Override
	public String[] getRepoLibDescriptor() {
		return new String[] { "Swamp Environment Sounds",
				"Sonidos Ambientales del Pantano",
				"Sound effects published by LokiF at OpenGameArt.org",
				"Efectos de sonido publicados por LokiF en OpenGameArt.org" };
	}
}
