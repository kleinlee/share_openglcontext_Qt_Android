//vec4使用4个向量xyzw
attribute vec4 av_Position;
//这里绘制2D图形，所以使用vec2两个向量的即可xy
attribute vec2 af_Position;
varying vec2 v_texPo;
void main() {
    v_texPo = af_Position;
    gl_Position = av_Position;
}
