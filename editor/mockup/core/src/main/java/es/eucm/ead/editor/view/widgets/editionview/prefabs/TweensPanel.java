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
package es.eucm.ead.editor.view.widgets.editionview.prefabs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.prefabtweens.BlinkTween;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.prefabtweens.DecreaseTween;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.prefabtweens.HorizontalMoveTween;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.prefabtweens.IncreaseTween;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.prefabtweens.PrefabTween;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.prefabtweens.Rotate360Tween;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.prefabtweens.VerticalMoveTween;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.entities.ModelEntity;

public class TweensPanel extends PrefabPanel {

	private static final float SPACE = 20;

	private Table table;

	public TweensPanel(String icon, float size, Controller controller,
			Actor touchable) {
		super(icon, size, "edition.prefabAnimations", controller, touchable);

		table = new Table();
		table.defaults().uniform();

		table.add(
				new Rotate360Tween("tween80x80", i18n.m("edition.rotate360"),
						controller, skin)).pad(SPACE).fill();
		table.add(
				new HorizontalMoveTween("tween80x80", i18n
						.m("edition.horizontal"), controller, skin)).pad(SPACE)
				.fill();
		table.add(
				new VerticalMoveTween("tween80x80", i18n.m("edition.vertical"),
						controller, skin)).pad(SPACE).fill();
		table.row();
		table.add(
				new DecreaseTween("tween80x80", i18n.m("edition.decrease"),
						controller, skin)).pad(SPACE).fill();
		table.add(
				new IncreaseTween("tween80x80", i18n.m("edition.increase"),
						controller, skin)).pad(SPACE).fill();
		table.add(
				new BlinkTween("tween80x80", i18n.m("edition.blink"),
						controller, skin)).pad(SPACE).fill();

		panel.add(table);
	}

	@Override
	protected void showPanel() {

		ModelEntity modelEntity = (ModelEntity) selection
				.getSingle(Selection.SCENE_ELEMENT);
		for (Actor actor : table.getChildren()) {
			if (actor instanceof PrefabTween) {
				PrefabTween button = (PrefabTween) actor;
				for (ModelComponent component : modelEntity.getComponents())
					if (component instanceof Tween) {
						if (button.getTween() == (Tween) component) {
							button.setState(true);
							break;
						} else {
							button.setState(false);
						}
					}
			}
		}

		super.showPanel();
	}

	@Override
	protected InputListener trashListener() {
		return new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				for (Actor actor : table.getChildren()) {
					if (actor instanceof PrefabTween) {
						PrefabTween button = (PrefabTween) actor;
						if (button.isChecked()) {
							button.setChecked(false);
						}
					}
				}
			}
		};
	}

}
