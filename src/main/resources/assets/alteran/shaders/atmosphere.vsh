#version 330

in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texCoordIn;

uniform mat4 modelView;
uniform mat4 projection;
uniform mat4 model;

uniform mat4 viewInverseMat;

out vec3 FragPos;
out vec3 pos;
out vec2 texCoord;

void main()
{
    gl_Position = projection * modelView * vec4(position, 1.0);
    FragPos = (viewInverseMat * vec4(position, 1.0)).xyz;
    pos = gl_Position.xyz;
    texCoord = texCoordIn;
}