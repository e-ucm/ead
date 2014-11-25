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

import es.eucm.ead.repobuilder.BuildRepoLibs;
import es.eucm.ead.repobuilder.RepoLibraryBuilder;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Frames;

/**
 * Created by jtorrente on 20/11/14.
 */
public class VectorCharacters extends RepoLibraryBuilder {
	public VectorCharacters() {
		super("vectorcharacters");
	}

	@Override
	protected void doBuild() {
		setCommonProperty(AUTO_IDS, "true");
		setCommonProperty(LIB_NAME, "vectorcharacters-monsters-and-animals");
		setCommonProperty(PUBLISHER, "vectorcharacters");
		setCommonProperty(CATEGORIES,
				RepoCategories.ELEMENTS_CHARACTERS.toString());

		setCommonProperty(TAGS,
				"VectorCharacters,animated;animado,characters;personajes,cartoon");
		setCommonProperty(AUTHOR_NAME, "VectorCharacters");
		setCommonProperty(AUTHOR_URL, "vectorcharacters.net/");
		setCommonProperty(LICENSE,
				DefaultLicenses.License.LINK_AUTHOR.toString());

		setCommonProperty(MAX_WIDTH, "350");
		setCommonProperty(MAX_HEIGHT, "350");
		leelo();
		dirtyDrop();

		catWithAnimatedTail();
		catWithTail();
		coloredCat("blue", "azul");
		coloredCat("cream", "crema");
		coloredCat("cyan", "cyan");
		coloredCat("green", "turquesa");
		coloredCat("yellow", "amarillo");

		setCommonProperty(MAX_WIDTH, "600");
		setCommonProperty(MAX_HEIGHT, "600");
		pinkHappyMonster();
		yellowAngryMonster();
		octopus();

		blueMonster();
		evilMonster();
		redMonster();

		repoLib("", "", "", "", null);
	}

	private void evilMonster() {
		// Evil monster
		repoEntity("Evil monster", "Monstruo malvado", "", "", "evil.png", null)
				.tag("monster", "monstruo").tag("blink", "parpadear");
		float blink = 0.12F;
		float normal = 4;
		frame("evil_01.png", normal).frame("evil_02.png", blink)
				.frame("evil_01.png", normal).frame("evil_02.png", blink)
				.frame("evil_01.png", normal).frame("evil_02.png", blink)
				.frame("evil_03.png", normal).frame("evil_02.png", blink)
				.frame("evil_03.png", normal).frame("evil_02.png", blink)
				.adjustEntity(getLastEntity());
	}

	private void blueMonster() {
		float d1 = 0.125F;
		// Blue monster
		repoEntity("Funky monster", "Monstruo azul", "", "", "blue.png", null)
				.tag("monster", "monstruo").tag("ghost", "fantasma")
				.tag("sing", "cantar").tag("dance", "bailar")
				.tag("music", "música");
		for (int i = 0; i < 4; i++) {
			frame("blue_01.png", d1).frame("blue_04.png", d1)
					.frame("blue_02.png", d1).frame("blue_03.png", d1);
		}
		frame("blue_06.png", d1).frame("blue_05.png", d1)
				.frame("blue_06.png", d1).frame("blue_08.png", d1)
				.frame("blue_07.png", d1).frame("blue_08.png", d1);
		adjustEntity(getLastEntity());
	}

	private void redMonster() {
		float d1 = 0.125F;
		// Red monster
		repoEntity("Red monster", "Monstruo rojo", "", "", "red.png", null)
				.tag("monster", "monstruo");
		frame("red_01.png", d1).frame("red_02.png", d1).frame("red_01.png", d1)
				.frame("red_03.png", d1).frame("red_04.png", d1)
				.frame("red_03.png", d1).frame("red_01.png", d1)
				.frame("red_05.png", d1).frame("red_06.png", d1)
				.frame("red_05.png", d1).frame("red_01.png", d1)
				.adjustEntity(getLastEntity());
	}

	private void catWithAnimatedTail() {
		String catDescriptionEn = "Free sweet cat designed by DXone and published at vectorcharacters.net. Blinks, takes tongue out and shakes tail. Animated by the mokap team.";
		String catDescriptionEs = "Dulce gatito diseñado por DXone y publicado en vectorcharacters.net. Parpadea, saca la lengua, y agita la cola. Animado por el equipo de mokap";

		repoEntity("Sweet cat - animated tail",
				"Dulce gatito rosa - cola animada", catDescriptionEn,
				catDescriptionEs, "cat.png", null).tagAnimatedCharacter()
				.tag("animal", "animal").tag("cat", "gato").tag("cute", "mono")
				.tag("pet", "mascota").tag("pink", "rosa")
				.category(RepoCategories.ELEMENTS)
				.category(RepoCategories.ELEMENTS_CHARACTERS);
		authorName("DXone - Vectorcharacters");
		authorUrl("vectorcharacters.net/animal-vector-characters/sweet-cat-vector-character");
		ModelEntity catEntity = getLastEntity();

		// Tail
		entity(catEntity, null, 580, 150);
		for (int i = 1; i <= 6; i++) {
			frame("tail_0" + i + ".png", 0.1F);
		}
		getLastEntity().setOriginX(0);
		// getLastEntity().setRotation(-25F);
		float period = 5F;
		instantaneousRotation(-10F);
		rotation(10F, period);

		// Body
		entity(catEntity, null, 0, 0);
		String[] bodyFrames = new String[5];
		for (int i = 1; i <= 5; i++) {
			bodyFrames[i - 1] = "body_0" + i + ".png";
		}
		blinkFrameAnimation(bodyFrames);
		catEntity.setScaleX(0.3F);
		catEntity.setScaleY(0.3F);
	}

	private void catWithTail() {
		String catDescriptionEn = "Free sweet cat designed by DXone and published at vectorcharacters.net. Blinks and takes tongue out. Animated by the mokap team.";
		String catDescriptionEs = "Dulce gatito diseñado por DXone y publicado en vectorcharacters.net. Parpadea y saca la lengua. Animado por el equipo de mokap.";

		repoEntity("Sweet cat", "Dulce gatito", catDescriptionEn,
				catDescriptionEs, "blue_cat.png", null).tagAnimatedCharacter()
				.tag("animal", "animal").tag("cat", "gato").tag("cute", "mono")
				.tag("pet", "mascota").tag("blue", "añil")
				.category(RepoCategories.ELEMENTS)
				.category(RepoCategories.ELEMENTS_CHARACTERS);
		authorName("DXone - Vectorcharacters");
		authorUrl("vectorcharacters.net/animal-vector-characters/sweet-cat-vector-character");

		// Body
		String[] bodyFrames = new String[5];
		for (int i = 1; i <= 5; i++) {
			bodyFrames[i - 1] = "bluecat_0" + i + ".png";
		}
		blinkFrameAnimation(bodyFrames);
		adjustEntity(getLastEntity());
	}

	private void coloredCat(String colorEn, String colorEs) {
		String catDescriptionEn = "Free sweet cat designed by DXone and published at vectorcharacters.net. Blinks and takes tongue out. Animated by the mokap team.";
		String catDescriptionEs = "Dulce gatito diseñado por DXone y publicado en vectorcharacters.net. Parpadea y saca la lengua. Animado por el equipo de mokap.";

		repoEntity("Sweet cat - no tail (" + colorEn + ")",
				"Dulce gatito - sin cola " + (colorEs + ")"), catDescriptionEn,
				catDescriptionEs, "notailcat_" + colorEn + ".png", null)
				.tagAnimatedCharacter().tag("animal", "animal")
				.tag("cat", "gato").tag("cute", "mono").tag("pet", "mascota")
				.tag(colorEn, colorEs).category(RepoCategories.ELEMENTS)
				.category(RepoCategories.ELEMENTS_CHARACTERS);
		authorName("DXone - Vectorcharacters");
		authorUrl("vectorcharacters.net/animal-vector-characters/sweet-cat-vector-character");

		// Body
		String[] bodyFrames = new String[5];
		for (int i = 1; i <= 5; i++) {
			bodyFrames[i - 1] = "notailcat_" + colorEn + "_0" + i + ".png";
		}
		blinkFrameAnimation(bodyFrames);
		adjustEntity(getLastEntity());
	}

	private void leelo() {
		String nameEn = "Leelo";
		String nameEs = "Leelo";
		String descriptionEn = "Cute little blue cartoon character waving a hand.";
		String descriptionEs = "Pequeño personajillo azul sonriendo y saludando con la mano.";

		String authorName = "DXone - VectorCharacters";
		String authorUrl = "http://vectorcharacters.net/monster-vector-characters/sweet-cartoon-character";
		String tags = "blue,azul,monster,monstruo,smiling,sonriendo,waving hand,wave hand,saludar,saludar con la mano,cheerful,contento,funny,gracioso";

		String thumbnail = "leelo.png";
		String imagePrefix = "leelo_0";
		int startIndex = 1;
		int endIndex = 5;
		float frameDuration = 0.1F;

		character(nameEn, nameEs, descriptionEn, descriptionEs, authorName,
				authorUrl, tags, thumbnail, imagePrefix, startIndex, endIndex,
				frameDuration);
	}

	private void dirtyDrop() {
		String nameEn = "Dirty Water Drop";
		String nameEs = "Gota de agua sucia";
		String descriptionEn = "A filthy drop of water. Animated.";
		String descriptionEs = "Una gota de agua sucia. Viene con animación.";

		String authorName = "moyicat - VectorCharacters";
		String authorUrl = "vectorcharacters.net/monster-vector-characters/free-water-drop-vector-character";
		String tags = "ecology,eco,water,monster,sick,brown,drop,ecología,agua,monstruo,enfermo,marrón,gota";

		String thumbnail = "dirty_drop.png";
		String imagePrefix = "dirty_dro__0";
		int startIndex = 0;
		int endIndex = 30;
		float frameDuration = 0.1F;

		character(nameEn, nameEs, descriptionEn, descriptionEs, authorName,
				authorUrl, tags, thumbnail, imagePrefix, startIndex, endIndex,
				frameDuration);
	}

	private void pinkHappyMonster() {
		String nameEn = "Happy pink monster";
		String nameEs = "Monstruo rosa contento";
		String descriptionEn = "A beautiful smiling monster listening to music.";
		String descriptionEs = "Monstro sonriente escuchando música.";

		String authorName = "VectorCharacters";
		String authorUrl = "vectorcharacters.net/monster-vector-characters/cute-monster-character";
		String tags = "pink,monstruo,rosa,contento,escuchando música,sonriente,mascota,cartoon character, cheerful, clean, cute, cute monster, cute monster mascot, fresh, friendly, funny character, Happy monster character, headphones, lovely, mascot";

		String thumbnail = "pink_happy_monste.png";
		String imagePrefix = "pink_happy_monste__0";
		int startIndex = 0;
		int endIndex = 30;
		float frameDuration = 0.1F;

		character(nameEn, nameEs, descriptionEn, descriptionEs, authorName,
				authorUrl, tags, thumbnail, imagePrefix, startIndex, endIndex,
				frameDuration);
	}

	private void octopus() {
		String nameEn = "Happy octopus";
		String nameEs = "Pulpo feliz";
		String descriptionEn = "A happy cartoon octopus.";
		String descriptionEs = "Un pulpo contento moviendo las patas";

		String authorName = "PixEden";
		String authorUrl = "http://vectorcharacters.net/animal-vector-characters/6-vector-aquatic-animals";
		String tags = "animal,mascot,pet,mascota,contento,cheerful,smiling,sonriente,happy,friendly,glossy,purple,morado,púrpura,undersea,bajo del mar,molusco,seafood,marine,ocean,sea,marino,océano,mar";

		String thumbnail = "pulpo.png";
		String imagePrefix = "pulpo";
		int startIndex = 1;
		int endIndex = 4;
		float frameDuration = 0.1F;

		character(nameEn, nameEs, descriptionEn, descriptionEs, authorName,
				authorUrl, tags, thumbnail, imagePrefix, startIndex, endIndex,
				frameDuration);
	}

	private void yellowAngryMonster() {
		String nameEn = "Angry yellow monster";
		String nameEs = "Monstruo amarillo musculoso";
		String descriptionEn = "A yellow monster ready to punch you in your face!";
		String descriptionEs = "Personaje amarillo animado, ¡listo para darte en toda la cara!";

		String authorName = "noskill1343 - VectorCharacters";
		String authorUrl = "vectorcharacters.net/monster-vector-characters/muscle-vector-illustration";
		String tags = "angry,monster,yellow,fire,fitness,strong,furious,muscle,mascot,enfadado,monstruo,mascota,amarillo,fuego,músculo,musculoso";

		String thumbnail = "yellow_angry_monste.png";
		String imagePrefix = "yellow_angry_monste__0";
		int startIndex = 0;
		int endIndex = 30;
		float frameDuration = 0.1F;

		character(nameEn, nameEs, descriptionEn, descriptionEs, authorName,
				authorUrl, tags, thumbnail, imagePrefix, startIndex, endIndex,
				frameDuration);
	}

	private void character(String nameEn, String nameEs, String descriptionEn,
			String descriptionEs, String authorName, String authorUrl,
			String tags, String thumbnail, String imagePrefix, int startIndex,
			int endIndex, float frameDuration) {
		repoEntity(
				nameEn,
				nameEs,
				descriptionEn
						+ "Obtained from VectorCharacters.net. Animated by the mokap team.",
				descriptionEs
						+ "Obtenido de VectorCharacters.net. Animado por el equipo de mokap.",
				thumbnail, null).tagAnimatedCharacter()
				.category(RepoCategories.ELEMENTS)
				.category(RepoCategories.ELEMENTS_CHARACTERS);
		;
		authorName(authorName);
		authorUrl(authorUrl);
		license(DefaultLicenses.License.LINK_AUTHOR.toString());
		for (String tag : tags.split(",")) {
			tag(tag, "");
		}
		for (int i = startIndex; i <= endIndex; i++) {
			frame(getLastEntity(), makeFrameUri(i, imagePrefix, endIndex),
					frameDuration, Frames.Sequence.YOYO);
		}
		adjustEntity(getLastEntity());
	}

	private String makeFrameUri(int index, String prefix, int end) {
		String maxLength = "" + end;
		String i = "" + index;
		while (i.length() < maxLength.length()) {
			i = "0" + i;
		}
		return prefix + i + ".png";
	}

	private void instantaneousRotation(float angle) {
		tween(RotateTween.class, null, 1, null, null, 0F, true, null, null,
				angle, null, null, null);
	}

	private void rotation(float angleIncrement, float duration) {
		tween(RotateTween.class, null, -1, null, true, duration, true,
				Tween.EaseEquation.SINE, Tween.EaseType.INOUT, angleIncrement,
				null, null, null);
	}

	public static void main(String[] args) {
		String[] argsForBuilder = new String[] { "-out",
				args[0], "-libs",
				VectorCharacters.class.getName(), "-imagemagick",
				args[1] };
		BuildRepoLibs.main(argsForBuilder);
	}
}
