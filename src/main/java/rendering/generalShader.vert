#version 150

in vec2 position;

out vec3 fragmentColor;
uniform mat4 MVP;
uniform vec3 color;

void main(){
  gl_Position = MVP * vec4(position, 0.0, 1.0);
  fragmentColor = color;
}