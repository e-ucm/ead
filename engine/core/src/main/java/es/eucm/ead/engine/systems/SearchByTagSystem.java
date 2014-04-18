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
package es.eucm.ead.engine.systems;

import ashley.core.*;
import ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import es.eucm.ead.engine.components.TagsComponent;
import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Parser;
import es.eucm.ead.engine.expressions.operators.OperatorFactory;
import es.eucm.ead.engine.systems.variables.VarsContext;

/**
 * System that is in charge of executing tag-based queries in entities.
 * 
 * Created by Javier Torrente on 18/04/14.
 */
public class SearchByTagSystem extends IteratingSystem {

	private static final String LOG_TAG = "SearchByTagSystem";

	private ObjectMap<String, Expression> expressionMap;

	private OperatorFactory operatorFactory;

	private PooledEngine engine;

	// The same object is always used to notify results. This avoid reducing the
	// number of objects that are created.
	private Array<Entity> results;

	public SearchByTagSystem(PooledEngine engine) {
		super(Family.getFamilyFor(SearchByTagComponent.class));
		this.operatorFactory = new OperatorFactory();
		this.expressionMap = new ObjectMap<String, Expression>();
		this.engine = engine;
		results = new Array<Entity>();
	}

	@Override
	public void processEntity(Entity entityWithPendingQueries, float deltaTime) {
		// Get all entities that have a tags component. Searches will be
		// performed only over them.
		IntMap<Entity> entities = engine.getEntitiesFor(Family
				.getFamilyFor(TagsComponent.class));

		// Get pending queries to execute for this entity and process all of
		// them
		SearchByTagComponent queries = entityWithPendingQueries
				.getComponent(SearchByTagComponent.class);
		while (queries.getExpressions().size > 0) {
			String expression = queries.getExpressions().removeIndex(0);
			SearchByTagListener listener = queries.getListeners()
					.removeIndex(0);
			results.clear();

			// Iterate through entities with TagsComponent
			for (Entity entity : entities.values()) {
				// Get tags in the form of vars context. Needed for expression
				// evaluation
				TagsComponent tagsComponent = entity
						.getComponent(TagsComponent.class);
				VarsContext varsContext = tagsComponent.getVarsContext();
				// Evaluate
				if (!expressionMap.containsKey(expression)) {
					Expression e = Parser.parse(expression, operatorFactory);
					expressionMap.put(expression, e);
				}
				try {
					Object evaluation = expressionMap.get(expression).evaluate(
							varsContext);
					if (evaluation instanceof Boolean) {
						if (((Boolean) evaluation).booleanValue()) {
							results.add(entity);
						}
					} else {
						Gdx.app.debug(
								LOG_TAG,
								"Could not terminate SearchByTags job because the expression evaluation did not return a boolean.");
					}
				} catch (ExpressionEvaluationException e) {
					Gdx.app.debug(
							LOG_TAG,
							"Could not terminate SearchByTags job because the expression could not be evaluated",
							e);
				}
			}

			// Notify listener
			listener.results(results, expression);
		}
		entityWithPendingQueries.remove(SearchByTagComponent.class);
	}

	/**
	 * Component that contains a set of tag-based search jobs to be executed.
	 * Each job is comprised by a {@code String} that defines the tag-based
	 * query, and a {@code SearchByTagListener} listener that has to be invoked
	 * when results are ready.
	 */
	public static class SearchByTagComponent extends Component implements
			Pool.Poolable {
		private Array<String> expressions;
		private Array<SearchByTagListener> listeners;

		public SearchByTagComponent() {
			expressions = new Array<String>();
			listeners = new Array<SearchByTagListener>();
		}

		/**
		 * @return The tag-based queries that a given entity has pending for
		 *         execution.
		 */
		public Array<String> getExpressions() {
			return expressions;
		}

		/**
		 * @return The listeners that should be notified for each query stored.
		 *         Listeners and expressions are paired because they occupy the
		 *         same position in their respective arrays.
		 */
		public Array<SearchByTagListener> getListeners() {
			return listeners;
		}

		/**
		 * Adds a search job for execution during the next tick.
		 * 
		 * @param query
		 *            The string that represents the tag-based expression for
		 *            evaluation. Cannot be {@code null}.
		 * @param listener
		 *            The listener that gets notified when results are ready.
		 *            Cannot be {@code null}.
		 */
		public void addQuery(String query, SearchByTagListener listener) {
			if (query != null && listener != null) {
				expressions.add(query);
				listeners.add(listener);
			} else {
				Gdx.app.debug(LOG_TAG, "Query and listener cannot be null");
			}
		}

		@Override
		public void reset() {
			expressions.clear();
			listeners.clear();
		}
	}

	/**
	 * Listener that gets invoked when a query is executed.
	 */
	public static interface SearchByTagListener {
		/**
		 * Notifies that the tag-based {@code query} scheduled was completed and
		 * results are available in {@code entities}.
		 * 
		 * @param entities
		 *            An array containing all entities that matched the
		 *            tag-based query. Listeners should make sure of processing
		 *            or copying the array immediately since it will be rapidly
		 *            cleared and used for other notifications.
		 * @param query
		 *            The tag-based query executed. Examples: "$tag1",
		 *            "(or $tag1 $tag2)".
		 */
		public void results(Array<Entity> entities, String query);
	}

}
