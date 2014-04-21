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
package es.eucm.ead.engine.components;

import ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import es.eucm.ead.engine.systems.variables.VarsContext;
import es.eucm.ead.schema.components.Tags;

import java.util.List;

/**
 * Simple container of tags for engine entities. Engine equivalent to
 * {@link Tags}.
 * 
 * Stores the tags in a {@link VarsContext} because it facilitates query
 * evaluation.
 * 
 * Created by Javier Torrente on 18/04/14.
 */
public class TagsComponent extends Component implements Pool.Poolable {

	// This VarsContext contains variables of boolean type, indicating if the
	// entity contains or not a given tag.
	// It first gets initialized with those tags present in the owner's tags
	// component:
	// <presentTag1, true> <presentTag2, true> ...

	// When its used for evaluating an expression, it also adds any tag
	// referenced in the expression that is not present in the object:
	// <notPresentTag1, false> <notPresentTag2, false> ...
	// That's why hasVariable is redefined.
	private VarsContext varsContext = new VarsContext() {
		@Override
		public boolean hasVariable(String name) {
			boolean hasVariable = super.hasVariable(name);
			if (!hasVariable)
				this.registerVariable(name, false);
			return true;
		}
	};

	public void setTags(List<String> tags) {
		reset();
		for (String tag : tags) {
			varsContext.registerVariable(tag, true);
		}
	}

	@Override
	public void reset() {
		varsContext.clear();
	}

	public VarsContext getVarsContext() {
		return varsContext;
	}
}
