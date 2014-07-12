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
package es.eucm.ead.editor.view.controllers.values;

import es.eucm.ead.editor.control.actions.editor.ShowContextMenu;
import es.eucm.ead.editor.indexes.FuzzyIndex;
import es.eucm.ead.editor.indexes.FuzzyIndex.Term;
import es.eucm.ead.editor.view.controllers.SearchResultsWidget.SearchListener;
import es.eucm.ead.editor.view.controllers.SearchResultsWidget;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.SearchWidget;

public class SearchController extends ValueController<SearchWidget, Object> {

	private FuzzyIndex index;

	public SearchController(FuzzyIndex index) {
		this.index = index;
	}

	@Override
	protected void initialize() {
		SearchResultsWidget searchResultsWidget = new SearchResultsWidget(
				index, controller.getApplicationAssets().getSkin());
		searchResultsWidget.addListener(new ResultsListener());
		widget.getSearchButton().addListener(
				new ActionOnClickListener(controller, ShowContextMenu.class,
						widget, searchResultsWidget));
	}

	@Override
	public void setWidgetValue(Object value) {
		Term term = index.getTerm(value);
		widget.setText(term == null ? "" : term.getTermString());
	}

	public class ResultsListener extends SearchListener {

		@Override
		public void termSelected(Term term) {
			change(term.getData());
		}
	}
}
