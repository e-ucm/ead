package es.eucm.ead.repobuilder.libs;

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

import es.eucm.ead.repobuilder.RepoLibraryBuilder;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;
import es.eucm.ead.schema.editor.components.repo.licenses.RepoLicense;

/**
 * Created by Javier Torrente on 23/09/14.
 */
public class Monejos extends RepoLibraryBuilder {

	public Monejos() {
		super("gloria");
	}

	@Override
	protected void doBuild() {
		setCommonProperty(AUTO_IDS, "true");
		setCommonProperty(LIB_NAME, "monejos");
		setCommonProperty(PUBLISHER, "mokap");

		setCommonProperty(MAX_WIDTH, "1024");
		setCommonProperty(MAX_HEIGHT, "1024");
		setCommonProperty(TAGS, "eAdventure,eUCM,cartoon");
		setCommonProperty(AUTHOR_NAME, "Gloria Pozuelo Fernández");
		setCommonProperty(LICENSE, DefaultLicenses.License.CC_BY.toString());

		float d = 0.8F;
		repoEntity("Monejo", "Monejo", "A beautiful simple animated character",
				"Un sencillo personajillo", "monejo.png", null)
				.tagFullyAnimatedCharacter()
				.category(RepoCategories.ELEMENTS_CHARACTERS)
				.frameState(2, d, WALK, RIGHT, "Monejo2 andando_10",
						"Monejo2 andando_04");
		frameState(2, d, WALK, DOWN, "Monejo_adandoDeFrente_04",
				"Monejo_adandoDeFrente_01");
		frameState(2, d, TALK, DOWN, "Monejo hablando_03", "Monejo hablando_02");
		frameState(2, d, TALK, RIGHT, "Monejo hablando lado_04",
				"Monejo hablando lado_01");
		frameState(3, d, GRAB, USE, RIGHT, "monejo cogiendo_01");
		frameState(2, 2, DEFAULT, IDLE, "Monejo hablando_02");
		frameState(2, d, WALK, UP, "monejo andandoDespaldas_04",
				"monejo andandoDespaldas_01").adjustEntity(getLastEntity());

		repoEntity(
				"Computer mouse",
				"Ratón de ordenador",
				"An animation of a computer mouse performing right and left clicks",
				"Animación de un ratón de ordenador haciendo click izquierdo y derecho",
				"mouse.png", null).tag("animation", "animación")
				.tag("mouse", "ratón").tag("computer", "ordenador")
				.tag("PC", "PC").category(RepoCategories.ELEMENTS_OBJECTS);
		frameState(1, 1, "idle", "ratonSimple_01");
		frameState(2, 1, DEFAULT, "left click", "ratonSimple_01", "raton_04");
		frameState(1, 1, "right click", "ratonSimple_01", "raton_01")
				.adjustEntity(getLastEntity());

		repoEntity("Blond lab guy", "Chico de laboratorio rubio",
				"A lab coated guy with blond hair",
				"Un personaje de laboratorio rubio", "labguy1.png",
				"Lab2_DeFrente_01").tagCharacter()
				.tag("lab coat", "bata laboratorio").tag("lab", "laboratorio")
				.tag("blond", "rubio").tag("scientist", "científico")
				.category(RepoCategories.ELEMENTS_CHARACTERS)
				.adjustEntity(getLastEntity());

		repoEntity("Dark hair lab guy with glasses",
				"Chico de laboratorio moreno con gafas", "", "", "labguy2.png",
				"Lab_DeFrente_01").tagCharacter()
				.tag("lab coat", "bata de laboratorio")
				.tag("lab", "laboratorio").tag("dark hair", "moreno")
				.tag("glasses", "gafas").tag("scientist", "científico")
				.category(RepoCategories.ELEMENTS_CHARACTERS)
				.adjustEntity(getLastEntity());

		repoEntity("<e-Adventure> doctor", "Médico <e-Adventure>",
				"A freky doctor fan of <e-Adventure>",
				"Un médico fan de <e-Adventure>, un poco friqui",
				"freakdoctor.png", null).tagFullyAnimatedCharacter()
				.tag("doctor", "médico").tag("scientist", "científico")
				.tag("red hair", "pelirojo").tag("glasses", "gafas")
				.category(RepoCategories.ELEMENTS_CHARACTERS);
		frameState(3, 0.2F, DEFAULT, IDLE, DOWN, "DoctorParadoFrente_01");
		frameState(2, 0.2F, IDLE, UP, "DoctorParadoDespaldas_01");
		frameState(2, 0.2F, IDLE, RIGHT, "DoctorHablandoLado_03");
		frameState(3, 0.2F, USE, GRAB, RIGHT, "DoctorCogiendo_01");
		frameState(2, 0.5F, TALK, RIGHT, "DoctorHablandoLado_03",
				"DoctorHablandoLado_02");
		frameState(2, 0.5F, TALK, DOWN, "DoctorHablandoFrente_03",
				"DoctorHablandoFrente_01");
		frameState(2, 0.5F, WALK, RIGHT, "DoctorAndandoLado_03",
				"DoctorAndandoLado_02");
		frameState(2, 0.5F, WALK, DOWN, "DoctorAndandoFrente_02",
				"DoctorAndandoFrente_01");
		frameState(2, 0.5F, WALK, UP, "DoctorAndandoDespaldas_04",
				"DoctorAndandoDespaldas_01").adjustEntity(getLastEntity());

		repoEntity("Blue cap monejo", "Monejo con gorra azul",
				"A monejo with blue cap and jacket and dark glasses",
				"Un monejo con gorra y chaqueta azules, y gafas oscuras",
				"bluecap.png", null).tagAnimatedCharacter()
				.tag("blue cap", "gorra azul")
				.category(RepoCategories.ELEMENTS_CHARACTERS);
		frameState(1, 1, IDLE, "DeLado_01").frameState(2, 0.5F, DEFAULT, TALK,
				"DeFrenteHablando", "DeFrente_01")
				.adjustEntity(getLastEntity());

		repoLib("Monejos", "Monejos",
				"A library with a few interesting cartoon characters",
				"Una biblioteca con seis personajes", null);
	}
}
