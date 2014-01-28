package es.eucm.editor.view.builders;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.XmlReader.Element;
import es.eucm.editor.view.ViewBuilder;
import es.eucm.editor.view.ViewFactory;
import es.eucm.editor.view.widgets.ScenePreview;

public class ScenePreviewBuilder extends ViewBuilder {

	public ScenePreviewBuilder(ViewFactory viewFactory) {
		super(viewFactory);
	}

	@Override
	protected Actor buildView(Element element, Skin skin) {
		return new ScenePreview();
	}
}
