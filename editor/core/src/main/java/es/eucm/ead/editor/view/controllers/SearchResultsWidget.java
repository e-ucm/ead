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
package es.eucm.ead.editor.view.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.indexes.FuzzyIndex;
import es.eucm.ead.editor.indexes.FuzzyIndex.Term;
import es.eucm.ead.editor.view.controllers.SearchResultsWidget.SearchEvent.Type;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;

/**
 * A widget with a text field to perform searches, with a menu with the results
 * of the search.
 */
public class SearchResultsWidget extends LinearLayout {

	public static final float RESULTS_HEIGHT = 200.0f;

	private FuzzyIndex fuzzyIndex;

	private ContextMenu contextMenu;

	private ScrollPane scrollPane;

	private Array<Term> found;

	public SearchResultsWidget(FuzzyIndex index, Skin skin) {
		super(false);
		final TextField searchField = new TextField("", skin);
		addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor,
					boolean focused) {
				if (focused) {
					event.getStage().setKeyboardFocus(searchField);
				}
			}

			@Override
			public void scrollFocusChanged(FocusEvent event, Actor actor,
					boolean focused) {
				if (focused) {
					event.getStage().setScrollFocus(scrollPane);
				}
			}
		});
		searchField.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				fuzzyIndex.search(searchField.getText(), found);

				contextMenu.clearChildren();

				for (Term term : found) {
					contextMenu.item(term.getTermString()).setUserObject(term);
				}
				contextMenu.pack();
				scrollPane.setWidth(contextMenu.getWidth());
				return true;
			}
		});
		add(searchField).expandX();

		contextMenu = new ContextMenu(skin);
		contextMenu.addCaptureListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				fireTermSelected((Term) event.getTarget().getUserObject());
				return true;
			}
		});

		scrollPane = new ScrollPane(contextMenu) {
			@Override
			public float getPrefHeight() {
				return Math.min(RESULTS_HEIGHT, getWidget().getHeight());
			}
		};

		add(scrollPane).expandX();
		fuzzyIndex = index;
		found = new Array<Term>();
	}

	private void fireTermSelected(Term term) {
		SearchEvent searchEvent = Pools.obtain(SearchEvent.class);
		searchEvent.setType(Type.termSelected);
		searchEvent.setTerm(term);
		fire(searchEvent);
		Pools.free(searchEvent);
	}

	public static class SearchListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof SearchEvent) {
				SearchEvent searchEvent = (SearchEvent) event;
				switch (searchEvent.getType()) {
				case termSelected:
					termSelected(searchEvent.getTerm());
					break;
				}
			}
			return false;
		}

		/**
		 * A term has been selected
		 */
		public void termSelected(Term term) {
		}
	}

	public static class SearchEvent extends Event {

		public enum Type {
			termSelected
		}

		private Type type;

		private Term term;

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public Term getTerm() {
			return term;
		}

		public void setTerm(Term term) {
			this.term = term;
		}
	}
}
