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
package es.eucm.ead.engine.systems.effects;

import ashley.core.Component;
import ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Pool;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.systems.SearchByTagSystem;
import es.eucm.ead.engine.systems.SearchByTagSystem.SearchByTagComponent;
import es.eucm.ead.engine.systems.SearchByTagSystem.SearchByTagListener;
import es.eucm.ead.schema.effects.AddByTag;

/**
 * Executes effects of type {@link AddByTag}.
 * 
 * The execution of this type of effects entails what follows:
 * <ol>
 * <li>A {@link SearchByTagComponent} job is queued to evaluate the tag
 * expression associated to the effect Examples: "$tag1", "(and $tag1 $tag2)".
 * {@link SearchByTagSystem} will evaluate the query and do the search across
 * entities in the next update().</li>
 * <li>Once {@link SearchByTagSystem} completes the evaluation of the query (in
 * the next tick), it invokes {@link #results(Array, String)} with an array of
 * entities that matched the query. Then the component to be added is created,
 * and its added to each entity returned.</li>
 * </ol>
 * 
 * Created by Javier Torrente on 18/04/14.
 */
public class AddByTagExecutor extends EffectExecutor<AddByTag> implements
		SearchByTagListener, Pool.Poolable {

	private DelayedRemovalArray<AddByTag> pending;

	private EntitiesLoader loader;

	public AddByTagExecutor(EntitiesLoader loader) {
		this.loader = loader;
		pending = new DelayedRemovalArray<AddByTag>();
	}

	@Override
	public void execute(Entity owner, AddByTag effect) {
		if (!owner.hasComponent(SearchByTagComponent.class)) {
			owner.add(engine.createComponent(SearchByTagComponent.class));
		}
		// Schedule query for execution
		SearchByTagComponent queries = owner
				.getComponent(SearchByTagComponent.class);
		pending.add(effect);
		queries.addQuery(effect.getTagsExpression(), this);
	}

	@Override
	// Invoked when the query is resolved, providing an array of entities that
	// match the query
	public void results(Array<Entity> entities, String query) {
		pending.begin();
		for (AddByTag effect : pending) {
			if (effect != null && query.equals(effect.getTagsExpression())) {
				// Build component to be added
				Component component = loader
						.getComponent(effect.getComponent());
				if (component != null) {
					// Add to entities
					for (Entity entity : entities) {
						entity.add(component);
					}
				}
				pending.removeValue(effect, true);
			}
		}
		pending.end();
	}

	@Override
	public void reset() {
		pending.clear();
	}
}
