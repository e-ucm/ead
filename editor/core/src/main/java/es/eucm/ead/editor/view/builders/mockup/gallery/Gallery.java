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

import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.control.actions.CombinedAction;
import es.eucm.ead.editor.control.actions.DeleteScene;
import es.eucm.ead.editor.control.actions.EditScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.MapEvent;
import es.eucm.ead.editor.view.builders.mockup.camera.Picture;
import es.eucm.ead.editor.view.builders.mockup.camera.Video;
import es.eucm.ead.editor.view.builders.mockup.edition.SceneEdition;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.DescriptionCard;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ElementButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.editor.view.widgets.mockup.buttons.SceneButton;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;

/**
 * This gallery displays both {@link Scene}s and {@link SceneElement}s.
 */
public class Gallery extends BaseGalleryWithNavigation<DescriptionCard> {

	public static final String NAME = "mockup_gallery";

	private static final String ADD_TO_GALLERY_BUTTON = "ic_newproject";
	private static final String IC_PHOTOCAMERA = "ic_photocamera",
			IC_VIDEOCAMERA = "ic_videocamera";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .25F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .12F;

	/**
	 * The element that is being deleted when the user chooses to delete
	 * elements.
	 */
	private DescriptionCard deletingEntity;
	/**
	 * If true next time we show this view the gallery elements will be updated.
	 */
	private boolean needsUpdate;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {
		final Model model = controller.getModel();
		model.addMapListener(model.getScenes(), new ModelListener<MapEvent>() {
			@Override
			public void modelChanged(MapEvent event) {
				Gallery.this.needsUpdate = true;
				if (event.getType() == MapEvent.Type.ENTRY_REMOVED) {
					Gallery.super.onEntityDeleted(Gallery.this.deletingEntity);
				}
			}
		});
		this.needsUpdate = true;
		return super.build(controller);
	}

	@Override
	protected HiddenPanel filterPanel(I18N i18n, Skin skin) {
		final HiddenPanel filterPanel = new HiddenPanel(skin);
		filterPanel.setVisible(false);

		Button applyFilter = new TextButton(
				i18n.m("general.gallery.accept-filter"), skin);
		applyFilter.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				filterPanel.hide();
				return false;
			}
		});

		// FIXME load real tags
		CheckBox[] tags = new CheckBox[] { new CheckBox("Almohada", skin),
				new CheckBox("Camilla", skin), new CheckBox("Doctor", skin),
				new CheckBox("Enfermera", skin), new CheckBox("Guantes", skin),
				new CheckBox("Habitación", skin),
				new CheckBox("Hospital", skin),
				new CheckBox("Quirófano", skin),
				new CheckBox("Medicamentos", skin),
				new CheckBox("Médico", skin), new CheckBox("Paciente", skin),
				new CheckBox("Vehículo", skin) };
		Table tagList = new Table(skin);
		tagList.left();
		tagList.defaults().left();
		for (int i = 0; i < tags.length; ++i) {
			tagList.add(tags[i]);
			if (i < tags.length - 1)
				tagList.row();
		}
		// END FIXME

		ScrollPane tagScroll = new ScrollPane(tagList, skin, "opaque");

		filterPanel.add(tagScroll).fill().colspan(3).left();
		filterPanel.row();
		filterPanel.add(applyFilter).colspan(3).expandX();
		return filterPanel;
	}

	@Override
	protected boolean updateGalleryElements(Controller controller,
			Array<DescriptionCard> elements, Vector2 viewport, I18N i18n,
			Skin skin) {
		elements.clear();
		for (int i = 0; i < 10; i++) {
			DescriptionCard card = null;
			card = new ElementButton(viewport, i18n, null, skin);
			elements.add(card);
		}
		if (this.needsUpdate) {
			this.needsUpdate = false;
			Map<String, Scene> map = controller.getModel().getScenes();
			for (Entry<String, Scene> entry : map.entrySet()) {
				final SceneButton sceneWidget = new SceneButton(viewport, i18n,
						entry.getKey(), entry.getValue(), skin);
				elements.add(sceneWidget);
			}
			return true;
		}
		return false;
	}

	@Override
	protected Button bottomLeftButton(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		MenuButton pictureButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.photo"), skin, IC_PHOTOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT, controller, ChangeView.class, Picture.NAME);
		return pictureButton;
	}

	@Override
	protected Button bottomRightButton(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		MenuButton videoButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.video"), skin, IC_VIDEOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.LEFT, controller, ChangeView.class, Video.NAME);
		return videoButton;
	}

	@Override
	protected Button getFirstPositionActor(Vector2 viewport, I18N i18n,
			Skin skin, Controller controller) {
		final Button addToGalleryButton = new IconButton(viewport, skin,
				ADD_TO_GALLERY_BUTTON);
		return addToGalleryButton;
	}

	@Override
	protected void entityClicked(InputEvent event, DescriptionCard target,
			Controller controller, I18N i18n) {
		if (target instanceof SceneButton) {
			// Start editing the clicked scene...
			controller.action(CombinedAction.class, EditScene.class,
					new Object[] { ((SceneButton) target).getKey() },
					ChangeView.class, new Object[] { SceneEdition.NAME });
		} else if (target instanceof ElementButton) {
			// Start editing the clicked element...
		}
	}

	@Override
	protected void entityDeleted(DescriptionCard entity, Controller controller) {
		if (entity instanceof SceneButton) {
			// Start deleting the clicked scene...
			this.deletingEntity = entity;
			controller.action(DeleteScene.class,
					((SceneButton) entity).getKey());
		} else if (entity instanceof ElementButton) {
			// Start deleting the clicked element...
		}
	}
}
