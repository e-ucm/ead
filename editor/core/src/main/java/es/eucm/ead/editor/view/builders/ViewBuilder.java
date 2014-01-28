package es.eucm.ead.editor.view.builders;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.engine.I18N;

public interface ViewBuilder {

	Actor build(Skin skin, I18N i18n);
}
