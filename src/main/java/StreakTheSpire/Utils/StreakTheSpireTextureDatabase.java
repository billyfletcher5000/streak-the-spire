package StreakTheSpire.Utils;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public enum StreakTheSpireTextureDatabase {
    MOD_ICON("StreakTheSpire/textures/modIcon.png"),
    TIP_BOX_NINESLICE("StreakTheSpire/textures/ui/tip_box_9slice_sq.png"),
    DEBUG_BOX_NINESLICE("StreakTheSpire/textures/ui/debug_box_9slice.png"),
    DEBUG_CENTRE("StreakTheSpire/textures/ui/debug_centre.png"),

    CURSOR_RESIZE("StreakTheSpire/textures/ui/cursor_resize.png"),
    CURSOR_MOVE("StreakTheSpire/textures/ui/cursor_move.png"),

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
