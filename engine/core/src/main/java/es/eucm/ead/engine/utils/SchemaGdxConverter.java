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

import com.badlogic.gdx.math.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Conversion from schema to gdx data-holding classes.
 * 
 * @author mfreire
 */
public class SchemaGdxConverter {

	/**
	 * Converts a GDX polygon to an EAD polygon.
	 * 
	 * @param p
	 *            polygon to convert
	 * @return a schema polygon
	 */
	public static es.eucm.ead.schema.data.Polygon gdxToSchemaPolygon(Polygon p) {
		float[] cs = p.getVertices();
		ArrayList<Float> resultVertices = new ArrayList<Float>(cs.length);
		for (float f : cs) {
			resultVertices.add(f);
		}
		es.eucm.ead.schema.data.Polygon result = new es.eucm.ead.schema.data.Polygon();
		result.setPoints(resultVertices);
		return result;
	}

	/**
	 * Converts an EAD schema polygon to a libgdx polygon.
	 * 
	 * The input polygon is assumed to have a single ring (ẗhat is, no holes).
	 * 
	 * @return the resulting libgdx polygon
	 */
	public static Polygon schemaToGdxPolygon(
			es.eucm.ead.schema.data.Polygon schemaPolygon) {
		List<Float> coords = schemaPolygon.getPoints();
		float[] cs = new float[coords.size()];
		int i = 0;
		for (float f : coords) {
			cs[i++] = f;
		}
		return new Polygon(cs);
	}
}
