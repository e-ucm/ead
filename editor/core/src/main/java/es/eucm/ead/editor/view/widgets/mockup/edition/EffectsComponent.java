package es.eucm.ead.editor.view.widgets.mockup.edition;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;

public class EffectsComponent extends EditionComponent {

	private static final String IC_EFFECTS = "ic_effects";

	private static final int PAD_R = 30;

	public EffectsComponent(EditionWindow parent, Controller controller,
			Skin skin) {
		super(parent, controller, skin);

		setModal(false);

		Label label = new Label(i18n.m("edition.tool.effects"), skin,
				"default-thin-opaque");
		label.setWrap(false);
		label.setAlignment(Align.center);
		label.setFontScale(0.7f);

		Table table = new Table(skin);

		// Load the real postprocessor tools
		CheckBox cb1 = new CheckBox("Color diluido", skin);
		CheckBox cb2 = new CheckBox("Pincel seco", skin);
		CheckBox cb3 = new CheckBox("Neón", skin);
		CheckBox cb4 = new CheckBox("Bordes", skin);
		CheckBox cb5 = new CheckBox("Sombreado", skin);
		CheckBox cb6 = new CheckBox("Ondas marinas", skin);
		CheckBox cb7 = new CheckBox("Efecto 7", skin);

		// Load options of postprocessor tools
		Button prop1 = new IconButton(viewport, skin.getDrawable("ic_settings"));
		Button prop2 = new IconButton(viewport, skin.getDrawable("ic_settings"));
		Button prop3 = new IconButton(viewport, skin.getDrawable("ic_settings"));
		Button prop4 = new IconButton(viewport, skin.getDrawable("ic_settings"));
		Button prop5 = new IconButton(viewport, skin.getDrawable("ic_settings"));
		Button prop6 = new IconButton(viewport, skin.getDrawable("ic_settings"));
		Button prop7 = new IconButton(viewport, skin.getDrawable("ic_settings"));

		new ButtonGroup(prop1, prop2, prop3, prop4, prop5, prop6, prop7);

		table.add(cb1).left().expandX();
		table.add(prop1).right().padRight(PAD_R);
		table.row();
		table.add(cb2).left();
		table.add(prop2).right().padRight(PAD_R);
		table.row();
		table.add(cb3).left();
		table.add(prop3).right().padRight(PAD_R);
		table.row();
		table.add(cb4).left();
		table.add(prop4).right().padRight(PAD_R);
		table.row();
		table.add(cb5).left();
		table.add(prop5).right().padRight(PAD_R);
		table.row();
		table.add(cb6).left();
		table.add(prop6).right().padRight(PAD_R);
		table.row();
		table.add(cb7).left();
		table.add(prop7).right().padRight(PAD_R);

		ScrollPane sp = new ScrollPane(table);

		defaults().fill().expand();
		this.add(label);
		this.row();
		this.add(sp);
	}

	@Override
	protected Button createButton(Vector2 viewport, Skin skin, I18N i18n) {
		return new ToolbarButton(viewport, skin.getDrawable(IC_EFFECTS),
				i18n.m("edition.effects"), skin);
	}

}
