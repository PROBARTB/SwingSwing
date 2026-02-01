package EALiodufiowAMS2.engine.rendering.graphicsRenderers;

import EALiodufiowAMS2.engine.rendering.*;
import EALiodufiowAMS2.engine.rendering.renderingObject.MaterialBlendMode;
import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.Camera;
import EALiodufiowAMS2.engine.Scene;
import org.lwjgl.BufferUtils;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL33.*;


public final class GpuBackend implements RenderBackend {
    private RenderingEngineListener listener;
    private GpuInfo gpuInfo;

    private int width;
    private int height;

    private Scene scene;
    private Camera camera;

    private boolean initialized = false;

    private final ShaderProgram shader;
    private final Map<Mesh, GpuMeshHandle> meshCache = new HashMap<>();
    private final Map<BufferedImage, Integer> textureCache = new IdentityHashMap<>();

    private final List<DrawCommand> commands = new ArrayList<>();

    public GpuBackend(int width, int height) {
        this.width = width;
        this.height = height;
        this.shader = new ShaderProgram();
    }

    @Override
    public void setListener(RenderingEngineListener listener) {
        this.listener = listener;
    }

    @Override
    public RenderingMode getRenderingMode() { return RenderingMode.GPU; }
    @Override
    public int getWidth() { return this.width; }
    @Override
    public int getHeight() { return this.height; }

    public GpuInfo getGpuInfo() {
        if(!initialized) throw new IllegalStateException("GpuBackend not initialized");
        return gpuInfo;
    }
    public void setGpuInfo(GpuInfo gpuInfo) { this.gpuInfo = gpuInfo; }

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
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
    }

    @Override
    public BufferedImage getFrameBuffer() {
        if (!initialized) return null;
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = (x + (width * y)) * 4;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                int a = buffer.get(i + 3) & 0xFF;
                img.setRGB(x, height - y - 1, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return img;
    }

    @Override
    public void init() {
        shader.initProgram();
        initialized = true;
        listener.onBackendInitialized();
    }

    private static final class RenderItem {
        GpuMeshHandle handle;
        Mesh.SubMesh subMesh;
        Material material;
        Matrix4 model;
        double depthKey;
    }

    private final List<RenderItem> opaqueItems      = new ArrayList<>();
    private final List<RenderItem> cutoutItems      = new ArrayList<>();
    private final List<RenderItem> transparentItems = new ArrayList<>();


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

        opaqueItems.clear();
        cutoutItems.clear();
        transparentItems.clear();

        // copy the list to prevent concurrent access due to multithreading
        for (DrawCommand cmd : new ArrayList<>(commands)) {
            Mesh mesh = cmd.getMesh();
            GpuMeshHandle handle = meshCache.computeIfAbsent(mesh, this::uploadMesh);

            Matrix4 model = cmd.getTransform().toModelMatrix();
            Map<Integer, Material> materialsBySlot = cmd.getMaterialsBySlot();

            for (Mesh.SubMesh sub : mesh.getSubMeshes()) {
                Material mat = materialsBySlot.get(sub.materialSlot);
                if (mat == null) continue;

                RenderItem item = new RenderItem();
                item.handle   = handle;
                item.subMesh  = sub;
                item.material = mat;
                item.model    = model;
                item.depthKey = computeDepthKey(model, sub.bounds);

                switch (mat.getBlendMode()) {
                    case OPAQUE      -> opaqueItems.add(item);
                    case CUTOUT      -> cutoutItems.add(item);
                    case TRANSPARENT -> transparentItems.add(item);
                }
            }
        }


        // OPAQUE
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        for (RenderItem item : opaqueItems) {
            drawRenderItem(item);
        }

        // CUTOUT
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        for (RenderItem item : cutoutItems) {
            drawRenderItem(item);
        }

        // TRANSPARENT
        glDepthMask(false);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);

        transparentItems.sort(Comparator.comparingDouble(item -> -item.depthKey));
        for (RenderItem item : transparentItems) {
            drawRenderItem(item);
        }

        glDepthMask(true);

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
        shader.unbind();
    }


    private double computeDepthKey(Matrix4 model, BoundingBox localBounds) {
        Vec3 centerLocal = localBounds.getCenter();
        Vec3 centerWorld = model.transformPoint(centerLocal);
        Vec3 camPos      = camera.getTransform().getPos();
        return centerWorld.sub(camPos).lengthSquared();
    }



    private void drawRenderItem(RenderItem item) {
        glBindVertexArray(item.handle.vaoId);

        // model matrix
        shader.setUniformMat4("uModel", item.model);

        Mesh.SubMesh sub = item.subMesh;
        Material mat     = item.material;

        int glMode = switch (sub.primitiveType) {
            case TRIANGLES -> GL_TRIANGLES;
            case LINES     -> GL_LINES;
            default        -> throw new UnsupportedOperationException("Unsupported primitive type: " + sub.primitiveType);
        };

        boolean useTexture = (sub.primitiveType == PrimitiveType.TRIANGLES) && (mat.getTexture() != null);
        int texId = 0;

        if (useTexture) {
            texId = textureCache.computeIfAbsent(mat.getTexture(), this::uploadTexture);
        }

        shader.setUniformVec4("uColor", mat.getColor());
        shader.setUniformInt("uUseTexture", useTexture ? 1 : 0);
        shader.setUniformInt("uBlendMode", mat.getBlendMode().ordinal());
        shader.setUniformFloat("uAlphaCutoff", 0.5f); // hardcoded alpha cutoff level

        if (useTexture) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texId);
            shader.setUniformInt("uTexture", 0);
        } else {
            glBindTexture(GL_TEXTURE_2D, 0);
        }

        glDrawElements(
                glMode,
                sub.indexCount,
                GL_UNSIGNED_INT,
                (long) sub.indexOffset * Integer.BYTES
        );
    }



    public void dispose() {
        for (GpuMeshHandle h : meshCache.values()) {
            glDeleteBuffers(h.vboId);
            glDeleteBuffers(h.eboId);
            glDeleteVertexArrays(h.vaoId);
        }
        meshCache.clear();

        for (Integer texId : textureCache.values()) {
            glDeleteTextures(texId);
        }
        textureCache.clear();

        shader.dispose();
    }


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

    private int uploadTexture(BufferedImage img) {
        int texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);

        int w = img.getWidth();
        int h = img.getHeight();

        int[] pixels = new int[w * h];
        img.getRGB(0, 0, w, h, pixels, 0, w);

        ByteBuffer buffer = BufferUtils.createByteBuffer(w * h * 4);
        // top-down send & rotate v in the shader later
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

    private record GpuMeshHandle(int vaoId, int vboId, int eboId, int vertexCount, int indexCount) {
    }

    public static final class ShaderProgram {
        private int programId = 0;

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

        public ShaderProgram() {
        }

        public void initProgram() {
            if (programId != 0) return;

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

        public void setUniformFloat(String name, float value) {
            int loc = glGetUniformLocation(programId, name);
            if (loc < 0) return;
            glUniform1f(loc, value);
        }

    }
}
