package com.halulu.example;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.AttributeSet;

//import com.jianji.delogo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import android.graphics.PixelFormat;

public class GLTestView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private Context context;

    //绘制坐标范围
    float[] vertexData = {
            -1f, -1f,
            1f, -1.0f,
            -1f, 1.0f,
            1f, 1f
    };
    float[] textureData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };
    //为坐标分配本地内存地址，不受jvm的影响
    FloatBuffer vertexBuffer;
    FloatBuffer textureBuffer;

    public GLTestView(Context context) {
        this(context, null);
    }
    public GLTestView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextFactory(new MyEGLContextFactory());
        this.context = context;
        //设置OpenGL的版本
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
        setRenderer(this);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())//对齐，加快处理速度
                .asFloatBuffer()
                .put(vertexData);
        //指向0的位置
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);
    }

    private int program;
    private int avPosition;
    private int afPosition;
    private int textureId;

    /**
     * Called when the surface is created or recreated.
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexSource = ShaderUtil.readRawTxt(context, R.raw.vertex_shader);
        String fragmentSource = ShaderUtil.readRawTxt(context, R.raw.fragment_shader);

        program = ShaderUtil.createProgram(vertexSource, fragmentSource);
        if (program > 0) {
            avPosition = GLES20.glGetAttribLocation(program, "av_Position");
            afPosition = GLES20.glGetAttribLocation(program, "af_Position");

//            int[] textureIds = new int[1];
//            //生成纹理
//            GLES20.glGenTextures(1, textureIds, 0);
//            if (textureIds[0] == 0) {
//                return;
//            }
//            textureId = textureIds[0];
            textureId = 1;
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
//            //环绕方式
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //过滤方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 500,500,0,GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,null);
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic0003);
//            if (bitmap == null) {
//                return;
//            }
//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }
    }

    /**
     * Called when the surface changed size.
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置大小位置
        GLES20.glViewport(0, 0, width, height);
    }

    /**
     * Called to draw the current frame.
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        Thread t = Thread.currentThread();
        System.out.println("111111111111111111  onDrawFrame Thread.currentThread" + t.getId());

        //清屏，清理掉颜色的缓冲区
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //设置清屏的颜色，这里是float颜色的取值范围的[0,1]
        GLES20.glClearColor(1.0f, 0f, 0f, 1.0f);

        //使用program
        GLES20.glUseProgram(program);

        //设置为可用的状态
        GLES20.glEnableVertexAttribArray(avPosition);
        //size 指定每个顶点属性的组件数量。必须为1、2、3或者4。初始值为4。（如position是由3个（x,y,z）组成，而颜色是4个（r,g,b,a））
        //stride 指定连续顶点属性之间的偏移量。如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。初始值为0。
        //size 2 代表(x,y)，stride 8 代表跨度 （2个点为一组，2个float有8个字节）
        GLES20.glVertexAttribPointer(avPosition, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);

        GLES20.glEnableVertexAttribArray(afPosition);
        GLES20.glVertexAttribPointer(afPosition, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 1);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glFinish();
    }

    public void updateFrame()
    {
        EGL10 mEGL = (EGL10) EGLContext.getEGL();

        EGLContext share_context = mEGL.eglGetCurrentContext();
        }
}


