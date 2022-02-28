/****************************************************************************
**
** Copyright (C) 2017 The Qt Company Ltd.
** Contact: https://www.qt.io/licensing/
**
** This file is part of the examples of the Qt Toolkit.
**
** $QT_BEGIN_LICENSE:BSD$
** Commercial License Usage
** Licensees holding valid commercial Qt licenses may use this file in
** accordance with the commercial license agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and The Qt Company. For licensing terms
** and conditions see https://www.qt.io/terms-conditions. For further
** information use the contact form at https://www.qt.io/contact-us.
**
** BSD License Usage
** Alternatively, you may use this file under the terms of the BSD license
** as follows:
**
** "Redistribution and use in source and binary forms, with or without
** modification, are permitted provided that the following conditions are
** met:
**   * Redistributions of source code must retain the above copyright
**     notice, this list of conditions and the following disclaimer.
**   * Redistributions in binary form must reproduce the above copyright
**     notice, this list of conditions and the following disclaimer in
**     the documentation and/or other materials provided with the
**     distribution.
**   * Neither the name of The Qt Company Ltd nor the names of its
**     contributors may be used to endorse or promote products derived
**     from this software without specific prior written permission.
**
**
** THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
** "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
** LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
** A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
** OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
** LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
** DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
** THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
** (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
** OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."
**
** $QT_END_LICENSE$
**
****************************************************************************/

#include "window_singlethreaded.h"
#include "cuberenderer.h"
#include <QOpenGLContext>
#include <QOpenGLFunctions>
#include <QOpenGLFramebufferObject>
#include <QOpenGLShaderProgram>
#include <QOpenGLVertexArrayObject>
#include <QOpenGLBuffer>
#include <QOffscreenSurface>
#include <QScreen>
#include <QQmlEngine>
#include <QQmlComponent>
#include <QQuickItem>
#include <QQuickWindow>
#include <QQuickRenderControl>
#include <QCoreApplication>
#include <QQuickRenderTarget>
#include <QQuickGraphicsDevice>
#include <QImage>
#include <QDateTime>

class RenderControl : public QQuickRenderControl
{
public:
    RenderControl(QWindow *w) : m_window(w) { }
    QWindow *renderWindow(QPoint *offset) override;

private:
    QWindow *m_window;
};

QWindow *RenderControl::renderWindow(QPoint *offset)
{
    if (offset)
        *offset = QPoint(0, 0);
    return m_window;
}

WindowSingleThreaded::WindowSingleThreaded()
    : m_rootItem(nullptr),
      m_textureId(0),
      m_quickInitialized(false),
      m_quickReady(false),
      m_dpr(0)
{
    setSurfaceType(QSurface::OpenGLSurface);

    QSurfaceFormat format;
    // Qt Quick may need a depth and stencil buffer. Always make sure these are available.
    format.setDepthBufferSize(16);
    format.setStencilBufferSize(8);
    setFormat(format);

    m_context = new QOpenGLContext;
    m_context->setFormat(format);
    m_context->create();

    m_cubeRenderer = new CubeRenderer();

    m_renderControl = new RenderControl(this);

    // Create a QQuickWindow that is associated with out render control. Note that this
    // window never gets created or shown, meaning that it will never get an underlying
    // native (platform) window.
    m_quickWindow = new QQuickWindow(m_renderControl);

    m_updateTimer.start(200);
    connect(&m_updateTimer, &QTimer::timeout, this, &WindowSingleThreaded::render);

    m_quickWindow->setGeometry(0, 0, 500, 500);
    m_cubeRenderer->resize(500, 500);

    m_quickWindow->setGraphicsDevice(QQuickGraphicsDevice::fromOpenGLContext(m_context));
    m_renderControl->initialize();
    m_quickInitialized = true;

    createTexture();
}
#include <QJniEnvironment>
#include <jni.h>
#include <QJniObject>

void WindowSingleThreaded::setJavaEGL()
{
    QJniEnvironment env;
    jclass myClazz = env.findClass("com/halulu/example/FloatViewManager");
    m_jniFloatViewManager = new QJniObject(myClazz);
    QNativeInterface::QAndroidApplication::runOnAndroidMainThread([&]() {
        m_jniFloatViewManager->callMethod<void>("Create",
                                                "(Landroid/app/Activity;)V",
                                                QNativeInterface::QAndroidApplication::context());
    }).waitForFinished();
}



WindowSingleThreaded::~WindowSingleThreaded()
{
}

void WindowSingleThreaded::createTexture()
{
    QJniEnvironment env;
    QJniObject::callStaticMethod<void>("com.halulu.example.MyEGLContextFactory",
                                       "setOpenGLContent");
    setJavaEGL();
}
#include "QtGui/private/qeglplatformcontext_p.h"
void WindowSingleThreaded::updateTexture()
{
    if (i_count % 10 != 0)
    {
        i_count ++;
        return;
    }

//    m_context->makeCurrent(this);
    QOpenGLFunctions *f = m_context->functions();
    QImage img;
    QString path = ":/img/pic000" + QString::number(1 + (i_count/10)%4)+ ".png";
    img.load(path);
    img.convertTo(QImage::Format_RGBA8888);


    f->glBindTexture(GL_TEXTURE_2D, 1);
//    f->glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//    f->glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    f->glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    f->glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    //过滤方式
    f->glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    f->glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    f->glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.width(), img.height(), 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, img.bits());
    f->glBindTexture(GL_TEXTURE_2D, 0);
    i_count ++;
}


#include <QDateTime>
void WindowSingleThreaded::render()
{
    QDateTime current_date_time = QDateTime::currentDateTime();
    QString current_time = current_date_time.toString("hh:mm:ss.zzz ");
    qDebug() << "current_time " << current_time << QThread::currentThreadId();;

    updateTexture();
    m_quickReady = true;
    m_cubeRenderer->render(this, m_context, m_quickReady ? m_textureId : 0);
}
