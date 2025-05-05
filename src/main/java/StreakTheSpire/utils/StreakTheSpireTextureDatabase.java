package StreakTheSpire.utils;

import com.badlogic.gdx.graphics.Texture;

public enum StreakTheSpireTextureDatabase {
    MOD_ICON("StreakTheSpire/textures/modIcon.png")
    ;

    private final String internalPath;
    private Texture texture;

    StreakTheSpireTextureDatabase(String internalPath) {
        this.internalPath = internalPath;
    }

    public void load() {
        this.texture = new Texture(internalPath);
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
