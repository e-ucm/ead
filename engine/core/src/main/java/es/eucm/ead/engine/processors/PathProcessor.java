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
package es.eucm.ead.engine.processors;

import ashley.core.Component;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Polygon;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.PathFinderComponent;
import es.eucm.ead.engine.paths.PathFinder;
import es.eucm.ead.engine.paths.PathUtils;
import es.eucm.ead.engine.utils.SchemaGdxConverter;
import es.eucm.ead.schema.components.PathBoundary;

/**
 * Converts PathBoundary model components to PathBoundary engine components.
 */
public class PathProcessor extends ComponentProcessor<PathBoundary> {
	public PathProcessor(GameLoop engine) {
		super(engine);
	}

	@Override
	public Component getComponent(PathBoundary component) {
		Polygon pathBoundary = SchemaGdxConverter.schemaToGdxPolygon(component
				.getBoundaryPolygons().get(0));
		Matrix3 transform = PathUtils.getProjectionMatrix(SchemaGdxConverter
				.schemaToGdxPolygon(component.getViewSquare()));
		PathFinderComponent pathFinder = engine
				.createComponent(PathFinderComponent.class);
		pathFinder.initialize(new PathFinder(pathBoundary, transform),
				component.getSpeed());
		return pathFinder;
	}
}
