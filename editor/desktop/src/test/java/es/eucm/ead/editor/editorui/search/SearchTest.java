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
package es.eucm.ead.editor.editorui.search;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.actions.editor.ShowContextMenu;
import es.eucm.ead.editor.editorui.EditorUITest;
import es.eucm.ead.editor.indexes.FuzzyIndex;
import es.eucm.ead.editor.indexes.FuzzyIndex.Term;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;

public class SearchTest extends EditorUITest {
	@Override
	protected void builUI(Group root) {
		final Array<Term> found = new Array<Term>();
		final FuzzyIndex fuzzyIndex = new FuzzyIndex();
		for (int i = 0; i < 1000; i += 7) {
			fuzzyIndex.addTerm("item" + i, null);
		}

		Skin skin = controller.getApplicationAssets().getSkin();
		final ContextMenu contextMenu = new ContextMenu(skin);
		final ScrollPane scrollPane = new ScrollPane(contextMenu) {
			@Override
			public float getPrefHeight() {
				return Math.min(200, getWidget().getHeight());
			}
		};
		final TextField textField = new TextField("", skin);
		textField.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				fuzzyIndex.search(textField.getText(), found);
				contextMenu.clear();
				for (Term term : found) {
					contextMenu.item(term.getTermString());
				}
				contextMenu.pack();
				scrollPane.setWidth(contextMenu.getWidth());
				controller.action(ShowContextMenu.class, textField, scrollPane);
				scrollPane.getStage().setScrollFocus(scrollPane);
				return true;
			}
		});
		LinearLayout linearLayout = new LinearLayout(true);
		linearLayout.add(textField).centerX();
		linearLayout.setFillParent(true);
		root.addActor(linearLayout);
	}

	public static void main(String[] args) {
		new LwjglApplication(new SearchTest(), "Scene Editor test", 1000, 600);
	}

}
