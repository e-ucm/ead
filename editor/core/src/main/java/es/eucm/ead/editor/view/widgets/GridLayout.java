package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class GridLayout extends AbstractWidget {
		
		private float padLeft, padTop, padRight, padBottom;

		private boolean expand;

		public GridLayout() {		
			expand=false;
		}

		public GridLayout pad(float pad) {
			padLeft = padTop = padRight = padBottom = pad;
			return this;
		}
		
		public GridLayout expand() {
			this.expand = true;
			return this;
		}
/*		
		@Override
		public float getPrefWidth() {
			return getChildrenMaxWidth() + (padLeft + padRight)
					* getChildren().size;
		}

		@Override
		public float getPrefHeight() {
			return getChildrenMaxHeight() + padTop + padBottom;
		}
*/
		@Override
		public void layout() {
			float xOffset = padRight;
			float yOffset = padBottom;

			for (Actor a : getChildren()) {
				float width = expand ? getWidth() : Math.min(
						getWidth(), getPrefWidth(a));
				float height = expand ? getHeight() : Math.min(
						getHeight(), getPrefHeight(a));
				
				if(xOffset + width > getWidth()){
					yOffset += padTop + padBottom + height;
					xOffset=0;
				}
					
				a.setBounds(xOffset, getHeight() - yOffset - height, width, height);
				xOffset += padLeft + padRight + width;
			}
		}

}

