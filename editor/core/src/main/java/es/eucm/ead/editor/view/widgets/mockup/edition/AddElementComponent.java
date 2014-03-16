package es.eucm.ead.editor.view.widgets.mockup.edition;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;

public class AddElementComponent extends EditionComponent{

	private static final String IC_ADD = "tree_plus";
			
	public AddElementComponent(EditionWindow parent, Controller controller,
			Skin skin) {
		super(parent, controller, skin);
				
		this.add(new TextButton(i18n.m("edition.tool.add-recent-element"), skin)).fillX().expandX();
		this.row();
		this.add(new TextButton(i18n.m("edition.tool.add-photo-element"), skin)).fillX().expandX();
		this.row();
		this.add(new TextButton(i18n.m("edition.tool.add-gallery-element"), skin)).fillX().expandX();
	}

	@Override
	protected Button createButton(Vector2 viewport, Skin skin, I18N i18n) {
		return new ToolbarButton(viewport, skin.getDrawable(IC_ADD), i18n.m("edition.add"), skin);
	}

}
