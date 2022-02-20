#version 330 core

uniform sampler2D sampler;

uniform vec3 camPos;
uniform mat3 camRot;
uniform mat4 projection;
uniform mat4 modelView;
uniform mat4 model;
uniform float currentScale;

uniform mat4 viewInverseMat;

in vec3 FragPos;
in vec3 pos;
in vec2 texCoord;

// math const
const float PI = 3.14159265359;
const float MAX = 10000.0;

void main()
{
    vec4 color = texture(sampler, FragPos.xy / currentScale);

    vec3 lightPos = (vec4(vec3(2500.0, 2500.0, 0.0) * currentScale, 1.0) * modelView).xyz;
    vec3 lightColor = vec3(1.0, 0.9, 0.85);

    vec3 norm = normalize(FragPos);
    vec3 lightDir = normalize(lightPos - FragPos);

    float specularStrength = 0.8;

    float diff = dot(norm, normalize(lightDir));
    float diffuse = pow(max(0.0, diff), 0.9);

    //    vec3 viewDir = normalize(camPos - FragPos);
    //    vec3 reflectDir = reflect(-lightDir, norm);
    //
    //    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 8);
    //    vec3 specular = specularStrength * spec * lightColor;

    vec3 result = (diffuse * 1.5 + 0.02) * lightColor;
    // gl_FragColor = vec4(specular, 1.0);
    //gl_FragColor = vec4(FragPos.xyz, 1.0);
    //gl_FragColor = vec4(texCoord, 0.0, 1.0);
    gl_FragColor = color * vec4(result, 1.0);
}