import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;

class Agent {

	void drawPlan(Graphics g, Model m) {
		g.setColor(Color.red);
		g.drawLine((int)m.getX(), (int)m.getY(), (int)m.getDestinationX(), (int)m.getDestinationY());
	}

	void update(Model m)
	{
		Controller c = m.getController();
		while(true)
		{
			MouseEvent e = c.nextMouseEvent();
			if(e == null)
				break;
			m.setDestination(e.getX(), e.getY());
		}
	}

	public static void main(String[] args) throws Exception
	{
		Controller.playGame();
	}
}
