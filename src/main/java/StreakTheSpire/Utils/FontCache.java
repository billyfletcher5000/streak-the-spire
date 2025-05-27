package StreakTheSpire.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class FontCache {
    private final HashMap<String, BitmapFont> fontCache = new HashMap<>();

    public void createSDFFont(String identifier, String fontFilePath, String texturePath, TextureCache textureCache) {
        if(fontCache.containsKey(identifier) && fontCache.get(identifier) != null)
            return;

        Texture texture = textureCache.getSDFFontTexture(texturePath);
        BitmapFont font = new BitmapFont(Gdx.files.internal(fontFilePath), new TextureRegion(texture), false);

        fontCache.put(identifier, font);
    }

    public BitmapFont getFont(String identifier) {
        return fontCache.get(identifier);
    }
}
