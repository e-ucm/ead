package es.eucm.ead.engine.io.serializers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.schema.renderers.Text;

public class TextSerializer extends DefaultSerializer<Text> {

	@Override
	public Text read(Json json, JsonValue jsonData, Class type) {
		Text text = super.read(json, jsonData, type);
		if (text.getFont() != null) {
			Engine.assets.load(text.getFont(), BitmapFont.class);
		}
		return text;
	}
}
