package es.eucm.ead.editor.view.listeners;


/**
 * General interface to listen changes in actions' state
 */
public interface ActionListener {

    /**
     * The state of the action changed
     *
     * @param actionClass
     *            the action class
     * @param enable
     *            if the action is enabled
     */
    void enableChanged(Class actionClass, boolean enable);
}
