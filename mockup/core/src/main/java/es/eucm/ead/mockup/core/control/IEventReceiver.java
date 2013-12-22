package es.eucm.ead.mockup.core.control;

/**
 * Defines a series of auxiliary events.
 */
public interface IEventReceiver {

	/**
	 * Called once when it's created.
	 * Here takes place the initialization.
	 */
	public void create();
	
	/**
	 * This event it's called every time the receiver gains focus.
	 */
	public void show();
	
	/**
	 * Called once right before we lose focus.
	 */
	public void hide();
	
}
