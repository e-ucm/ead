package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class PlaceHolder extends WidgetGroup {

	private Actor content;

	public void setContent(Actor content) {
		if (this.content != null) {
			this.content.remove();
		}
		this.content = content;
		addActor(content);
	}

	@Override
	public void layout() {
		if (content != null) {
			content.setBounds(0, 0, getWidth(), getHeight());
		}
	}
}
