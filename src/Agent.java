import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;


class MS {
	float x;
	float y;

	float f;
	float g;
	float h;
	public float cost;
	MS parent;
	ArrayList<MS> neighbors = null;



	MS()
	{}

	MS(MS copyState)
	{
		this.x = copyState.x;
		this.y = copyState.y;
		this.cost = copyState.cost;
		MS temp;
		temp = copyState;
		this.parent = temp;
	}

	MS(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	MS(float c, float x, float y, MS par)
	{
		this.cost = c;
		this.x = x;
		this.y = y;
		MS temp;
		temp = par;
		this.parent = temp;
	}

	MS(float cost, MS par) {

		MS temp = new MS();
		this.cost = cost;
		temp = par;
		this.parent = temp;

	}

	boolean isEqual(MS checkIf)
	{
		return (int) this.x == (int) checkIf.x && (int) this.y == (int) checkIf.y;
	}

	LinkedList<MS> getPath()
	{
		LinkedList<MS> temp = new LinkedList<>();
		Iterator x = temp.descendingIterator();

		temp.add(this);
		while(x.hasNext())
		{
			MS element = (MS)x.next();
			temp.add(element);
		}
		return temp;

	}
}

class CostComparator implements Comparator<MS> {

	public int compare(MS a, MS b)
	{
			if(a.cost < b.cost)
				return -1;
			else if(a.cost > b.cost )
				return 1;

		return 0;
	}
}

class StateComparator implements Comparator<MS> {

	public int compare(MS a, MS b)
	{
		if(a.x < b.x || a.y < b.y)
			return -1;
		else if(a.x > b.x || a.y > b.y)
			return 1;
		return 0;

	}
}


 class MyPlanner {

	float heuristic(MS a, MS goal)
	{
		return (float)Math.sqrt((a.x - goal.x) * (a.x - goal.x) + (a.y - goal.y) * (a.y - goal.y));
	}

	public MS search(MS startState, MS goalState, Model m) {

		CostComparator costComparator = new CostComparator();
		StateComparator stateComparator = new StateComparator();

		TreeSet<MS> todo = new TreeSet<>(costComparator); //FIFO counter
		TreeSet<MS> seenIt= new TreeSet<>(stateComparator);
		startState.cost = 0.0f;
		startState.parent = null;

		//close set
		seenIt.add(startState);

		//open set
		todo.add(startState);

		while(!todo.isEmpty()) {

			MS currentState = new MS();
			currentState = todo.pollFirst(); // get lowest-cost state

			if(currentState.isEqual(goalState))
				 return currentState; // this is the final state

				ArrayList<MS> action = GenerateNeighbors(currentState,goalState,m);

				MS oldchild = new MS();
			for (MS a : action) {

				if(seenIt.contains(a))
				{
//					float tempG = eDistance(a,goalState) + m.getTravelSpeed(a.x,a.y);
					oldchild = seenIt.floor(a);
					if(currentState.cost + a.cost< oldchild.cost)
					{
						oldchild.cost = currentState.cost + a.cost;
						oldchild.parent = currentState;
					}
				}
				else
				{
					a.cost = currentState.cost + a.cost;
					a.parent = currentState;
					todo.add(a);
					seenIt.add(a);
				}
			}
		}
		throw new RuntimeException("There is no path to the goal");
	}

	ArrayList<MS> GenerateNeighbors(MS currentState, MS goalState, Model m)
	{

		ArrayList<MS> tempList = new ArrayList<>();
		int toggle = 10;

		for (int i = 0; i < 4; i++) {
			MS temp = new MS(currentState);

			if(i%2 == 0)
			{
				temp.x += toggle;
			}
			else
			{
				temp.y += toggle;
			}

			if(temp.x < Model.XMAX && temp.y < Model.YMAX  && temp.x > 0 && temp.y > 0)
			{
				temp.cost = 1/m.getTravelSpeed(temp.x,temp.y);
			}
			tempList.add(temp);

			if(i == 1)
			{
				toggle *= -1;
			}
		}

		return tempList;
	}
	public static float eDistance(MS a, MS goal)
	{
		return (float)Math.sqrt((a.x - goal.x) * (a.x - goal.x) + (a.y - goal.y) * (a.y - goal.y));

	}

}



class Agent {

	void drawPlan(Graphics g, Model m) {
		g.setColor(Color.red);
		g.drawLine((int)m.getX(), (int)m.getY(), (int)m.getDestinationX(), (int)m.getDestinationY());
	}

	void update(Model m)
	{
		Controller c = m.getController();
		MS startState = new MS(0.0f, null);
		startState.x = m.getX();
		startState.y = m.getY();

		//main loop
		while(true)
		{
			MouseEvent e = c.nextMouseEvent();
			if(e == null)
				break;
			m.setDestination(e.getX(), e.getY());
		}

		MS goalState = new MS(m.getDestinationX(), m.getDestinationY());

		MyPlanner planner = new MyPlanner();
		MS finalState = planner.search(startState, goalState, m);
		LinkedList<MS> path = finalState.getPath();

		for (MS p :
				path) {
			log(p.x+ " "+ p.y);
		}
if(path.size() > 1)
{
	m.getSprites().get(0).xDestination= path.get(1).x;
	m.getSprites().get(0).yDestination = path.get(1).y;
}
else
{
	m.getSprites().get(0).xDestination= path.get(0).x;
	m.getSprites().get(0).yDestination = path.get(0).y;
}

		//draw the line from the path from finalState to startState

		//f(n) = g(n) + h(n) where g is cost and h is heuristic

	}




	public static void log(String x)
	{
		System.out.println(x);
	}

	public static void main(String[] args) throws Exception
	{

		Controller.playGame();
	}
}
