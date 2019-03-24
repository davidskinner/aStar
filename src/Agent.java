import javafx.scene.input.MouseButton;

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
			finalState= finalState.parent;
			temp.add(finalState);
		}

		Iterator x = temp.descendingIterator();

		LinkedList<MS> temps = new LinkedList<>();

		while(x.hasNext())
		{
			MS element = (MS)x.next();
			temps.add(element);
		}

		return temps;

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


class AstarComparator implements Comparator<MS> {

	Model model;
	MS goalState;
	float lc;

	AstarComparator(Model m, MS g)
	{
		model = m;
		goalState = g;
	}

	public static float eDistance(MS a, MS goal)
	{
		return (float)Math.sqrt((a.x - goal.x) * (a.x - goal.x) + (a.y - goal.y) * (a.y - goal.y));
	}

	public int compare(MS a, MS b)
	{
		this.lc = 1.0f/model.getTravelSpeed(285, 577); // 285,577
		if(a.cost + eDistance(a,goalState)*(lc)/10 < b.cost + eDistance(b,goalState)*(lc)/10)
			return -1;
		else if(a.cost + eDistance(a,goalState)*(lc)/10> b.cost + eDistance(b,goalState)*(lc)/10)
			return 1;
		return 0;
	}
}

class
StateComparator implements Comparator<MS> {

	public int compare(MS a, MS b)
	{
		int ax = (int)(a.x/10);
		int ay = (int)(a.y/10);
		int bx = (int)(b.x/10);
		int by = (int)(b.y/10);

		if(ax < bx)
		return -1;
	else if(ax > bx)
		return 1;
	else if(ay < by)
		return -1;
	else if(ay > by)
		return 1;
	return 0;

	}
}



 class MyPlanner {

	TreeSet<MS> frontier;

	 public MS ASTAR(MS startState, MS goalState, Model m) {

		 StateComparator stateComparator = new StateComparator();

		 AstarComparator astarComparator = new AstarComparator(m, goalState);

		 frontier = new TreeSet<>(astarComparator); //FIFO counter
		 TreeSet<MS> beenthere= new TreeSet<>(stateComparator);
		 startState.cost = 0.0f;
		 startState.parent = null;

		 //close set
		 beenthere.add(startState);

		 //open set
		 frontier.add(startState);

		 beenthere.add(startState);
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
			 ArrayList<MS> children = generateChildren(s,goalState,m);

			 for (MS child : children) {

				 float acost = 1/m.getTravelSpeed(child.x,child.y);
				 if(beenthere.contains(child))
				 {
					 oldchild = beenthere.floor(child);
					 if(s.cost + acost < oldchild.cost)
					 {
						 oldchild.cost = s.cost + acost;
						 oldchild.parent = s;
					 }
				 }
				 else
				 {
					 child.cost = s.cost + acost;
					 child.parent = s;
					 frontier.add(child);
					 beenthere.add(child);
				 }
			 }
		 }
		 throw new RuntimeException("There is no path to the goal");
	 }


	public MS UCS(MS startState, MS goalState, Model m) {

		CostComparator costComparator = new CostComparator();
		StateComparator stateComparator = new StateComparator();

		frontier = new TreeSet<>(costComparator); //FIFO counter
		TreeSet<MS> beenthere= new TreeSet<>(stateComparator);
		startState.cost = 0.0f;
		startState.parent = null;

		//close set
		beenthere.add(startState);

		//open set
		frontier.add(startState);

		beenthere.add(startState);
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
				ArrayList<MS> children = generateChildren(s,goalState,m);

			for (MS child : children) {
				
				float acost = 1/m.getTravelSpeed(child.x,child.y);
				if(beenthere.contains(child))
				{
					oldchild = beenthere.floor(child);
					if(s.cost + acost < oldchild.cost)
					{
						oldchild.cost = s.cost + acost;
						oldchild.parent = s;
					}
				}
				else
				{
					child.cost = s.cost + acost;
					child.parent = s;
					frontier.add(child);
					beenthere.add(child);
				}
			}
		}
		throw new RuntimeException("There is no path to the goal");
	}

	ArrayList<MS> generateChildren(MS currentState, MS goalState, Model m)
	{

		ArrayList<MS> tempList = new ArrayList<>();

		int[][] A;
		A = new int[8][2];
		A[0][0] = 0;
		A[0][1] = 10;

		A[1][0] = 10;
		A[1][1] = 10;

		A[2][0] = 10;
		A[2][1] = 0;

		A[3][0] = 10;
		A[3][1] = -10;

		A[4][0] = 0;
		A[4][1] = -10;

		A[5][0] = -10;
		A[5][1] = -10;

		A[6][0] = -10;
		A[6][1] = 0;

		A[7][0] = -10;
		A[7][1] = 10;

		for (int i = 0; i < 8; i++) {
			MS temp = new MS(currentState);

				temp.x += A[i][0];
				temp.y += A[i][1];

				if(temp.x < Model.XMAX && temp.y < Model.YMAX  && temp.x > 0 && temp.y > 0)
				{
					tempList.add(temp);
				}
		}
		return tempList;
	}
}

class Agent {

	MyPlanner myplanner = new MyPlanner();
	LinkedList<MS> path = new LinkedList<>();
	MS goalState = new MS();
	boolean ucs;

	void drawPlan(Graphics g, Model m) {
		g.setColor(Color.red);
		for (int i = 0; i <path.size()-1 ; i++) {
			g.drawLine((int)path.get(i).x, (int) path.get(i).y, (int)path.get(i+1).x,(int)path.get(i+1).y);
		}

		g.setColor(Color.red);
		while(!myplanner.frontier.isEmpty())
		{
			MS temp;
			temp = myplanner.frontier.pollFirst();
			g.fillOval((int)temp.x,(int)temp.y,10,10);
		}
	}
	float x = 100;
	float y = 100;

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

			x = e.getX();
			y = e.getY();

			if (e.getButton() == MouseEvent.BUTTON1)
			{
				ucs = true;
			}
			else if( e.getButton() == MouseEvent.BUTTON3)
			{
				ucs = false;
			}
		}

		goalState = new MS(x,y);
//		System.out.println(	x + "," + y);
		myplanner = new MyPlanner();

		MS finalState;
		if(ucs)
		{
			//pass in comparator here
			finalState = myplanner.UCS(startState, goalState, m);

		}
		else
		{
			//pass in comparator
			finalState = myplanner.ASTAR(startState, goalState, m);
		}


		path = finalState.getPath(finalState);
		
		//set destination to next one in path
		if(path.size() == 1)
		{
			//if there is only the current one in the path stay still
			m.setDestination(m.getDestinationX(), m.getDestinationY());

		}
		else
		{
			//move to the next in path
			m.setDestination(path.get(1).x, path.get(1).y);
		}
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




