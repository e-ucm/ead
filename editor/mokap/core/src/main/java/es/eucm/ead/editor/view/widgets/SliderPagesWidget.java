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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

public class SliderPagesWidget extends Table {

	private ScrollPane scroll;

	private boolean wasPanDragFling;

	private int numPages;

	private Array<Actor> iconPages;

	public SliderPagesWidget(String background, Skin skin, Actor... actors) {
		super(skin);
		setFillParent(true);
		setBackground(skin.getDrawable(background));
		wasPanDragFling = false;
		numPages = actors.length;

		LinearLayout linearCircles = new LinearLayout(true);
		LinearLayout linearPages = new LinearLayout(true);

		scroll = new ScrollPane(linearPages) {

			@Override
			public void act(float delta) {
				super.act(delta);
				if (isPanning() || isDragging() || isFlinging()) {
					SliderPagesWidget.this.actualizePagesIcon();
				}
				if (wasPanDragFling && !isPanning() && !isDragging()
						&& !isFlinging()) {
					scrollToPage();
				} else if (!wasPanDragFling && isPanning() || isDragging()
						|| isFlinging()) {
					wasPanDragFling = true;
				}
			}
		};
		scroll.setOverscroll(false, false);

		iconPages = linearCircles.getChildren();

		ClickListener listener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor actor = event.getListenerActor();
				int index = iconPages.indexOf(actor, true);
				scrollToPage(index);
			}
		};

		for (Actor page : actors) {
			linearPages.add(page);

			IconButton pageCircle = new IconButton(SkinConstants.IC_CIRCLE,
					skin, SkinConstants.STYLE_SLIDER_PAGES);
			pageCircle.addListener(listener);
			linearCircles.add(pageCircle);
		}

		add(scroll).expand().fill();
		row();
		add(linearCircles);

		scrollToPage(0);
	}

	public void scrollToPage() {
		float diff = scroll.getScrollX() % getWidth();
		int index = (int) Math.floor(scroll.getScrollX() / getWidth())
				+ (diff > getWidth() / 2 ? 1 : 0);
		scrollToPage(index);

	}

	private void actualizePagesIcon() {
		float diff = scroll.getScrollX() % getWidth();
		int index = (int) Math.floor(scroll.getScrollX() / getWidth())
				+ (diff > getWidth() / 2 ? 1 : 0);
		for (Actor page : iconPages) {
			((IconButton) page).setDisabled(false);
		}
		((IconButton) iconPages.get(index)).setDisabled(true);
	}

	public void scrollToPage(int index) {
		if (index < 0) {
			index = 0;
		} else if (index >= numPages) {
			index = numPages - 1;
		}
		wasPanDragFling = false;
		scroll.scrollTo(index * getWidth(), scroll.getScrollY(),
				scroll.getWidth(), scroll.getHeight());
		actualizePagesIcon();
	}
}
