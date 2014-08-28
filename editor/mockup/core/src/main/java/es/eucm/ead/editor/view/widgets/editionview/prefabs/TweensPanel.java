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
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
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
				button.setState(false);
				for (ModelComponent component : modelEntity.getComponents())
					if (component instanceof Tween) {
						if (isTheSameTween(button.getTween(), (Tween) component)) {
							button.setTween((Tween) component);
							button.setState(true);
							break;
						}
					}
			}
		}

		super.showPanel();
	}

	private boolean isTheSameTween(Tween tween1, Tween tween2) {
		if (tween1.getClass() == tween2.getClass()
				&& tween1.getDuration() == tween1.getDuration()
				&& tween1.getRepeat() == tween2.getRepeat()
				&& tween1.isYoyo() == tween2.isYoyo()
				&& tween1.isRelative() == tween2.isRelative()
				&& tween1.getDelay() == tween2.getDelay()) {
			if (tween1 instanceof AlphaTween) {
				if (((AlphaTween) tween1).getAlpha() != ((AlphaTween) tween2)
						.getAlpha()) {
					return false;
				}
			} else if (tween1 instanceof MoveTween) {
				MoveTween aux1 = (MoveTween) tween1;
				MoveTween aux2 = (MoveTween) tween2;
				if (Float.compare(aux1.getX(), aux2.getX()) != 0
						|| Float.compare(aux1.getY(), aux2.getY()) != 0) {
					return false;
				}
			} else if (tween1 instanceof ScaleTween) {
				ScaleTween aux1 = (ScaleTween) tween1;
				ScaleTween aux2 = (ScaleTween) tween2;
				if (aux1.getScaleX() != aux2.getScaleX()
						|| aux1.getScaleY() != aux2.getScaleY()) {
					return false;
				}
			} else if (tween1 instanceof RotateTween) {
				if (((RotateTween) tween1).getRotation() != ((RotateTween) tween2)
						.getRotation()) {
					return false;
				}
			}
			return true;
		}
		return false;
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
