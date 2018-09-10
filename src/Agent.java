import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;


class MS {
	float x;
	float y;
	public float cost;
	MS parent;

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
		return Math.round((this.x/10.0)*10) == Math.round((checkIf.x/10.0)*10) && Math.round((this.y/10)*10) == Math.round((checkIf.y/10)*10);
	}

	LinkedList<MS> getPath(MS finalState)
	{
		LinkedList<MS> temp = new LinkedList<>();
		temp.add(finalState);
		while(finalState.parent != null)
		{
//            System.out.println(currentLevel.printState());
			finalState= finalState.parent;
			temp.add(finalState);

		}

		return temp;

//		Iterator x = temp.descendingIterator();

//		while(x.hasNext())
//		{
//			MS element = (MS)x.next();
////			System.out.println(element.printState());
//
//		}

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

	public MS UCS(MS startState, MS goalState, Model m) {

		CostComparator costComparator = new CostComparator();
		StateComparator stateComparator = new StateComparator();

		TreeSet<MS> frontier = new TreeSet<>(costComparator); //FIFO counter
		TreeSet<MS> beenthere= new TreeSet<>(stateComparator);
		startState.cost = 0.0f;
		startState.parent = null;

		//close set
		beenthere.add(startState);

		//open set
		frontier.add(startState);

		MS s = new MS();
		MS oldchild;

		while(!frontier.isEmpty()) {


			s = frontier.pollFirst(); // get lowest-cost state

			float x = Math.round((s.x/10.0))*10;
			float y = Math.round((s.y/10.0))*10;
			float gx = Math.round((goalState.x/10.0))*10;
			float gy = Math.round((goalState.y/10.0))*10;

			boolean same;
			if(x == gx && y == gy)
			{
				same = true;
			}
			else
			{
				same = false;
			}


			if(same)
				 return s; // this is the final state

//			beenthere.add(s);

				ArrayList<MS> action = GenerateNeighbors(s,goalState,m);

			for (MS a : action) {

//				float acost = 1/m.getTravelSpeed(a.x,a.y);


				if(beenthere.contains(a))
				{
					oldchild = beenthere.floor(a);
					oldchild.cost = 1/m.getTravelSpeed(oldchild.x, oldchild.y) + eDistance(oldchild,goalState);
//					oldchild.cost = 1/m.getTravelSpeed(oldchild.x,oldchild.y);
					if(a.cost < oldchild.cost)
					{
						oldchild.cost =  a.cost;
						oldchild.parent = s;
					}
				}
				else
				{
					//when beenthere doesn't have the child in it
					a.cost = 1/m.getTravelSpeed(a.x,a.y) + eDistance(a,goalState);
					a.parent = s;
					frontier.add(a);
					beenthere.add(a);
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
				tempList.add(temp);
			}

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
			//this sets the destination of the can. It modifies x and y destination values to the clicked place
			m.setDestination(e.getX(), e.getY());
		}

		MS goalState = new MS(m.getDestinationX(), m.getDestinationY());

		MyPlanner planner = new MyPlanner();
		MS finalState = planner.UCS(startState, goalState, m);
		LinkedList<MS> path = finalState.getPath(finalState);

		log(String.valueOf(path.size()));
		log(String.valueOf(goalState.x + " " + goalState.y));


		//set destination to next one in path
if(path.size() == 1)
{
	//if there is only the current one in the path stay still
	m.setDestination(path.get(0).x, path.get(0).y);

}
else
{
	//move to the next in path
	m.setDestination(path.get(1).x, path.get(1).y);
}

		//draw the line from the path from finalState to startState

		//f(n) = g(n) + h(n) where g is cost and h is heuristic

	}




	public static void log(String x)
	{
//		System.out.println(x);
	}

	public static void main(String[] args) throws Exception
	{

		Controller.playGame();
	}
}




