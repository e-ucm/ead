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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Toasts;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.asynk.ExportMockupProject;
import es.eucm.ead.editor.control.actions.model.ChangeInitialScene;
import es.eucm.ead.editor.control.actions.model.CloneScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.builders.gallery.CreditsView;
import es.eucm.ead.editor.view.widgets.IconTextButton;
import es.eucm.ead.editor.view.widgets.SendEmailPane;
import es.eucm.ead.editor.view.widgets.IconTextButton.Position;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithScalePanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * A widget that displays some edition options for a specific scene.
 */
public class OthersWidget extends IconWithScalePanel implements FieldListener {

	private static final float BASE_PAD = 10, SIDE_PAD = 20;
	private Controller controller;

	private Skin skin;

	private I18N i18n;

	private IconTextButton makeInitial;
	private IconTextButton cloneScene;
	private IconTextButton share;
	private IconTextButton about;
	private IconTextButton contact;

	private SendEmailPane emailPane;

	public OthersWidget(Controller controlle, Actor root) {
		super("others80x80", 5f, 1, controlle.getApplicationAssets().getSkin());
		this.controller = controlle;

		Assets assets = controller.getApplicationAssets();
		skin = assets.getSkin();
		i18n = assets.getI18N();

		makeInitial = new IconTextButton(i18n.m("scene.makeInitial"), skin,
				skin.getDrawable("clone80x80"), Position.RIGHT);
		makeInitial.setPadding(BASE_PAD, SIDE_PAD * 2, BASE_PAD, SIDE_PAD,
				BASE_PAD);
		cloneScene = new IconTextButton(i18n.m("edition.cloneScene"), skin,
				skin.getDrawable("clone80x80"), Position.RIGHT);
		cloneScene.setPadding(BASE_PAD, SIDE_PAD * 2, BASE_PAD, SIDE_PAD,
				BASE_PAD);
		share = new IconTextButton(i18n.m("send.share"), skin,
				skin.getDrawable("share80x80"), Position.RIGHT);
		share.setPadding(BASE_PAD, SIDE_PAD * 2, BASE_PAD, SIDE_PAD, BASE_PAD);
		about = new IconTextButton(i18n.m("about.credits"), skin,
				skin.getDrawable("share80x80"), Position.RIGHT);
		about.setPadding(BASE_PAD, SIDE_PAD * 2, BASE_PAD, SIDE_PAD, BASE_PAD);

		emailPane = new SendEmailPane(controller, skin, root);

		final Toasts toasts = ((MockupViews) controller.getViews()).getToasts();
		ChangeListener buttonClicked = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				hidePanel();
				if (actor == makeInitial) {
					Object object = controller.getModel().getSelection()
							.getSingle(Selection.SCENE);
					if (object instanceof ModelEntity) {
						ModelEntity scene = (ModelEntity) object;
						Model model = controller.getModel();
						String sceneId = model.getIdFor(scene);
						controller.action(ChangeInitialScene.class, sceneId);
					}
				} else if (actor == cloneScene) {
					controller.action(CloneScene.class);
					toasts.showNotification(controller.getApplicationAssets()
							.getI18N().m("edition.sceneCloned"), 2.5f);
				} else if (actor == share) {
					controller.action(ExportMockupProject.class);
				} else if (actor == about) {
					controller
							.action(ChangeMockupView.class, CreditsView.class);
				} else if (actor == contact) {
					emailPane.show();
				}
			}
		};

		makeInitial.addListener(buttonClicked);
		cloneScene.addListener(buttonClicked);
		share.addListener(buttonClicked);
		about.addListener(buttonClicked);

		panel.add(makeInitial).expandX().fill();
		panel.add(cloneScene).expandX().fill();
		panel.add(share).expandX().fill();
		panel.add(about).expandX().fill();

		Model model = controller.getModel();
		model.addSelectionListener(new SelectionListener() {
			@Override
			public boolean listenToContext(String contextId) {
				return Selection.SCENE.equals(contextId);
			}

			@Override
			public void modelChanged(SelectionEvent event) {
				if (event.getType() != SelectionEvent.Type.REMOVED) {
					Object object = event.getSelection()[0];
					if (object instanceof ModelEntity) {
						updateInitialSceneButton((ModelEntity) object);
					}
				}
			}
		});

		model.addLoadListener(new ModelListener<LoadEvent>() {

			@Override
			public void modelChanged(LoadEvent event) {
				if (event.getType() == LoadEvent.Type.LOADED) {
					prepareInitialSceneListener();
				}
			}
		});
		prepareInitialSceneListener();
	}

	public void prepareInitialSceneListener() {
		Model model = controller.getModel();
		model.removeListenerFromAllTargets(this);
		ModelEntity game = model.getGame();
		GameData gameData = Q.getComponent(game, GameData.class);
		model.addFieldListener(gameData, this);
	}

	private boolean isInitial(ModelEntity entity) {
		Model model = controller.getModel();
		String sceneId = model.getIdFor(entity);
		String initialScene = Q.getComponent(model.getGame(), GameData.class)
				.getInitialScene();
		return initialScene != null && initialScene.equals(sceneId);
	}

	@Override
	public void modelChanged(FieldEvent event) {
		Object sel = controller.getModel().getSelection()
				.getSingle(Selection.SCENE);
		if (sel instanceof ModelEntity) {
			ModelEntity scene = (ModelEntity) sel;
			if (!panel.hasParent()) {
				showPanel();
			}
			updateInitialSceneButton(scene);
		}

	}

	private void updateInitialSceneButton(ModelEntity scene) {
		if (isInitial(scene)) {
			makeInitial.setDisabled(true);
			makeInitial.setTouchable(Touchable.disabled);
			makeInitial.changeText(i18n.m("scene.isInitial"));
		} else {
			makeInitial.setDisabled(false);
			makeInitial.setTouchable(Touchable.enabled);
			makeInitial.changeText(i18n.m("scene.makeInitial"));
		}
	}

	@Override
	public boolean listenToField(String fieldName) {
		return FieldName.INITIAL_SCENE.equals(fieldName);
	}

}
