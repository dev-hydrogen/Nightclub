#version 150

in vec4 vertexColor;

uniform vec4 ColorModulator;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor;
    if (color.a == 0.0) {
        discard;
    }
    
    if (ColorModulator == vec4(0, 1, 0, 0.75)) {
        fragColor = color * vec4(1, 1, 1, 1);
    } else {
        fragColor = color * ColorModulator;
    }
}