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
package es.eucm.ead.builder;

import es.eucm.ead.schema.data.Color;

/**
 * 
 * 
 * Created by jtorrente on 29/10/2015.
 */
public class ColorBuilder {

	private static final ColorBuilder builder = new ColorBuilder();

	public static final Color RED_CLASSIC = builder.rgb(1F, 0F, 0F);
	public static final Color GREEN_CLASSIC = builder.rgb(0F, 1F, 0F);
	public static final Color BLUE_CLASSIC = builder.rgb(0F, 0F, 1F);
	public static final Color BLACK_CLASSIC = builder.rgb(0F, 0F, 0F);
	public static final Color WHITE_CLASSIC = builder.rgb(1F, 1F, 1F);
	public static final Color YELLOW_CLASSIC = builder.rgb(1F, 1F, 0F);
	public static final Color ORANGE_CLASSIC = builder.rgb(255, 200, 0);
	public static final Color PURPLE_CLASSIC = builder.rgb(1F, 0F, 1F);
	public static final Color PINK_CLASSIC = builder.rgb(255, 175, 175);
	public static final Color CYAN_CLASSIC = builder.rgb(0F, 1F, 1F);
	public static final Color GREY_CLASSIC = builder.rgb(128, 128, 128);
	public static final Color DARK_GREY_CLASSIC = builder.rgb(64, 64, 64);
	public static final Color LIGHT_GREY_CLASSIC = builder.rgb(192, 192, 192);

	public static final Color RED_MATERIAL = builder.hex("#F44336");
	public static final Color PINK_MATERIAL = builder.hex("#E91E63");
	public static final Color PURPLE_MATERIAL = builder.hex("#9C27B0");
	public static final Color DEEP_PURPLE_MATERIAL = builder.hex("#673AB7");
	public static final Color INDIGO_MATERIAL = builder.hex("#3F51B5");
	public static final Color BLUE_MATERIAL = builder.hex("#2196F3");
	public static final Color LIGHT_BLUE_MATERIAL = builder.hex("#03A9F4");
	public static final Color CYAN_MATERIAL = builder.hex("#00BCD4");
	public static final Color TEAL_MATERIAL = builder.hex("#009688");
	public static final Color GREEN_MATERIAL = builder.hex("#4CAF50");
	public static final Color LIGHT_GREEN_MATERIAL = builder.hex("#8BC34A");
	public static final Color LIME_MATERIAL = builder.hex("#CDDC39");
	public static final Color YELLOW_MATERIAL = builder.hex("#FFEB3B");
	public static final Color AMBER_MATERIAL = builder.hex("#FFC107");
	public static final Color ORANGE_MATERIAL = builder.hex("#FF9800");
	public static final Color DEEP_ORANGE_MATERIAL = builder.hex("#FF5722");
	public static final Color BROWN_MATERIAL = builder.hex("#795548");
	public static final Color GREY_MATERIAL = builder.hex("#9E9E9E");
	public static final Color BLUE_GREY_MATERIAL = builder.hex("#607D8B");
	public static final Color WHITE_MATERIAL = builder.hex("#FFFFFF");
	public static final Color BLACK_MATERIAL = builder.hex("#000000");

	private float red = 0.0F;
	private float green = 0.0F;
	private float blue = 0.0F;
	private float alpha = 1.0F;

	public Color rgba(float r, float g, float b, float a) {
		checkRange("red", r);
		checkRange("red", g);
		checkRange("red", b);
		checkRange("red", a);
		return reset().red(r).green(g).blue(b).alpha(a).build();
	}

	public Color rgb(float r, float g, float b) {
		return rgba(r, g, b, 1.0F);
	}

	public Color rgba(int r, int g, int b, int a) {
		checkRange("red", r);
		checkRange("red", g);
		checkRange("red", b);
		checkRange("red", a);
		return rgba(r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F);
	}

	public Color rgb(int r, int g, int b) {
		return rgba(r, g, b, 255);
	}

	public Color hex(String hex) {
		if (hex.length() != 3 && hex.length() != 6 && hex.length() != 8) {
			throw new IllegalArgumentException(
					"The hex value is not correct. Should have 3, 6 or 8 hex characters (not including the starting # character): "
							+ hex);
		}
		hex = hex.toLowerCase();
		for (int i = 0; i < hex.length(); i++) {
			char c = hex.charAt(i);
			if ((c < '0' || c > '9') && (c < 'a' || c > 'f')) {
				throw new IllegalArgumentException(
						"The hex value is not correct. Only Hex characters 0-9 A-F can be used");
			}
		}
		float r = 0, g = 0, b = 0, a = 1;
		if (hex.length() == 3) {
			r = Integer.parseInt(hex.substring(0, 1), 16) / 15.0F;
			g = Integer.parseInt(hex.substring(1, 2), 16) / 15.0F;
			b = Integer.parseInt(hex.substring(2, 3), 16) / 15.0F;
			a = 1.0F;
		} else if (hex.length() >= 6) {
			r = Integer.parseInt(hex.substring(0, 2), 16) / 255F;
			g = Integer.parseInt(hex.substring(2, 4), 16) / 255F;
			b = Integer.parseInt(hex.substring(4, 6), 16) / 255F;
			if (hex.length() == 8) {
				a = Integer.parseInt(hex.substring(6, 8), 16) / 255F;
			}
		}
		return rgba(r, g, b, a);
	}

	public ColorBuilder red(float r) {
		red = r;
		return this;
	}

	public ColorBuilder green(float g) {
		green = g;
		return this;
	}

	public ColorBuilder blue(float b) {
		blue = b;
		return this;
	}

	public ColorBuilder alpha(float a) {
		alpha = a;
		return this;
	}

	public ColorBuilder opaque() {
		return alpha(1.0F);
	}

	public ColorBuilder transparent() {
		return alpha(0.0F);
	}

	public ColorBuilder translucid() {
		return alpha(0.5F);
	}

	public ColorBuilder reset() {
		red = 0.0F;
		green = 0.0F;
		blue = 0.0F;
		alpha = 1.0F;
		return this;
	}

	public Color build() {
		Color color = new Color();
		color.setR(red);
		color.setG(green);
		color.setB(blue);
		color.setA(alpha);
		return color;
	}

	public com.badlogic.gdx.graphics.Color toGdx(Color color) {
		return new com.badlogic.gdx.graphics.Color(color.getR(), color.getG(),
				color.getB(), color.getA());
	}

	private void checkRange(String attribute, int value) {
		if (value < 0 || value > 255) {
			throw new IllegalArgumentException(
					"Color could not be created. Attribute "
							+ attribute
							+ " has an invalid value. Must be between 0 and 255");
		}
	}

	private void checkRange(String attribute, float value) {
		if (value < 0F || value > 1.0F) {
			throw new IllegalArgumentException(
					"Color could not be created. Attribute "
							+ attribute
							+ " has an invalid value. Must be between 0.0 and 1.0");
		}
	}
}
