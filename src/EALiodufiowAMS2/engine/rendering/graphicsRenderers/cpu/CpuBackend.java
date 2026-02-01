package EALiodufiowAMS2.engine.rendering.graphicsRenderers.cpu;

import EALiodufiowAMS2.engine.rendering.*;
import EALiodufiowAMS2.engine.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.helpers.Matrix4;
import EALiodufiowAMS2.helpers.Mesh;
import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.helpers.Vertex;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.Camera;
import EALiodufiowAMS2.engine.Scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public final class CpuBackend implements RenderBackend {
    private RenderingEngineListener listener;

    private final Rasterizer rasterizer;
    private Camera camera;
    private Scene scene;

    private BufferedImage frontBuffer;
    private BufferedImage backBuffer; // double buffering, because rendering is on a separate thread and swing (CpuRenderView) reads in the wrong moment
    private final Object bufferLock = new Object();

    public CpuBackend(int width, int height) {
        setBuffers(width, height);
        this.rasterizer = new Rasterizer(backBuffer);
    }

    @Override
    public void setListener(RenderingEngineListener listener) {
        this.listener = listener;
    }

    @Override
    public void init() {
        listener.onBackendInitialized();
    }

    @Override
    public RenderingMode getRenderingMode() { return RenderingMode.CPU; }
    @Override
    public int getWidth() { return this.rasterizer.getBufferWidth(); }
    @Override
    public int getHeight() { return this.rasterizer.getBufferHeight(); }

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void resize(int width, int height) {
        setBuffers(width, height);
    }

    @Override
    public void beginFrame(Camera camera) {
        this.camera = camera;

        rasterizer.setFrameBuffer(backBuffer);
        rasterizer.clearBuffers(scene.getVoidMAterial());
    }

    private void setBuffers(int width, int height) {
        if (width <= 0 || height <= 0)  throw new IllegalArgumentException("Buffer size must be positive");
        frontBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void submit(DrawCommand cmd) {
        Mesh mesh = cmd.getMesh();
        Map<Integer, Material> materialsBySlot = cmd.getMaterialsBySlot();

        Matrix4 model = cmd.getTransform().toModelMatrix();
        Matrix4 view  = camera.getViewMatrix();
        Matrix4 proj  = camera.getProjectionMatrix();

        Matrix4 mv  = view.multiply(model);
        Matrix4 mvp = proj.multiply(mv);

        Vertex[] vertices = mesh.getVertices();
        int[] indices     = mesh.getIndices();

        Vec3[] viewSpace = new Vec3[vertices.length];
        Vec3[] ndcSpace  = new Vec3[vertices.length];

        transformVertices(vertices, mv, mvp, viewSpace, ndcSpace);

        for (Mesh.SubMesh sub : mesh.getSubMeshes()) {
            Material material = materialsBySlot.get(sub.materialSlot);
            if (material == null) continue;

            switch (sub.primitiveType) {
                case TRIANGLES -> rasterizeMesh(sub, indices, vertices, viewSpace, ndcSpace, material);
                case LINES     -> rasterizeLines(sub, indices, vertices, viewSpace, ndcSpace, material);
                default -> throw new UnsupportedOperationException("Unsupported primitive type: " + sub.primitiveType);
            }
        }
    }



    @Override
    public void endFrame() {
        swapBuffers();
    }
    public void swapBuffers() {
        synchronized (bufferLock) {
            BufferedImage tmp = frontBuffer;
            frontBuffer = backBuffer;
            backBuffer = tmp;
        }
    }


    @Override
    public BufferedImage getFrameBuffer() {
        synchronized (bufferLock) {
            return frontBuffer;
        }
    }

    public Object getBufferLock() {
        return bufferLock;
    }

    private void transformVertices(
            Vertex[] vertices,
            Matrix4 mv,
            Matrix4 mvp,
            Vec3[] viewSpace,
            Vec3[] ndcSpace
    ) {
        for (int i = 0; i < vertices.length; i++) {
            Vec3 pos = vertices[i].position;

            viewSpace[i] = mv.transformPoint(pos); // Model View - cords in camera space
            ndcSpace[i] = mvp.transformPoint(pos); // Model View Projection - NDC - Normalized Device Coordinates - 3D normalized [-1,1] in cam space.
        }
    }

    private void rasterizeMesh(
            Mesh.SubMesh sub,
            int[] indices,
            Vertex[] vertices,
            Vec3[] viewSpace,
            Vec3[] ndcSpace,
            Material material
    ) {
        int w = rasterizer.getBufferWidth();
        int h = rasterizer.getBufferHeight();

        int start = sub.indexOffset;
        int end   = sub.indexOffset + sub.indexCount;

        for (int i = start; i < end; i += 3) {
            int i0 = indices[i];
            int i1 = indices[i + 1];
            int i2 = indices[i + 2];

            if (!isTriangleInFront(viewSpace, i0, i1, i2)) continue;

            Point[] pts = projectTriangleToScreen(ndcSpace, i0, i1, i2, w, h);
            if (pts == null) continue;

            double[] depths = gatherDepths(viewSpace, i0, i1, i2);
            double[] uvs    = gatherUVs(vertices, i0, i1, i2);

            rasterizer.drawTriangle(
                    pts,
                    depths,
                    uvs,
                    material.getTexture(),
                    material.getColor()
            );
        }
    }



    private boolean isTriangleInFront(Vec3[] viewSpace, int i0, int i1, int i2) {
        return (viewSpace[i0].z < 0.0) || (viewSpace[i1].z < 0.0) || (viewSpace[i2].z < 0.0);
    }

    private Point[] projectTriangleToScreen(
            Vec3[] ndcSpace,
            int i0, int i1, int i2,
            int width, int height
    ) {
        Point2DWithDepth s0 = projectToScreen(ndcSpace[i0], width, height);
        Point2DWithDepth s1 = projectToScreen(ndcSpace[i1], width, height);
        Point2DWithDepth s2 = projectToScreen(ndcSpace[i2], width, height);

        if (s0 == null || s1 == null || s2 == null) return null;

        return new Point[] {
                new Point(s0.x, s0.y),
                new Point(s1.x, s1.y),
                new Point(s2.x, s2.y)
        };
    }

    private double[] gatherDepths(Vec3[] viewSpace, int i0, int i1, int i2) {
        return new double[] {
                -viewSpace[i0].z,
                -viewSpace[i1].z,
                -viewSpace[i2].z
        };
    }

    private double[] gatherUVs(Vertex[] vertices, int i0, int i1, int i2) {
        return new double[] {
                vertices[i0].u, vertices[i0].v,
                vertices[i1].u, vertices[i1].v,
                vertices[i2].u, vertices[i2].v
        };
    }


    private record Point2DWithDepth(int x, int y, double z) { }

    private Point2DWithDepth projectToScreen(Vec3 ndc, int width, int height) {
        double x = ndc.x;
        double y = ndc.y;

        int sx = (int) Math.round((x * 0.5 + 0.5) * width);
        int sy = (int) Math.round((-y * 0.5 + 0.5) * height);

        return new Point2DWithDepth(sx, sy, ndc.z);
    }

    private boolean isLineInFront(Vec3[] viewSpace, int i0, int i1) {
        return (viewSpace[i0].z < 0.0) || (viewSpace[i1].z < 0.0);
    }


    private void rasterizeLines(
            Mesh.SubMesh sub,
            int[] indices,
            Vertex[] vertices,
            Vec3[] viewSpace,
            Vec3[] ndcSpace,
            Material material
    ) {
        int w = rasterizer.getBufferWidth();
        int h = rasterizer.getBufferHeight();

        int start = sub.indexOffset;
        int end   = sub.indexOffset + sub.indexCount;

        for (int i = start; i < end; i += 2) {
            int i0 = indices[i];
            int i1 = indices[i + 1];

            if (!isLineInFront(viewSpace, i0, i1)) continue;

            Point2DWithDepth s0 = projectToScreen(ndcSpace[i0], w, h);
            Point2DWithDepth s1 = projectToScreen(ndcSpace[i1], w, h);

            if (s0 == null || s1 == null) continue;

            rasterizer.drawStraightLine(
                    new Point(s0.x, s0.y), -viewSpace[i0].z,
                    new Point(s1.x, s1.y), -viewSpace[i1].z,
                    material.getColor()
            );
        }
    }


}
