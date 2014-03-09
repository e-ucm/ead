package es.eucm.ead.editor.assets;

import es.eucm.ead.schema.actors.SceneMetadata;
import es.eucm.ead.schema.components.Note;

/**
 * Loads files corresponding to {@link es.eucm.ead.schema.actors.SceneMetadata}
 * Created by Javier Torrente on 9/03/14.
 */
public class SceneMetadataLoader extends LoaderWithModelAccess<SceneMetadata> {

    public SceneMetadataLoader(ProjectAssets assets) {
        super(assets, SceneMetadata.class);
    }

    @Override
    protected void fillInDefaultValuesInContentLoaded(SceneMetadata object, String fileName, LoaderParametersWithModel<SceneMetadata> parameter) {
        // Calculate the sceneId from the file Path (e.g. /scenes/scene0.json -> scene0)
        String id = fileName.substring(Math.max(
                fileName.lastIndexOf("\\"),
                fileName.lastIndexOf("/")) + 1, fileName
                .toLowerCase().lastIndexOf(".json"));

        // Set default note, cannot be null
        if (object.getNotes() == null) {
            object.setNotes(new Note());
        }
        // Set default name (scene id)
        if (object.getName() == null) {
            object.setName(id);
        }
    }
}
