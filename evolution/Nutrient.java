package evolution;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

public class Nutrient extends Sprite {

	public static final BasicStroke STROKE = new BasicStroke(0.5f);
	
	public static final int NUM_TYPES = 4;
	public static final Color[] COLORS = {Color.red, Color.green, Color.blue, Color.orange};
	
	public int counter;
	public int type;
	
	public Nutrient(){ this(math.randomInt() % NUM_TYPES, math.random()*800, math.random()*600, math.random()*2-1, math.random()*2-1);  }
	public Nutrient(int type, double x, double y, double vx, double vy){
		this.type = type;
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.counter = 0;
	}
	
	public void init(){}
	public void loop(){
		this.counter = (this.counter+1)%2;
		if(this.counter == 0){
			this.x += this.vx;
			this.y += this.vy;
			
			if(this.x < 0 || this.x > Evolution.WIDTH || this.y < 0 || this.y > Evolution.HEIGHT) root.remove(this);
			
			for(Organism o : root._organisms){
				if(o.food == this.type){
					double dx =(o.x - this.x);
					double dy =(o.y - this.y);
					if(Math.abs(dx) < 30 && Math.abs(dy) < 30){
						if(o.hit(this.x, this.y)){
							o.addEnergy(1);
							root.remove(this);
						} else {
							double d = (dx*dx+dy*dy)/2 + 1;
							double c = -dx/d;
							double s = -dy/d;
							o.vx += c/10;
							o.vy += s/10;
						}
					}
				}
			}
		}
	}
	
	public void remove(){}
	public void paint(Graphics2D gfx){
		int w = 2;
		int h = 2;
		int x = this.x() - (w >> 1);
		int y = this.y() - (h >> 1);
		gfx.setColor(COLORS[this.type]);
		gfx.fillRect(x,y,w,h);
		gfx.setStroke(STROKE);
		gfx.setColor(Color.BLACK);
		gfx.drawRect(x,y,w,h);
	}
	
}