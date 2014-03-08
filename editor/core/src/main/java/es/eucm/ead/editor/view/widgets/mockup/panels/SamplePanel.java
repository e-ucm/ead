package es.eucm.ead.editor.view.widgets.mockup.panels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.engine.I18N;

public class SamplePanel extends Table{

	private Slider slider;
	
	private Color currentColor;
	
	private GridPanel<Actor> gridPanel;

	private Image cir;
	
	private Texture pixTex;
	
	private Pixmap circleSample;
	private final float maxPixRadius = 50f;
	private final int pixmapWidthHeight = 100,
			center = pixmapWidthHeight / 2;
	
	private final String text = "AaBbCcDd";
	private Label textSample;
	
	private boolean isText;
	
	/**
	 * Create a panel with a color palette if colors are true and a sample of 
	 * the size and color that can be text or a circle according the boolean text
	 * */
	public SamplePanel(I18N i18n, Skin skin, int cols, boolean text, boolean colors){
		super(skin);
		initialize(i18n, skin, cols, text, colors, Color.BLACK);
	}
	
	/**
	 * Create a panel with a color palette if colors are true and a sample of 
	 * the size and color that can be text or a circle according the boolean text.
	 * The initial color of tool is initColor
	 * */
	public SamplePanel(I18N i18n, Skin skin, int cols, boolean text, boolean colors, Color initColor){
		super(skin);
		initialize(i18n, skin, cols, text, colors, initColor);
	}
		
	private void initialize(I18N i18n, Skin skin, int cols, boolean isText,
			boolean colors, Color initColor) {
		
		this.currentColor = initColor;
		this.isText = isText;
		
		if(isText){
			this.textSample = new Label(this.text, skin);
		}
		
		slider = new Slider(15, 60, 0.5f, false, skin, "left-horizontal");
		slider.setValue(30);
		slider.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				updateDemoColor();

				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {

				updateDemoColor();

			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {

				updateDemoColor();

			}
		});

		createPalette();
		circleSample = new Pixmap(pixmapWidthHeight, pixmapWidthHeight,
				Format.RGBA8888);

		Blending b = Pixmap.getBlending();
		Pixmap.setBlending(Blending.None);
		circleSample.fill();
		Pixmap.setBlending(b);
		
		circleSample.setColor(currentColor);
		int radius = (int) getCurrentRadius();
		circleSample.fillCircle(center, center, radius);
		pixTex = new Texture(circleSample); // FIXME unmanaged upenGL
		// textures, TODO reload
		// onResume (after pause)
		pixTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		add(slider);
		row();
		add(textSample);
		row();

		if (!isText) {
			cir = new Image(pixTex);
			add(cir).align(Align.center).expand(false, false).fill(false)
					.size(60, 60);
		} else {
			textSample.setColor(currentColor);
			add(textSample).align(Align.left).size(60, 60).padLeft(8f);
		}
		if (colors) {
			row();
			add(i18n.m("edition.colors")+":").padLeft(8f);
			row();
			add(gridPanel);
		}
		
	}

	/**
	 * Updates the texture that displays the visual representation of our
	 * draw component.
	 */
	private void updateDemoColor() {
		if (isText) {
			updateTextSample();
		} else {
			updateCircleSample();
		}
	}

	/**
	 * Update the circle (color and size) that represent the brush
	 */
	private void updateCircleSample() {
		Blending b = Pixmap.getBlending();
		Pixmap.setBlending(Blending.None);
		circleSample.setColor(0f, 0f, 0f, 0f);
		circleSample.fill();
		Pixmap.setBlending(b);

		circleSample.setColor(currentColor);
		float radius = getCurrentRadius();
		circleSample.fillCircle(center, center, (int) radius);
		pixTex.draw(circleSample, 0, 0);
	}

	private float getCurrentRadius() {
		return maxPixRadius * slider.getValue() / slider.getMaxValue();
	}

	/**
	 * Update the label (color and size) that represent the text
	 */
	private void updateTextSample() {
		textSample.setColor(currentColor);
		textSample.setFontScale((slider.getValue() + 1)
				/ slider.getMaxValue());
	}

	/**
	 * Create a gridPanel with colors
	 * */
	private void createPalette() {
		Pixmap auxPixmap = new Pixmap(50, 50, Format.RGB888);
		final int COLORS = 12;
		final Color[] colrs = {
				Color.BLACK, Color.BLUE, Color.CYAN,
						new Color(.5f, .75f, .32f, 1f),
				Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK,
				Color.RED, Color.LIGHT_GRAY, Color.YELLOW, Color.WHITE };
		
		gridPanel = new GridPanel<Actor>(3, 20);
		ClickListener colorListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Image list = (Image) event.getListenerActor();
				currentColor = list.getColor();
				updateDemoColor();
			}
		};
		
		for (int i = 0; i < COLORS; i++) {
			Color c = colrs[i];
			auxPixmap.setColor(c);
			auxPixmap.fill();
			final Image colorB = new Image(new Texture(auxPixmap)); // FIXME
			// unmanaged upenGL textures,
			// TODO reload onResume (after pause)
			colorB.setColor(c);
			colorB.addListener(colorListener);
			gridPanel.addItem(colorB).expand().fill();
		}
		auxPixmap.dispose();
	}
	
	public void AddPersonalColor(Color color){
		Pixmap auxPixmap = new Pixmap(50, 50, Format.RGB888);
		auxPixmap.setColor(color);
		auxPixmap.fill();
		final Image colorB = new Image(new Texture(auxPixmap));
		this.gridPanel.addItem(colorB);
	}
	
	public float getSampleSize(){
		return slider.getValue();
	}
	
	public Color getColor(){
		return currentColor;
	}
}

