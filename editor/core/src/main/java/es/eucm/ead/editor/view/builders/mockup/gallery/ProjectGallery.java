package es.eucm.ead.editor.view.builders.mockup.gallery;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.GridLayout;
import es.eucm.ead.engine.I18N;

public class ProjectGallery extends BaseGallery {

	public static final String NAME = "mockup_project_gallery";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected WidgetGroup centerWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {

		GridLayout galleryTable = new GridLayout();
		galleryTable.pad(2);
		galleryTable.setFillParent(true);

		Table tIn = new Table().debug();
		tIn.pad(2);
		tIn.setFillParent(true);

		// FIXME (Testing GridLayout)
		for (int i = 10; i < 40; i++) {
			galleryTable.addActor(new TextButton("proyecto" + i, skin));
			tIn.add(new TextButton("proyecto " + i, skin));
			tIn.row();
		}
		// END FIXME

		Table window = new Table();
		window.setFillParent(true);

		ScrollPane sp = new ScrollPane(galleryTable);
		sp.setScrollingDisabled(true, false);
		sp.layout();

		return sp;
	}
}
