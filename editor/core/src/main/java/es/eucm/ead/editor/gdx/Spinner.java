package es.eucm.ead.editor.gdx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Value;

public class Spinner extends Table {

	private SpinnerStyle style;

	private TextField textField;

	private Button plusButton;

	private Button minusButton;

	private int step = 1;

	public Spinner(Skin skin) {
		this(skin.get(SpinnerStyle.class));
	}

	public Spinner(Skin skin, String styleName) {
		this(skin.get(styleName, SpinnerStyle.class));
	}

	public Spinner(SpinnerStyle style) {
		this.style = style;
		clearChildren();
		Table textFieldT = new Table();
		textFieldT.debug();
		add(textField = new TextField("", style)).width(
				Value.percentWidth(1.0f));
		Table table = new Table();
		table.debug();

		ButtonStyle plusStyle = new ButtonStyle();
		plusStyle.up = style.plusUp;
		plusStyle.down = style.plusDown;
		plusStyle.checked = style.plusChecked;
		plusStyle.disabled = style.plusDisabled;
		plusStyle.over = style.plusOver;

		ButtonStyle minusStyle = new ButtonStyle();
		minusStyle.up = style.minusUp;
		minusStyle.down = style.minusDown;
		minusStyle.checked = style.minusChecked;
		minusStyle.disabled = style.minusDisabled;
		minusStyle.over = style.minusOver;

		plusButton = new Button(plusStyle);
		plusButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				add(step);
				return false;
			}
		});
		minusButton = new Button(minusStyle);
		minusButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				add(-step);
				return false;
			}
		});

		table.add(plusButton).top().expand();
		table.row();
		table.add(minusButton).bottom().expand();
		add(table).expand().right();
		setWidth(getPrefWidth());
		setHeight(getPrefHeight());
	}

	public float getPrefWidth() {
		float width = super.getPrefWidth();
		width = Math.max(width, textField.getPrefWidth());
		if (style.background != null)
			width = Math.max(width, style.background.getMinWidth());
		return width;
	}

	public float getPrefHeight() {
		float height = super.getPrefHeight();
		height = Math.max(height, textField.getPrefHeight());
		if (style.background != null)
			height = Math.max(height, style.background.getMinHeight());
		return height;
	}

	public void add(int step) {
		try {
			int value = Integer.parseInt(textField.getText());
			value += step;
			textField.setText(value + "");
		} catch (NumberFormatException e) {
			textField.setText("0");
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}

	public String getText() {
		return textField.getText();
	}

	public void setText(String text) {
		this.textField.setText(text);
	}

	static public class SpinnerStyle extends TextFieldStyle {

		public Drawable plusUp;
		public Drawable plusDown;
		public Drawable plusChecked;
		public Drawable plusDisabled;
		public Drawable plusOver;
		public Drawable minusUp;
		public Drawable minusDown;
		public Drawable minusChecked;
		public Drawable minusDisabled;
		public Drawable minusOver;
	}
}
