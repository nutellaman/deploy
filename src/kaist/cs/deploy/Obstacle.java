package kaist.cs.deploy;

import java.awt.geom.Rectangle2D;

public class Obstacle extends Rectangle2D.Double {

	public final double att;
	public final double f;
	
	public Obstacle(double x, double y, double w, double h, double att, double f) {
		super(x,y,w,h);
		this.att = att;
		this.f = f;
	}
	
	public Obstacle(double x, double y, double w, double h, double f) {
		super(x,y,w,h);
		this.att = 0.5;
		this.f = f;
	}
	
	public Obstacle(double x, double y, double w, double h) {
		super(x,y,w,h);
		this.att = 0.5;
		this.f = 5;
	}
}
