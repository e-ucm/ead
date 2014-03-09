package es.eucm.ead.editor.assets;

import es.eucm.ead.engine.assets.SimpleLoaderParameters;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneMetadata;
import es.eucm.ead.schema.game.Game;
import es.eucm.ead.schema.game.GameMetadata;

import java.util.Map;

/**
 * This class is intended to provide access to the model ({@link es.eucm.ead.schema.game.Game}, {@link es.eucm.ead.schema.game.GameMetadata}, {@link es.eucm.ead.schema.actors.Scene}s and {@link es.eucm.ead.schema.actors.SceneMetadata}s) to the {@link es.eucm.ead.editor.assets.LoaderWithModelAccess}. This is necessary for loaders that require setting default values in the model.
 * Created by Javier Torrente on 9/03/14.
 */
public class LoaderParametersWithModel<T> extends SimpleLoaderParameters<T> {
    protected Game game;
    protected GameMetadata gameMetadata;
    protected Map<String, Scene> scenes;
    protected Map<String, SceneMetadata> scenesMetadata;

    public LoaderParametersWithModel(LoadedCallback loadedCallback) {
        this(loadedCallback, null, null, null, null);
    }

    public LoaderParametersWithModel(LoadedCallback loadedCallback, Game game, GameMetadata gameMetadata, Map<String, Scene> scenes, Map<String, SceneMetadata> scenesMetadata) {
        super(loadedCallback);
        this.game = game;
        this.gameMetadata = gameMetadata;
        this.scenes = scenes;
        this.scenesMetadata = scenesMetadata;
    }

    public Game getGame() {
        return game;
    }

    public GameMetadata getGameMetadata() {
        return gameMetadata;
    }

    public Map<String, Scene> getScenes() {
        return scenes;
    }

    public Map<String, SceneMetadata> getScenesMetadata() {
        return scenesMetadata;
    }
}
