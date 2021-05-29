#version 330 core

in vec3 vPosition;
// in vec3 vNormal;

uniform mat4 modelview;
uniform mat4 projection;

//out vec4 fPosition;
//out mat4 mv;
//out vec3 fNormal;

void main()
{
    //fPosition = vPosition;
	//fNormal = vNormal;
    //mv = modelview;
	gl_Position = projection*modelview*vec4(vPosition, 1.0);
}