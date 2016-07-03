package wit.cgd.bunnyhop;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


public class Main {
    private static boolean  rebuildAtlas        = true;
    private static boolean  drawDebugOutline    = false;
  

    public static void main(String[] args) {
        if (rebuildAtlas) {
            Settings settings = new Settings();
            settings.maxWidth = 1024;
            settings.maxHeight = 1024;
            settings.debug = drawDebugOutline;
            TexturePacker2.process(settings, "assets-raw/images", "../BunnyHop-android/assets/images","bunnyhop.pack");
            
        }

        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "BunnyHop";
        cfg.useGL20 = false;
        cfg.width = 800;
        cfg.height = 480;
        new LwjglApplication(new BunnyHopMain(), cfg);
    }
}