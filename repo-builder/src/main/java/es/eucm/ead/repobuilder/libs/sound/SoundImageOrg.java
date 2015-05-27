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
public class SoundImageOrg extends SoundLibrary {

	public SoundImageOrg() {
		super("soundImageOrg");
	}

	@Override
	public String[] getCommonProperties() {
		return new String[] {
				"soundimage.org-music",
				"Eric Matyas (soundimage.org)",
				RepoCategories.SOUNDS_MUSIC.toString(),
				RepoTags.appendTags("", "music", "música", "background music",
						"música de fondo", "space", "espacio", "looping",
						"bucle", "fantasy", "fantasía"), "Eric Matyas",
				"www.soundimage.org", DefaultLicenses.License.CC_BY.toString() };
	}

	@Override
	public boolean isBackgroundMusic(String nameEn) {
		return true;
	}

	@Override
	public String toSoundFile(String nameEn) {
		return nameEn.replace(" ", "-") + ".mp3";
	}

	@Override
	public String[] getSoundDescriptors() {
		return new String[] {
				"Chamber of Jewels#Hidden somewhere in a mystical land?",
				"Cámara de joyas#¿Escondida en algún sitio de una tierra mística?",

				"Corporate Ladder#A quiet, 90′s-style corporate technological feel.",
				"Escalera Corporativa#Una pieza relajada con un toque de tecnología de los años 90.",

				"Cosmic Switchboard#Sometime in the future, digital chatter fills the solar system as humanity begins to settle this new frontier.",
				"Centralista Cósmica#A veces en el futuro las charlas digitales llenan el sistema solar mientras que la humanidad comienza a asentar esta nueva frontera.",

				"Dance of the Satellites#At 200 miles up.",
				"El Baile de los Satélites#A 200 millas de altitud.",

				"Frantic Gameplay#A wacky loop that might work in a crazy driving game or maybe a first person running around type of game.",
				"Gameplay frenético#Una pieza que podría funcionar en un juego de carreras o puede que en algun tipo de juego en primera persona.",

				"Game Menu#A looping version of a game menu music.",
				"Menu de Juego#Una versión en bucle de una música para la pantalla de menu de unjuego.",

				"Insert Quarter#Vaguely in the mode of 80s arcade games.",
				"Introduce una Moneda#Vagamente sigue la tendencia de los juegos arcade de los años 80.",

				"Introspective Machines#Will it happen one day? If it does, the implications are truly profound (and the world will never be the same).",
				"Máquinas Introspectivas#¿Pasará algún día? Si pasa, las implicaciones seran verdaderamente profundas (y el mundo nunca será el mismo).",

				"Kingdom of Lost Dreams#Might be nice in an RPG or fantasy game.",
				"EL Reino de los Sueños Perdidos#Podría encajar bien en un juego de fantasía o de rol.",

				"Land Of Hope#Beyond the Sea of Despair...if we could only get to it.",
				"La Tierra de la Esperanza#Más allá del mar de la desesperación...si tan solo pudiéramos alcanzarla.",

				"Light Years#Might work in a science/planetarium show or a spaced-based game.",
				"Años Luz#Podría funcionar en un juego de ciencia o basado en el universo.",

				"Lost Forever#As one technological species grew to power, countless others fell to extinction and were lost forever.",
				"Perdidas para Siempre#Mientras que una especie tecnológica creció en el poder, incontables otras sufrieron la extinción y fueron perdidas para siempre.",

				"Our Mountain#Might work under an RPG title screen.",
				"Nuestra Montaña#Podría funcionar con algun menú principal de un jeugo de rol.",

				"Puzzle Game 5#Might work under a title / instructions menu.",
				"Juego de Puzzle 5#Podría funcionar con un menú de título o instrucciones.",

				"Quiet Tension#A simple string piece.",
				"Tensión silenciosa#Una pieza simple de un instrumento de cuerda.",

				"Racing Menu#Choose your car...choose your course...",
				"Menu de carreras#Elige tu coche...elige tu camino...",

				"Surfing the Jet Stream#On futuristic boogie boards.",
				"Surfeando las Olas#Sobre tablas de surf futurísticas.",

				"Techno Gameplay#Might work in a sci-fi game.",
				"Techno Gameplay#Podría funcionar en un juego de ciencia ficción.",

				"The Triumph of Technology#Set to a techno beat.",
				"El Triunfo de la Tecnología#Configurado con un compás techno.",

				"Upward Blip-ility#A very simple, minimalistic little chip tune-style piece. Might be fun in an cartoon-y app.",
				"Blip-ilidad Hacia Arriba#Una pieza minimalista. Podría ser divertida en una aplicación cartoon-izada.",

				"When Machines Dream#It will sure be interesting.",
				"Cuando las Máquinas Sueñan#Desde luego será algo interesante..." };
	}

	@Override
	public String[] getRepoLibDescriptor() {
		return new String[] { "SoundImage.org Music",
				"Música de SoundImage.org",
				"Music composed and  performed by Eric Matyas",
				"Música compuesta por Eric Matyas" };
	}
}
