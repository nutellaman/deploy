package kaist.cs.deploy;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Controller {
	
	public static final double OBSFORCE = 0.05;
	public static final double NODEFORCE = 30;
	public static final double VELOCITY_CLIP = 5;
	public static final double REDUCE = 0.95;
	public static final double DT = 1;
	
	private ArrayList<Node> mNodes;
	private ArrayList<Obstacle> mObs;
	
	public Controller() {
		mNodes = new ArrayList<Node>();
		mObs = new ArrayList<Obstacle>();
	}
	
	public boolean addNode(Node n) {
		return mNodes.add(n);
	}
	
	public boolean addObs(Obstacle o) {
		return mObs.add(o);
	}
	
	public Node nodeAt(int i) {
		return mNodes.get(i);
	}
	
	public Obstacle obsAt(int i) {
		return mObs.get(i);
	}
	
	public int nodeLen() {
		return mNodes.size();
	}
	
	public int obsLen() {
		return mObs.size();
	}
	
	public double getAttenuation(Node a, Node b) {
		Line2D.Double abline = new Line2D.Double(a, b);
		double res = 1;
		for (Obstacle o:mObs) {
			if (o.intersectsLine(abline)) {
				res *= o.att;
			}
		}
		
		return res;
	}
	
	public Point2D.Double getForceFromObs(Obstacle o, Node n) {
		Point2D.Double f = new Point2D.Double(0, 0);
		int c = o.outcode(n);
		Point2D.Double s;
		switch(c) {
		case Rectangle2D.OUT_BOTTOM:
			s = new Point2D.Double(n.x, o.y + o.height);
			break;
		case Rectangle2D.OUT_LEFT:
			s = new Point2D.Double(o.x, n.y);
			break;
		case Rectangle2D.OUT_RIGHT:
			s = new Point2D.Double(o.x + o.width, n.y);
			break;
		case Rectangle2D.OUT_TOP:
			s = new Point2D.Double(n.x, o.y);
			break;
		case Rectangle2D.OUT_TOP|Rectangle2D.OUT_RIGHT:
			s = new Point2D.Double(o.x, o.y);
			break;
		case Rectangle2D.OUT_TOP|Rectangle2D.OUT_LEFT:
			s = new Point2D.Double(o.x + o.width, o.y);
			break;
		case Rectangle2D.OUT_BOTTOM|Rectangle2D.OUT_LEFT:
			s = new Point2D.Double(o.x + o.width, o.y + o.height);
			break;
		case Rectangle2D.OUT_BOTTOM|Rectangle2D.OUT_RIGHT:
			s = new Point2D.Double(o.x, o.y + o.height);
			break;
		default:
			s = new Point2D.Double(o.getCenterX(), o.getCenterY());
		}
		
		Line2D.Double l = new Line2D.Double(n,s);
		
		for (Obstacle ob:mObs) {
			if (!ob.equals(o) && ob.intersectsLine(l)) {
				return f;
			}
		}
		
		f.x = OBSFORCE * o.f / Math.pow(n.distance(s), 3) * (n.x - s.x);
		f.y = OBSFORCE * o.f / Math.pow(n.distance(s), 3) * (n.y - s.y);
		return f;
	}
	
	public Point2D.Double forceTo(Node target) {
		
		Point2D.Double f = new Point2D.Double(0,0);
		
		for (Node n : mNodes) {
			if (!n.equals(target)) {
				double r = target.distance(n);
				double att = getAttenuation(target, n);
				double k = NODEFORCE * att / r / r / r;
				f.x += (target.x - n.x) * k;
				f.y += (target.y - n.y) * k;
			}
		}
		
		
		for (Obstacle o:mObs) {
			Point2D.Double k = getForceFromObs(o,target);
			f.x += k.x;
			f.y += k.y;
		}
		
		return f;
	}
	
	public Point2D.Double atBorder(Node n) {
		Point2D.Double result = new Point2D.Double(n.x + (n.v.x * DT), n.y + (n.v.y * DT));
		double round = 2;
		for (Obstacle o:mObs) {
			if (o.contains(result)) {
				int c = o.outcode(n);
				switch(c) {
				case Rectangle2D.OUT_BOTTOM:
					result.y = o.y + o.height + round;
					break;
				case Rectangle2D.OUT_LEFT:
					result.x = o.x - round;
					break;
				case Rectangle2D.OUT_RIGHT:
					result.x = o.x + o.width + round;
					break;
				case Rectangle2D.OUT_TOP:
					result.y = o.y - round;
					break;
				case Rectangle2D.OUT_TOP|Rectangle2D.OUT_RIGHT:
					result.y = o.y - round;
					break;
				case Rectangle2D.OUT_TOP|Rectangle2D.OUT_LEFT:
					result.y = o.y - round;
					break;
				case Rectangle2D.OUT_BOTTOM|Rectangle2D.OUT_LEFT:
					result.y = o.y + o.height + round;
					break;
				case Rectangle2D.OUT_BOTTOM|Rectangle2D.OUT_RIGHT:
					result.y = o.y + o.height + round;
					break;
				default:
					
				}
			}
			/*
			else {
				if (n.x <= o.x && n.x >= o.x - 5) {
					result.x = n.x;
				}
				if (n.x >= o.x + o.width && n.x <= o.x + o.width + 5) {
					result.x = n.x;
				}
				if (n.y <= o.y && n.y >= o.y - 5) {
					result.y = n.y;
				}
				if (n.y >= o.y + o.height && n.y <= o.y + o.height + 5) {
					result.y = n.y;
				}
				
			}*/ 
		}
		return result;
	}
	
	public void iterate() {
		for (Node n : mNodes) {
			n.setLocation(atBorder(n));
			Point2D.Double a = forceTo(n);
			n.v.x *= REDUCE;
			n.v.y *= REDUCE;
			n.v.x += a.x * DT;
			n.v.y += a.y * DT;
			if (n.v.x > VELOCITY_CLIP) {
				n.v.x = VELOCITY_CLIP;
			}
			else if (n.v.x < VELOCITY_CLIP * -1) {
				n.v.x = VELOCITY_CLIP * -1;
			}
			if (n.v.y > VELOCITY_CLIP) {
				n.v.y = VELOCITY_CLIP;
			}
			else if (n.v.y < VELOCITY_CLIP * -1) {
				n.v.y = VELOCITY_CLIP * -1;
			}
		}
	}
	
}
