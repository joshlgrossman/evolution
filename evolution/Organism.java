package evolution;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

public class Organism extends Sprite { 
	
	public static final BasicStroke STROKE = new BasicStroke(2);
	
	/*
	 * 0000 0000 0000 0000 0000 0000 0000 0000
	 *										
	 */
	
	public int DNA;
	public int bits(int number){ return (this.DNA & number); }
	public int bits(int number, int shift){ return (this.DNA & (number << shift)) >> shift; }
	
	public double energy;
	public double divideEnergy;
	public double divideChance;
	public double size;
	public double weight;
	public double speed;
	public int shape;
	public int food;
	public int waste;
	public Color color;
	public Path2D.Double image;
	
	public Organism(){ this(math.randomInt(), 0.25, math.random()*800, math.random()*600); }
	
	public Organism(int DNA){ this(DNA, 0.25, math.random()*800, math.random()*600); }
	
	public Organism(int DNA, double mutationFactor, double x, double y){
		this.x = x;
		this.y = y;
		this.DNA = DNA;
		double rand = math.random();
		while(rand < mutationFactor){
			this.DNA += 1 << (math.randomInt()%32);
			rand *= 2.0;
		}
	}
	
	public void init() {
		int a = this.bits(0xF);
		int b = this.bits(0xF,2);
		int c = this.bits(0xF,4);
		int d = this.bits(0xF,8);
		
		int e = this.bits(0x3,a);
		int f = this.bits(0x3,b);
		
		int g = this.bits(0x7,24);
		int h = this.bits(0x7,27);
		
		this.shape = (this.bits(b,c) + 3);
		this.size = (this.bits(a,b) + this.bits(b,c) + 8.0) / (this.bits(e*f,c) + 1.0) + 10.0;
		this.weight = (this.bits(d,e*f) + 1.0);
		
		double sizeRatio = ((a + b + 1.0) / (a + b + c + d + 1.0)) * 0.75 + 0.25;
		double weightRatio = ((double)this.weight/(double)16);
		
		this.width = this.size * sizeRatio;
		this.height = this.size - this.width;
		
		this.speed = (e * f + a + 1.0) / (b + 1.0);
		
		this.food = e;
		this.waste = f;
		if(this.food == this.waste)
			this.waste = (this.waste + 1) % 4;
		
		this.divideEnergy = (Math.max(g,h) + f + 1.0);
		this.divideChance = (g + 1.0)/(d + e + g + h + 5.0);
		
		int red = this.bits(0xF,0) << 4;
		int green = this.bits(0xF,4) << 4;
		int blue = this.bits(0xF,8) << 4;
		int alpha = (int)(weightRatio * 255);
		
		this.color = math.darken(new Color(red,green,blue,alpha), weightRatio);
		
		this.image = createShape(this.shape,this.width,this.height);
		
		this.energy = this.divideEnergy * 0.99;
	}
	
	public void loop() {
		this.vx *= 0.95;
		this.vy *= 0.95;
		
		double vsq = vx*vx + vy*vy;
		if(vsq < 0.01){
			this.vx += math.random()*0.4-0.2;
			this.vy += math.random()*0.4-0.2;
			this.energy -= 0.01;
		} else if(vsq < this.speed*this.speed){
			double v = Math.sqrt(vsq);
			this.vx += (vx/v)/this.weight;
			this.vy += (vy/v)/this.weight;
			this.energy -= 0.01;
		}
		
		if(this.x < 0 || this.x > Evolution.WIDTH) this.vx *= -0.975;
		if(this.y < 0 || this.y > Evolution.HEIGHT-30)this.vy *= -0.975; 
		
		this.rotation += (Math.abs(this.vx) + Math.abs(this.vy))/(math.TAU * this.weight);
		this.x += this.vx/this.weight;
		this.y += this.vy/this.weight;
		
		if(this.energy < 0)
			root.remove(this);
		else if(this.energy > this.divideEnergy && math.random() > this.divideChance){
			root.add(new Organism(this.DNA,this.divideChance,this.x,this.y));
			this.energy -= this.divideEnergy;
		}
	}
	
	public void remove() {
		root.add(new Nutrient(this.waste,this.x,this.y,this.vx*0.2,this.vy*0.2));
	}
	
	public void paint(Graphics2D g){
		int w = this.width();
		int h = this.height();
		int x = this.x() - (w >> 1);
		int y = this.y() - (h >> 1);
		AffineTransform at = new AffineTransform();
		at.translate(this.x,this.y);
		at.rotate(this.rotation);
		Path2D.Double img = (Path2D.Double)this.image.clone();
		img.transform(at);
		g.setColor(this.color);
		g.fill(img);
		g.setStroke(STROKE);
		g.setColor(getCellWall());
		g.draw(img);
	}
	
	public boolean hit(double x, double y){
		AffineTransform at = new AffineTransform();
		at.translate(this.x,this.y);
		at.rotate(this.rotation);
		Path2D.Double img = (Path2D.Double)this.image.clone();
		img.transform(at);
		return img.contains(x, y);
	}
	
	public Color getCellWall(){
		float alpha = (float)(this.energy/5);
		if(alpha < 0) alpha = 0;
		if(alpha > 1) alpha = 1;
		return new Color(0,0,0,alpha);
	}
	
	public void addEnergy(double e){
		this.energy += e;
		root.add(new Nutrient(this.waste,this.x,this.y,math.random()-0.5,math.random()-0.5));
	}
	
	public static Path2D.Double createShape(int sides, double width, double height){
		Path2D.Double p = new Path2D.Double();
		double a = 0.0;
		double da = math.TAU/(double)sides;
		double hw = width/2.0;
		double hh = height/2.0;
		p.moveTo(hw,0);
		for(int i = 0; i < sides; i++){
			a += da;
			p.lineTo(math.cos(a)*hw, math.sin(a)*hh);
		}
		AffineTransform at = new AffineTransform();
		at.setToRotation(math.randomAngle());
		p.transform(at);
		return p;
	}
	
	public String toString(){
		String s = "Organism: "+Integer.toHexString(this.DNA) +"\n";	
		return s;
	}
}