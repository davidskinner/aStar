import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;


class MyState
{
	float x = 200;
	float y = 200;
	public float cost;
	MyState parent;

	MyState()
	{}

	MyState(MyState copyState)
	{
		this.x = copyState.x;
		this.y = copyState.y;
		this.cost = copyState.cost;
		MyState temp;
		temp = copyState;
		this.parent = temp;
	}

	MyState(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	MyState(float cost, MyState par)
	{
		MyState temp;
		this.cost = cost;
		temp = par;
		this.parent = temp;
	}

	LinkedList<MyState> getPath(MyState finalState)
	{
		LinkedList<MyState> temp = new LinkedList<>();
		temp.add(finalState);
		while(finalState.parent != null)
		{
			finalState= finalState.parent;
			temp.add(finalState);
		}

		Iterator x = temp.descendingIterator();

		LinkedList<MyState> temps = new LinkedList<>();

		while(x.hasNext())
		{
			MyState element = (MyState)x.next();
			temps.add(element);
		}

		return temps;

	}
}

class CostComparator implements Comparator<MyState> {

	public int compare(MyState a, MyState b)
	{
			if(a.cost < b.cost)
				return -1;
			else if(a.cost > b.cost )
				return 1;
		return 0;
	}
}


class AstarComparator implements Comparator<MyState> {

	Model model;
	MyState goalState;
	float lc;

	AstarComparator(Model m, MyState g)
	{
		model = m;
		goalState = g;
	}

	public static float eDistance(MyState a, MyState goal)
	{
		return (float)Math.sqrt((a.x - goal.x) * (a.x - goal.x) + (a.y - goal.y) * (a.y - goal.y));
	}

	public int compare(MyState a, MyState b)
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
StateComparator implements Comparator<MyState> {

	public int compare(MyState a, MyState b)
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

	TreeSet<MyState> frontier;

	 public MyState ASTAR(MyState startState, MyState goalState, Model m) {

		 StateComparator stateComparator = new StateComparator();

		 AstarComparator astarComparator = new AstarComparator(m, goalState);

		 frontier = new TreeSet<>(astarComparator);
		 TreeSet<MyState> beenthere= new TreeSet<>(stateComparator);
		 startState.cost = 0.0f;
		 startState.parent = null;

		 //close set
		 beenthere.add(startState);

		 //open set
		 frontier.add(startState);

		 beenthere.add(startState);
		 frontier.add(startState);

		 MyState s = new MyState();
		 MyState oldchild;

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

			 ArrayList<MyState> children = generateChildren(s,goalState,m);

			 for (MyState child : children) {

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


	ArrayList<MyState> generateChildren(MyState currentState, MyState goalState, Model m)
	{

		ArrayList<MyState> tempList = new ArrayList<>();

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
			MyState temp = new MyState(currentState);

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
	LinkedList<MyState> path = new LinkedList<>();
	MyState goalState = new MyState();
	boolean ucs;

	void drawPlan(Graphics g, Model m) {
		g.setColor(Color.red);
		for (int i = 0; i <path.size()-1 ; i++) {
			g.drawLine((int)path.get(i).x, (int) path.get(i).y, (int)path.get(i+1).x,(int)path.get(i+1).y);
		}

		g.setColor(Color.red);
		while(!myplanner.frontier.isEmpty())
		{
			MyState temp;
			temp = myplanner.frontier.pollFirst();
			g.fillOval((int)temp.x,(int)temp.y,10,10);
		}
	}
	float x = 100;
	float y = 100;

	void update(Model m)
	{
		Controller c = m.getController();
		MyState startState = new MyState(0.0f, null);
//		startState.x = m.getX();
//		startState.y = m.getY();

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

		goalState = new MyState(x,y);
		System.out.println(	x + "," + y);
		myplanner = new MyPlanner();

		MyState finalState;

			//pass in comparator
			finalState = myplanner.ASTAR(startState, goalState, m);



		path = finalState.getPath(finalState);

		//set destination to next one in path
		if(path.size() == 1)
		{
			//if there is only the current one in the path stay still
//			m.setDestination(m.getDestinationX(), m.getDestinationY());

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

	public static int GetIntFromString(String s)
	{
		return Integer.valueOf(s);
	}

	public static void main(String[] args) throws Exception
	{
		Controller.playGame(GetIntFromString(args[0]),GetIntFromString(args[1]),GetIntFromString(args[2]),GetIntFromString(args[3]));
	}
}




