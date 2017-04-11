#version 150

in vec3 fragmentColor;
in vec2 oPosition;

uniform vec2 shaderPosition;

out vec3 color;

void main() {
	float vDistance = distance(oPosition, shaderPosition);
//	vDistance = 11;
	if (vDistance <= 3) {
		color = mix(vec3(1), fragmentColor, vDistance/3);
	} else {
  		color = fragmentColor;
  	}
}