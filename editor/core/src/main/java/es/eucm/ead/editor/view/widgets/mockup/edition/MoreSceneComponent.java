package es.eucm.ead.editor.view.widgets.mockup.edition;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;

public class MoreSceneComponent extends EditionComponent {

	private static final String IC_MORE = "ic_more",
			IC_CLONE = "ic_duplicate_scene";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .30F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .18F;

	public MoreSceneComponent(EditionWindow parent, Controller controller,
			Skin skin) {
		super(parent, controller, skin);

		// Load the name and description
		TextField name = new TextField("nombre", skin);
		TextArea description = new TextArea("descripcion", skin);

		Label tags = new Label("TAGS", skin, "default-thin-opaque");
		tags.setWrap(false);
		tags.setAlignment(Align.center);
		tags.setFontScale(0.7f);

		MenuButton cloneButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.clone"), skin, IC_CLONE,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);

		this.add(name).fillX().expandX();
		this.row();
		this.add(description).fill().expand().center();
		this.row();
		this.add(tags).bottom().fillX().expandX();
		this.row();
		this.add(cloneButton);

	}

	@Override
	protected Button createButton(Vector2 viewport, Skin skin, I18N i18n) {
		return new ToolbarButton(viewport, skin.getDrawable(IC_MORE),
				i18n.m("edition.more"), skin);
	}
}
