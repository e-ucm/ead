package es.eucm.ead.editor.view.builders.mockup.gallery;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.editor.model.Project;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ProjectButton;
import es.eucm.ead.editor.view.widgets.mockup.panels.GalleryGrid;
import es.eucm.ead.engine.I18N;

public class ProjectGallery extends BaseGallery {

	public static final String NAME = "mockup_project_gallery";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected Button topLeftButton(Skin skin) {
		return new TextButton("atras", skin);
	}

	@Override
	protected void addElementsToTheGallery(GalleryGrid<Actor> galleryTable,
			Vector2 viewport, I18N i18n, Skin skin) {
		Project project = new Project();
		for (int i = 0; i < 32; i++) {
			galleryTable.addItem(new ProjectButton(viewport, i18n, project,
					skin));
		}
	}

}
