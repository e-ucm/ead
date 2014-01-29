package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class AbstractWidget extends WidgetGroup {

	protected float getPrefWidth(Actor a) {
		if (a instanceof Widget) {
			return ((Widget) a).getPrefWidth();
		} else if (a instanceof WidgetGroup) {
			return ((WidgetGroup) a).getPrefWidth();
		} else {
			return a.getWidth();
		}
	}

	protected float getPrefHeight(Actor a) {
		if (a instanceof Widget) {
			return ((Widget) a).getPrefHeight();
		} else if (a instanceof WidgetGroup) {
			return ((WidgetGroup) a).getPrefHeight();
		} else {
			return a.getHeight();
		}
	}

	protected float getChildrenTotalWidth() {
		float totalWidth = 0;
		for (Actor a : this.getChildren()) {
			totalWidth += getPrefWidth(a);
		}
		return totalWidth;
	}

	protected float getChildrenTotalHeight() {
		float totalHeight = 0;
		for (Actor a : this.getChildren()) {
			totalHeight += getPrefHeight(a);
		}
		return totalHeight;
	}

	protected float getChildrenMaxHeight() {
		float maxHeight = 0;
		for (Actor a : this.getChildren()) {
			maxHeight = Math.max(getPrefHeight(a), maxHeight);
		}
		return maxHeight;
	}

	protected float getChildrenMaxWidth() {
		float maxWidth = 0;
		for (Actor a : this.getChildren()) {
			maxWidth = Math.max(getPrefWidth(a), maxWidth);
		}
		return maxWidth;
	}
}
