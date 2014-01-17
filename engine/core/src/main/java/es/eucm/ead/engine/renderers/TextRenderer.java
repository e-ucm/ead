package es.eucm.ead.engine.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.renderers.Text;

public class TextRenderer extends AbstractRenderer<Text> {

	private BitmapFont bitmapFont;

	private String text;

	private Color color;

	private TextBounds bounds;

	private float scale;

	@Override
	public void initialize(Text schemaObject) {
		text = I18N.m(schemaObject.getText());
		scale = schemaObject.getScale();

		es.eucm.ead.schema.components.Color c = schemaObject.getColor();
		color = c == null ? Color.WHITE : new Color(c.getR(), c.getG(), c
				.getB(), c.getA());

		String fontFile = schemaObject.getFont();
		if (fontFile == null || !Engine.assets.resolve(fontFile).exists()) {
			bitmapFont = Engine.assets.defaultFont();
		} else {
			bitmapFont = Engine.assets.get(schemaObject.getFont(),
					BitmapFont.class);
		}

		bounds = bitmapFont.getBounds(text);
	}

	@Override
	public void draw(Batch batch) {
		bitmapFont.setColor(color);
		bitmapFont.setScale(scale);
		bitmapFont.draw(batch, text, 0, 0);
	}

	@Override
	public float getHeight() {
		return bounds.height;
	}

	@Override
	public float getWidth() {
		return bounds.width;
	}

}
