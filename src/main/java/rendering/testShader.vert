#version 150

in vec2 position;

out vec3 fragmentColor;
out vec2 oPosition;

uniform mat4 worldMatrix;
uniform mat4 MVP;
uniform vec3 color;

vec4 temp;

void main(){
  gl_Position = MVP * worldMatrix * vec4(position, 0.0, 1.0);
  temp = worldMatrix * vec4(position, 0.0, 1.0);
  oPosition = vec2(temp.x, temp.y);
  fragmentColor = color;
}