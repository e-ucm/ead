package es.eucm.ead.mockup.core.view.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Cell;


public class ToolBar extends Panel{

	private HorizontalGroup bar;
	//private ToolBarStyle style;

	/**
	 * Create a {@link ToolBar toolbar} with default style.
	 * 
	 * @param skin the skin to use
	 */
	public ToolBar(Skin skin)
	{
		this(skin, "default");
	}

	/**
	 * Create a {@link ToolBar toolbar} with the specified style.
	 */
	public ToolBar(Skin skin, String styleName)
	{
		super(skin);

		bar = new HorizontalGroup();
		add(bar).expand().right();

		setStyle(skin.get(styleName, ToolBarStyle.class));
	}

	/**
	 * Apply the style of this {@link ToolBar toolbar}.
	 * 
	 * @param style the style to apply
	 */
	public void setStyle(ToolBarStyle style)
	{
		//this.style = style;

		if(style.background != null)
			this.setBackground(style.background);

		this.left();
	}
	
    /**
     * Add an actor to the {@link ToolBar toolbar}.
     */
    @Override
    public Cell<?> add(Actor actor)
    {
        if(actor instanceof HorizontalGroup)
            return super.add(actor);
        else
        {
            bar.addActor(actor);
            return null;
        }
    }

	/**
	 * Define the style of a {@link ToolBar toolbar}.
	 */
	public static class ToolBarStyle
	{

		/** Optional */
		public Drawable background;

		public ToolBarStyle()
		{

		}
		public ToolBarStyle(Drawable background) {
			this.background = background;
		}

		public ToolBarStyle(ToolBarStyle style) {
			this.background = style.background;
		}
	}
}
