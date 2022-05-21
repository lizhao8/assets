package com.jogl;

import java.awt.AWTEvent;
import java.awt.Color;

//旋转立方体

import java.awt.DisplayMode;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.assets.data.Mesh;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogl.base.Element;
import com.jogl.base.Face;
import com.jogl.base.Point;

public class Draw extends JFrame
		implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 6091122705513875964L;

	public static DisplayMode dm, dm_old;
	private GLU glu = new GLU();
	private float rquad = 3.0f;
	public Mesh mesh = new Mesh();
	int xAngle = 0;
	int yAngle = 0;
	int zAngle = 0;
	int angle = 0;
	float xMove = 0;
	float yMove = 0;
	float zMove = -10;
	float xRotate = 0;
	float yRotate = 0;
	float zRotate = 0;
	float nRange = 0;
	GLAutoDrawable drawable;

	public Draw() {

	}

	public Draw(Mesh mesh) {
		this.mesh = mesh;
	}

	@Override
	public void display(GLAutoDrawable drawable) {

		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		// 出现在屏幕中的位置，
		gl.glTranslatef(xMove, yMove, zMove);

		// gl.glRotatef(xAngle, 1, 0, 0);
		// gl.glRotatef(yAngle, 0, 1, 0);
		// gl.glRotatef(zAngle, 0, 0, 1);

//		gl.glRotatef(angle, xRotate, yRotate, zRotate);
//		gl.glBegin(GL2.GL_QUADS); // Start Drawing The Cube
//		gl.glColor3f(1f, 0f, 0f); // red color 红色面
//		gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Top)
//		gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Top)
//		gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Bottom Left Of The Quad (Top)
//		gl.glVertex3f(1.0f, 1.0f, 1.0f); // Bottom Right Of The Quad (Top)
//		gl.glColor3f(0f, 1f, 0f); // green color 绿色面
//		gl.glVertex3f(1.0f, -1.0f, 1.0f); // Top Right Of The Quad
//		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Top Left Of The Quad
//		gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad
//		gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad
//		gl.glColor3f(0f, 0f, 1f); // blue color 蓝色面
//		gl.glVertex3f(1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Front)
//		gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Top Left Of The Quad (Front)
//		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad
//		gl.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Right Of The Quad
//		gl.glColor3f(1f, 1f, 0f); // yellow (red + green) 黄色面
//		gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad
//		gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad
//		gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Back)
//		gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Back)
//		gl.glColor3f(1f, 0f, 1f); // purple (red + green) 紫色面
//		gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Left)
//		gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Left)
//		gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad
//		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Right Of The Quad
//		gl.glColor3f(0f, 1f, 1f); // sky blue (blue +green) 天空色面
//		gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Right)
//		gl.glVertex3f(1.0f, 1.0f, 1.0f); // Top Left Of The Quad
//		gl.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad
//		gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad
//		gl.glEnd(); // Done Drawing The Quad

		// 按面
//		gl.glColor3f(0f, 0f, 1f);
//		for (Face face : mesh.faceList) {
//			gl.glBegin(GL2.GL_TRIANGLES);
//			for (Point point : face.pointList) {
//				gl.glVertex3f(-point.x, point.y, point.z);
//			}
//			gl.glEnd();
//		}
		// 按元素
		float r = 0;
		float g = 0;
		float b = 0;
		gl.glBegin(GL2.GL_TRIANGLES);
		for (int i = 0; i < mesh.elementList.size(); i++) {
			Element element = mesh.elementList.get(i);
			if (index % mesh.elementList.size() == i) {
				if (Math.sqrt(index) >= mesh.elementList.size()) {
					index = 0;
				}
				gl.glColor3f(1, 0, 0);
			} else {
				gl.glColor3f(r, g, b);
			}

			for (Face face : element.faceList) {
				for (Point point : face.pointList) {
					gl.glVertex3f(-point.x, point.y, point.z);
				}
			}
			if (b < 1) {
				b += 0.1;
			} else {
				if (g < 1) {
					b = 0;
					g += 0.1;
				} else {
					g = 0;
					r += 0.1;
				}
			}
			// System.out.println(r + "," + g + "," + b);
		}
		// index++;
		gl.glEnd();
		gl.glFlush();
		rquad += 1;
	}

	int index = 0;

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0f, 0f, 0f, 0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		float nRange = -zMove;
		if (height == 0) {
			height = 1;
		}
		if (width == 0) {
			width = 1;
		}
		float w = (float) width;
		float h = (float) height;

		final GL2 gl = drawable.getGL().getGL2();
		this.drawable = drawable;

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		// glMatrixMode有3种模式:
		// GL_PROJECTION 投影,
		// GL_MODELVIEW 模型视图,
		// GL_TEXTURE 纹理.

		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, w / h, 1.0, 100.0);
		// 建立剪辑视图,正投影（左，右，底部，顶部，近，远）
		// nRange = (float) (Math.atan(nRange) * 180 / Math.PI);// 根据tan求角度
		// nRange = this.nRange-zMove;
		// gl.glOrtho(-nRange, nRange, -nRange, nRange, -nRange-50, nRange);

		/*
		 * if (w <= h) { gl.glOrtho(-nRange, nRange, -nRange * h / w, nRange * h / w,
		 * -nRange - 50, nRange); } else { gl.glOrtho(-nRange * w / h, nRange * w / h,
		 * -nRange, nRange, -nRange - 50, nRange); }
		 */

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	GLCanvas glcanvas;

	public void starting() {
		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setDoubleBuffered(true);
		// The canvas
		glcanvas = new GLCanvas(capabilities);
		glcanvas.addGLEventListener(this);
		glcanvas.setSize(1000, 1000);
		glcanvas.addMouseListener(this);
		glcanvas.addMouseMotionListener(this);
		glcanvas.addMouseWheelListener(this);
		glcanvas.addKeyListener(this);

		this.setName(" Multicolored cube");
		this.getContentPane().add(glcanvas);
		this.setSize(this.getContentPane().getPreferredSize());
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		final FPSAnimator animator = new FPSAnimator(glcanvas, 60, true);
		animator.start();

	}

	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0);
		}
	}
//
//	public static void main(String[] args) {
//
//		Draw cube = new Draw();
//		cube.starting();
//
//	}

	private boolean rightMouseDown = false;
	private boolean leftMouseDown = false;
	private int mouseX = 0;
	private int mouseY = 0;

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (MouseEvent.BUTTON1 == e.getButton()) {
			leftMouseDown = true;
		} else if (MouseEvent.BUTTON3 == e.getButton()) {
			rightMouseDown = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (MouseEvent.BUTTON1 == e.getButton()) {
			leftMouseDown = false;
		} else if (MouseEvent.BUTTON3 == e.getButton()) {
			rightMouseDown = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (leftMouseDown) {
			int x = e.getX();
			int y = e.getY();
			x = x - mouseX;
			y = y - mouseY;
			System.out.println(x + "," + y);
			xRotate = x;
			yRotate = y;
			// angle += 3;
//			
//			xRotate = (float) x / (float) y;
//			yRotate = 1;
//			int absx = Math.abs(x);
//			int absy = Math.abs(y);
//
//			if (absx > absy) {
//				angle += x / absx * 3;
//			} else {
//				angle += y / absy * 3;
//			}
//			if (angle % 360 == 0) {
//				angle = 0;
//			}
//			System.out.println(angle + "," + xRotate + "," + yRotate);

//			if (Math.abs(x) > Math.abs(y)) {
//				yRotate += x / Math.abs(x) * 3;
//				if (yRotate % 360 == 0) {
//					yRotate = 0;
//				}
//			} else {
//				xRotate += y / Math.abs(y) * 3;
//				if (xRotate % 360 == 0) {
//					xRotate = 0;
//				}
//			}

		}
//		mouseX = e.getX();
//		mouseY = e.getY();

		else if (rightMouseDown) {
			int x = e.getX();
			int y = e.getY();
			x = x - mouseX;
			y = y - mouseY;
			int absx = Math.abs(x);
			int absy = Math.abs(y);
			if (absx > 0) {
				xMove -= zMove / 1000.0 * x;
			}
			if (absy > 0) {
				yMove += zMove / 1000.0 * y;
			}
			System.out.println(xMove + "," + yMove);
			mouseX = e.getX();
			mouseY = e.getY();
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			zMove += -e.getUnitsToScroll() / 3.0;
		}
//		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
//			nRange += e.getUnitsToScroll() / 300.0;
//			glcanvas.reshape(0, 0, 1200, 1200);
//			System.out.println(nRange);
//		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			index++;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			index--;
		} else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
			index += 10;
		} else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			index -= 10;
		}
		System.out.println(index);

	}
}