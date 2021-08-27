/* CS2150Coursework.java
 * TODO: put your university username and full name here
 *
 * Scene Graph:
 *  Scene origin
 *  |
 *
 *  TODO: Provide a scene graph for your submission
 */
package coursework.zamanm4;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.opengl.Texture;

//import CS2150Coursework.Fish;
import GraphicsLab.*;


/**
 * TODO: Briefly describe your submission here
 *Goldfish game. The fish moves to pop a bubble and it disappears. The fish uses multiple keys to move
 *
 ** Scene Graph:
 * |
 * +--[S(17.0f,1.0f, 17.0f) Rx(90.0f)T(0.0f, 0.0f, -20.0f)] Back Plane
 * |
 * |
 * +--[Rx(-10.0f)T(fishOffsetX,-10.0f,fishOffsetY)] FishBody
 * |   
 * |   	
 * |    
 * |  
 * +--[T(3.0f, -2.5f, -9.5f)] Bubble
 * 
 * <p>Controls:
 * <ul>up, down, left and right arrows to move the fish.
 * <li>Press the escape key to exit the application.
 * <li>Hold the x, y and z keys to view the scene along the x, y and z axis, respectively
 * <li>While viewing the scene along the x, y or z axis, use the up and down cursor keys
 *      to increase or decrease the viewpoint's distance from the scene origin
 * </ul>
 * TODO: Add any additional controls for your sample to the list above
 *
 */
public class CS2150Coursework extends GraphicsLab
{
	private final int fishBodyList = 1;
	private final int planeList = 2;

	private Texture underwaterBG;
	private Texture bubble;

	/** how high the person is from the ground */
	private float fishOffsetY  = 0.0f;
	private float fishOffsetX  = 0.0f;
	
	private float rotationAngle= 90.0f;

	/** the bubble's current Y offset from the scene origin */
	private float currentBubbleY = 7.0f;
	/** the bubble's highest possible Y offset */
	private final float highestBubbleY = currentBubbleY;
	/** the bubble's lowest possible Y offset */
	private final float lowestBubbleY  = -2.0f;
	/** is the /bubble rising? (false = the bubble is falling) */
	private boolean risingBubble = true;





	//TODO: Feel free to change the window title and default animation scale here
	public static void main(String args[])
	{   new CS2150Coursework().run(WINDOWED,"CS2150 Coursework Submission",0.01f);
	}

	protected void initScene() throws Exception
	{//TODO: Initialise your resources here - might well call other methods you write.
		//I drew the bubble and underwater images that im using
		underwaterBG = loadTexture("/zamanm4/coursework/zamanm4/textures/underthesea.bmp");
		bubble = loadTexture("/zamanm4/coursework/zamanm4/textures/bubblebig.bmp");

		//global ambient light level
		GL11.glEnable(GL11.GL_LIGHTING);
		float globalAmbient[] = {0.2f, 0.2f, 0.2f, 1.0f};
		//set the global ambient lighting
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(globalAmbient));

		//first light for the scene is white
		float diffuse0[] = {0.6f, 0.6f, 0.6f, 1.0f};
		//with dim ambient contribution
		float ambient0[] = {0.1f, 0.1f, 0.1f, 1.0f};
		//positioned above the viewpoint
		float position0[] = {0.0f, 10.0f, 0.0f, 1.0f};

		// supply OpenGL with the properties for the first light
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, FloatBuffer.wrap(ambient0));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, FloatBuffer.wrap(diffuse0));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, FloatBuffer.wrap(diffuse0));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, FloatBuffer.wrap(position0));
		// enable the first light
		GL11.glEnable(GL11.GL_LIGHT0);

		// enable lighting calculations
		GL11.glEnable(GL11.GL_LIGHTING);
		// ensure that all normals are re-normalised after transformations automatically
		GL11.glEnable(GL11.GL_NORMALIZE);

		GL11.glNewList(planeList,GL11.GL_COMPILE);
		{   drawUnitPlane();
		}
		GL11.glEndList();

		//calls fish
		GL11.glNewList(fishBodyList,GL11.GL_COMPILE);
		{    drawUnitFishBody();
		}
		GL11.glEndList();
		
		 GL11.glDisable(GL11.GL_CULL_FACE);


	}
	protected void checkSceneInput()
	{//TODO: Check for keyboard and mouse input here


		if(Keyboard.isKeyDown(Keyboard.KEY_R))
		{   risingBubble = true;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_L))
		{   risingBubble = false;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{   resetAnimations();
		}

		{
			/**if the key_Left is pressed the fish will move to the left*/
			if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				fishOffsetX += -0.1f * getAnimationScale();

			}
			/**if the key_Right is pressed the fish will move to the right*/
			if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				fishOffsetX  += 0.1f* getAnimationScale();

			}
			/**if the key_Up is pressed the fish will move up */
			if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				fishOffsetY += 0.1f * getAnimationScale();

			}
			/**if the key_Down is pressed the fish will move down */
			if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				fishOffsetY += -0.1f* getAnimationScale();
			}
		}
		/**if r is pressed the fish will rotate */
		if(Keyboard.isKeyDown(Keyboard.KEY_R))
        {   rotationAngle += 2.0f * getAnimationScale(); // Make the fish go around if the R key is pressed
            if (rotationAngle > 360.0f) // 
            {  rotationAngle = 0.0f;
            }
        }

	}



	protected void updateScene()
	{
		//TODO: Update your scene variables here - remember to use the current animation scale value
		//        (obtained via a call to getAnimationScale()) in your modifications so that your animations
		//        can be made faster or slower depending on the machine you are working on

		// if the bubble is rising, and it isn't at its highest,
		// the bubble's Y offset is incremented
		if(risingBubble && currentBubbleY < highestBubbleY)
		{   currentBubbleY += 1.0f * getAnimationScale();
		}
		// else if the bubble is falling, and it isn't at its lowest,
		//  the bubble's Y offset decremented
		else if(!risingBubble && currentBubbleY > lowestBubbleY)
		{   currentBubbleY -= 1.0f * getAnimationScale();
		}

	}

	private void resetAnimations() {
		// reset all variables that take part in the animations
		// to their initial values... which should all be zero
		final float zero = 0.0f;
		fishOffsetY = zero;

		currentBubbleY = highestBubbleY;
		risingBubble = true;
	}
	private void drawUnitPLane() {
		// TODO Auto-generated method stub

		Vertex v1 = new Vertex(-0.5f, 0.0f,-0.5f); // left,  back
		Vertex v2 = new Vertex( 0.5f, 0.0f,-0.5f); // right, back
		Vertex v3 = new Vertex( 0.5f, 0.0f, 0.5f); // right, front
		Vertex v4 = new Vertex(-0.5f, 0.0f, 0.5f); // left,  front

		// draw the plane geometry. order the vertices so that the plane faces up
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v4.toVector(),v3.toVector(),v2.toVector(),v1.toVector()).submit();

			GL11.glTexCoord2f(0.0f,0.0f);
			v4.submit();

			GL11.glTexCoord2f(1.0f,0.0f);
			v3.submit();

			GL11.glTexCoord2f(1.0f,1.0f);
			v2.submit();

			GL11.glTexCoord2f(0.0f,1.0f);
			v1.submit();
		}
		GL11.glEnd();

		// if the user is viewing an axis, then also draw this plane
		// using lines so that axis aligned planes can still be seen
		if(isViewingAxis())
		{
			// also disable textures when drawing as lines
			// so that the lines can be seen more clearly
			GL11.glPushAttrib(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINE_LOOP);
			{
				v4.submit();
				v3.submit();
				v2.submit();
				v1.submit();
			}
			GL11.glEnd();
			GL11.glPopAttrib();
		}

	}

	protected void renderScene()
	{//TODO: Render your scene here - remember that a scene graph will help you write this method! 
		//      It will probably call a number of other methods you will write.

		//Background
		GL11.glPushMatrix();
		{
			// disable lighting calculations so that they don't affect
			// the appearance of the texture 
			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
			GL11.glDisable(GL11.GL_LIGHTING);
			// change the geometry colour to white so that the texture
			// is bright and details can be seen clearly
			Colour.WHITE.submit();
			// enable texturing and bind an appropriate texture
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D,underwaterBG.getTextureID());

			// position, scale and draw the back plane
			GL11.glTranslatef(0.0f,0.0f,-20.0f);
			GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
			GL11.glScaled(17.0f, 1.0f, 17.0f);
			drawUnitPLane();

			// disable textures and reset any local lighting changes
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPopAttrib();
		}
		GL11.glPopMatrix();
		
	     // draw the BUBBLE
        GL11.glPushMatrix();
        {
            // how shiny are the front faces of the moon (specular exponent)
            float bubbleFrontShininess  = 2.0f;
            // specular reflection of the front faces of the moon
            float bubbleFrontSpecular[] = {0.6f, 0.6f, 0.6f, 1.0f};
            // diffuse reflection of the front faces of the moon
            float bubbleFrontDiffuse[]  = {0.6f, 0.6f, 0.6f, 1.0f};

            // set the material properties for the sun using OpenGL
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, bubbleFrontShininess);
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(bubbleFrontSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(bubbleFrontDiffuse));

            // change the geometry colour to white so that the texture
            // is bright and details can be seen clearly
            Colour.WHITE.submit();
            // enable texturing and bind an appropriate texture
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            //if (risingBubble)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,bubble.getTextureID());  
            //els
            //	GL11.glBindTexture(GL11.GL_TEXTURE_2D, bubbleTexture.getTextureID());   
            
            // position and draw the moon using a sphere quadric object
            GL11.glTranslatef(0.0f, currentBubbleY, -19.0f);
            GL11.glScalef(2.0f, 2.0f, 2.0f);
            GL11.glRotatef(270.0f, 1, 0, 0);
            Sphere sphere = new Sphere();
            sphere.setTextureFlag(true);
            sphere.draw(0.7f, 20, 20);
            
           GL11.glDisable(GL11.GL_TEXTURE_2D);            
        }
        GL11.glPopMatrix();
	
    	//draw fish
			GL11.glPushMatrix();
			{
				GL11.glTranslatef(fishOffsetX, fishOffsetY, -10.0f);
				GL11.glRotatef(270.0f, 3.0f, 1.0f, 0.0f);;
				
			     GL11.glRotatef(rotationAngle, 3.0f, 1.0f, 0.0f);
				// how shiny are the front faces of the fish (specular exponent)
				float fishFrontShininess = 2.0f;
				// specular reflection of the front faces of the fish
				float fishFrontSpecular[] = {0.1f, 0.1f, 0.1f, 1.0f};
				// diffuse reflection of the front faces of the fish
				float fishFrontDiffuse[] = {2.55f, 0.65f, 0.0f, 1.0f};

				// Set the material properties for the fish using OpenGL
				GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, fishFrontShininess);
				GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(fishFrontSpecular));
				GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(fishFrontDiffuse));
				GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, FloatBuffer.wrap(fishFrontDiffuse));

				// draw the fish using its display list
				GL11.glCallList(fishBodyList);
			}
			GL11.glPopMatrix();	
	



		
		//draw pearl
		GL11.glPushMatrix();
		if (fishOffsetX < 0.9f ) {
			// how shiny are the front faces of the pearl (specular exponent)
			float pearlFrontShininess  =  1.0f;
			// specular reflection of the front faces of the pearl
			float pearlFrontSpecular[] = {1.0f, 1.0f, 1.0f, 1.0f};
			// diffuse reflection of the front faces of the pearl
			float pearlFrontDiffuse[]  = {2.55f, 0.5f, 1.0f, 1.0f};

			// set the material properties for the pearl using OpenGL
			GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, pearlFrontShininess);
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(pearlFrontSpecular));
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(pearlFrontDiffuse));

			// change the geometry colour to white so that the texture
			// is bright and details can be seen clearly
			Colour.WHITE.submit();
			// enable texturing and bind an appropriate texture

			//GL11.glEnable(GL11.GL_TEXTURE_2D);

			
			//translate the pearl
			GL11.glTranslatef(3.0f, -2.5f, -9.5f);
			//create the pearl
			new Sphere().draw(1.0f, 10, 10);
			//disable the texture and lighting 
			//GL11.glDisable(GL11.GL_TEXTURE_2D);
			//GL11.glDisable(GL11.GL_LIGHTING);
		}

		GL11.glPopMatrix();
		
	}

	protected void setSceneCamera()
	{
		// call the default behaviour defined in GraphicsLab. This will set a default perspective projection
		// and default camera settings ready for some custom camera positioning below...  
		super.setSceneCamera();

		//TODO: If it is appropriate for your scene, modify the camera's position and orientation here
		//        using a call to GL11.gluLookAt(...)
	}

	protected void cleanupScene()
	{//TODO: Clean up your resources here
	}

	private void drawUnitPlane()
	{
		Vertex v1 = new Vertex(-0.8f, 0.0f,-0.5f); // left,  back
		Vertex v2 = new Vertex( 0.8f, 0.0f,-0.5f); // right, back
		Vertex v3 = new Vertex( 0.8f, 0.0f, 0.5f); // right, front
		Vertex v4 = new Vertex(-0.8f, 0.0f, 0.5f); // left,  front

		// draw the plane geometry. order the vertices so that the plane faces up
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v4.toVector(),v3.toVector(),v2.toVector(),v1.toVector()).submit();

			GL11.glTexCoord2f(0.0f,0.0f);
			v4.submit();

			GL11.glTexCoord2f(1.0f,0.0f);
			v3.submit();

			GL11.glTexCoord2f(1.0f,1.0f);
			v2.submit();

			GL11.glTexCoord2f(0.0f,1.0f);
			v1.submit();
		}
		GL11.glEnd();
		// if the user is viewing an axis, then also draw this plane
		// using lines so that axis aligned planes can still be seen
		if(isViewingAxis())
		{
			// also disable textures when drawing as lines
			// so that the lines can be seen more clearly
			GL11.glPushAttrib(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINE_LOOP);
			{
				v4.submit();
				v3.submit();
				v2.submit();
				v1.submit();
			}
			GL11.glEnd();
			GL11.glPopAttrib();
		}
	}

	

	public void drawUnitFishBody() {
		// TODO Auto-generated method stub
		Vertex v1 = new Vertex(  0.0f,  0.0f,  0.7f);
		Vertex v2 = new Vertex(  0.75f,  0.0f,  0.7f);
		Vertex v3 = new Vertex(  1.2f,  0.0f,  0.7f);
		Vertex v4 = new Vertex(  1.7f,  0.0f,  0.0f);

		Vertex v5 = new Vertex(  1.2f, -0.3f,  0.0f);
		Vertex v6 = new Vertex(  0.75f, -0.7f,  0.0f);
		Vertex v7 = new Vertex(  0.0f, -0.7f,  0.0f);
		Vertex v8 = new Vertex( -1.3f,  0.0f,  0.0f);

		Vertex v9 = new Vertex( -1.9f, -0.7f,  0.0f);
		Vertex m1 = new Vertex( -1.55f, 0.1f,  0.5f);
		Vertex m2 = new Vertex(  1.2f,  0.3f,  0.0f);
		Vertex m3 = new Vertex(  0.75f,  0.75f,  0.0f);

		Vertex m4 = new Vertex(  0.0f,  0.75f,  0.0f);
		Vertex m5 = new Vertex( -1.9f,  0.75f,  0.0f);
		Vertex m6 = new Vertex(  0.0f,  0.0f, -0.7f);
		Vertex m7 = new Vertex(  0.75f,  0.0f, -0.7f);

		Vertex m8 = new Vertex(  1.2f,  0.0f, -0.7f);
		Vertex m9 = new Vertex(-1.55f,  0.1f, -0.7f);

		//Bottom Face One
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v8.toVector(), v7.toVector(), v1.toVector()).submit();

			v8.submit();
			v7.submit();
			v1.submit();

			GL11.glEnd();
		}

		//Bottom Face Two
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v7.toVector(), v6.toVector(), v2.toVector(), v1.toVector()).submit();

			v7.submit();
			v6.submit();
			v2.submit();
			v1.submit();

			GL11.glEnd();
		}

		//Bottom Face Three
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v6.toVector(), v5.toVector(), v3.toVector(), v2.toVector()).submit();

			v6.submit();
			v5.submit();
			v3.submit();
			v2.submit();

			GL11.glEnd();
		}

		//Bottom Face Four
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v5.toVector(), v4.toVector(), v3.toVector()).submit();

			v5.submit();
			v4.submit();
			v3.submit();

			GL11.glEnd();
		}

		//Top Face One
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v8.toVector(), v1.toVector(), m4.toVector()).submit();

			v8.submit();
			v1.submit();
			m4.submit();

			GL11.glEnd();
		}

		//Top Face Two
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v1.toVector(), v2.toVector(), m3.toVector(), m4.toVector()).submit();

			v1.submit();
			v2.submit();
			m3.submit();
			m4.submit();

			GL11.glEnd();
		}

		//Top Face Three
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v2.toVector(), v3.toVector(), m2.toVector(), m3.toVector());

			v2.submit();
			v3.submit();
			m2.submit();
			m3.submit();

			GL11.glEnd();
		}

		//Top Face Four
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v3.toVector(), v4.toVector(), m2.toVector());

			v3.submit();
			v4.submit();
			m2.submit();

			GL11.glEnd();
		}

		//Back Face One
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v4.toVector(), v5.toVector(), m8.toVector());

			v4.submit();
			v5.submit();
			m8.submit();

			GL11.glEnd();
		}

		//Back Face Two
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v5.toVector(), v6.toVector(), m7.toVector(), m8.toVector()).submit();

			v5.submit();
			v6.submit();
			m7.submit();
			m8.submit();

			GL11.glEnd();
		}

		//Back Face Three
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v6.toVector(), v7.toVector(), m6.toVector(), m7.toVector()).submit();

			v5.submit();
			v7.submit();
			m6.submit();
			m7.submit();

			GL11.glEnd();
		}

		//Back Face Four
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v7.toVector(), v8.toVector(), m7.toVector()).submit();

			v7.submit();
			v8.submit();
			m6.submit();

			GL11.glEnd();
		}

		//Back Top Face One
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v4.toVector(), m8.toVector(), m2.toVector());

			v4.submit();
			m8.submit();
			m2.submit();

			GL11.glEnd();
		}

		//Back Top Face Two
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(m8.toVector(), m7.toVector(), m3.toVector(), m2.toVector()).submit();

			m8.submit();
			m7.submit();
			m3.submit();
			m2.submit();

			GL11.glEnd();
		}

		//Back Top Face Three
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(m7.toVector(), m6.toVector(), m4.toVector(), m3.toVector()).submit();

			m7.submit();
			m6.submit();
			m4.submit();
			m3.submit();

			GL11.glEnd();
		}

		//Back Top Face Four
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(m6.toVector(), v8.toVector(), m4.toVector()).submit();

			m6.submit();
			v8.submit();
			m4.submit();

			GL11.glEnd();
		}

		//Finn Face One
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v9.toVector(), v8.toVector(), m1.toVector()).submit();

			v9.submit();
			v8.submit();
			m1.submit();

			GL11.glEnd();
		}

		//Finn Face Two
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v9.toVector(), m1.toVector(), m5.toVector());

			v9.submit();
			m1.submit();
			m5.submit();

			GL11.glEnd();
		}

		//Finn Face Three
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(m1.toVector(), v8.toVector(), m5.toVector()).submit();

			m1.submit();
			v8.submit();
			m5.submit();

			GL11.glEnd();
		}

		//Back Finn Face One
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v8.toVector(), v9.toVector(), m9.toVector()).submit();

			v8.submit();
			v9.submit();
			m9.submit();

			GL11.glEnd();
		}

		//Back Finn Face Two
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v9.toVector(), m5.toVector(), m9.toVector()).submit();

			v9.submit();
			m5.submit();
			m9.submit();

			GL11.glEnd();
		}

		//Back Finn Face Three
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v8.toVector(), m9.toVector(), m5.toVector()).submit();

			v8.submit();
			m9.submit();
			m5.submit();

			GL11.glEnd();
		}


	}
	  


}




