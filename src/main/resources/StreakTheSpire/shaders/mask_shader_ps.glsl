#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform vec4 u_mask_color;
uniform sampler2D u_texture;
void main()
{
  vec4 tex_color = texture2D(u_texture, v_texCoords);
  gl_FragColor.rgb = mix(v_color.rgb * tex_color.rgb, u_mask_color.rgb, u_mask_color.a);
  gl_FragColor.a = v_color.a * tex_color.a;
}