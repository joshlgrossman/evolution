package evolution;

public abstract class Sprite {
	
	public Evolution root;
	
	public Sprite(){ this.root = Evolution.root; }
	
	public double x = 0.0, y = 0.0;
	public double vx = 0.0, vy = 0.0;
	public double width = 0.0, height = 0.0;
	public double rotation = 0.0;
	
	public abstract void init();
	public abstract void loop();
	public abstract void remove();
	public abstract void paint(java.awt.Graphics2D gfx);
	
	public int x(){ return (int)Math.round(this.x); }
	public int y(){ return (int)Math.round(this.y); }
	public int width(){ return (int)Math.round(this.width); }
	public int height(){ return (int)Math.round(this.height); }
	
	public static void trace(Object... output){ Evolution.trace(output); }
	
}