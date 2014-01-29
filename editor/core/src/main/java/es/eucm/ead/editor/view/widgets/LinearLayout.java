package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class LinearLayout extends AbstractWidget {

	private boolean horizontal;

	private float padLeft, padTop, padRight, padBottom;

	private boolean expand;

	private int verticalAlign = Align.top;

	private int horizontalAlign = Align.left;

	public LinearLayout(boolean horizontal) {
		this.horizontal = horizontal;
	}

	public LinearLayout pad(float pad) {
		padLeft = padTop = padRight = padBottom = pad;
		return this;
	}

	public LinearLayout padLeft(float padLeft) {
		this.padLeft = padLeft;
		return this;
	}

	public LinearLayout padTop(float padTop) {
		this.padTop = padTop;
		return this;
	}

	public LinearLayout padRight(float padRight) {
		this.padRight = padRight;
		return this;
	}

	public LinearLayout padBottom(float padBottom) {
		this.padBottom = padBottom;
		return this;
	}

	public LinearLayout center() {
		this.horizontalAlign = Align.center;
		return this;
	}

	public LinearLayout left() {
		this.horizontalAlign = Align.left;
		return this;
	}

	public LinearLayout right() {
		this.horizontalAlign = Align.right;
		return this;
	}

	public LinearLayout middle() {
		this.verticalAlign = Align.center;
		return this;
	}

	public LinearLayout top() {
		this.verticalAlign = Align.top;
		return this;
	}

	public LinearLayout bottom() {
		this.verticalAlign = Align.bottom;
		return this;
	}

	public LinearLayout expand() {
		this.expand = true;
		return this;
	}

	@Override
	public float getPrefWidth() {
		return horizontal ? getChildrenTotalWidth() + (padLeft + padRight)
				* getChildren().size : getChildrenMaxWidth() + padLeft
				+ padRight;
	}

	@Override
	public float getPrefHeight() {
		return horizontal ? getChildrenMaxHeight() + padTop + padBottom
				: getChildrenTotalHeight() + (padBottom + padTop)
						* getChildren().size;
	}

	@Override
	public void layout() {
		float xOffset = padLeft;
		float yOffset = padBottom;

		for (Actor a : getChildren()) {
			float width = !horizontal && expand ? getWidth() : getPrefWidth(a);
			float height = horizontal && expand ? getHeight()
					: getPrefHeight(a);

			a.setBounds(xOffset, getHeight() - yOffset - height, width, height);

			if (horizontal) {
				xOffset += padLeft + padRight + width;
			} else {
				yOffset += padTop + padBottom + height;
			}
		}
	}

}
