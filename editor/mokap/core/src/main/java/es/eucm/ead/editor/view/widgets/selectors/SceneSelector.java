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
package es.eucm.ead.editor.view.widgets.selectors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.ScenesGallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.i18n.I18N;

public class SceneSelector extends LinearLayout implements Selector<String>,
		BackListener {

	private SelectorListener<String> selectorListener;

	private SelectScenesGallery sceneGallery;

	public SceneSelector(Controller controller) {
		super(false);
		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18N = controller.getApplicationAssets().getI18N();
		background(skin.getDrawable(SkinConstants.DRAWABLE_BLANK));

		MultiWidget toolbar = new MultiWidget(skin, SkinConstants.STYLE_TOOLBAR);
		LinearLayout buttons = new LinearLayout(true);
		toolbar.addWidgets(buttons);

		Actor cancel = WidgetBuilder.toolbarIcon(SkinConstants.IC_GO,
				i18N.m("cancel"));
		buttons.add(cancel);
		cancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				selectorListener.cancelled();
			}
		});

		buttons.add(new Label(i18N.m("select.scene"), skin,
				SkinConstants.STYLE_TOOLBAR));
		buttons.addSpace();

		add(toolbar).expandX();
		add(sceneGallery = new SelectScenesGallery(2.35f, 3, controller))
				.expand(true, true);
	}

	@Override
	public void prepare(SelectorListener<String> selectorListener,
			Object... args) {
		this.selectorListener = selectorListener;
		sceneGallery.getGallery().uncheckAll();
		sceneGallery.prepare();
		if (args.length > 0 && args[0] != null) {
			Actor actor = findActor((String) args[0]);
			if (actor instanceof Cell) {
				((Cell) actor).checked(true);
			}
		}
	}

	public class SelectScenesGallery extends ScenesGallery {

		public SelectScenesGallery(float rows, int columns,
				Controller controller) {
			super(rows, columns, controller);
		}

		@Override
		public Cell addTile(Object id, String title, String thumbnailPath) {
			Cell cell = super.addTile(id, title, thumbnailPath);
			cell.setName(id.toString());
			return cell;
		}

		@Override
		protected void prepareActionButton(Actor actor) {
			actor.remove();
		}

		@Override
		protected void prepareGalleryItem(Actor actor, final Object id) {
			actor.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					gallery.uncheckAll();
					Cell cell = (Cell) event.getListenerActor().getParent();
					cell.checked(true);
					selectorListener.selected(id.toString());
				}
			});
		}
	}

	@Override
	public boolean onBackPressed() {
		selectorListener.cancelled();
		return true;

	}
}
