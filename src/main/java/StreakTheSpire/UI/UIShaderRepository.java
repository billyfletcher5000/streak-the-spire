package StreakTheSpire.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class UIShaderRepository {
    private static final String maskShaderVSPath = "StreakTheSpire/shaders/mask_shader_vs.glsl";
    private static final String maskShaderPSPath = "StreakTheSpire/shaders/mask_shader_ps.glsl";
    private static ShaderProgram maskShader = null;;

    public static ShaderProgram getMaskShader() {
        if(maskShader != null)
            return maskShader;

        String maskShaderVS = Gdx.files.internal(maskShaderVSPath).readString();
        String maskShaderPS = Gdx.files.internal(maskShaderPSPath).readString();
        maskShader = new ShaderProgram(maskShaderVS, maskShaderPS);
        return maskShader;
    }

    private static final String sdfOutlineShadowFontShaderVSPath = "StreakTheSpire/shaders/sdf_outline_shadow_font_shader_vs.glsl";
    private static final String sdfOutlineShadowFontShaderPSPath = "StreakTheSpire/shaders/sdf_outline_shadow_font_shader_ps.glsl";
    private static ShaderProgram sdfOutlineShadowFontShader = null;

    public static ShaderProgram getSDFOutlineShadowFontShader() {
        if(sdfOutlineShadowFontShader != null)
            return sdfOutlineShadowFontShader;

        String maskShaderVS = Gdx.files.internal(sdfOutlineShadowFontShaderVSPath).readString();
        String maskShaderPS = Gdx.files.internal(sdfOutlineShadowFontShaderPSPath).readString();
        sdfOutlineShadowFontShader = new ShaderProgram(maskShaderVS, maskShaderPS);
        return sdfOutlineShadowFontShader;
    }
}
