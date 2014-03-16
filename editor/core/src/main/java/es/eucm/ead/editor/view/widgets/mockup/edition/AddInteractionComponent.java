package es.eucm.ead.editor.view.widgets.mockup.edition;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.engine.I18N;

public class AddInteractionComponent extends EditionComponent {

	private static final String IC_INTERACT = "ic_interactivezone",
			IC_FINGER = "ic_finger", IC_REC = "ic_rectangle",
			IC_POLYGON = "ic_polygon";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .25F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .18F;

	public AddInteractionComponent(EditionWindow parent, Controller controller,
			Skin skin) {
		super(parent, controller, skin);

		Label label = new Label(i18n.m("edition.tool.add-interaction"), skin,
				"default-thin-opaque");
		label.setWrap(false);
		label.setAlignment(Align.center);
		label.setFontScale(0.7f);

		MenuButton fingerButton = new BottomProjectMenuButton(viewport,
				i18n.m("edition.tool.tactile"), skin, IC_FINGER,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		MenuButton rectangleButton = new BottomProjectMenuButton(viewport,
				i18n.m("edition.tool.rectangular"), skin, IC_REC,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		MenuButton poligButton = new BottomProjectMenuButton(viewport,
				i18n.m("edition.tool.polygonal"), skin, IC_POLYGON,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);

		this.add(label).fillX().expandX();
		this.row();
		this.add(fingerButton).fillX().expandX();
		this.row();
		this.add(rectangleButton).fillX().expandX();
		this.row();
		this.add(poligButton).fillX().expandX();

	}

	@Override
	protected Button createButton(Vector2 viewport, Skin skin, I18N i18n) {
		return new ToolbarButton(viewport, skin.getDrawable(IC_INTERACT),
				i18n.m("edition.area"), skin);
	}

}
