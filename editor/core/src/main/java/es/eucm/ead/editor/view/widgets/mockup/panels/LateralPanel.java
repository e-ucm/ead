package es.eucm.ead.editor.view.widgets.mockup.panels;

import java.awt.Panel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Controller;

public class LateralPanel extends Table {

	private final float FADE_DURATION = .4f;
	private Drawable stageBackground;

	public LateralPanel(Controller controller, Skin skin, String styleName) {
		super(skin);
		setStyle(skin.get(styleName, LateralPanelStyle.class));
	}

	public void show() {
		if (FADE_DURATION > 0) {
			setPosition(getStage().getWidth(),
					getY());
			addAction(Actions.moveTo(getStage().getWidth() - getWidth(),
					getY(), FADE_DURATION, Interpolation.sineOut));

		}
		setVisible(true);
	}

	public void hide() {
		if (FADE_DURATION > 0) {
			addAction(Actions.sequence(Actions.moveTo(getStage().getWidth(),
					getY(), FADE_DURATION), Actions.run(new Runnable() {

				@Override
				public void run() {
					setVisible(false);
				}

			})));
		}
	}

	/**
	 * Apply a {@link PanelStyle style}.
	 * 
	 * @param style
	 *            the style to apply
	 * @throws IllegalArgumentException
	 *             if the style is null
	 */
	public void setStyle(LateralPanelStyle style) {
		if (style == null) {
			throw new IllegalArgumentException("style cannot be null");
		}

		stageBackground = style.stageBackground;
		this.setBackground(style.background);
		invalidateHierarchy();
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha) {
		if (stageBackground != null) {
			Color color = getColor();
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
			Stage stage = getStage();
			stageBackground.draw(batch, 0, 0, stage.getWidth(), stage.getHeight());

		}
		super.drawBackground(batch, parentAlpha);
	}

	/**
	 * Define the style of a {@link Panel panel}.
	 * 
	 */
	static public class LateralPanelStyle {

		/** Optional. */
		public Drawable background;
		/** Optional. */
		public Drawable stageBackground;

		public LateralPanelStyle() {
		}

		public LateralPanelStyle(Drawable background, Drawable stageBackground) {
			this.background = background;
			this.stageBackground = stageBackground;
		}

		public LateralPanelStyle(LateralPanelStyle style) {
			this.background = style.background;
			this.stageBackground = style.stageBackground;
		}
	}
}
