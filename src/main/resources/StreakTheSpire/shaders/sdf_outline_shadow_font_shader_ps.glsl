#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_fontScale;
uniform float u_outlineDistance; // Between 0 and 0.5, 0 = thick outline, 0.5 = no outline
uniform vec4 u_outlineColor;
uniform vec2 u_shadowOffset; // Between 0 and spread(3.0) / textureSize
uniform float u_shadowSmoothing; // Between 0 and 0.5

varying vec4 v_color;
varying vec2 v_texCoord;

const vec4 shadowColor = vec4(0, 0, 0, 0.33);

void main() {
    float smoothing = 1/16.0f;//0.25 / (3.0 * u_fontScale);
    float distance = texture2D(u_texture, v_texCoord).a;
    float outlineFactor = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
    vec4 text = mix(u_outlineColor, v_color, outlineFactor);
    float alpha = smoothstep(u_outlineDistance - smoothing, u_outlineDistance + smoothing, distance);
    text.a = text.a * alpha;
    gl_FragColor = text;

    float shadowDistance = texture2D(u_texture, v_texCoord - u_shadowOffset).a;
    float shadowAlpha = smoothstep(0.5 - u_shadowSmoothing, 0.5 + u_shadowSmoothing, shadowDistance);
    vec4 shadow = vec4(shadowColor.rgb, shadowColor.a * shadowAlpha);

    gl_FragColor = mix(shadow, text, text.a);
}
