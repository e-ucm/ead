package es.eucm.ead.editor.view.widgets.mockup.editionComponents;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.editor.view.widgets.mockup.panels.SamplePanel;
import es.eucm.ead.engine.I18N;

public class TextComponent extends EditionComponent{
	
	private String IC_TEXT = "ic_text";
	
	public TextComponent(EditionWindow parent, Vector2 viewport, I18N i18n, Skin skin){
		super(skin, parent);
		
		super.button = new ToolbarButton(viewport, skin.getDrawable(IC_TEXT), i18n.m("edition.text"), skin);
		super.button.addListener(buttonListener());
		
		
		Label label = new Label(i18n.m("edition.tool.text"), skin, "default-thin-opaque");
		label.setWrap(false);
		label.setAlignment(Align.center);
		label.setFontScale(0.7f);
		
		this.add(label).center();
		this.row();
		this.add(new SamplePanel(i18n, skin, 3, true, true));
	}
	
	//TODO add functionality
}
