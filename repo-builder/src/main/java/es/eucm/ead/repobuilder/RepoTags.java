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
package es.eucm.ead.repobuilder;

/**
 * Convenient utility for recurrent/default tags used to structure the
 * repository Created by jtorrente on 03/05/2015.
 */
public enum RepoTags {

	/*-----------------------------------------------
	| To identify resources from a specific source
	 *-----------------------------------------------*/

	/**
	 * Resources obtained from www.vecteezy.com
	 */
	SOURCE_VECTEEZY("vecteezy"),

	/**
	 * Resources obtained from www.vectorcharacters.net
	 */
	SOURCE_VECTOR_CHARACTERS("vectorcharacters.net"),

	/**
	 * Resources obtained from http://vector4free.com/
	 */
	SOURCE_VECTOR4FREE("vector4free.com"),

	/*-----------------------------------------------
	 | Characteristics of elements
	 *-----------------------------------------------*/
	CHARACTERISTIC_ANIMATED("animated", "animado"),

	CHARACTERISTIC_STATIC("not-animated", "no animado"),

	/*-----------------------------------------------
	| Design style
	 *-----------------------------------------------*/
	DESIGN_FLAT("flat", "plano"),

	/*-----------------------------------------------
	 | Metalic-like, shinny style
	 *-----------------------------------------------*/
	DESIGN_METALIC("metalic-style", "estilo-metalico"),

	/*-----------------------------------------------
	 | Visual style
	 *-----------------------------------------------*/
	STYLE_CARTOON("cartoon"),

	/*-----------------------------------------------
	 | Type of element
	 *-----------------------------------------------*/
	TYPE_CHARACTER("character", "personaje"), EAD1X_FULL_CHARACTER("walk"
			+ RepoLibraryBuilder.I18N_SEPARATOR + "andar"
			+ RepoLibraryBuilder.MAIN_SEPARATOR + "grab"
			+ RepoLibraryBuilder.I18N_SEPARATOR + "agarrar"
			+ RepoLibraryBuilder.MAIN_SEPARATOR + "talk"
			+ RepoLibraryBuilder.I18N_SEPARATOR + "hablar"
			+ RepoLibraryBuilder.MAIN_SEPARATOR + "use"
			+ RepoLibraryBuilder.I18N_SEPARATOR + "usar"), TYPE_OBJECT(
			"object", "objeto"), TYPE_BACKGROUND("background", "fondo");

	private String tag;

	private RepoTags(String tag) {
		this.tag = tag;
	}

	private RepoTags(String tagEn, String tagEs) {
		this.tag = tagEn + RepoLibraryBuilder.I18N_SEPARATOR + tagEs;
	}

	@Override
	public String toString() {
		return tag;
	}

	private static String MAIN_SEPARATOR = RepoLibraryBuilder.MAIN_SEPARATOR;

	/**
	 * @return A String concatenating all the tags specified as argument, using
	 *         the appropriate separator
	 */
	public static String appendTags(RepoTags... tags) {
		String appendedTags = "";
		for (RepoTags tag : tags) {
			appendedTags += tag.tag + RepoLibraryBuilder.MAIN_SEPARATOR;
		}
		if (appendedTags.endsWith(RepoLibraryBuilder.MAIN_SEPARATOR)) {
			appendedTags = appendedTags.substring(0, appendedTags.length() - 1);
		}
		return appendedTags;
	}

	/**
	 * Appends the given tag to the list, using appropriate separators
	 * 
	 * @return tagList+MAIN_SEPARATOR+tagEn+I18NSEPARATOR+tagEs
	 * 
	 *         where each tag (tagEn, tagEs) is specified in contiguous
	 *         positions of the array "tags" English tags must be in even
	 *         positions, Spanish tags in odd positions
	 * 
	 *         Example of how tags must be formed: [tag1En, tag1Es, tag2En,
	 *         tag2Es]
	 */
	public static String appendTags(String tagList, String... tags) {
		int i = 0;
		while (i < tags.length - 1) {
			String tagEn = tags[i];
			String tagEs = tags[i + 1];
			i += 2;
			tagList += RepoLibraryBuilder.MAIN_SEPARATOR + tagEn
					+ RepoLibraryBuilder.I18N_SEPARATOR + tagEs;
		}
		return tagList;
	}

}
