package evolution;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

public class Evolution extends Canvas implements WindowListener, MouseListener, MouseMotionListener {
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final int MAX_SPRITES = 1000;
	public static final Color BG = new Color(0x99EBEBEB, true);
	
	public static JFrame stage;
	public static Evolution root;
	
	public BufferStrategy _bufferStrat;
	public ArrayList<Sprite> _sprites;
	public ArrayList<Organism> _organisms;
	public ArrayList<Nutrient> _nutrients;
	public int num_sprites;
	
	public static void main(String[] args){

		//System.setProperty("sun.java2d.trace", "timestamp,log,count");
		//System.setProperty("sun.java2d.opengl", "True");
		System.setProperty("sun.java2d.accthreshold","0");
		System.setProperty("sun.java2d.d3d", "True");
		System.setProperty("sun.java2d.translaccel", "true");
		System.setProperty("sun.java2d.ddforcevram", "True");
		System.setProperty("sun.java2d.ddscale","True");
				
		stage = new JFrame();
		root = new Evolution();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		stage.setAutoRequestFocus(true);
		stage.setSize(WIDTH,HEIGHT);
		stage.setLocation(screenSize.width/2-WIDTH/2,screenSize.height/2-HEIGHT/2 - 40);
        stage.setResizable(false);
		stage.setVisible(true);
		stage.addWindowListener(root);
		stage.addMouseMotionListener(root);
		root.addMouseListener(root);
		stage.requestFocus();
		
		stage.add(root);
		
		root.init();
		
		int delay = 40;
		long cycle = System.currentTimeMillis();
		
		while(true){
			root.loop();
			root.repaint();
			cycle += delay;
			long difference = cycle - System.currentTimeMillis();
			stage.requestFocus();
			try {
				Thread.sleep(Math.max(difference,0));
			} catch(Exception e){
				System.exit(0);
				break;
			}
		}
		
	}
	
	public Evolution(){}
	
	public void add(Sprite sprite){
		if(this.num_sprites < MAX_SPRITES){
			this._sprites.add(sprite);
			sprite.init();
		}
	}
	
	public void add(Organism organism){
		if(this.num_sprites < MAX_SPRITES){
			this._organisms.add(organism);
			this._sprites.add(organism);
			organism.init();
		}
	}
	
	public void add(Nutrient nutrient){
		if(this.num_sprites < MAX_SPRITES){
			this._nutrients.add(nutrient);
			this._sprites.add(nutrient);
			nutrient.init();
		}
	}
	
	public void remove(Sprite sprite){
		sprite.remove();
		this._sprites.remove(sprite);
	}
	
	public void remove(Organism organism){
		organism.remove();
		this._sprites.remove(organism);
		this._organisms.remove(organism);
	}
	
	public void remove(Nutrient nutrient){
		nutrient.remove();
		this._sprites.remove(nutrient);
		this._nutrients.remove(nutrient);
	}
	
	public synchronized void init(){
		this.createBufferStrategy(2);
		this._bufferStrat = this.getBufferStrategy();
		this._sprites = new ArrayList<Sprite>();
		this._organisms = new ArrayList<Organism>();
		this._nutrients = new ArrayList<Nutrient>();
		
		for(int i = 0; i < 10; i++){
			Organism o = new Organism();
			this.add(o);
		}
	}
	
	public synchronized void loop(){
		try {
			this.num_sprites = this._sprites.size();
			
			for(Sprite sprite : this._sprites)
				sprite.loop();	
			
			if(this._sprites.size() < 500){
				Nutrient n = new Nutrient();
				this.add(n);
			}
		} catch (Exception e){}
	}
	
	public synchronized void update(Graphics g){ this.paint(g); }
	public synchronized void paint(Graphics g){
		if(this._bufferStrat != null){
			do {
				Graphics2D gfx = (Graphics2D)this._bufferStrat.getDrawGraphics();
				gfx.setColor(BG);
				gfx.fillRect(0, 0, WIDTH, HEIGHT);
				
				gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				
				for(Sprite sprite : this._sprites)
					sprite.paint(gfx);
				
				gfx.dispose();
				
				this._bufferStrat.show();
			} while(this._bufferStrat.contentsRestored());
			
			Toolkit.getDefaultToolkit().sync();
		}
	}
	
	public static void trace(Object... output){
		String str = "";
		for(Object obj : output)
			str += obj.toString() + " ";
		System.out.println(str);
	}
	
	
	public void windowClosing(WindowEvent e){
		stage.remove(root);
		stage.setVisible(false);
		stage.dispose();
		System.exit(0);
	}
	
	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	
	public void mouseDragged(MouseEvent me){}
	public void mouseMoved(MouseEvent me){}
	public void mousePressed(MouseEvent me){}
	public void mouseReleased(MouseEvent me){}
	public void mouseClicked(MouseEvent me){
		for(Organism o : this._organisms)
			trace(o);
	}
	public void mouseEntered(MouseEvent me){}
	public void mouseExited(MouseEvent me){}
	
}