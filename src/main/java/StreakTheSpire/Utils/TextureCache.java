package StreakTheSpire.Utils;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.util.HashMap;

public class TextureCache {

    private HashMap<String, Texture> textures = new HashMap<>();

    public TextureCache() {}

    public Texture getTexture(String path) { return getTexture(path, true); }

    public Texture getTexture(String path, boolean linearFiltering) {
        if (textures.containsKey(path)) {
            return textures.get(path);
        }

        Texture texture = ImageMaster.loadImage(path, linearFiltering);
        if (texture != null)
            textures.put(path, texture);

        return texture;
    }
}
