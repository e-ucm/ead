package es.eucm.ead.engine;

import com.badlogic.gdx.Gdx;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * Launcher for a game that has been exported as a standalone JAR application (everything embedded into a single jar file).
 *
 * It initializes the engine desktop full screen and launches the game which is expected to live under the assets/ folder.
 *
 * For more details on the structure a game in JAR format has, see the wiki page:
 * <a target="_blank" href="https://github.com/e-ucm/ead/wiki/Game-JAR-format">https://github.com/e-ucm/ead/wiki/Game-JAR-format</a>
 *
 * Created by Javier Torrente on 20/03/14.
 */
public class EngineJarGame {

    /**
     * Relative path of the folder that contains the game assets (game.json, scenes/sceneX.json...)
     */
    private static final String GAME_PATH ="assets/";

    /**
     * Relative path to the folder that contains the images to be used as icons for the application.
     * Any image found under this folder will be treated as an icon.
     */
    private static final String APP_ICONS_PATH="appicons/";

    /**
     * App icon filenames supported
     */
    private static final String[] ICON_FILENAMES ={"16.png", "32.png", "64.png", "128.png", "256.png", "512.png", "1024.png"};

    public static void main(String args[]) {
        // Determine the size of the window for the game to be full screen
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        // Create the engine
        EngineDesktop engine = new EngineDesktop(screenWidth, screenHeight);
        // Load and set app icons
        List<? extends Image> icons =loadApplicationIcons();
        if (icons.size()>0){
            engine.setApplicationIcons(icons);
        }
        // Run the game
        engine.run(GAME_PATH, true);
    }

    /**
     * Loads a set of application icons, if any available. On desktop Java uses these icons
     * instead of the Java logo on the OS task bar and also on the upper left little
     * icon used in the Window.
     *
     * These icons are expected to live under the {@link #APP_ICONS_PATH} folder inside the jar.
     * They also must be provided in PNG format with the names specified in {@link #ICON_FILENAMES}.
     * There's no obligation of providing a specific number of these icons - it could be any
     * number between 0 and ICON_FILENAMES.length
     *
     * @return  A list with the images loaded, ready to be passed to the {@link javax.swing.JFrame#setIconImages(java.util.List)}
     * method
     */
    private static java.util.List<? extends Image> loadApplicationIcons(){
        List<BufferedImage> list = new ArrayList<BufferedImage>();
        for (String iconSubpath: ICON_FILENAMES){
            String iconPath = APP_ICONS_PATH+iconSubpath;
            InputStream inputStream = EngineJarGame.class.getResourceAsStream(iconPath);
            if (inputStream!=null){
                try {
                    BufferedImage bufferedImage = ImageIO.read(inputStream);
                    list.add(bufferedImage);
                } catch (IOException e) {
                    Gdx.app.debug(EngineJarGame.class.getCanonicalName(), "Exception reading icon: "+iconPath, e);
                }
            }
        }
        return list;
    }
}
