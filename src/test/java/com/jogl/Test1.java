package com.jogl;

import javax.swing.JFrame;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
 
public class Test1 implements GLEventListener{
	private GL2 gl;
	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
	}
	@Override
	public void dispose(GLAutoDrawable drawable) {
		
	}
	@Override
	public void display(GLAutoDrawable drawable) {
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		gl.glRectf(-0.5f, -0.5f, 0.5f, 0.5f);
		gl.glFlush();
	}
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		
	}
 
	public static void main(String[] args) {
		GLCanvas canvas = new GLCanvas(new GLCapabilities(GLProfile.get(GLProfile.GL2)));
		Test1 opengl = new Test1();
		canvas.addGLEventListener(opengl);
		
		canvas.setSize(500,500);
		JFrame frame = new JFrame("第一个JOGL程序");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.getContentPane().add(canvas);
		frame.setSize(frame.getContentPane().getPreferredSize());
		frame.setVisible(true);
		
	}
}
