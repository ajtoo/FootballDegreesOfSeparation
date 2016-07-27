package sprunth.dosfootball;


import spark.HaltException;
import spark.QueryParamsMap;
import spark.Spark;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Set;

public class Runner {

    private static DosGraph graph;

    public static void main(String [] args) {

        graph = new DosGraph();

        SampleRun();

        SetupWebEndPoints();
    }

    private static void SetupWebEndPoints() {
        Spark.get("/AllPlayers", (req, resp) -> {
            return graph.PlayerNames;
        });

        Spark.get("/DegreeOfSeparation", (req, resp) -> {
            Set<String> queryParams = req.queryParams();
            QueryParamsMap queryValues = req.queryMap();
            boolean hasP1 = queryParams.contains("p1");
            boolean hasP2 = queryParams.contains("p2");
            if (!hasP1 || !hasP2){
                // invalid request
                Spark.halt(400, "Invalid Request");
            }

            String p1Name = queryValues.value("p1");
            String p2Name = queryValues.value("p2");
            p1Name = p1Name.substring(1, p1Name.length()-1);
            p2Name = p2Name.substring(1, p2Name.length()-1);

            Player p1 = graph.GetPlayer(p1Name);
            Player p2 = graph.GetPlayer(p2Name);

            if (p1 == null || p2 == null) {
                // name not found
                Spark.halt(422, "Player Not Found");
            }

            ArrayList<Player> path = graph.FindDegreesOfSeparation(p1, p2);

            String pathString = GetPathString(path);

            return pathString;
        });
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

    private static String GetPathString(ArrayList<Player> path) {
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<path.size()-1; i++){
            Player currentPlayer = path.get(i);
            Player nextPlayer = path.get(i+1);

            sb.append(currentPlayer.Name);
            sb.append(" \u2192 ");
            sb.append(currentPlayer.TeamLinks.get(nextPlayer));
            sb.append(" \u2192 ");
        }
        sb.append(path.get(path.size()-1).Name);
        return sb.toString();
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
