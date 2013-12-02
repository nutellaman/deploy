package kaist.cs.deploy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DeployView extends JPanel {

	public Controller c;
	public int itercount = 1;
	public DeployView() {
		super();
		c = new Controller();
		c.addObs(new Obstacle(10,10,10,210));
		c.addObs(new Obstacle(10,10,210,10));
		c.addObs(new Obstacle(110,10,10,110));
		c.addObs(new Obstacle(210,10,10,210));
		c.addObs(new Obstacle(10,210,210,10));
		//c.addObs(new Obstacle(30,30,70,170,1,0));
		//c.addObs(new Obstacle(30,130,170,70,1,0));
		c.addNode(new Node(50,50));
		c.addNode(new Node(51,55));
		c.addNode(new Node(55,58));
		c.addNode(new Node(53,80));
		c.addNode(new Node(58,51));

	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.clearRect(0, 0, 500, 500);
		g2.setColor(Color.BLACK);
		for (int i=0; i<c.obsLen();i++) {
			g2.drawRect((int)c.obsAt(i).x + 50, (int)c.obsAt(i).y + 50, (int)c.obsAt(i).width, (int)c.obsAt(i).height);
		}
		
		for (int i=0; i<c.nodeLen(); i++) {
			g2.setColor(new Color(1.0f, 0.4f, 0.4f, 0.5f));
			g2.fillOval((int)Math.round(c.nodeAt(i).x) + 50 - (int)c.nodeAt(i).coverage, (int)Math.round(c.nodeAt(i).y) + 50 - (int)c.nodeAt(i).coverage, 
					(int)c.nodeAt(i).coverage * 2, (int)c.nodeAt(i).coverage * 2);
			}
		
		for (int i=0; i<c.nodeLen(); i++) {
			g2.setColor(Color.BLACK);
			g2.fillOval((int)Math.round(c.nodeAt(i).x) + 50 - 2, (int)Math.round(c.nodeAt(i).y) + 50 - 2, 4, 4);
			g2.drawString("velocity: (" + c.nodeAt(i).v.x + "," + c.nodeAt(i).v.y + ")", 50, 300 + (i*20));
		}

		g2.drawString("Iteration " + itercount++, 50, 180);
	}
	
	public void iterate() {
		c.iterate(itercount);
	}
	
	public static void main(String[] args) {
		JFrame window = new JFrame();
		DeployView frame = new DeployView();

		window.setContentPane(frame);
		window.setSize(500,500);
		window.setVisible(true);
		while(true) {
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				
			}
			frame.iterate();
			frame.repaint();
		}
	}
}
