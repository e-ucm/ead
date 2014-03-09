package es.eucm.ead.editor.assets;

import es.eucm.ead.schema.components.Note;
import es.eucm.ead.schema.game.GameMetadata;

/**
 * Loads files corresponding to {@link es.eucm.ead.schema.game.GameMetadata}
 * Created by Javier Torrente on 9/03/14.
 */
public class GameMetadataLoader extends LoaderWithModelAccess<GameMetadata> {

    public GameMetadataLoader(ProjectAssets assets) {
        super(assets, GameMetadata.class);
    }

    @Override
    protected void fillInDefaultValuesInContentLoaded(GameMetadata object, String fileName, LoaderParametersWithModel<GameMetadata> parameter) {
        // Note in GameMetadata cannot be null
        if (object.getNotes() == null) {
            object.setNotes(new Note());
        }
    }
}
