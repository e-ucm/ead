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
package es.eucm.ead.editor.view.builders.mockup.gallery;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.RepositoryManager;
import es.eucm.ead.editor.control.RepositoryManager.OnEntityImportedListener;
import es.eucm.ead.editor.control.RepositoryManager.ProgressListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.CombinedAction;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.view.builders.mockup.edition.SceneEdition;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ElementButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * The gallery that will display our online elements. Has a top tool bar and a
 * gallery grid. This class handles the default client implementation.
 */
public class RepositoryGallery extends BaseGallery<ElementButton> implements
		ProgressListener, OnEntityImportedListener {

	public static final String NAME = "mockup_repository_gallery";

	private static final String IC_GO_BACK = "ic_goback";

	private TextButton updateButton;
	private boolean listenerAdded = false;

	private final RepositoryManager repoManager = new RepositoryManager();

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected WidgetGroup centerWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		setSelectable(false);
		if (!listenerAdded) {
			listenerAdded = true;
			controller.getModel().addLoadListener(
					new ModelListener<LoadEvent>() {

						@Override
						public void modelChanged(LoadEvent event) {
							repoManager.setPreviousElements("");
						}

					});
		}
		return super.centerWidget(viewport, i18n, skin, controller);
	}

	@Override
	protected void addActorToHide(Actor actorToHide) {
		// Do nothing because this gallery doesn't have "selecting mode".
	}

	@Override
	protected Button topLeftButton(Vector2 viewport, Skin skin,
			Controller controller) {
		final Button backButton = new ToolbarButton(viewport, skin, IC_GO_BACK);
		backButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, SceneEdition.NAME));
		return backButton;
	}

	@Override
	protected Button getFirstPositionActor(Vector2 viewport, I18N i18n,
			Skin skin, final Controller controller) {
		updateButton = new TextButton(i18n.m("update.repository"), skin);
		updateButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				update(controller);
			}
		});
		return updateButton;
	}

	private void setButtonDisabled(boolean disabled, Button button) {
		Touchable t = disabled ? Touchable.disabled : Touchable.enabled;

		button.setDisabled(disabled);
		button.setTouchable(t);
	}

	@Override
	protected String getTitle(I18N i18n) {
		return i18n.m("general.mockup.repository");
	}

	@Override
	protected boolean updateGalleryElements(Controller controller,
			Array<ElementButton> elements, Vector2 viewport, I18N i18n,
			Skin skin) {
		elements.clear();
		for (ElementButton elem : repoManager.getElements()) {
			elements.add(elem);
		}
		return true;
	}

	@Override
	protected void entityClicked(InputEvent event, ElementButton target,
			Controller controller, I18N i18n) {
		// Start editing the clicked element
		repoManager.importElement(target, controller, this);
	}

	@Override
	public void initialize(Controller controller) {
		update(controller);
	}

	private void update(Controller controller) {
		setButtonDisabled(true, updateButton);
		repoManager.update(controller, this);
	}

	@Override
	public void finished(boolean succeeded, Controller controller) {
		setButtonDisabled(false, updateButton);
		if (!succeeded) {
			// TODO show an user-friendly notification to the user
			// indicating that something went wrong and the update process
			// couldn't be finished.
		} else {
			super.initialize(controller);
		}
	}

	@Override
	public void entityImported(ModelEntity entity, Controller controller) {
		if (entity != null) {
			controller.action(CombinedAction.class, AddSceneElement.class,
					new Object[] { entity }, ChangeView.class,
					new Object[] { SceneEdition.NAME });
		} else {
			// TODO show an user-friendly notification to the user
			// indicating that something went wrong and the import process
			// couldn't be finished.

		}

	}
}