#ifdef GL_ES
precision mediump float;
#endif

// varying input variables from our vertex shader
varying vec2 v_texCoords;

// Data sent from libgdx SpriteBatch
//(Read Only and the same for all fragments)
uniform sampler2D u_texture;
uniform sampler2D u_color_table;
uniform float u_palette_index;

void main()
{
    vec4 color = texture2D(u_texture, v_texCoords);
    //u_palette_index represents the y value for the color table, indicates whish palette will be used
    //the wizard sprite has red values corresponding to y values on the color table, indicating which color on the palette to use for a fragment
    vec2 index = vec2(u_palette_index, color.r);
    vec4 index_color = texture2D(u_color_table, index);
    //alpha from original texture used, the color table has alpha 1 everywhere
    gl_FragColor = vec4(index_color.rgb, color.a);

}