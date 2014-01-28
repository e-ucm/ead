package es.eucm.ead.editor.view.classic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.FPSCounter;
import es.eucm.ead.editor.view.widgets.Table;
import es.eucm.ead.editor.view.widgets.Window;
import es.eucm.ead.engine.I18N;

public class MainBuilder implements ViewBuilder {

	public Actor build(Skin skin, I18N i18n) {
		Window window = new Window();

		Table root = window.root(new Table());

		root.row().left().add(new TextButton("general.open", skin))
				.add(new TextButton("general.save", skin));

		root.row().expandY();

		root.row().right().add(new FPSCounter(skin));

		return window;
	}
}
