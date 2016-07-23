package sprunth.dosfootball;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

public class Runner {

    private static DosGraph graph;

    public static void main(String [] args) {

        graph = new DosGraph();

        SampleRun();
    }

    private static void SampleRun()
    {
        LoadTestPlayerGraph();

        Player start = graph.GetPlayer("p1");
        Player end = graph.GetPlayer("p5");

        ArrayList<Player> path = graph.FindDegreesOfSeparation(start, end);

        System.out.print("Degrees of Seperation: ");
        System.out.println(path.size() - 1);

        PrintPath(path);
    }

    private static void PrintPath(ArrayList<Player> path)
    {
        Player prevPlayer = null;

        for(Player p : path)
        {
            if (prevPlayer != null)
            {
                System.out.println("  " + prevPlayer.TeamLinks.get(p));
            }

            System.out.println(p.Name);
            prevPlayer = p;
        }
    }

    private static void LoadPlayerGraph()
    {
        throw new NotImplementedException();
    }

    private static void LoadTestPlayerGraph()
    {
        Player p1 = graph.AddPlayer("p1");
        Player p2 = graph.AddPlayer("p2");
        Player p3 = graph.AddPlayer("p3");
        Player p4 = graph.AddPlayer("p4");
        Player p5 = graph.AddPlayer("p5");

        p1.AddTeamLink("Team1", p2);
        p2.AddTeamLink("Team2", p3);
        p3.AddTeamLink("Team5", p4);

        p2.AddTeamLink("Team3", p4);
        p4.AddTeamLink("Team4", p5);
    }
}
