package com.halulu.example;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderUtil {
    private static final String TAG = "ShaderUtil";

    /**
     * 读取shader
     *
     * @param context
     * @param rawId
     * @return
     */
    public static String readRawTxt(Context context, int rawId) {
        InputStream inputStream = context.getResources().openRawResource(rawId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer sb = new StringBuffer();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 加载shader
     *
     * @param shaderType
     * @param source
     * @return
     */
    private static int loadShader(int shaderType, String source) {
        //创建shader
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            //关联source
            GLES20.glShaderSource(shader, source);
            //编译
            GLES20.glCompileShader(shader);
            //获取编译状态
            int[] compile = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compile, 0);
            if (compile[0] != GLES20.GL_TRUE) {
                //失败
                Log.e(TAG, "loadShader: shader compile error");
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * 生成程序
     *
     * @param vertexSource
     * @param fragmentSource
     * @return
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) {
            return 0;
        }
        //创建
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            //附加着色器
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            //连接
            GLES20.glLinkProgram(program);
            //检查连接是否成功
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                //出错
                Log.e(TAG, "createProgram: link program error");
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
}

