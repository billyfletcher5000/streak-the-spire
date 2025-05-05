package StreakTheSpire.Utils;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public enum StreakTheSpireTextureDatabase {
    MOD_ICON("StreakTheSpire/textures/modIcon.png"),

    IRONCLAD_ICON("images/ui/charSelect/ironcladButton.png"),
    SILENT_ICON("images/ui/charSelect/silentButton.png"),
    DEFECT_ICON("images/ui/charSelect/defectButton.png"),
    WATCHER_ICON("images/ui/charSelect/watcherButton.png")
    ;

    private final String internalPath;
    private Texture texture;

    StreakTheSpireTextureDatabase(String internalPath) {
        this.internalPath = internalPath;
    }

    public void load() {
        this.texture = ImageMaster.loadImage(internalPath);
    }

    public Texture getTexture() {
        return texture;
    }

    public static void loadAll() {
        for(StreakTheSpireTextureDatabase item : StreakTheSpireTextureDatabase.values()) {
            item.load();
        }
    }
}
