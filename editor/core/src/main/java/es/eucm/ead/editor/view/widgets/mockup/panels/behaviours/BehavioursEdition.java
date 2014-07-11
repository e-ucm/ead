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
package es.eucm.ead.editor.view.widgets.mockup.panels.behaviours;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BehaviorButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.FlagButton;
import es.eucm.ead.editor.view.widgets.mockup.edition.ConditionWidget;
import es.eucm.ead.editor.view.widgets.mockup.edition.FlagPanel;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.editor.components.Variables;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.EndGame;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.effects.GoTo;
import es.eucm.ead.schema.effects.RemoveEntity;
import es.eucm.ead.schema.effects.controlstructures.If;

/**
 * Panel to edit behavior properties
 * 
 */
public class BehavioursEdition extends HiddenPanel {

	private static final float DEFAULT_PAD = 10f;
	private static final float PREF_HEIGHT = 0.95f;

	private static final float PREF_WIDTH = 0.95f;

	private static final float LITTLE_MARGIN = 20f;
	private static final float BIG_MARGIN = 270f;

	private String SELECT, CHANGE_VAR, END_GAME, GO_SCENE, GO_TO,
			REMOVE_ENTITY;

	private BehaviorButton current;

	private SelectBox<String> effect;

	private Label title;

	private Vector2 viewport;

	private Container scroll;

	private TextField repeatsText;
	private Label repeatsLabel;

	private TextField timeText;
	private Label timeLabel;

	private EffectBehaviourPanel changeVar;

	private EffectBehaviourPanel endGame;

	private GoScenePanel goScene;

	private EffectBehaviourPanel goTo;

	private EffectBehaviourPanel removeEntity;

	private Label nothing;

	private LinearLayout time;
	private LinearLayout repeats;

	private Table conditionsGroup;

	private Skin skin;

	private I18N i18n;

	private FlagPanel flagPanel;

	Array<VariableDef> variableDefList;

	public BehavioursEdition(final Skin skin, Controller controller,
			final Vector2 viewport, final FlagPanel flagPanel) {
		super(skin);

		this.i18n = controller.getApplicationAssets().getI18N();
		this.flagPanel = flagPanel;
		this.skin = skin;

		this.CHANGE_VAR = i18n.m("general.effects.change-var");
		this.GO_TO = i18n.m("general.effects.go-to");
		this.GO_SCENE = i18n.m("general.effects.go-scene");
		this.END_GAME = i18n.m("general.effects.end-game");
		this.REMOVE_ENTITY = i18n.m("general.effects.remove-entity");
		this.SELECT = i18n.m("general.effect.select");

		this.variableDefList = Q.getComponent(controller.getModel().getGame(),
				Variables.class).getVariablesDefinitions();

		this.setVisible(false);

		this.changeVar = new ChangeVarPanel(skin, i18n, flagPanel);
		this.goTo = new GoToPanel(skin, i18n);
		this.goScene = new GoScenePanel(controller);
		this.endGame = new EndGamePanel(skin, i18n);
		this.removeEntity = new RemoveEntityPanel(skin, i18n);

		this.viewport = viewport;

		this.nothing = new Label(SELECT, skin);

		this.title = new Label(i18n.m("general.edition.behavior"), skin);
		this.row();

		final String[] effects = { SELECT, CHANGE_VAR, END_GAME, GO_SCENE,
				GO_TO, REMOVE_ENTITY };

		this.effect = new SelectBox<String>(skin) {
			@Override
			public void hideList() {
				super.hideList();
				changeTable(effect.getSelected(), null);
			}
		};
		this.effect.setItems(effects);

		this.scroll = new Container(nothing);

		Table left = new Table().debug();
		Table right = new Table().debug();

		Table main = new Table();

		main.add(left).fill().expand().left();
		main.add(right).fill().expand().right();

		this.conditionsGroup = new Table();
		ScrollPane conditions = new ScrollPane(conditionsGroup);
		TextButton addButton = new TextButton(i18n.m("edition.add"), skin);

		left.add(new Label(i18n.m("general.edition.behavior.condition"), skin));
		left.row();
		left.add(addButton);
		left.row();
		left.add(conditions).expandX();

		right.add(effect).top();
		right.row();
		right.add(scroll).expand().fill();
		right.row();

		this.timeLabel = new Label(i18n.m("general.time") + ": ", skin);
		this.timeText = new TextField("", skin);

		this.repeatsLabel = new Label(i18n.m("general.repeats") + ": ", skin);
		this.repeatsText = new TextField("", skin);

		time = new LinearLayout(true);
		time.add(timeLabel);
		time.add(timeText);
		right.add(time).expandX();
		right.row();

		repeats = new LinearLayout(true);
		repeats.add(repeatsLabel);
		repeats.add(repeatsText);
		right.add(repeats).expandX();

		pad(DEFAULT_PAD);
		defaults().space(DEFAULT_PAD);
		this.add(title).top();
		this.row();
		this.add(main).expand().fill().center();
		this.row();

		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				conditionsGroup
						.add(new ConditionWidget(viewport, i18n, flagPanel,
								skin)).expandX().fill();
			}
		});

		Button accept = new TextButton(i18n.m("general.accept"), skin);
		Button back = new TextButton(i18n.m("general.gallery.back"), skin);

		LinearLayout bottom = new LinearLayout(true);
		bottom.add(accept)
				.margin(LITTLE_MARGIN, LITTLE_MARGIN, BIG_MARGIN, LITTLE_MARGIN)
				.expandX();
		bottom.add(back)
				.margin(BIG_MARGIN, LITTLE_MARGIN, BIG_MARGIN, LITTLE_MARGIN)
				.expandX();

		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				BehavioursEdition.this.hide();
			}
		});

		accept.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				boolean isOk = false;
				if (scroll.getWidget() instanceof EffectBehaviourPanel) {
					setBehaviourCondition();
					isOk = ((EffectBehaviourPanel) scroll.getWidget())
							.actBehaviour(current.getBehaviour());
					if (current.getBehaviour().getEvent() instanceof Timer) {
						((Timer) current.getBehaviour().getEvent())
								.setTime(Float.valueOf(timeText.getText()));
						((Timer) current.getBehaviour().getEvent())
								.setRepeat(Integer.valueOf(repeatsText
										.getText()));
					}
				}
				if (isOk) {
					current.setEffectDesc(effect.getSelected());
					BehavioursEdition.this.hide();
				}
			}
		});
		this.add(bottom);
	}

	public void show(BehaviorButton button) {
		this.current = button;
		Effect effect = getEffect();

		// Set the selected item of SelectBox
		if (effect instanceof ChangeVar) {
			this.effect.setSelected(CHANGE_VAR);
			changeTable(CHANGE_VAR, effect);
		} else if (effect instanceof EndGame) {
			this.effect.setSelected(END_GAME);
			changeTable(END_GAME, effect);
		} else if (effect instanceof GoScene) {
			this.effect.setSelected(GO_SCENE);
			changeTable(GO_SCENE, effect);
		} else if (effect instanceof GoTo) {
			this.effect.setSelected(GO_TO);
			changeTable(GO_TO, effect);
		} else if (effect instanceof RemoveEntity) {
			this.effect.setSelected(REMOVE_ENTITY);
			changeTable(REMOVE_ENTITY, effect);
		} else {
			changeTable(SELECT, effect);
			this.effect.setSelected(SELECT);
		}

		if (this.current.getBehaviour().getEvent() instanceof Timer) {
			this.time.setVisible(true);
			this.repeats.setVisible(true);

			this.timeText.setText(""
					+ ((Timer) this.current.getBehaviour().getEvent())
							.getTime());
			this.repeatsText.setText(""
					+ ((Timer) this.current.getBehaviour().getEvent())
							.getRepeat());
		} else {
			this.time.setVisible(false);
			this.repeats.setVisible(false);
		}

		// Load the boolean variables
		conditionsGroup.clear();
		String condition = null;
		Effect currentEffect = this.current.getBehaviour().getEffects().get(0);
		if (currentEffect instanceof If) {
			condition = ((If) currentEffect).getCondition();
		}
		if (condition != null) {
			String[] varList = condition.split("\\$");
			for (int i = 1; i < varList.length; i++) {
				String[] var = varList[i].split(" ");
				for (VariableDef fl : this.variableDefList) {
					if (fl.getName().equals(var[0])) {
						FlagButton fButton = new FlagButton(fl, skin);
						ConditionWidget newCondition = new ConditionWidget(
								viewport, i18n, flagPanel, skin, fButton);
						newCondition.setStateSelected(var[1]);
						conditionsGroup.add(newCondition).expandX().fill();
						conditionsGroup.row();
						break;
					}
				}
			}
		}
		show();
	}

	@Override
	public float getPrefWidth() {
		return this.viewport.x * PREF_WIDTH;
	}

	@Override
	public float getPrefHeight() {
		return this.viewport.y * PREF_HEIGHT;
	}

	/**
	 * Change the the properties to edit according the effect selected
	 * 
	 * @param item
	 * @param effect
	 */
	private void changeTable(String item, Effect effect) {
		this.scroll.setWidget(null);
		if (item.equalsIgnoreCase(CHANGE_VAR)) {
			if (effect != null) {
				for (VariableDef var : variableDefList) {
					if (var.getName()
							.equals(((ChangeVar) effect).getVariable())) {
						((ChangeVarPanel) this.changeVar).actPanel(var,
								((ChangeVar) effect).getExpression());
						break;
					}
				}
			}
			this.scroll.setWidget(this.changeVar);
		} else if (item.equalsIgnoreCase(END_GAME)) {
			this.scroll.setWidget(this.endGame);
		} else if (item.equalsIgnoreCase(GO_SCENE)) {
			if (effect != null) {
				((GoScenePanel) this.goScene).actPanel(((GoScene) effect)
						.getName());
			}
			this.scroll.setWidget(this.goScene);
		} else if (item.equalsIgnoreCase(GO_TO)) {
			if (effect != null) {
				((GoToPanel) this.goTo).actPanel(((GoTo) effect).getX(),
						((GoTo) effect).getY());
			}
			this.scroll.setWidget(this.goTo);
		} else if (item.equalsIgnoreCase(REMOVE_ENTITY)) {
			this.scroll.setWidget(this.removeEntity);
		} else {
			this.scroll.setWidget(this.nothing);
		}
	}

	private void setBehaviourCondition() {
		String condition = "";

		SnapshotArray<Actor> conditionsWidget = this.conditionsGroup
				.getChildren();
		if (conditionsWidget.size == 0) {
			condition = "btrue";
		} else {
			for (Actor actor : conditionsWidget) {
				if (((ConditionWidget) actor).getVariableDef() != null) {
					if (condition == "") {
						condition = "( eq $"
								+ ((ConditionWidget) actor).getVariableDef()
										.getName()
								+ " "
								+ ((ConditionWidget) actor)
										.getBooleanSelected() + " )";
					} else {
						condition = "( and "
								+ condition
								+ " ( eq $"
								+ ((ConditionWidget) actor).getVariableDef()
										.getName()
								+ " "
								+ ((ConditionWidget) actor)
										.getBooleanSelected() + " ) )";
					}
				}
			}
		}

		Effect effect = current.getBehaviour().getEffects().removeIndex(0);
		if (effect instanceof If) {
			((If) effect).setCondition(condition);
		} else {
			If ifEffect = new If();
			ifEffect.setCondition(condition);
			current.getBehaviour().getEffects().add(ifEffect);
		}
	}

	private Effect getEffect() {
		Behavior behavior = current.getBehaviour();
		if (behavior.getEffects().size == 0) {
			Effect newF = new Effect();
			behavior.getEffects().add(newF);
		}
		return behavior.getEffects().get(0);
	}

	public void initialize(Controller controller) {
		this.goScene.refresh();
	}
}
