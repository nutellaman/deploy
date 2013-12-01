package kaist.cs.deploy;

import java.awt.geom.Point2D;

public class Node extends Point2D.Double {
	
	public double coverage = 5;
	public Point2D.Double v;
	
	public Node (double x, double y) {
		super(x,y);
		this.v = new Point2D.Double(0, 0);
	}
}
