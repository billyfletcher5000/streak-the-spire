package StreakTheSpire.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class UIShaderRepository {
    private static String maskShaderVSPath = "StreakTheSpire/shaders/mask_shader_vs.glsl";
    private static String maskShaderPSPath = "StreakTheSpire/shaders/mask_shader_ps.glsl";
    private static ShaderProgram maskShader = null;;

    public static ShaderProgram getMaskShader() {
        if(maskShader != null)
            return maskShader;

        String maskShaderVS = Gdx.files.internal(maskShaderVSPath).readString();
        String maskShaderPS = Gdx.files.internal(maskShaderPSPath).readString();
        maskShader = new ShaderProgram(maskShaderVS, maskShaderPS);
        return maskShader;
    }
}
