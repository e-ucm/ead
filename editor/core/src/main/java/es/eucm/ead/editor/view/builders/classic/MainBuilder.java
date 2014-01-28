package es.eucm.ead.editor.view.builders.classic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.FPSCounter;
import es.eucm.ead.editor.view.widgets.Table;
import es.eucm.ead.editor.view.widgets.Window;
import es.eucm.ead.editor.view.widgets.scenes.ScenePreview;
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

		root.row()
				.left()
				.add(new TextButton(i18n.m("general.open"), skin),
						OpenGame.NAME)
				.add(new TextButton(i18n.m("general.save"), skin));

		root.row().expandY().add(new ScenePreview(controller)).toBack();

		root.row().right().add(new FPSCounter(skin));

		return window;
	}
}
