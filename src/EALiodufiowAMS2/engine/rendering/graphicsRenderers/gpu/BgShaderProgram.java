package EALiodufiowAMS2.engine.rendering.graphicsRenderers.gpu;

public final class BgShaderProgram extends ShaderProgram{
    private static final String VS_SRC = """
                #version 330 core
                
                layout(location = 0) in vec2 aPos;
                layout(location = 1) in vec2 aUV;
                
                out vec2 vUV;
                
                uniform float uScaleX;   // TILE / TILE_FIT / STRETCH
                uniform float uScaleY;
                
                void main() {
                    vUV = aUV * vec2(uScaleX, uScaleY);
                
                    // aPos w zakresie 0..1 → przeskaluj do NDC -1..1
                    vec2 pos = aPos * 2.0 - 1.0;
                    pos.y = -pos.y; // flip Y
                
                    gl_Position = vec4(pos, 0.0, 1.0);
                }
                """;


    private static final String FS_SRC = """
                #version 330 core
                
                in vec2 vUV;
                out vec4 FragColor;
                
                uniform sampler2D uTexture;
                
                void main() {
                    FragColor = texture(uTexture, vUV);
                }
                """;

    public BgShaderProgram() {
        super(VS_SRC, FS_SRC);
    }

}
