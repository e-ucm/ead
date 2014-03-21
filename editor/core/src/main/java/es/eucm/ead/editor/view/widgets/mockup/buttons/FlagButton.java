package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.schema.components.VariableDef;

/**
 * Button that shows a flag
 * 
 */
public class FlagButton extends TextButton {

	private Vector2 viewport;

	private VariableDef flag;

	public FlagButton(String text, Vector2 viewport, Skin skin) {
		super(text, skin);
		this.viewport = viewport;
	}

	public FlagButton(VariableDef flag, Vector2 viewport, Skin skin) {
		super(flag.getName(), skin);
		this.viewport = viewport;
		this.flag = flag;
	}

	@Override
	public float getPrefWidth() {
		if (viewport == null) {
			return super.getPrefWidth();
		} else {
			return this.viewport.x * .15f;
		}
	}

	public VariableDef getVariableDef() {
		return flag;
	}

	public void setVariableDef(VariableDef var) {
		this.setText(var.getName());
		this.flag = var;
	}
}
