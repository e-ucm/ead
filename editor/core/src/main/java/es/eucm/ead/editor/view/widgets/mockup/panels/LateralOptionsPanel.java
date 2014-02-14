package es.eucm.ead.editor.view.widgets.mockup.panels;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.I18N;

public class LateralOptionsPanel extends LateralPanel{
	
	public LateralOptionsPanel(Controller controller, Skin skin) {
		super(controller, skin, "default");

		setVisible(false);
		
		I18N i18n = controller.getEditorAssets().getI18N();

		Label skins = new Label(i18n.m("general.mockup.skins"), skin);
		String skinStyle = "default-radio", lineString = "- - - - - - - - - - - - -";
		CheckBox skinDefault = new CheckBox(i18n.m("general.mockup.skins.default"), skin, skinStyle);
		skinDefault.setChecked(true);
		CheckBox skinN2 = new CheckBox(i18n.m("general.mockup.skins.funny"), skin, skinStyle);
		CheckBox skinN3 = new CheckBox(i18n.m("general.mockup.skins.pro"), skin, skinStyle);
		Label line = new Label(lineString, skin);
		Label lenguajes = new Label(i18n.m("general.mockup.language"), skin);
		CheckBox spanish = new CheckBox(i18n.m("general.mockup.language.es"), skin, skinStyle);
		spanish.setChecked(true);
		CheckBox english = new CheckBox(i18n.m("general.mockup.language.en"), skin, skinStyle);

		new ButtonGroup(skinDefault, skinN2, skinN3);
		new ButtonGroup(spanish, english);

		Table t = new Table(skin);
		ScrollPane sp = new ScrollPane(t, skin);
		sp.setupFadeScrollBars(0f, 0f);
		sp.setScrollingDisabled(true, false);
		t.add(skins);
		t.row();
		t.add(skinDefault).left();
		t.row();
		t.add(skinN2).left();
		t.row();
		t.add(skinN3).left();
		t.row();
		t.add(line);
		t.row();
		t.add(lenguajes);
		t.row();
		t.add(spanish).left();
		t.row();
		t.add(english).left();
		
		this.add(sp);
		
		this.setHeight(600);
	}
}
