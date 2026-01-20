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

    private int fboId;
    private int colorTexId;
    private int depthRbId;

    private final ShaderProgram shader;

    private final Map<Mesh, GpuMeshHandle> meshCache = new HashMap<>();
    private final Map<BufferedImage, Integer> textureCache = new IdentityHashMap<>();
    private final List<DrawCommand> commands = new ArrayList<>();

    private static boolean glInitialized = false;
    private static long hiddenWindow = 0;

    private static void ensureGlContext() {
        if (glInitialized) return;

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Failed to init GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);

        hiddenWindow = GLFW.glfwCreateWindow(1, 1, "", 0, 0);
        if (hiddenWindow == 0) {
            throw new IllegalStateException("Failed to create hidden GLFW window");
        }

        GLFW.glfwMakeContextCurrent(hiddenWindow);
        GL.createCapabilities();

        glInitialized = true;
    }


    public GpuBackend(int width, int height) {
        ensureGlContext();

        this.width = width;
        this.height = height;

        initFbo();
        this.shader = createDefaultShader();
    }

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        recreateFbo();
    }

    @Override
    public void beginFrame(Camera camera) {
        this.camera = camera;
        commands.clear();

        glBindFramebuffer(GL_FRAMEBUFFER, fboId);
        glViewport(0, 0, width, height);
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.3f, 0.35f, 0.4f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void submit(DrawCommand cmd) {
        commands.add(cmd);
    }

    @Override
    public void endFrame() {
        if (camera == null) return;

        Matrix4 view = camera.getViewMatrix();
        Matrix4 proj = camera.getProjectionMatrix();

        shader.bind();

        // Ustaw view/proj jako uniformy
        shader.setUniformMat4("uView", view);
        shader.setUniformMat4("uProj", proj);

        // Na początek: bez sortowania, po kolei
        for (DrawCommand cmd : commands) {
            Mesh mesh = cmd.getMesh();
            GpuMeshHandle handle = meshCache.computeIfAbsent(mesh, this::uploadMesh);

            // Model matrix
            Matrix4 model = cmd.getTransform().toModelMatrix();
            shader.setUniformMat4("uModel", model);

            // Materiał: kolor + tekstura
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
            }

            glBindVertexArray(handle.vaoId);

            Mesh.FaceRange range = mesh.getFaceRange(cmd.getFaceType());
            if (range == null) continue;

            glDrawElements(GL_TRIANGLES, range.indexCount, GL_UNSIGNED_INT, (long) range.startIndex * Integer.BYTES);
        }

        glBindVertexArray(0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        shader.unbind();
    }

    @Override
    public BufferedImage getFrameBuffer() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

        glBindTexture(GL_TEXTURE_2D, colorTexId);

        IntBuffer ib = BufferUtils.createIntBuffer(width * height);
        glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, ib);

        int[] raw = new int[width * height];
        ib.get(raw);

        // Odwrócenie pionowe + zamiana RGBA → ARGB
        for (int y = 0; y < height; y++) {
            int srcRow = (height - 1 - y) * width;
            int dstRow = y * width;

            for (int x = 0; x < width; x++) {
                int rgba = raw[srcRow + x];

                int r = (rgba)       & 0xFF;
                int g = (rgba >> 8)  & 0xFF;
                int b = (rgba >> 16) & 0xFF;
                int a = (rgba >> 24) & 0xFF;

                pixels[dstRow + x] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        glBindTexture(GL_TEXTURE_2D, 0);
        return img;
    }



    // ----------------- FBO -----------------

    private void initFbo() {
        fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        // Color attachment
        colorTexId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, colorTexId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexId, 0);

        // Depth renderbuffer
        depthRbId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthRbId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRbId);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("FBO is not complete");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void recreateFbo() {
        if (fboId != 0) {
            glDeleteFramebuffers(fboId);
            glDeleteTextures(colorTexId);
            glDeleteRenderbuffers(depthRbId);
        }
        initFbo();
    }

    // ----------------- Mesh upload -----------------

    private GpuMeshHandle uploadMesh(Mesh mesh) {
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();

        glBindVertexArray(vao);

        Vertex[] verts = mesh.getVertices();
        int[] indices = mesh.getIndices();

        // Vertex data: position (3) + uv (2) = 5 floats
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
        // OpenGL expects bottom-up, ale tu możemy wysłać top-down i tylko przy odczycie odwracać
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

    // ----------------- Shader -----------------

    private ShaderProgram createDefaultShader() {
        String vsSrc = """
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

        String fsSrc = """
                #version 330 core
                in vec2 vTexCoord;
                out vec4 FragColor;

                uniform sampler2D uTexture;
                uniform vec4 uColor;
                uniform int uUseTexture;

                void main() {
                    if (uUseTexture == 1) {
                        vec4 texColor = texture(uTexture, vTexCoord);
                        FragColor = texColor * uColor;
                    } else {
                        FragColor = uColor;
                    }
                }
                """;

        return new ShaderProgram(vsSrc, fsSrc);
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
        private final int programId;

        public ShaderProgram(String vertexSrc, String fragmentSrc) {
            int vs = compileShader(GL_VERTEX_SHADER, vertexSrc);
            int fs = compileShader(GL_FRAGMENT_SHADER, fragmentSrc);

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
