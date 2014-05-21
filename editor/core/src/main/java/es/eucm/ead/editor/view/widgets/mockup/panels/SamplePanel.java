/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
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

import es.eucm.ead.editor.view.widgets.mockup.edition.draw.BrushStrokes;
import es.eucm.ead.engine.I18N;

public class SamplePanel extends Table {

	private static final String IC_STAIN = "ic_stain";
	private static final int NUM_STAIN = 6;

	private enum Type {
		DRAW, ERASE, WRITE
	}

	private Slider slider;

	private Color currentColor;

	private GridPanel<Actor> gridPanel;

	private Texture pixTex;

	private Pixmap circleSample;
	private final float maxPixRadius = 50f;
	private final int pixmapWidthHeight = 100, center = pixmapWidthHeight / 2;

	private final String text = "AaBbCcDd";
	private Label textSample;

	private BrushStrokes brushStrokes;

	private Type type;

	private Skin skin;

	/**
	 * Create a panel with a color palate if colors are true and a sample of the
	 * size and color that can be text or a circle according the boolean text
	 * */
	public SamplePanel(I18N i18n, Skin skin, int cols, boolean text,
			boolean colors) {
		super(skin);
		initialize(i18n, skin, cols, text, colors, Color.BLACK);
	}

	/**
	 * Create a panel with a color palate if colors are true and a sample of the
	 * size and color that can be text or a circle according the boolean text.
	 * The initial color of tool is initColor
	 * */
	public SamplePanel(I18N i18n, Skin skin, int cols, boolean text,
			boolean colors, Color initColor) {
		super(skin);
		initialize(i18n, skin, cols, text, colors, initColor);
	}

	private void initialize(I18N i18n, Skin skin, int cols, boolean text,
			boolean colors, Color initColor) {

		this.skin = skin;

		this.currentColor = initColor;
		if (text) {
			type = Type.WRITE;
		} else if (colors) {
			type = Type.DRAW;
		} else {
			type = Type.ERASE;
		}

		int minValue = 5;
		if (type == Type.WRITE) {
			this.textSample = new Label(this.text, skin);
			minValue = 20;
		}

		this.slider = new Slider(minValue, 60, 1f, false, skin,
				"left-horizontal");
		this.slider.setValue(30);
		this.slider.addListener(new InputListener() {
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
		this.circleSample = new Pixmap(pixmapWidthHeight, pixmapWidthHeight,
				Format.RGBA8888);

		final Blending b = Pixmap.getBlending();
		Pixmap.setBlending(Blending.None);
		this.circleSample.fill();
		Pixmap.setBlending(b);

		this.circleSample.setColor(currentColor);
		final int radius = (int) getCurrentRadius();
		this.circleSample.fillCircle(center, center, radius);
		this.pixTex = new Texture(circleSample); // FIXME unmanaged upenGL
		// textures, TODO reload
		// onResume (after pause)
		this.pixTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		add(this.slider).expandX().fillX();
		row();

		if (type != Type.WRITE) {
			Image cir = new Image(this.pixTex);
			add(cir).align(Align.center);
		} else {
			this.textSample.setColor(currentColor);
			this.textSample.setAlignment(Align.center);
			this.textSample.setOrigin(40, 40);
			add(this.textSample).align(Align.center).size(80, 80);
		}
		if (colors) {
			row();
			add(i18n.m("edition.colors") + ":").padLeft(8f);
			row();
			add(gridPanel).expand().fill();
		}

	}

	/**
	 * Updates the texture that displays the visual representation of our draw
	 * component.
	 */
	private void updateDemoColor() {
		if (type == Type.WRITE) {
			updateTextSample();
		} else {
			updateCircleSample();
		}
	}

	/**
	 * Update the circle (color and size) that represent the brush
	 */
	private void updateCircleSample() {
		final Blending b = Pixmap.getBlending();
		Pixmap.setBlending(Blending.None);
		this.circleSample.setColor(0f, 0f, 0f, 0f);
		this.circleSample.fill();
		Pixmap.setBlending(b);

		this.circleSample.setColor(this.currentColor);
		final float radius = getCurrentRadius();
		this.circleSample.fillCircle(this.center, this.center, (int) radius);
		this.pixTex.draw(circleSample, 0, 0);
		this.brushStrokes.setRadius(radius);
		if (type == Type.DRAW) {
			this.brushStrokes.setColor(this.currentColor);
		}
	}

	private float getCurrentRadius() {
		return maxPixRadius * this.slider.getValue()
				/ this.slider.getMaxValue();
	}

	/**
	 * Update the label (color and size) that represent the text
	 */
	private void updateTextSample() {
		this.textSample.setColor(this.currentColor);
		this.textSample.setFontScale((1.5f * this.slider.getValue())
				/ this.slider.getMaxValue());
	}

	/**
	 * Create a gridPanel with colors
	 * */
	private void createPalette() {
		final int COLORS = 12;
		final Color[] colrs = { Color.BLACK, Color.BLUE, Color.CYAN,
				new Color(.5f, .75f, .32f, 1f), Color.GREEN, Color.MAGENTA,
				Color.ORANGE, Color.PINK, Color.RED, Color.LIGHT_GRAY,
				Color.YELLOW, Color.WHITE };

		this.gridPanel = new GridPanel<Actor>(3, 10);
		final ClickListener colorListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				final Image list = (Image) event.getListenerActor();
				currentColor = list.getColor();
				updateDemoColor();
			}
		};

		for (int i = 0; i < COLORS; i++) {
			final Color c = colrs[i];
			final Image colorB = new Image(skin, IC_STAIN + (i % NUM_STAIN));
			// FIXME
			// unmanaged upenGL textures,
			// TODO reload onResume (after pause)
			colorB.setColor(c);
			colorB.addListener(colorListener);
			this.gridPanel.addItem(colorB).expand().fill();
		}
	}

	public void AddPersonalColor(Color color) {
		final Pixmap auxPixmap = new Pixmap(50, 50, Format.RGB888);
		auxPixmap.setColor(color);
		auxPixmap.fill();
		final Image colorB = new Image(new Texture(auxPixmap));
		this.gridPanel.addItem(colorB);
	}

	public float getSampleSize() {
		return this.slider.getValue();
	}

	@Override
	public Color getColor() {
		return this.currentColor;
	}

	public void setBrushStrokes(BrushStrokes brushStrokes) {
		this.brushStrokes = brushStrokes;
		this.brushStrokes.setRadius(getCurrentRadius());
		if (type == Type.DRAW) {
			this.brushStrokes.setColor(this.currentColor);
			this.brushStrokes.setMaxDrawRadius(this.maxPixRadius);
		}
	}

	public BrushStrokes getBrushStrokes() {
		return this.brushStrokes;
	}
}
