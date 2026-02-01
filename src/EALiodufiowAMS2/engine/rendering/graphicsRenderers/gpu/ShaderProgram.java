package EALiodufiowAMS2.engine.rendering.graphicsRenderers.gpu;

import EALiodufiowAMS2.helpers.Matrix4;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private int programId = 0;

    private final String VS_SRC;
    private final String FS_SRC;

    public ShaderProgram(String VS_SRC, String FS_SRC) {
        this.VS_SRC = VS_SRC;
        this.FS_SRC = FS_SRC;
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