package StreakTheSpire.Utils;

import com.badlogic.gdx.graphics.Color;

public enum ColorEnum {
    RED("cc6464"),
    GREEN("81c05a"),
    BLUE("65c4c9"),
    PURPLE("ae78c5"),
    YELLOW("c5a741"),
    WHITE("cbc7cc");

    private Color mainColor;
    private Color dimmedColor;

    ColorEnum(String mainColorHex) {
        this.mainColor = Color.valueOf(mainColorHex);
        Color c = mainColor.cpy();
        c.r -= 0.25f;
        c.g -= 0.25f;
        c.b -= 0.25f;
        c.clamp();
        this.dimmedColor = c;
    }

    public Color get() { return mainColor; }
    public Color getDimmed() { return dimmedColor; }

    public static ColorEnum getColor(Color color) {
        for (ColorEnum c : ColorEnum.values()) {
            if (c.mainColor.equals(color) || c.dimmedColor.equals(color)) {
                return c;
            }
        }

        return null;
    }
}
