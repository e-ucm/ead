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
package es.eucm.ead.repobuilder.libs.timemokapsule;

import es.eucm.ead.repobuilder.BuildRepoLibs;
import es.eucm.ead.repobuilder.RepoTags;
import es.eucm.ead.repobuilder.libs.PlaygroundLibrary;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Created by jtorrente on 20/11/14.
 */
public class TimeMokapsule extends PlaygroundLibrary {
	public TimeMokapsule() {
		super("robots");
	}

	@Override
	protected void doBuildImpl() {
		// Objects
		ufo();
		alienShip();
		pinkPlanet();
		purplePlanet();
		orangePlanet();
		earth();
		moonForeground();

		// Landscapes
		landscape1();
		landscape2();
		landscape3();

		// Create the robots
		c4g2();
		c9q3();
		r0n8();
		r2g6();
		r5p2();
		r1v1();
		c6b4();
		whiterobot();

		// Create the monsters
		ebb();
		robb();
		jellyMonster();
	}

	@Override
	protected String libName() {
		return "Time Mokapsule";
	}

	@Override
	protected String libDescriptionEn() {
		return "Wonderful futuristic world designed for the Mokap workshop @ Playground";
	}

	@Override
	protected String libDescriptionEs() {
		return "Maravilloso mundo futurista diseñado para el workshop sobre Mokap a celebrarse en el Playground";
	}

	@Override
	protected String mainPublisher() {
		return "Vecteezy.com";
	}

	@Override
	protected RepoTags mainSource() {
		return RepoTags.SOURCE_VECTEEZY;
	}

	@Override
	protected String additionalTags() {
		// Tags for all elements obtained from vecteezy
		// (http://www.vecteezy.com/vector-art/85377-human-robot-vectors)
		return RepoTags.appendTags(RepoTags.STYLE_CARTOON.toString(), "cute",
				"gracioso", "toy", "juguete", "futuristic", "futurista",
				"future", "futuro", "isolated", "aislado", "space", "espacio");
	}

	@Override
	protected String authorName() {
		return mainPublisher();
	}

	@Override
	protected String authorUrl() {
		return "www.vecteezy.com";
	}

	/* **********************
	 * ROBOTS *********************
	 */

	private void c4g2() {
		String desEn = "C4G2 was created as a chimney sweep robot. It was created to be able to reach even the highest chimneys. Unfortunately one bad day,"
				+ "a loose brick damaged his transmission system and now it can't stop jumping!";
		String desEs = "C4G2 fue creado como un robot deshollinador. Fue creado para ser capaz de alcanzar incluso las chimeneas más altas. Desafortuadamente, un"
				+ " mal día un ladrillo suelto dañó su sistema de transmisión y ahora ¡no puede parar de saltar!";
		elementWithSpineAnimation("C4G2", "animation", 500, 0.6F, desEn, desEs);
		tagRobot(true, false);
	}

	private void c9q3() {
		String desEn = "C9Q3 is an intelligent portable spotlight, created for the mines. It can illuminate the darkest pits.";
		String desEs = "C9Q3 es un foco de luz portable e inteligente, creado para las minas. Puede iluminar incluso los agujeros más oscuros.";
		elementWithSpineAnimation("C9Q3", "animation", 500, 0.6F, desEn, desEs);
		tagRobot(true, false);
	}

	private void r0n8() {
		String desEn = "R0N8 was the flagship calculation unit for the Space Agency. Its powerful CPU could process millions of stellar trajectories per second, for amusement of "
				+ "the general commander. But not any more. Since the agency acquired a shinny R1 model out of the line, it has been replaced and left for minor calculus.";
		String desEs = "R0N8 fue una vez la unidad de cálculo estrella de la Agencia Espacial. Su potente CPU podía procesar millones de trayectorias estelares por segundo, "
				+ "para regocijo de la comandacía general. Pero ya no. Desde que la agencia adquirió un reluciente modelo R1 directamente salido de la cadena de montaje, el"
				+ "R0N8 ha sido relegado a trabajos de cálculo menores.";
		elementWithSpineAnimation("R0N8", "Movement", 400, 0.6F, desEn, desEs);
		tagRobot(true, false);
	}

	private void r2g6() {
		String desEn = "R2G6 was never the hardest working robot in the world. It is kind of lazy, always willing to let anyone do the hard work.";
		String desEs = "R2G6 nunca fue el robot más trabajador del mundo. Es más bien vago, siempre dispuesto a dejar que cualquiera se ocupe del trabajo duro.";
		elementWithSpineAnimation("R2G6", "animation", 400, 0.6F, desEn, desEs);
		tagRobot(true, false);
	}

	private void r5p2() {
		String desEn = "This shinny 4-armed robot was designed to work in car manufacturing. However, an unfortunate voltage drop while upgrading his OS left him a little \"out of his mind\"";
		String desEs = "Este reluciente robot de cuatro brazos fue diseñado para trabajar en la industria del automóvil. Sin embargo, un desafortunado corte de corriente mientras actualizaba su sistema operativo le dejó un poco \"tocado del ala\"";
		elementWithSpineAnimation("r5p2", "stand", 400, 0.6F, desEn, desEs);
		tagRobot(true, false);
	}

	private void r1v1() {
		String desEn = "A robot that just needs to pee!";
		String desEs = "¡Un robot que necesita ir al baño con urgencia!";
		elementWithSpineAnimation("r1v1", "default", 400, 0.6F, desEn, desEs);
		tagRobot(true, false);
	}

	private void c6b4() {
		String desEn = "Purple protocol android. Can translate more than 400 languages!";
		String desEs = "Androide púrpura de protocolo. ¡Puede traducir más de 400 idiomas!";
		elementWithSpineAnimation("c6b4", "default", 400, 0.6F, desEn, desEs);
		tagRobot(true, false);
	}

	private void whiterobot() {
		String desEn = "ES1 is a shinny metallic defense robot. Built by ES'TARC industries as a scout for military incursions beyond the enemy lines, she was reconverted recently for domestic use. She is obedient and extremely efficient. ";
		String desEs = "El model ES1 es un robot de defensa metálico y brillante. Construido inicialmente por ES'TARC industries como explorador para avanzadillas militares, fue posteriormente reconvertido como robot de uso doméstico. Es muy obediente y extremadamente eficiente.";
		elementWithSpineAnimation("whiterobot", "default", 300, 0.75F, desEn,
				desEs);
		tagRobot(false, true);
	}

	/* **********************
	 * BACKGROUNDS *********************
	 */
	private void landscape1() {
		String nameEn = "Launching platform";
		String nameEs = "Plataforma de despegue";
		String descriptionEn = "This background features an animation that triggers the rocket to the sky";
		String descriptionEs = "Este fondo contiene una animación en la que el cohete es lanzado hacia el espacio";
		String thumbnail = "landscape.png";
		landscape(LandscapeDemo.class, nameEn, nameEs, descriptionEn,
				descriptionEs, thumbnail);
		tag("Launching platform", "Plataforma de despegue")
				.tag("Control station", "Estación de control")
				.tag(RepoTags.DESIGN_FLAT).tag("NASA")
				.tag(RepoTags.CHARACTERISTIC_ANIMATED);
	}

	private void landscape2() {
		String nameEn = "Outer space";
		String nameEs = "Espacio exterior";
		String descriptionEn = "Background with little animations on the planets to make it alive!";
		String descriptionEs = "Fondo con animaciones en los planetas";
		String thumbnail = "landscape2.png";
		landscape(LandscapeDemo2.class, nameEn, nameEs, descriptionEn,
				descriptionEs, thumbnail);
		tag("space", "espacio").tag(RepoTags.DESIGN_FLAT)
				.tag("planet", "planeta").tag("universe", "universo")
				.tag(RepoTags.CHARACTERISTIC_ANIMATED);
	}

	private void landscape3() {
		String nameEn = "Martian city";
		String nameEs = "Ciudad alienígena";
		String descriptionEn = "Space city where inhabitants live in a safe environment. The city gates are animated (plus a few other elements), opening and closing from time to time.";
		String descriptionEs = "Ciudad en el espacio donde sus habitantes viven en un entorno seguro. Las puertas de la ciudad tienen animación (así como algunos otros elementos), abriéndose y cerrándose de cuando en cuando";
		String thumbnail = "landscape3.png";
		landscape(LandscapeDemo3.class, nameEn, nameEs, descriptionEn,
				descriptionEs, thumbnail);
		tag("city", "ciudad").tag("Mars", "Marte").tag(RepoTags.DESIGN_FLAT)
				.tag(RepoTags.CHARACTERISTIC_ANIMATED);
	}

	/* **********************
	 * OBJECTS *********************
	 */
	private void ufo() {
		String nameEn = "UFO";
		String nameEs = "OVNI";
		String descriptionEn = "A green space ship from outer space projecting a green ray for abduction";
		String descriptionEs = "Una nave verde del espacio exterior, lista para abducir con su rayo especial";
		String thumbnail = "images/ufo_thumbnail.png";
		ModelEntity ufo = repoEntity(nameEn, nameEs, descriptionEn,
				descriptionEs, thumbnail,
				RepoCategories.ELEMENTS_OBJECTS.toString(), (String) null)
				.getLastEntity();
		tag(RepoTags.CHARACTERISTIC_ANIMATED).tag(RepoTags.DESIGN_FLAT)
				.tag("flying saucer", "platillo volante").tag("ufo", "ovni")
				.tag("spaceship", "nave espacial").tag("alien", "alienígena")
				.tag(RepoTags.TYPE_OBJECT);
		ModelEntity halo = entity(ufo, "images/ufo_halo.png", 0, 0)
				.getLastEntity();
		tween(halo, AlphaTween.class, 0.5F, -1, 0.5F, true, 0F, false,
				Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, 0.0F, null,
				null, null);
		ModelEntity rays = entity(ufo, "images/ufo_rays.png", 0, 0)
				.getLastEntity();
		tween(rays, AlphaTween.class, 0.7F, -1, 0.5F, true, 0F, false,
				Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, 0.0F, null,
				null, null);
		ModelEntity spaceship = entity(ufo, "images/ufo_spaceship.png", 0, 0)
				.getLastEntity();
		ModelEntity star = entity(ufo, "images/ufo_star.png", 0, 0)
				.getLastEntity();
		tween(star, AlphaTween.class, 1.2F, -1, 1.2F, true, 0F, false,
				Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, 0.0F, null,
				null, null);
		tween(ufo, MoveTween.class, 0F, -1, 0F, true, 2F, true,
				Tween.EaseEquation.SINE, Tween.EaseType.INOUT, 10F, -8F, null,
				null);
	}

	private void moonForeground() {
		String nameEn = "Moon foreground";
		String nameEs = "Luna para poner en primer plano";
		String descriptionEn = "Inanimate object to be used in foreground";
		String descriptionEs = "Objeto inanimado para ser usado como primer plano";
		String image = "images/MoonForeground.png";
		repoEntity(nameEn, nameEs, descriptionEn, descriptionEs, image,
				RepoCategories.ELEMENTS_OBJECTS.toString(), image)
				.tag(RepoTags.CHARACTERISTIC_STATIC).tag(RepoTags.DESIGN_FLAT)
				.tag(RepoTags.TYPE_OBJECT).tag("planet", "planeta")
				.tag("moon", "luna").tag("satellite", "satélite")
				.tag("foreground", "primer plano");
		adjustEntity(getLastEntity());
	}

	private void pinkPlanet() {
		String nameEn = "Pink planet";
		String nameEs = "Planeta rosa";
		String descriptionEn = "Strange pink planet, slightly animated";
		String descriptionEs = "Extraño plantea rosa, ligeramente animado";
		String image = "images/PinkPlanet.png";
		planet(nameEn, nameEs, descriptionEn, descriptionEs, image, 2F, 12.0F,
				12.0F, -14.0F, 18.0F, -8.0F, -10.0F);
	}

	private void purplePlanet() {
		String nameEn = "Purple planet";
		String nameEs = "Planeta morado";
		String descriptionEn = "Slightly animated planet";
		String descriptionEs = "Planeta ligeramente animado";
		String image = "images/PurplePlanet.png";
		planet(nameEn, nameEs, descriptionEn, descriptionEs, image, 2.5F, 6.0F,
				12.0F, 15F, 18.0F, 24.0F, 20.0F, 30.0F, 12.0F, 24.0F, 2.0F,
				14.0F, -6.0F, 6.0F, -10.0F);
	}

	private void orangePlanet() {
		String nameEn = "Orange planet";
		String nameEs = "Planeta naranja";
		String descriptionEn = "Slightly animated planet";
		String descriptionEs = "Planeta ligeramente animado";
		String image = "images/OrangePlanet.png";
		planet(nameEn, nameEs, descriptionEn, descriptionEs, image, 1.5F,
				-5.0F, -10.0F, -10F, -14.0F, -15.0F, -18.0F, -20.0F, -12.0F,
				-15.0F, -2.0F, -8.0F, 6.0F, 2.0F, 10.0F);
	}

	private void earth() {
		String nameEn = "Earth";
		String nameEs = "Planeta Tierra";
		String descriptionEn = "Slightly animated Earth plante";
		String descriptionEs = "La Tierra. Ligeramente animada";
		String image = "images/Earth.png";
		planet(nameEn, nameEs, descriptionEn, descriptionEs, image, 3.0F,
				-15.0F, -30.0F, -30F, -32.0F, -45.0F, -42.0F, -50.0F, -36.0F,
				-45.0F, -20.0F, -24.0F, 18.0F, 6.0F, 30.0F);
	}

	private void planet(String nameEn, String nameEs, String descriptionEn,
			String descriptionEs, String image, float duration,
			float... coordinates) {
		repoEntity(nameEn, nameEs, descriptionEn, descriptionEs, image,
				RepoCategories.ELEMENTS_OBJECTS.toString(), image);
		tag("planet", "planeta").tag(RepoTags.CHARACTERISTIC_ANIMATED)
				.tag(RepoTags.TYPE_OBJECT).tag(RepoTags.DESIGN_FLAT);
		Timeline timeline = new Timeline();
		timeline.setMode(Timeline.Mode.SEQUENCE);
		timeline.setRepeat(-1);

		for (int i = 0; i < coordinates.length - 1; i += 2) {
			timeline.getChildren().add(
					makeTween(MoveTween.class, 0F, 0, 0F, false, duration,
							false, Tween.EaseEquation.LINEAR,
							Tween.EaseType.INOUT, coordinates[i],
							coordinates[i + 1], null, null));
		}
		timeline.getChildren().add(
				makeTween(MoveTween.class, 0F, 0, 0F, false, duration, false,
						Tween.EaseEquation.LINEAR, Tween.EaseType.INOUT, 0F,
						0F, null, null));
		getLastEntity().getComponents().add(timeline);
		adjustEntity(getLastEntity());
	}

	private void alienShip() {
		String nameEn = "Alien spaceship";
		String nameEs = "Nave alienígena";
		String descriptionEn = "Space ship operated by two smiley green aliens";
		String descriptionEs = "Nave espacial operada por dos alienígenas sonrientes";
		ModelEntity ship = repoEntity(nameEn, nameEs, descriptionEn,
				descriptionEs, "images/aliens_thumbnail.png",
				RepoCategories.ELEMENTS_OBJECTS.toString(), (String) null)
				.getLastEntity();
		tag(RepoTags.DESIGN_FLAT).tag(RepoTags.TYPE_OBJECT)
				.tag(RepoTags.CHARACTERISTIC_ANIMATED)
				.tag("alien", "alienígena").tag("space ship", "nave espacial");
		entity(ship, 0, 0).frames(0.2F, "images/Aliens_flame01.png",
				"images/Aliens_flame02.png", "images/Aliens_flame03.png",
				"images/Aliens_flame04.png");
		entity(ship, "images/Aliens_ship.png", 0, 0);
		adjustEntity(ship);
	}

	/* **********************
	 * MONSTERS *********************
	 */
	private void ebb() {
		// Ebb
		elementWithSpineAnimation("Ebb", "animation", 200, 0.7F,
				"Monster that likes licking things!",
				"Monstruo al que le gusta chupar cosas");
		tagMonster();

		lastElement.setAuthorName("Omarvec");
		lastElement.setAuthorUrl("http://www.vecteezy.com/members/omarvec");
		lastElement.setPublisher(RepoTags.SOURCE_VECTEEZY.toString());
		lastElement.setLicenseName(DefaultLicenses.License.CC_BY_SA_NC
				.toString());
		lastElement
				.setLicenseTerms("This work is licensed under a Creative Commons Attribution-Noncommercial-Share Alike 3.0 Unported License. You may redistribute, remix, tweak, and build upon this work non-commercially, as long as you credit the artist by linking back and license your new creations under the same terms.");
	}

	private void jellyMonster() {
		// Jelly monster
		elementWithSpineAnimation("JellyMonster", "animation", 300, 0.5F,
				"Jelly monster from the TimeMokapsule world",
				"Monstruo gelatinoso del mundo TimeMokapsule");
		tagMonster();

		lastElement.setAuthorName("DownloadFreeVector.com");
		lastElement.setAuthorUrl("http://vector4free.com/vectors/author/224/");
		lastElement.setPublisher(RepoTags.SOURCE_VECTOR4FREE.toString());
		lastElement.setLicenseName(DefaultLicenses.License.CC_BY.toString());
		lastElement
				.setLicenseTerms("https://creativecommons.org/licenses/by/4.0/");
	}

	private void robb() {
		elementWithSpineAnimation("Robb", "animation", 300, 0.7F,
				"Green monster from the TimeMokapsule's world",
				"Monstruo verde del mundo TimeMokapsule");
		tagMonster();

		lastElement.setAuthorName("Omarvec");
		lastElement
				.setAuthorUrl("http://www.vecteezy.com/birds-animals/24572-robb-the-alien");
		lastElement.setPublisher(RepoTags.SOURCE_VECTEEZY.toString());
		lastElement.setLicenseName(DefaultLicenses.License.CC_BY_SA_NC
				.toString());
		lastElement
				.setLicenseTerms("This work is licensed under a Creative Commons Attribution-Noncommercial-Share Alike 3.0 Unported License. You may redistribute, remix, tweak, and build upon this work non-commercially, as long as you credit the artist by linking back and license your new creations under the same terms.");
	}

	private void tagMonster() {
		tag("alien", "extraterrestre").tag("martian", "marciano")
				.tag("monster", "monstruo")
				.tag(RepoTags.CHARACTERISTIC_ANIMATED)
				.tag(RepoTags.TYPE_CHARACTER);
	}

	private void tagRobot(boolean flat, boolean metalic) {
		tag("robot", "robot").tag("technology", "tecnología")
				.tag("science", "ciencia").tag("electric", "eléctrico")
				.tag("machine", "máquina")
				.tag("spaceman", "hombre del espacio")
				.tag("mechanical", "mecánico").tag("robotic", "robótico")
				.tag("human robot", "robot humanoide")
				.tag("robot face", "cara de robot")
				.tag("robot toy", "robot de juguete")
				.tag(RepoTags.CHARACTERISTIC_ANIMATED)
				.tag(RepoTags.TYPE_CHARACTER);
		if (flat) {
			tag(RepoTags.DESIGN_FLAT.toString());
		}
		if (metalic) {
			tag(RepoTags.DESIGN_METALIC.toString());
		}
	}

	public static void main(String[] args) {
		String[] argsForBuilder = new String[] { "-out", args[0], "-libs",
				TimeMokapsule.class.getName(), "-imagemagick", args[1],
				"-engine-lib", args[2] };
		BuildRepoLibs.main(argsForBuilder);
	}
}
