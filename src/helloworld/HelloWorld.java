package helloworld;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class HelloWorld {
        char keypr = 'L'; 

	// The window handle
	private long window;

	public void run() throws InterruptedException {++++++       
                
		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
            

                
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(640, 640, "Snake gAme", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
                        else if(( key == GLFW_KEY_W && action == GLFW_RELEASE && keypr != 'D')){
                            keypr = 'U';
                        } else if(( key == GLFW_KEY_A && action == GLFW_RELEASE && keypr != 'R')){
                            keypr = 'L';
                        } else if(( key == GLFW_KEY_S && action == GLFW_RELEASE && keypr != 'U')){
                            keypr = 'D';
                        } else if(( key == GLFW_KEY_D && action == GLFW_RELEASE && keypr != 'L')){
                            keypr = 'R';
                        }
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() throws InterruptedException {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		float red = 1;
                float green = 0;
                float blue = 0;
                Game game = new Game();
                
                Thread.sleep(500);
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
                        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                        if (game.loop(keypr) == 1){
                            glfwSetWindowShouldClose(window, true);
                        }
                        if (red == 1 && blue <= 0 ) {
                                green +=0.1;
                        }
                        if (green >= 1 && blue <= 0 ) {
                                red -=0.1;
                        }
                        if (red <= 0 && green >= 1 ) {
                                blue +=0.1;
                        }
                        if (red <= 0 && blue >= 1 ) {
                                green -=0.1;
                        }
                        if (blue >= 1 && green <= 0 ) {
                                red +=0.1;
                        }
                        if (red >= 1 && green <= 0 ) {
                                blue -=0.1;
                        }
                        
			glfwSwapBuffers(window); // swap the color buffers
                        
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
                        glClear(GL_COLOR_BUFFER_BIT); 
                        Thread.sleep(100);
                        
		}
                
	}       

	public static void main(String[] args) throws InterruptedException {
		new HelloWorld().run();
                
	}
        
        public void render() {
            //
        }
        
        void display(float red,float green,float blue) {
           glClear(GL_COLOR_BUFFER_BIT);         // Clear the color buffer (background)

           // Draw a Red 1x1 Square centered at origin
           //glBegin(GL_QUADS);              // Each set of 4 vertices form a quad
              //glColor3f(red, green, blue); // Red
              //glVertex2f(-0.5f, -0.5f);    // x, y
              //glVertex2f( 0.5f, -0.5f);
              //glVertex2f( 0.5f,  0.5f);
              //glVertex2f(-0.5f,  0.5f);
           //glEnd();

           //glFlush();  // Render now
        }


}
class Game {

    private final Snake snake;
    private final Apple apple;
    Game(){
        this.snake = new Snake();
        this.apple = new Apple(snake);
        
    }
    public int loop(char keypr) {
        this.snake.direction = keypr;
        this.snake.move();
        this.snake.draw();
        this.apple.draw();
        if (this.apple.isColideSnake()){
            this.apple.teleport();
            this.snake.newbody = true;
        } 

        if (    this.snake.body[0].x > 31 || 
                this.snake.body[0].x < 0 || 
                this.snake.body[0].y > 31 || 
                this.snake.body[0].y < 0 ||
                this.snake.isColideItself()) {
            return 1;
        }
        return 0;
    }

}

class Snake {
    public int snakelength = 0;
    public Body [] body = new Body[256];
    public char direction;
    public boolean isDying;
    public boolean newbody;
    Snake(){
        addBody(10,10);
    }
    public void addBody(int x, int y){
        body[this.snakelength] = new Body(x,y);
        this.snakelength++;
    }
    public boolean isColideItself(){
        for (int i = 1 ;i < this.snakelength; i++){
            if ((body[i].x == body[0].x) && (body[i].y == body[0].y)){
                return true;
            }
        }
        return false;
    }
    public void draw(){
        for (int i = 0; i < this.snakelength; i++){
            body[i].draw();
        }
    }
    public void move(){
        int x = this.body[0].x;
        int y = this.body[0].y;
        switch (this.direction) {
            case 'U':
                this.body[0].y +=1;
                break;
            case 'D':
                this.body[0].y -=1;
                break;
            case 'L':
                this.body[0].x -=1;
                break;
            case 'R':
                this.body[0].x +=1;
                break;
            default:
                break;
        }
            for (int i = 1; i < this.snakelength; i++) {
                int a;
                a = x;
                x = this.body[i].x;
                this.body[i].x = a;

                a = y;
                y = this.body[i].y;
                this.body[i].y = a;
                
        }
        if ((this.body[0].x < 0) || (this.body[0].x > 16) || (this.body[0].y < 0) || (this.body[0].y > 16)){
            	this.isDying = true;
        }
        if (this.newbody){
            addBody(x,y);
            this.newbody = false;
        }
    }
}
class Body{
    public int x,y;
    Body(int x, int y){
        this.x = x;
        this.y = y;
    }

    void draw() {
        float a = (this.x - 16)/16f;
        float b = (this.y - 15)/16f;
        
        glBegin(GL_QUADS);              // Each set of 4 vertices form a quad
           glColor3f(0.0f, 1.0f, 0.0f); // Red
               // x, y
           glVertex2f((float)(a  + 0.0625),(float)(b - 0.0625)); 
           glVertex2f((float)(a + 0.0625),(float)b); 
           glVertex2f((float)a,(float)b);
           glVertex2f((float)a,(float)(b - 0.0625)); 
           
        glEnd();
        glFlush();
    }
}
class Apple{
    final private Random rand = new Random();
    public int x,y;
    public Snake a;
    Apple(Snake a){
        this.a = a;
        teleport();
    }
    public void teleport(){
        while (true){
            int col = 0;
            this.x = rand.nextInt(31);
            this.y = rand.nextInt(31);
            for(int i = 0; i < this.a.snakelength; i++){
                if (( this.a.body[i].x == this.x) && (this.a.body[i].y == this.y)){
                    col++;
                }
            }
            if (col == 0){
                break;
            }
        }
    }
    public boolean isColideSnake(){
        for (int i = 0 ;i < this.a.snakelength; i++){
            if (( this.a.body[i].x == this.x) && (this.a.body[i].y == this.y)){
                return true;
            }
        }
        return false;
    }
    public void draw(){
        float a = (this.x - 16)/16f;
        float b = (this.y - 15)/16f;
        
        glBegin(GL_QUADS);              // Each set of 4 vertices form a quad
           glColor3f(1.0f, 0.0f, 0.0f); // Green
               // x, y
           glVertex2f((float)(a  + 0.0625),(float)(b - 0.0625)); 
           glVertex2f((float)(a + 0.0625),(float)b); 
           glVertex2f((float)a,(float)b);
           glVertex2f((float)a,(float)(b - 0.0625));
           
        glEnd();
        glFlush();
    }
}