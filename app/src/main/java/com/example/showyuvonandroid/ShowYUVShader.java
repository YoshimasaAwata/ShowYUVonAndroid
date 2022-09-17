package com.example.showyuvonandroid;

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

}
