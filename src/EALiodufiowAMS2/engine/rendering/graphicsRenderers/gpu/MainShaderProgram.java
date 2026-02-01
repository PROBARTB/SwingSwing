package EALiodufiowAMS2.engine.rendering.graphicsRenderers.gpu;

public final class MainShaderProgram extends ShaderProgram{
    private static final String VS_SRC = """
                #version 330 core
                layout(location = 0) in vec3 aPosition;
                layout(location = 1) in vec2 aTexCoord;

                uniform mat4 uModel;
                uniform mat4 uView;
                uniform mat4 uProj;

                out vec2 vTexCoord;

                void main() {
                    vTexCoord = aTexCoord;
                    gl_Position = uProj * uView * uModel * vec4(aPosition, 1.0);
                }
                """;

    private static final String FS_SRC = """
                #version 330 core
                in vec2 vTexCoord;
                out vec4 FragColor;
                
                uniform sampler2D uTexture;
                uniform vec4 uColor;
                uniform int uUseTexture;
                
                uniform int uBlendMode;      // 0 = OPAQUE, 1 = CUTOUT, 2 = TRANSPARENT
                uniform float uAlphaCutoff;
                
                void main() {
                
                    vec4 baseColor = uColor;
                
                    if (uUseTexture == 1) {
                        vec4 texColor = texture(uTexture, vec2(vTexCoord.x, 1.0 - vTexCoord.y));
                        baseColor = texColor;
                    }
                
                    if (uBlendMode == 1) {
                        if (baseColor.a < uAlphaCutoff) {
                            discard;
                        }
                        FragColor = baseColor;
                        return;
                    }
                
                    FragColor = baseColor;
                }
                """;

    public MainShaderProgram() {
        super(VS_SRC, FS_SRC);
    }

}
