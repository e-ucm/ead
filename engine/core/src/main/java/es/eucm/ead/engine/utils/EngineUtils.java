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
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.Parameters;

public class EngineUtils {

	private static final Matrix3 tmpMatrix = new Matrix3();

	private static GlyphLayout glyphLayout = new GlyphLayout();

	public static <T extends Parameters> T buildWithParameters(Assets assets,
			VariablesManager variablesManager, T parameters) {
		if (parameters.getParameters().size > 0) {
			Class clazz = parameters.getClass();
			Parameters clone = (Parameters) assets.fromJson(clazz,
					assets.toJson(parameters, clazz));
			for (Parameter parameter : clone.getParameters()) {
				Object value = variablesManager
						.evaluateExpression((String) parameter.getValue());
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
			float nextWordWidth = getWidth(font, words[contWord] + " ");
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
				currentLineWidth = getWidth(font, line);
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
					&& getWidth(f, currentLine + word.charAt(i)) < lineWidth) {
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

	private static float getWidth(BitmapFont font, String chr) {
		glyphLayout.setText(font, chr);
		return glyphLayout.width;
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
	 * Calculate the bounds of the possible children of the given actor. If
	 * actor has no children, then resultOrigin and resultSize are set to
	 * actor's bounds.
	 */
	public static void calculateBounds(Actor actor, Vector2 resultOrigin,
			Vector2 resultSize) {
		resultOrigin.set(0, 0);
		resultSize.set(actor.getWidth(), actor.getHeight());
		if (actor instanceof Group && ((Group) actor).getChildren().size > 0) {
			calculateBounds(((Group) actor).getChildren(), resultOrigin,
					resultSize);
		}
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

		Vector2 origin = Pools.obtain(Vector2.class);
		Vector2 size = Pools.obtain(Vector2.class);
		Vector2 leftTop = Pools.obtain(Vector2.class);
		Vector2 rightBottom = Pools.obtain(Vector2.class);
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		for (Actor actor : actors) {
			calculateBounds(actor, origin, size);
			size.add(origin);
			leftTop.set(origin.x, size.y);
			rightBottom.set(size.x, origin.y);
			actor.localToParentCoordinates(origin);
			actor.localToParentCoordinates(size);
			actor.localToParentCoordinates(leftTop);
			actor.localToParentCoordinates(rightBottom);

			minX = Math.min(
					minX,
					Math.min(origin.x, Math.min(size.x,
							Math.min(leftTop.x, rightBottom.x))));
			minY = Math.min(
					minY,
					Math.min(origin.y, Math.min(size.y,
							Math.min(leftTop.y, rightBottom.y))));
			maxX = Math.max(
					maxX,
					Math.max(origin.x, Math.max(size.x,
							Math.max(leftTop.x, rightBottom.x))));
			maxY = Math.max(
					maxY,
					Math.max(origin.y, Math.max(size.y,
							Math.max(leftTop.y, rightBottom.y))));
		}
		Pools.free(origin);
		Pools.free(size);
		Pools.free(leftTop);
		Pools.free(rightBottom);
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

		Vector2 origin = Pools.obtain(Vector2.class);
		Vector2 size = Pools.obtain(Vector2.class);
		Vector2 tmp3 = Pools.obtain(Vector2.class);
		Vector2 tmp4 = Pools.obtain(Vector2.class);

		calculateBounds(group.getChildren(), origin, size);

		/*
		 * minX and minY are the new origin (new 0, 0), so everything inside the
		 * group must be translated that much.
		 */
		for (Actor actor : group.getChildren()) {
			actor.setPosition(actor.getX() - origin.x, actor.getY() - origin.y);
		}

		/*
		 * Now, we calculate the current origin (0, 0) and the new origin (minX,
		 * minY), and group is translated by that difference.
		 */
		group.localToParentCoordinates(tmp3.set(0, 0));
		group.localToParentCoordinates(tmp4.set(origin.x, origin.y));
		tmp4.sub(tmp3);
		group.setBounds(group.getX() + tmp4.x, group.getY() + tmp4.y, size.x,
				size.y);
		group.setOrigin(size.x / 2.0f, size.y / 2.0f);

		Pools.free(origin);
		Pools.free(size);
		Pools.free(tmp3);
		Pools.free(tmp4);
	}

	/**
	 * Sets position, rotation, scale and origin in actor to meet the 3 given
	 * points
	 */
	public static void applyTransformation(Actor actor, Vector2 origin,
			Vector2 tangent, Vector2 normal) {
		/*
		 * We are going to calculate the affine transformation for the actor to
		 * fit the bounds represented by the handles. The affine transformation
		 * is defined as follows:
		 */
		// |a b tx|
		// |c d ty|=|Translation Matrix| x |Scale Matrix| x |Rotation
		// Matrix|
		// |0 0 1 |
		/*
		 * More info about affine transformations:
		 * https://people.gnome.org/~mathieu
		 * /libart/libart-affine-transformation-matrices.html, To obtain the
		 * matrix, we want to resolve the following equation system:
		 */
		// | a b tx| |0| |o.x|
		// | c d ty|*|0|=|o.y|
		// | 0 0 1 | |1| | 1 |
		//
		// | a b tx| |w| |t.x|
		// | c d ty|*|0|=|t.y|
		// | 0 0 1 | |1| | 1 |
		//
		// | a b tx| |0| |n.x|
		// | c d ty|*|h|=|n.y|
		// | 0 0 1 | |1| | 1 |
		/*
		 * where o is handles[0] (origin), t is handles[2] (tangent) and n is
		 * handles[6] (normal), w is actor.getWidth() and h is
		 * actor.getHeight().
		 * 
		 * This matrix defines that the 3 points defining actor bounds are
		 * transformed to the 3 points defining modifier bounds. E.g., we want
		 * that actor origin (0,0) is transformed to (handles[0].x,
		 * handles[0].y), and that is expressed in the first equation.
		 * 
		 * Resolving these equations is obtained:
		 */
		// a = (t.x - o.y) / w
		// b = (t.y - o.y) / w
		// c = (n.x - o.x) / h
		// d = (n.y - o.y) / h
		/*
		 * Values for translation, scale and rotation contained by the matrix
		 * can be obtained directly making operations over a, b, c and d:
		 */
		// tx = o.x
		// ty = o.y
		// sx = sqrt(a^2+b^2)
		// sy = sqrt(c^2+d^2)
		// rotation = atan(c/d)
		// or
		// rotation = atan(-b/a)
		/*
		 * Rotation can give two different values (this happens when there is
		 * more than one way of obtaining the same transformation). To avoid
		 * that, we ignore the rotation to obtain the final values.
		 */

		Vector2 tmp1 = Pools.obtain(Vector2.class);
		Vector2 tmp2 = Pools.obtain(Vector2.class);
		Vector2 tmp3 = Pools.obtain(Vector2.class);
		Vector2 tmp4 = Pools.obtain(Vector2.class);
		Vector2 tmp5 = Pools.obtain(Vector2.class);

		Vector2 o = tmp1.set(origin.x, origin.y);
		Vector2 t = tmp2.set(tangent.x, tangent.y);
		Vector2 n = tmp3.set(normal.x, normal.y);

		Vector2 vt = tmp4.set(t).sub(o);
		Vector2 vn = tmp5.set(n).sub(o);

		// Ignore rotation
		float rotation = actor.getRotation();
		vt.rotate(-rotation);
		vn.rotate(-rotation);

		t.set(vt).add(o);
		n.set(vn).add(o);

		Vector2 bottomLeft = Pools.obtain(Vector2.class);
		Vector2 size = Pools.obtain(Vector2.class);

		calculateBounds(actor, bottomLeft, size);

		float a = (t.x - o.x) / size.x;
		float c = (t.y - o.y) / size.x;
		float b = (n.x - o.x) / size.y;
		float d = (n.y - o.y) / size.y;

		Pools.free(tmp1);
		Pools.free(tmp2);
		Pools.free(tmp3);
		Pools.free(tmp4);
		Pools.free(tmp5);
		Pools.free(bottomLeft);
		Pools.free(size);

		// Math.sqrt gives a positive value, but it also have a negatives.
		// The
		// signum is calculated computing the current rotation
		float signumX = vt.angle() > 90.0f && vt.angle() < 270.0f ? -1.0f
				: 1.0f;
		float signumY = vn.angle() > 180.0f ? -1.0f : 1.0f;

		float scaleX = (float) Math.sqrt(a * a + b * b) * signumX;
		float scaleY = (float) Math.sqrt(c * c + d * d) * signumY;

		actor.setScale(scaleX, scaleY);

		/*
		 * To obtain the correct translation value we need to subtract the
		 * amount of translation due to the origin.
		 */
		tmpMatrix.setToTranslation(actor.getOriginX(), actor.getOriginY());
		tmpMatrix.rotate(actor.getRotation());
		tmpMatrix.scale(actor.getScaleX(), actor.getScaleY());
		tmpMatrix.translate(-actor.getOriginX(), -actor.getOriginY());

		/*
		 * Now, the matrix has how much translation is due to the origin
		 * involved in the rotation and scaling operations
		 */
		float x = o.x - tmpMatrix.getValues()[Matrix3.M02];
		float y = o.y - tmpMatrix.getValues()[Matrix3.M12];
		actor.setPosition(x, y);
	}

	/**
	 * For a given actor computes and applies the transformation to keep the
	 * same screen transformation in a new group
	 * 
	 * @param actor
	 * @param parent
	 */
	public static void computeTransformFor(Actor actor, Group parent) {
		Vector2 tmp1 = Pools.obtain(Vector2.class);
		Vector2 tmp2 = Pools.obtain(Vector2.class);
		Vector2 tmp3 = Pools.obtain(Vector2.class);
		Vector2 tmp4 = Pools.obtain(Vector2.class);
		Vector2 tmp5 = Pools.obtain(Vector2.class);

		calculateBounds(actor, tmp4, tmp5);
		Vector2 o = tmp1.set(tmp4.x, tmp4.y);
		Vector2 t = tmp2.set(tmp4.x + tmp5.x, tmp4.y);
		Vector2 n = tmp3.set(tmp4.x, tmp4.y + tmp5.y);
		actor.localToAscendantCoordinates(parent, o);
		actor.localToAscendantCoordinates(parent, t);
		actor.localToAscendantCoordinates(parent, n);
		actor.setRotation(actor.getRotation() + actor.getParent().getRotation());
		applyTransformation(actor, o, t, n);

		Pools.free(tmp1);
		Pools.free(tmp2);
		Pools.free(tmp3);
		Pools.free(tmp4);
		Pools.free(tmp5);
	}

	/**
	 * @return the nearest entity associated to the given actor
	 */
	public static EngineEntity getActorEntity(Actor actor) {
		if (actor == null) {
			return null;
		}
		Object o = actor.getUserObject();
		return o instanceof EngineEntity ? (EngineEntity) o
				: getActorEntity(actor.getParent());
	}
}
