/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved.
 * Copyright (c) 2010 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 * 
 * Modified by: Mikael Murstam
 */



import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import com.jogamp.opengl.util.ImmModeSink;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public class MultisampleDemoES1 implements GLEventListener, KeyListener, Runnable {

    boolean multisample;
    ImmModeSink foreground;
    ImmModeSink background;
    float red,green,blue;
    
    public MultisampleDemoES1() {
        this.multisample = true;
    }

    public MultisampleDemoES1(boolean multisample) {
        this.multisample = multisample;
    }
    
    public float aspect;
    
    private void drawFrameXY(GL2ES1 gl, float x, float y, float border, float cut) {
                
        foreground.glBegin(GL.GL_LINES);
        
        float corner = border+cut;
               
        foreground.glVertex3f(-(aspect-border-x), (1.0f-corner-y), 0f);            
        foreground.glVertex3f(-(aspect-border-x), -(1.0f-corner-y), 0f);
        
        foreground.glVertex3f(-(aspect-border-x), (1.0f-corner-y), 0f);
        foreground.glVertex3f(-(aspect-corner-x), (1.0f-border-y), 0f); 
        
        foreground.glVertex3f((aspect-border-x), (1.0f-corner-y), 0f);            
        foreground.glVertex3f((aspect-border-x), -(1.0f-corner-y), 0f);
        
        foreground.glVertex3f((aspect-border-x), (1.0f-corner-y), 0f);
        foreground.glVertex3f((aspect-corner-x), (1.0f-border-y), 0f); 
               
        foreground.glVertex3f(-(aspect-corner-x), (1.0f-border-y), 0f);            
        foreground.glVertex3f((aspect-corner-x),  (1.0f-border-y), 0f);
        
        foreground.glVertex3f((aspect-border-x), -(1.0f-corner-y), 0f);
        foreground.glVertex3f((aspect-corner-x), -(1.0f-border-y), 0f); 
        
        foreground.glVertex3f(-(aspect-corner-x), -(1.0f-border-y), 0f);            
        foreground.glVertex3f((aspect-corner-x),  -(1.0f-border-y), 0f);
        
        foreground.glVertex3f(-(aspect-border-x), -(1.0f-corner-y), 0f);
        foreground.glVertex3f(-(aspect-corner-x), -(1.0f-border-y), 0f);
        
        foreground.glEnd(gl, false);
        
    }
    
    private void drawFrame(GL2ES1 gl) {
        drawFrameXY(gl, 0f, 0f, 0.15f, 0.15f);
    }

    private void drawFrame(GL2ES1 gl, float border, float cut) {
        drawFrameXY(gl, 0f, 0f, border, cut);
    }
    
    private void drawBackground(GL2ES1 gl) {
        background.glBegin(ImmModeSink.GL_QUADS);
                      
        background.glTexCoord2f(0.0f,  1.0f);
        background.glVertex3f(-aspect,  1.0f, 0f);
        
        background.glTexCoord2f(0.0f,  0.0f);
        background.glVertex3f(-aspect, -1.0f, 0f);
        
        background.glTexCoord2f(1.0f,  0.0f);
        background.glVertex3f( aspect, -1.0f, 0f);
        
        background.glTexCoord2f(1.0f,  1.0f);
        background.glVertex3f( aspect,  1.0f, 0f);            
        
        background.glEnd(gl, false);
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        System.err.println();
        System.err.println("Requested: " + drawable.getNativeSurface().getGraphicsConfiguration().getRequestedCapabilities());
        System.err.println();
        System.err.println("Chosen   : " + drawable.getChosenGLCapabilities());
        System.err.println();
        gl = drawable.getGL().getGL2ES1();
        
        gl.glEnable(GL.GL_MULTISAMPLE);
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL.GL_BLEND); 
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL.GL_TEXTURE_2D);
        
        gl.glClearColor(0, 0, 0, 0);
        //      gl.glEnable(GL.GL_DEPTH_TEST);
        //      gl.glDepthFunc(GL.GL_LESS);
        gl.glMatrixMode(GL2ES1.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2ES1.GL_PROJECTION);
        gl.glLoadIdentity();
        //gl.glOrtho(-(float)width/(float)height, (float)width/(float)height, -1, 1, -1, 1);
        aspect=(float)width/(float)height;
        gl.glOrtho(-aspect, aspect, -1, 1, -1, 1);
        
        foreground = ImmModeSink.createFixed(  40, 
                                                3, GL.GL_FLOAT, // vertex
                                                0, GL.GL_FLOAT, // color
                                                0, GL.GL_FLOAT, // normal
                                                3, GL.GL_FLOAT, // texCoords 
                                                GL.GL_STATIC_DRAW);
        background     = ImmModeSink.createFixed(  40, 
                                                3, GL.GL_FLOAT, // vertex
                                                0, GL.GL_FLOAT, // color
                                                0, GL.GL_FLOAT, // normal
                                                3, GL.GL_FLOAT, // texCoords 
                                                GL.GL_STATIC_DRAW);

        //for(float i=0; i < 0.48f; i+=0.02f)
          //  drawFrameXY(gl, i, i, 0.1f, 0.15f);
                
        try {
            //wallpaper = TextureIO.newTexture(new File("bg2.png"), false);
            wallpaper = TextureIO.newTexture(new File("bg.png"), false);
            wallpaper.bind(gl);
            //wallpaper = TextureIO.newTexture(new File("9.gif"), false);
        }
        catch (IOException exc) {
            System.out.println("Couldn't load texture...");
            System.exit(1);
        }
        
        drawBackground(gl);
        drawFrameXY(gl, 0.08f, 0.08f, 0.1f, 0.15f);
        
        /*
        foreground.glBegin(GL.GL_TRIANGLE_STRIP);
        
        foreground.glVertex2f(-0.5f, -0.5f);
        foreground.glVertex2f( 0.5f, -0.5f);
        foreground.glVertex2f(-0.5f,  0.5f);
        foreground.glVertex2f( 0.5f,  0.5f);        
        
        foreground.glEnd(gl, false);*/
        
        
    }
    
    private static int width;
    private static int height;
    private boolean terminate = false;
    private GL2ES1 gl;
    private GLProfile glp;
    private Texture wallpaper;

    @Override
    public void run(){

        /* This demo are based on the GL2ES2 GLProfile that uses common hardware acceleration
         * functionality of desktop OpenGL 3, 2 and mobile OpenGL ES 2 devices.
         * JogAmp JOGL will probe all the installed libGL.so, libEGL.so and libGLESv2.so librarys on
         * the system to find which one provide hardware acceleration for your GPU device.
         * Its common to find more than one version of these librarys installed on a system.
         * For example on a ARM Linux system JOGL may find
         * Hardware accelerated Nvidia tegra GPU drivers in: /usr/lib/nvidia-tegra/libEGL.so
         * Software rendered Mesa Gallium driver in: /usr/lib/arm-linux-gnueabi/mesa-egl/libEGL.so.1
         * Software rendered Mesa X11 in: /usr/lib/arm-linux-gnueabi/mesa/libGL.so
         * Good news!: JOGL does all this probing f
         * the GLProfile you want to use.
         */

        glp = GLProfile.get(GLProfile.GL2ES1);
        GLCapabilities caps = new GLCapabilities(glp);
	// We may at this point tweak the caps and request a translucent drawable
        caps.setBackgroundOpaque(true);
        GLWindow glWindow = GLWindow.create(caps);
        
        GLProfile.initSingleton(); // hack to initialize GL for BCM_IV (Rasp.Pi)
                                   // On the Raspberry Pi broadcom screenmode resolution is detected
                                   // during GL initialization, using the dispmanx api.

        // A screen may span multiple MonitorDevices representing their combined virtual size.
        // http://jogamp.org/deployment/jogamp-current/javadoc/jogl/javadoc/com/jogamp/newt/Screen.html
        // http://jogamp.org/files/screenshots/newt-mmonitor/html/
        int screenIdx = 0;
        Display dpy = NewtFactory.createDisplay(null); // Get the default display on the system
        Screen screen = NewtFactory.createScreen(dpy, screenIdx);
        screen.addReference();
        
        width=screen.getWidth();
        height=screen.getHeight();
        
        System.out.println("width: "+width);
        System.out.println("height: "+height);

        // In this demo we prefer to setup and view the GLWindow directly
        // this allows the demo to run on -Djava.awt.headless=true systems
        glWindow.setSize(width,height);
	glWindow.setPosition(0,0);
        glWindow.setUndecorated(true);
        glWindow.setPointerVisible(false);
        glWindow.setVisible(true);
        //glWindow.setAlwaysOnTop(true);
        glWindow.requestFocus();

        // Finally we connect the GLEventListener application code to the NEWT GLWindow.
        // GLWindow will call the GLEventListener init, reshape, display and dispose
        // functions when needed.
        glWindow.addGLEventListener(this);
	glWindow.addKeyListener(this);
        Animator animator = new Animator();
        animator.add(glWindow);
        animator.start();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        background.destroy(drawable.getGL());
        background = null;
        foreground.destroy(drawable.getGL());
        foreground = null;
        System.exit(0);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        wallpaper.enable(gl);
        //gl.glColor4f(red, green, blue, 1.0f);
        background.draw(gl, true);
        wallpaper.disable(gl);
        
        gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
        foreground.draw(gl, true);
        
        if(terminate) {
            dispose(drawable);
        }
    }

    // Unused routines
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
            terminate=true;            
        }else
        if(e.getKeyCode()==KeyEvent.VK_LEFT) {
        }else
        if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
        }else
        if(e.getKeyCode()==KeyEvent.VK_UP) {
        }else
        if(e.getKeyCode()==KeyEvent.VK_DOWN) {
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}