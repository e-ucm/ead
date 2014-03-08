package es.eucm.ead.editor.view.widgets.mockup.EditionComponents;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;

public class PaintComponent extends EditionComponent{
	
	private String IC_PAINT = "ic_pencil";
	
	public PaintComponent(EditionWindow parent, Vector2 viewport, I18N i18n, Skin skin){
		super(skin, parent);
		
		super.button = new ToolbarButton(viewport, skin.getDrawable(IC_PAINT), "Pintar", skin); //Use i18n and ToolbarButton
		super.button.addListener(buttonListener());
		
		
		Label label = new Label("Herramienta de pincel", skin, "default-thin-opaque"); //use i28n
		label.setWrap(false);
		label.setAlignment(Align.center);
		label.setFontScale(0.7f);
		
		this.add(label).center();
	}
}
