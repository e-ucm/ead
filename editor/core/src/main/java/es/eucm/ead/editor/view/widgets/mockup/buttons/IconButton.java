package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;

/**
 * A button used to display one image.
 */
public class IconButton extends ImageButton {

	private final float prefWidth;

	/**
	 * Creates a squared button with a size of 0.075 * screen's width.
	 * 
	 * @param imageUp
	 */
	public IconButton(Skin skin, String drawable) {
		super(skin, drawable);
		prefWidth = 0.075f;
		init();
	}

	public IconButton(Skin skin, String drawable, Controller controller,
			String actionName, Object... args) {
		super(skin, drawable);
		prefWidth = 0.075f;
		addListener(new ActionOnClickListener(controller, actionName, args));
		init();
	}
	
	public IconButton(Skin skin, String drawable, float prefWidth) {
		super(skin, drawable);
		this.prefWidth = prefWidth;
		init();
	}

	public IconButton(Skin skin, String drawable, float prefWidth,
			Controller controller, String actionName, Object... args) {
		super(skin, drawable);
		this.prefWidth = prefWidth;
		addListener(new ActionOnClickListener(controller, actionName, args));
		init();
	}

	protected void init() {
		getImageCell().expand().fill();
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth() * prefWidth;
	}

	@Override
	public float getPrefHeight() {
		// We make sure it's a square
		return getPrefWidth();
	}
}
