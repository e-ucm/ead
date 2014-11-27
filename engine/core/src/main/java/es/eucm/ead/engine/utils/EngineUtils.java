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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.Parameters;

public class EngineUtils {

	private static final Vector2 tmp1 = new Vector2(), tmp2 = new Vector2(),
			tmp3 = new Vector2(), tmp4 = new Vector2();

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

	/**
	 * Calculate the bounds of the given actors as a group
	 *
	 * @param actors
	 *            the actors
	 * @param resultOrigin
	 *            result origin of the bounds
	 * @param resultSize
	 *            result size of the bounds
	 */
	public static void calculateBounds(Array<Actor> actors,
			Vector2 resultOrigin, Vector2 resultSize) {
		resultOrigin.set(0, 0);
		resultSize.set(0, 0);
		if (actors.size == 0) {
			return;
		}

		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		for (Actor actor : actors) {
			tmp1.set(0, 0);
			tmp2.set(actor.getWidth(), 0);
			tmp3.set(0, actor.getHeight());
			tmp4.set(actor.getWidth(), actor.getHeight());
			actor.localToParentCoordinates(tmp1);
			actor.localToParentCoordinates(tmp2);
			actor.localToParentCoordinates(tmp3);
			actor.localToParentCoordinates(tmp4);

			minX = Math.min(minX, Math.min(tmp1.x,
					Math.min(tmp2.x, Math.min(tmp3.x, tmp4.x))));
			minY = Math.min(minY, Math.min(tmp1.y,
					Math.min(tmp2.y, Math.min(tmp3.y, tmp4.y))));
			maxX = Math.max(maxX, Math.max(tmp1.x,
					Math.max(tmp2.x, Math.max(tmp3.x, tmp4.x))));
			maxY = Math.max(maxY, Math.max(tmp1.y,
					Math.max(tmp2.y, Math.max(tmp3.y, tmp4.y))));
		}
		resultOrigin.set(minX, minY);
		resultSize.set(maxX - minX, maxY - minY);
	}

	/**
	 * Adjusts the position and size of the given group to its children
	 */
	public static void adjustGroup(Actor root) {
		if (!(root instanceof Group)) {
			return;
		}

		Group group = (Group) root;
		if (group.getChildren().size == 0) {
			return;
		}

		for (Actor actor : group.getChildren()) {
			if (actor instanceof Group) {
				adjustGroup(actor);
			}
		}

		calculateBounds(group.getChildren(), tmp1, tmp2);

		if (tmp1.x != 0 || tmp1.y != 0 || tmp2.x != group.getWidth()
				|| tmp2.y != group.getHeight()) {
			/*
			 * minX and minY are the new origin (new 0, 0), so everything inside
			 * the group must be translated that much.
			 */
			for (Actor actor : group.getChildren()) {
				actor.setPosition(actor.getX() - tmp1.x, actor.getY() - tmp1.y);
			}

			/*
			 * Now, we calculate the current origin (0, 0) and the new origin
			 * (minX, minY), and group is translated by that difference.
			 */
			group.localToParentCoordinates(tmp3.set(0, 0));
			group.localToParentCoordinates(tmp4.set(tmp1.x, tmp1.y));
			tmp4.sub(tmp3);
			group.setBounds(group.getX() + tmp4.x, group.getY() + tmp4.y,
					tmp2.x, tmp2.y);
			group.setOrigin(group.getWidth() / 2.0f, group.getHeight() / 2.0f);
		}
	}
}
