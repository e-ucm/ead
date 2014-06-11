package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.LoadEvent;

/**
 * This class adds to {@link EditorAction} the property of
 * being enabled when a game is loaded in the editor.
 *
 * Created by Angel on 11/06/2014.
 */
public abstract class EnabledOnloadAction extends EditorAction implements  Model.ModelListener<LoadEvent>{

    /**
     * Constructors propagates the call to father class
     */
    public EnabledOnloadAction(boolean initialEnable, boolean allowNullArguments){
        super(initialEnable, allowNullArguments);
    }

    protected EnabledOnloadAction(boolean initialEnable, boolean allowNullArguments,
                           Class... validArguments) {
        super(initialEnable, allowNullArguments, validArguments);
    }

    protected EnabledOnloadAction(boolean initialEnable, boolean allowNullArguments,
                           Class[]... validArguments) {
        super(initialEnable, allowNullArguments, validArguments);
    }

    /**
     * When the action is initialized, the {@link EnabledOnloadAction} will add
     * itself as {@link LoadEvent}listener in the controller and be enabled/disabled
     * taking into account if the game is loaded or not
     */
    @Override
    public void initialize(Controller controller) {
        super.initialize(controller);
        controller.getModel().addLoadListener(this);
        setEnabled(controller.getModel().getGame() != null);
    }


    /**
     * Modifies the {@link Action#enabled} attribute.
     *
     * NOTE: {@link Action#setEnabled(boolean)} notify the view listeners to
     * change their appearance accordingly.
     *
     */
    @Override
    public void modelChanged(LoadEvent event) {
        if (event.getType() == LoadEvent.Type.LOADED) {
            setEnabled(true && !waitforAditionalEvents());
        } else if (event.getType() == LoadEvent.Type.UNLOADED) {
            setEnabled(false && !waitforAditionalEvents());
        }
    }

    /**
     * The {@link Action} could be waiting for other events besides
     * the {@link LoadEvent} to be enabled.
     *
     * This method is called at {@link EnabledOnloadAction#modelChanged}
     * and children classes could override it to
     *
     */
    public boolean waitforAditionalEvents(){
        return false;
    }

}
