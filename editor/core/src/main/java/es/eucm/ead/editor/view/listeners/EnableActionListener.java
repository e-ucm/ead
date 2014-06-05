package es.eucm.ead.editor.view.listeners;

import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import es.eucm.ead.editor.control.actions.Action;

/**
 * Listener to notify to {@link Disableable} widget that the "enable" property of certain
 * {@link Action} has changed.
 *
 * {@link Disableable} element should modify its view accordingly in
 * {@link Disableable#setDisabled(boolean)} method.
 *
 */
public class EnableActionListener implements ActionListener {

    private Disableable disableable;

    public EnableActionListener(Disableable disableable) {
        this.disableable = disableable;
    }

    @Override
    public void enableChanged(Class action, boolean enable) {
        disableable.setDisabled(!enable);
    }

}
