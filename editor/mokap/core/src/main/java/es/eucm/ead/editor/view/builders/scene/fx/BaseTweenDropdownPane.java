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
package es.eucm.ead.editor.view.builders.scene.fx;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.generic.RemoveFromArray;
import es.eucm.ead.editor.control.actions.model.scene.AddComponent;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.DropdownPane;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.Switch;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.modelwidgets.ModelSelectBox;
import es.eucm.ead.editor.view.widgets.modelwidgets.ModelTextField;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.entities.ModelEntity;

public class BaseTweenDropdownPane extends DropdownPane {

	private Controller controller;

	private I18N i18n;

	private Timeline component;

	private Switch switchWidget;

	private Table dropActor;

	private MultiWidget arrow;

	public BaseTweenDropdownPane(Controller controller, String name, Skin skin) {
		super(skin);
		initialize(controller, name);
	}

	public BaseTweenDropdownPane(Controller controller, String name, Skin skin,
			String background) {
		super(skin, background);
		initialize(controller, name);
	}

	private void initialize(Controller c, String name) {
		this.controller = c;
		i18n = controller.getApplicationAssets().getI18N();

		Array<String> amountArray = new Array<String>();
		amountArray.addAll(i18n.m("little"), i18n.m("middle"), i18n.m("much"));

		Array<String> speedArray = new Array<String>();
		speedArray.addAll(i18n.m("slow"), i18n.m("normal"), i18n.m("fast"));

		Table head = new Table();

		dropActor = new Table();
		dropActor.setTouchable(Touchable.enabled);
		dropActor.add(WidgetBuilder.label(name, SkinConstants.STYLE_EDITION))
				.expandX().width(0).fillX();

		arrow = WidgetBuilder.multiToolbarIcon(WidgetBuilder.icon(
				SkinConstants.IC_ARROW_DOWN, SkinConstants.STYLE_GRAY),
				WidgetBuilder.icon(SkinConstants.IC_ARROW_UP,
						SkinConstants.STYLE_GRAY));

		dropActor.add(arrow);

		head.add(switchWidget = new Switch(skin)).padRight(
				WidgetBuilder.dpToPixels(16));
		head.add(dropActor).expandX().fillX();

		setHead(head, dropActor);
		pack();

		addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (actor == switchWidget) {
					ModelEntity sceneElement = (ModelEntity) controller
							.getModel().getSelection()
							.getSingle(Selection.SCENE_ELEMENT);
					if (switchWidget.isStateOn()) {
						open();
						activateComponent();
						controller.action(AddComponent.class, sceneElement,
								component);
					} else {
						close();
						deactivateComponent();
						controller.action(RemoveFromArray.class, sceneElement,
								sceneElement.getComponents(), component);
					}
				}
			}
		});
	}

	private void activateComponent() {
		dropActor.setTouchable(Touchable.enabled);
		arrow.setVisible(true);
		dropActor.getColor().a = 1f;
	}

	private void deactivateComponent() {
		dropActor.setTouchable(Touchable.disabled);
		arrow.setVisible(false);
		dropActor.getColor().a = 0.5f;
	}

	@Override
	public void open() {
		super.open();
		arrow.setSelectedWidget(1);
	}

	@Override
	public void close() {
		super.close();
		arrow.setSelectedWidget(0);
	}

	public void loadComponent(Timeline timeline, float baseTime,
			float baseAmount, boolean stateOn) {
		this.component = timeline;

		if (stateOn) {
			switchWidget.setStateOn(true);
			activateComponent();
		} else {
			switchWidget.setStateOn(false);
			deactivateComponent();
		}

		close();

		prepareModelWidgets(timeline, body);
	}

	private void prepareModelWidgets(Timeline timeline, Group widget) {
		for (Actor child : widget.getChildren()) {
			if (child instanceof ModelSelectBox) {
				((ModelSelectBox) child).loadComponent(timeline);
			} else if (child instanceof ModelTextField) {
				((ModelTextField) child).loadComponent(timeline,
						timeline.getRepeat());
			} else if (child instanceof Group) {
				prepareModelWidgets(timeline, (Group) child);
			}
		}
	}

}
