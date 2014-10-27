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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
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

	private static final float SPACE = .2f;

	private Table table;

	private static int count = 0;

	private static final ClickListener tweenListener = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			PrefabTween listenerActor = (PrefabTween) event.getListenerActor();
			TweensPanel panel = (TweensPanel) listenerActor.getUserObject();
			if (listenerActor.isChecked()) {
				if (count == 0) {
					panel.setUsed(true);
				}
				count++;
			} else {
				count--;
				if (count == 0) {
					panel.setUsed(false);
				}
			}
		}
	};

	public TweensPanel(Controller controller) {
		super("tween80x80", "edition.prefabAnimations", controller);

		table = new Table();
		table.defaults().uniform();

		Rotate360Tween rotate360 = new Rotate360Tween("rotate80x80",
				"edition.rotate360", controller, skin);
		Value space = Value.percentHeight(SPACE, rotate360);
		table.pad(space).defaults().space(space);
		rotate360.addListener(tweenListener);
		rotate360.setUserObject(this);
		table.add(rotate360).fill();

		HorizontalMoveTween horizontal = new HorizontalMoveTween(
				"horizontal_move80x80", "edition.horizontal", controller, skin);
		horizontal.addListener(tweenListener);
		horizontal.setUserObject(this);
		table.add(horizontal).fill();

		VerticalMoveTween vertical = new VerticalMoveTween(
				"vertical_move80x80", "edition.vertical", controller, skin);
		vertical.addListener(tweenListener);
		vertical.setUserObject(this);
		table.add(vertical).fill();

		table.row();

		DecreaseTween decrease = new DecreaseTween("decrease80x80",
				"edition.decrease", controller, skin);
		decrease.addListener(tweenListener);
		decrease.setUserObject(this);
		table.add(decrease).fill();

		IncreaseTween increase = new IncreaseTween("increase80x80",
				"edition.increase", controller, skin);
		increase.addListener(tweenListener);
		increase.setUserObject(this);
		table.add(increase).fill();

		BlinkTween blink = new BlinkTween("blink80x80", "edition.blink",
				controller, skin);
		blink.addListener(tweenListener);
		blink.setUserObject(this);
		table.add(blink).fill();

		panel.add(table);
	}

	@Override
	protected void actualizePanel() {
	}

	@Override
	protected void selectionChanged() {
		ModelEntity modelEntity = (ModelEntity) selection
				.getSingle(Selection.SCENE_ELEMENT);
		setUsed(false);
		count = 0;
		for (Actor actor : table.getChildren()) {
			if (actor instanceof PrefabTween) {
				PrefabTween button = (PrefabTween) actor;
				button.setState(false);
				for (ModelComponent component : modelEntity.getComponents())
					if (component instanceof Tween) {
						String id = component.getId();
						if (id != null && button.getTween().getId().equals(id)) {
							button.setTween((Tween) component);
							button.setState(true);
							if (count == 0) {
								setUsed(true);
							}
							count++;
							break;
						}
					}
			}
		}

	}

	@Override
	protected void trashClicked() {
		for (Actor actor : table.getChildren()) {
			if (actor instanceof PrefabTween) {
				PrefabTween button = (PrefabTween) actor;
				if (button.isChecked()) {
					button.setChecked(false);
				}
			}
		}
		setUsed(false);
	}

}
