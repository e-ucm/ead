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
package es.eucm.ead.engine.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.Parameters;

public class EngineUtils {

	public static <T extends Parameters> T buildWithParameters(Assets assets,
			VariablesManager variablesManager, T parameters) {
		if (parameters.getParameters().size > 0) {
			Class clazz = parameters.getClass();
			Parameters clone = (Parameters) assets.fromJson(clazz,
					assets.toJson(parameters, clazz));
			for (Parameter parameter : clone.getParameters()) {
				Object value = variablesManager.evaluateExpression(parameter
						.getValue());
				variablesManager.getAccessor().set(clone, parameter.getName(),
						value);
			}
			return (T) clone;
		} else {
			return parameters;
		}
	}

	/**
	 * Splits the text into lines fitting the preferred width, using the given
	 * font
	 */
	public static Array<String> lines(String text, float preferredWidth,
			BitmapFont font) {
		Array<String> lines = new Array<String>();
		String[] words = text.split(" ");
		String line = "";
		int contWord = 0;
		float currentLineWidth = 0;
		while (contWord < words.length) {
			float nextWordWidth = font.getBounds(words[contWord] + " ").width;
			if (currentLineWidth + nextWordWidth <= preferredWidth) {
				currentLineWidth += nextWordWidth;
				line += words[contWord++] + " ";
			} else if (!"".equals(line)) {
				lines.add(line);
				currentLineWidth = 0;
				line = "";
			} else {
				line = splitLongWord(font, lines, words[contWord++],
						preferredWidth);
				currentLineWidth = font.getBounds(line).width;
			}
		}
		if (!"".equals(line)) {
			lines.add(line);
		}
		return lines;
	}

	private static String splitLongWord(BitmapFont f, Array<String> lines,
			String word, float lineWidth) {
		boolean finished = false;
		String currentLine = "";
		int i = 0;
		while (!finished) {
			currentLine = "";
			while (i < word.length()
					&& f.getBounds(currentLine + word.charAt(i)).width < lineWidth) {
				currentLine += word.charAt(i++);
			}
			if (i == word.length()) {
				finished = true;
			} else {
				lines.add(currentLine);
			}
		}
		return currentLine;
	}

	/**
	 * @return Returns a direct child of parent in the hierarchy of the given
	 *         actor. Null if actor is not a descendant of parent
	 */
	public static Actor getDirectChild(Group parent, Actor actor) {
		if (actor == null || !actor.isDescendantOf(parent)) {
			return null;
		}

		Actor firstChild = actor;
		while (firstChild != null && firstChild.getParent() != parent) {
			firstChild = firstChild.getParent();
		}
		return firstChild;
	}
}
