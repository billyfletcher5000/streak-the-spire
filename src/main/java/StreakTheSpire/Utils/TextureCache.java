package StreakTheSpire.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import javax.xml.soap.Text;
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

    public Texture getSDFFontTexture(String path) {
        if (textures.containsKey(path)) {
            return textures.get(path);
        }

        Texture texture = new Texture(Gdx.files.internal(path), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);

        textures.put(path, texture);
        return texture;
    }
}
