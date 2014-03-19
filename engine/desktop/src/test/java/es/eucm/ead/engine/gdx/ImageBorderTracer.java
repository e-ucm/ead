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
package es.eucm.ead.engine.gdx;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;


import java.util.ArrayList;

/**
 * Traces the borders of an image, creating a polygon that can be used for quick
 * hit-detection.
 */
public class ImageBorderTracer extends GeoTester.GeoViewer {

	protected final ArrayList<Polygon> red = new ArrayList<Polygon>();
	protected final ArrayList<Polygon> blue = new ArrayList<Polygon>();

	private Texture samplePixmap;
	private Texture imagePixmap;

	public static void main(String args[]) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1000;
		config.height = 800;
		new LwjglApplication(new ImageBorderTracer(), config);
	}

	public static Pixmap createSamplePixmap(int width, int height,
		Pixmap.Format fmt) {
		if (fmt == null) {
			fmt = Pixmap.Format.RGBA8888;
		}
		Pixmap p = new Pixmap(width, height, fmt);
		p.setColor(Color.YELLOW);
		p.fillCircle(width / 4, height / 2, width / 5);
		p.fillCircle(width * 3 / 4, height / 2, width / 5);
		return p;
	}

	public static Pixmap openImagePixmap() {
		return new Pixmap(Gdx.files.classpath("border-test/medic.png"));
	}

	@Override
	public void create() {
		super.create();

		Pixmap pm;

		pm = createSamplePixmap(300, 300, null);
		samplePixmap = new Texture(pm);
		for (Geometry g : GeometryUtils.findBorders(pm, .1, 2)) {
			red.add(GeometryUtils.jtsCoordsToGdx(g.getCoordinates()));
		}

		pm = openImagePixmap();
		imagePixmap = new Texture(pm);
		for (Geometry g : GeometryUtils.findBorders(pm, .1, 2)) {
			Coordinate[] cs = g.getCoordinates();
			for (Coordinate c : cs) {
				c.setCoordinate(new Coordinate(c.x + 400, c.y));
			}
			blue.add(GeometryUtils.jtsCoordsToGdx(cs));
		}
	}

	@Override
	public void render() {
		super.render();

		sb.begin();
		sb.draw(samplePixmap, 0, 0);
		sb.draw(imagePixmap, 400, 0);
		sb.end();

		renderPolygonShapes(red, Color.RED);
		renderPolygonShapes(blue, Color.BLUE);
	}
}
