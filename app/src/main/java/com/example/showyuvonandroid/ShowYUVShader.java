package com.example.showyuvonandroid;

import android.opengl.GLES20;
import android.opengl.GLException;

public class ShowYUVShader {

    static final String VERTEX_SOURCE =
            "attribute vec3 position;\n" +
            "attribute vec2 vertexUV;\n" +
            "varying mediump vec2 uv;\n" +
            "void main(void)\n" +
            "{\n" +
            " gl_Position.xyz = position;\n" +
            " gl_Position.w = 1.0;\n" +
            " uv = vertexUV;\n" +
            "}";

    static final String FRAGMENT_SOURCE =
            "varying mediump vec2 uv;\n" +
            "uniform sampler2D textureSampler;\n" +
            "void main(void)\n" +
            "{\n" +
            "    gl_FragColor = texture2D(textureSampler, uv);\n" +
            "}";

    static final String ERROR_VERTEX = "バーテックスシェーダー作成失敗";
    static final String ERROR_FRAGMENT = "フラグメントシェーダー作成失敗";
    static final String ERROR_PROGRAM = "プログラムのリンク失敗";

    private int vertexShaderID = -1;
    private int fragmentShaderID = -1;
    private int programID = -1;
    private boolean available = false;

    public int getProgramID() {
        return programID;
    }

    public boolean isAvailable() {
        return available;
    }

    public ShowYUVShader() throws GLException {
        int[] result = new int[1];

        // 頂点シェーダを作成、コンパイル
        vertexShaderID = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShaderID, VERTEX_SOURCE);
        GLES20.glCompileShader(vertexShaderID);
        // コンパイル結果を取得
        GLES20.glGetShaderiv(vertexShaderID, GLES20.GL_COMPILE_STATUS, result, 0);
        if (result[0] == GLES20.GL_FALSE) {
            String infoLog = GLES20.glGetShaderInfoLog(vertexShaderID);
            System.err.println(infoLog);
            deleteAllObjects();
            throw new GLException(GLES20.glGetError(), ERROR_VERTEX);
        }

        // フラグメントシェーダを作成、コンパイル
        fragmentShaderID = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShaderID, FRAGMENT_SOURCE);
        GLES20.glCompileShader(fragmentShaderID);
        // コンパイル結果を取得
        GLES20.glGetShaderiv(fragmentShaderID, GLES20.GL_COMPILE_STATUS, result, 0);
        if (result[0] == GLES20.GL_FALSE) {
            String infoLog = GLES20.glGetShaderInfoLog(fragmentShaderID);
            System.err.println(infoLog);
            deleteAllObjects();
            throw new GLException(GLES20.glGetError(), ERROR_FRAGMENT);
        }

        // プログラムをリンク
        programID = GLES20.glCreateProgram();
        GLES20.glAttachShader(programID, vertexShaderID);
        GLES20.glAttachShader(programID, fragmentShaderID);
        GLES20.glLinkProgram(programID);

        // プログラムをチェック
        GLES20.glGetShaderiv(programID, GLES20.GL_LINK_STATUS, result, 0);
        if (result[0] == GLES20.GL_FALSE) {
            String infoLog = GLES20.glGetProgramInfoLog(programID);
            System.err.println(new String(infoLog));
            deleteAllObjects();
            throw new GLException(GLES20.glGetError(), ERROR_PROGRAM);
        }
        available = true;
    }

    public void deleteAllObjects() {
        if (fragmentShaderID > 0) {
            if (programID > 0) {
                GLES20.glDetachShader(programID, fragmentShaderID);
            }
            GLES20.glDeleteShader(fragmentShaderID);
            fragmentShaderID = -1;
        }
        if (vertexShaderID > 0) {
            if (programID > 0) {
                GLES20.glDetachShader(programID, vertexShaderID);
            }
            GLES20.glDeleteShader(vertexShaderID);
            vertexShaderID = -1;
        }
        if (programID > 0) {
            GLES20.glDeleteProgram(programID);
            programID = -1;
        }
    }

    public int getPositionAttribute() {
        return GLES20.glGetAttribLocation(programID, "position");
    }

    public int getVertexUVAttribute() {
        return GLES20.glGetAttribLocation(programID, "vertexUV");
    }

    public int getTextureSamplerUniform() {
        return GLES20.glGetUniformLocation(programID, "textureSampler");
    }
}
