package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;


/**
 * An Image used in the NavigationPanel.
 */
public class Icon extends Image{

	private final float prefWidth;
	
	/**
	 * Creates a squared icon with a size of 0.075 * screen's width.
	 */
	public Icon(Drawable drawable) {
		super(drawable);
		setScaling(Scaling.fit);
		this.prefWidth = 0.075f;
	}
	
	public Icon(Drawable drawable, float prefWidth) {
		super(drawable);
		setScaling(Scaling.fit);
		this.prefWidth = prefWidth;
	}
	
	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth() * this.prefWidth;
	}

	@Override
	public float getPrefHeight() {
		// We make sure it's a square
		return getPrefWidth();
	}
}
