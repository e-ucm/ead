package es.eucm.ead.editor.view.builders.mockup.edition;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.editionComponents.EditionComponent;

public class SceneEdition extends EditionWindow{

	public static final String NAME = "mockup_scene_edition";

	@Override
	public String getName() {
		return NAME;
	}
	/**
	 * Add the EditionComponents that are not shared with ElementEdition
	 * */
	@Override
	protected Array<EditionComponent> editionComponents(Vector2 viewport, Controller controller){
		Array<EditionComponent> notShared = super.editionComponents(viewport, controller);
		//Add components
		return notShared;
	}
}
