package es.eucm.ead.editor.view.builders.classic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.FPSCounter;
import es.eucm.ead.editor.view.widgets.Table;
import es.eucm.ead.editor.view.widgets.Window;
import es.eucm.ead.editor.view.widgets.menu.Menu;
import es.eucm.ead.editor.view.widgets.scene.ScenePreview;
import es.eucm.ead.engine.I18N;

public class MainBuilder implements ViewBuilder {

	public static final String NAME = "main";

	@Override
	public String getName() {
		return NAME;
	}

	public Actor build(Controller controller) {
		Skin skin = controller.getAssets().getSkin();
		I18N i18n = controller.getAssets().getI18N();
		Window window = new Window();

		Table root = window.root(new Table(controller));

		Menu menu = new Menu(controller, skin);
		menu.item(i18n.m("general.file"))
				.subitem(i18n.m("general.open"), OpenGame.NAME)
				.subitem(i18n.m("general.recents"));

		menu.item(i18n.m("general.edit")).subitem(i18n.m("general.undo"))
				.subitem(i18n.m("general.redo"));

		menu.item(i18n.m("general.help"));

		root.row().left().add(menu);

		root.row().expandY().add(new ScenePreview(controller)).toBack();

		root.row().right().add(new FPSCounter(skin));

		return window;
	}
}
