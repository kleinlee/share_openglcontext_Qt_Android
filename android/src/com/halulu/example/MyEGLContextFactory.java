package com.halulu.example;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;


//import android.opengl.EGL14;
//import android.opengl.EGLConfig;
//import android.opengl.EGLContext;
//import android.opengl.EGLDisplay;


public class MyEGLContextFactory implements GLSurfaceView.EGLContextFactory {
    /** 共享的opengl上下文 */
    private static EGLContext share_context = null;
    //创建第一个GLSurfaceView的时候，传null,后面的传
    public MyEGLContextFactory ()
    {
    }
    private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    @Override
    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
        int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };

        if(MyEGLContextFactory.share_context  == null)
        {
            EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
            MyEGLContextFactory.share_context = context;

//            long handle = context.getNativeHandle();
//            EGLContext dpy = (EGLContext)handle;
            System.out.println("66666666666666666666666");

            return context;
        }
        return egl.eglCreateContext(display, eglConfig, MyEGLContextFactory.share_context, attrib_list);
    }

    @Override
    public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
        if (!egl.eglDestroyContext(display, context)) {
        }
    }

    public static void setOpenGLContent() {
        EGL10 mEGL = (EGL10) EGLContext.getEGL();

        share_context = mEGL.eglGetCurrentContext();
    }

    public static EGLContext getOpenGLContent() {
        return share_context;
    }
}
