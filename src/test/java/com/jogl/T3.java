package com.jogl;


//地球、太阳、月亮 动画

import javax.swing.JFrame;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

public class T3 implements GLEventListener {
	private GL2 gl;
	private GLU glu = new GLU();
	private GLUT glut = new GLUT();
	float day = 1; // 默认表示这一年第一天


	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		gl.setSwapInterval(60);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2f(-1.0f, 0.0f);
		gl.glVertex2f(1.0f, 0.0f);
		gl.glVertex2f(0.0f, -1.0f);
		gl.glVertex2f(0.0f, 1.0f);
		gl.glEnd();

		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		/*
		 * glViewport();它有四个参数， 前两个参数定义视口左下角坐标（(0,0)表示屏幕的最左下角）， 后两个参数指定视口的宽高。
		 */
		gl.glViewport(0, 0, 500, 500);

		glu.gluPerspective(90, 1, 1, 4000); // 可视空间 棱台（侧底面角度，长宽比，Z近原点距离，Z远原点距离）
		/*
		 * 相机位置 gluLookAt(eyex,eyey,eyez,x,y,z,dx,dy,dz); 前三个参数是设置相机的位置，这个坐标是在世界坐标系中的位置，
		 * 中间三个参数是设置观察目标也就是相机的朝向方向， 后三个参数是设置相机的“上”方向
		 ***/
		glu.gluLookAt(0, 0, 2000, 0, 0, 0, 0, 1, 0);

		// 太阳
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		glut.glutSolidSphere(500, 40, 40);

		// 地球
		gl.glRotatef(day * 1.0f, 0, 0, -1);
		gl.glTranslatef(900, 0, 0);
		gl.glColor3f(0.0f, 0.0f, 1.0f);
		glut.glutSolidSphere(100, 40, 40);

		// 月亮
		gl.glRotatef((day % 30) * (360 / 30.0f) - day * 1.0f, 0, 0, -1.0f);
		gl.glTranslatef(200, 0, 0);
		gl.glColor3f(0.0f, 1.0f, 1.0f);
		glut.glutSolidSphere(50, 40, 40);
		day++;
		gl.glFlush();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

	}

	public static void main(String[] args) {
		T3 opengl = new T3();
		GLCapabilities capabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(opengl);
		canvas.addGLEventListener(opengl);

		canvas.setSize(800, 800);
		JFrame frame = new JFrame("第一个JOGL程序");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.getContentPane().add(canvas);
		frame.setSize(frame.getContentPane().getPreferredSize());
		frame.setVisible(true);

	}
}