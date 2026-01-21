package EALiodufiowAMS2.rendering.graphicsRenderers;

import EALiodufiowAMS2.helpers.Matrix4;
import EALiodufiowAMS2.helpers.Mesh;
import EALiodufiowAMS2.helpers.Vertex;
import EALiodufiowAMS2.rendering.DrawCommand;
import EALiodufiowAMS2.rendering.renderingObject.Material;
import EALiodufiowAMS2.world.Camera;
import EALiodufiowAMS2.world.scenes.Scene;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL33.*;


public final class GpuBackend implements RenderBackend {

    private int width;
    private int height;

    private Scene scene;
    private Camera camera;

    // GPU resources
    private final ShaderProgram shader;
    private final Map<Mesh, GpuMeshHandle> meshCache = new HashMap<>();
    private final Map<BufferedImage, Integer> textureCache = new IdentityHashMap<>();

    // per-frame commands (budowane przez RenderingEngine w EDT)
    private final List<DrawCommand> commands = new ArrayList<>();

    public GpuBackend(int width, int height) {
        this.width = width;
        this.height = height;
        // UWAGA: tu nie dotykamy GL – kontekst nie jest jeszcze gotowy.
        // Shader i inne zasoby GL tworzymy w init(), wołanym z GlRenderCanvas.initGL().
        this.shader = new ShaderProgram(); // tylko trzyma źródła, realny program w init()
    }

    // ----------------- API RenderBackend -----------------

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        // brak GL-call’i – viewport ustawiamy w drawPreparedFrame()
    }

    @Override
    public void beginFrame(Camera camera) {
        this.camera = camera;
        commands.clear();
    }

    @Override
    public void submit(DrawCommand cmd) {
        commands.add(cmd);
    }

    @Override
    public void endFrame() {
        // nic GL – tylko kończymy budowanie listy komend
    }

    @Override
    public BufferedImage getFrameBuffer() {
        // GPU backend nie wspiera readbacku – renderuje bezpośrednio do canvasu
        return null;
    }

    // ----------------- API używane przez GlRenderCanvas -----------------

    /**
     * Wołane z GlRenderCanvas.initGL(), gdy kontekst GL jest już current.
     * Tu tworzymy realny program shaderowy i inne zasoby GL zależne od kontekstu.
     */
    public void init() {
        shader.initProgram(); // kompilacja i linkowanie shaderów
        // brak FBO – rysujemy bezpośrednio do default framebuffer canvasu
    }

    /**
     * Wołane z GlRenderCanvas.paintGL(), gdy kontekst GL jest current.
     * Wykonuje GL-call’e na podstawie komend zbudowanych w beginFrame/submit/endFrame.
     */
    public void drawPreparedFrame() {
        if (camera == null || scene == null) {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glViewport(0, 0, width, height);
            glClearColor(0f, 0f, 0f, 1f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            return;
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // tło (void material)
        Material voidMat = scene.getVoidMAterial();
        if (voidMat != null) {
            java.awt.Color c = voidMat.getColor();
            float r = c.getRed()   / 255.0f;
            float g = c.getGreen() / 255.0f;
            float b = c.getBlue()  / 255.0f;
            float a = c.getAlpha() / 255.0f;
            glClearColor(r, g, b, a);
        } else {
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Matrix4 view = camera.getViewMatrix();
        Matrix4 proj = camera.getProjectionMatrix();

        shader.bind();
        shader.setUniformMat4("uView", view);
        shader.setUniformMat4("uProj", proj);

        for (DrawCommand cmd : commands) {
            Mesh mesh = cmd.getMesh();
            GpuMeshHandle handle = meshCache.computeIfAbsent(mesh, this::uploadMesh);

            // model matrix
            Matrix4 model = cmd.getTransform().toModelMatrix();
            shader.setUniformMat4("uModel", model);

            // materiał
            Material mat = cmd.getMaterial();
            int texId = 0;
            boolean hasTexture = false;

            if (mat.getTexture() != null) {
                texId = textureCache.computeIfAbsent(mat.getTexture(), this::uploadTexture);
                hasTexture = true;
            }

            shader.setUniformVec4("uColor", mat.getColor());
            shader.setUniformInt("uUseTexture", hasTexture ? 1 : 0);

            if (hasTexture) {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, texId);
                shader.setUniformInt("uTexture", 0);
            } else {
                glBindTexture(GL_TEXTURE_2D, 0);
            }

            glBindVertexArray(handle.vaoId);

            Mesh.FaceRange range = mesh.getFaceRange(cmd.getFaceType());
            if (range != null) {
                glDrawElements(GL_TRIANGLES, range.indexCount, GL_UNSIGNED_INT,
                        (long) range.startIndex * Integer.BYTES);
            }
        }

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
        shader.unbind();
    }

    /**
     * Sprzątanie zasobów GL – wołane z GlRenderCanvas.dispose().
     */
    public void dispose() {
        // meshe
        for (GpuMeshHandle h : meshCache.values()) {
            glDeleteBuffers(h.vboId);
            glDeleteBuffers(h.eboId);
            glDeleteVertexArrays(h.vaoId);
        }
        meshCache.clear();

        // tekstury
        for (Integer texId : textureCache.values()) {
            glDeleteTextures(texId);
        }
        textureCache.clear();

        shader.dispose();
    }

    // ----------------- Mesh upload -----------------

    private GpuMeshHandle uploadMesh(Mesh mesh) {
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();

        glBindVertexArray(vao);

        Vertex[] verts = mesh.getVertices();
        int[] indices = mesh.getIndices();

        // position (3) + uv (2) = 5 floats
        FloatBuffer vbuf = BufferUtils.createFloatBuffer(verts.length * 5);
        for (Vertex v : verts) {
            vbuf.put((float) v.position.x);
            vbuf.put((float) v.position.y);
            vbuf.put((float) v.position.z);
            vbuf.put((float) v.u);
            vbuf.put((float) v.v);
        }
        vbuf.flip();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vbuf, GL_STATIC_DRAW);

        IntBuffer ibuf = BufferUtils.createIntBuffer(indices.length);
        ibuf.put(indices).flip();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ibuf, GL_STATIC_DRAW);

        int stride = 5 * Float.BYTES;

        // aPosition (location = 0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0L);
        glEnableVertexAttribArray(0);

        // aTexCoord (location = 1)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3L * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);

        return new GpuMeshHandle(vao, vbo, ebo, verts.length, indices.length);
    }

    // ----------------- Texture upload -----------------

    private int uploadTexture(BufferedImage img) {
        int texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);

        int w = img.getWidth();
        int h = img.getHeight();

        int[] pixels = new int[w * h];
        img.getRGB(0, 0, w, h, pixels, 0, w);

        ByteBuffer buffer = BufferUtils.createByteBuffer(w * h * 4);
        // wysyłamy top-down, a w shaderze odwracamy v
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = pixels[y * w + x];
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8)  & 0xFF;
                int b = (argb)       & 0xFF;
                buffer.put((byte) r);
                buffer.put((byte) g);
                buffer.put((byte) b);
                buffer.put((byte) a);
            }
        }
        buffer.flip();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glBindTexture(GL_TEXTURE_2D, 0);
        return texId;
    }

    // ----------------- Helper classes -----------------

    private static final class GpuMeshHandle {
        final int vaoId;
        final int vboId;
        final int eboId;
        final int vertexCount;
        final int indexCount;

        GpuMeshHandle(int vaoId, int vboId, int eboId, int vertexCount, int indexCount) {
            this.vaoId = vaoId;
            this.vboId = vboId;
            this.eboId = eboId;
            this.vertexCount = vertexCount;
            this.indexCount = indexCount;
        }
    }

    public static final class ShaderProgram {
        private int programId = 0;

        // źródła shaderów – program tworzymy dopiero w init()
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

                void main() {
                    if (uUseTexture == 1) {
                        vec4 texColor = texture(uTexture, vec2(vTexCoord.x, 1.0 - vTexCoord.y));
                        FragColor = texColor;
                    } else {
                        FragColor = uColor;
                    }
                }
                """;

        public ShaderProgram() {
            // pusty – realny program tworzymy w initProgram()
        }

        public void initProgram() {
            if (programId != 0) return; // już zainicjalizowany

            int vs = compileShader(GL_VERTEX_SHADER, VS_SRC);
            int fs = compileShader(GL_FRAGMENT_SHADER, FS_SRC);

            programId = glCreateProgram();
            glAttachShader(programId, vs);
            glAttachShader(programId, fs);
            glLinkProgram(programId);

            if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
                String log = glGetProgramInfoLog(programId);
                throw new IllegalStateException("Program link error: " + log);
            }

            glDetachShader(programId, vs);
            glDetachShader(programId, fs);
            glDeleteShader(vs);
            glDeleteShader(fs);
        }

        private int compileShader(int type, String src) {
            int id = glCreateShader(type);
            glShaderSource(id, src);
            glCompileShader(id);

            if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
                String log = glGetShaderInfoLog(id);
                throw new IllegalStateException("Shader compile error: " + log);
            }
            return id;
        }

        public void bind() {
            glUseProgram(programId);
        }

        public void unbind() {
            glUseProgram(0);
        }

        public void dispose() {
            if (programId != 0) {
                glDeleteProgram(programId);
                programId = 0;
            }
        }

        public void setUniformMat4(String name, Matrix4 mat) {
            int loc = glGetUniformLocation(programId, name);
            if (loc < 0) return;
            FloatBuffer fb = BufferUtils.createFloatBuffer(16);
            for (int i = 0; i < 16; i++) {
                fb.put((float) mat.m[i]);
            }
            fb.flip();
            glUniformMatrix4fv(loc, false, fb);
        }

        public void setUniformVec4(String name, java.awt.Color color) {
            int loc = glGetUniformLocation(programId, name);
            if (loc < 0) return;
            float r = color.getRed()   / 255.0f;
            float g = color.getGreen() / 255.0f;
            float b = color.getBlue()  / 255.0f;
            float a = color.getAlpha() / 255.0f;
            glUniform4f(loc, r, g, b, a);
        }

        public void setUniformInt(String name, int value) {
            int loc = glGetUniformLocation(programId, name);
            if (loc < 0) return;
            glUniform1i(loc, value);
        }
    }
}
