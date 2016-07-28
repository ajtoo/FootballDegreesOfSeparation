package sprunth.dosfootball;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.HaltException;
import spark.QueryParamsMap;
import spark.Spark;
import sun.awt.image.ImageWatched;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public class Runner {

    private static DosGraph graph;

    public static void main(String [] args) {

        graph = new DosGraph();

        // SampleRun();
        LoadPlayerGraph();

        SetupWebEndPoints();
    }

    private static void SetupWebEndPoints() {
        CorsFilter.apply();

        Spark.get("/AllPlayers", (req, resp) -> {
            return graph.PlayerNamesWithQuotes;
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

            if (path.size() > 0)
                return GetPathString(path);

            return "No Path Found";
        });
    }

    private static void SampleRun()
    {
        LoadPlayerGraph();

        Player start = graph.GetPlayer("Maicon");
        Player link = graph.GetPlayer("Ashley Cole");
        Player end = graph.GetPlayer("Brian Rowe");

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
            if (i == 0)
                sb.append(" was at ");
            else
                sb.append(" who was at ");
            sb.append(nextPlayer.TeamLinks.get(currentPlayer));
            sb.append(" with ");
        }
        sb.append(path.get(path.size()-1).Name);
        sb.append(" who was also at ");
        sb.append(path.get(path.size()-2).TeamLinks.get(path.get(path.size()-1)));

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
        JSONParser parser = new JSONParser();

        try
        {
            //open file and parse into json object
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader("footballsquads.json"));

            //iterate over team entries
            Iterator it = jsonObj.entrySet().iterator();
            while(it.hasNext())
            {
                Map.Entry pair = (Map.Entry) it.next();
                String teamName = (String) pair.getKey();
                JSONArray playerArray = (JSONArray) pair.getValue();

                AddPlayersFromTeam(playerArray);
                LinkTeammates(playerArray, teamName);
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("ERROR: Data file not found");
            e.printStackTrace();
        }
        catch(IOException e)
        {
            System.out.println("ERROR: Failed to read data file");
            e.printStackTrace();
        }
        catch(ParseException e)
        {
            System.out.println("ERROR: Failed to parse data file");
            e.printStackTrace();
        }
    }

    private static void AddPlayersFromTeam(JSONArray playerArray)
    {
        //iterate over players in a given team and add them
        Iterator playerIterator = playerArray.iterator();
        while(playerIterator.hasNext())
        {
            String playerName = (String) playerIterator.next();
            Player curPlayer;
            if(graph.GetPlayer(playerName) == null)
            {
                curPlayer = graph.AddPlayer(playerName);
            }
            else
            {
                curPlayer = graph.GetPlayer(playerName);
            }
        }
    }

    private static void LinkTeammates(JSONArray playerArray, String teamName)
    {
        //iterate over teammates to add links

        Iterator playerIterator = playerArray.iterator();
        Player curPlayer;
        while(playerIterator.hasNext())
        {
            String playerName = (String) playerIterator.next();
            curPlayer = graph.GetPlayer(playerName);
            Iterator otherPlayerIterator = playerArray.iterator();
            while(otherPlayerIterator.hasNext())
            {
                String linkPlayerName = (String) otherPlayerIterator.next();
                if(linkPlayerName.equals(playerName))   //don't link player to himself
                {
                    continue;
                }

                Player linkPlayer = graph.GetPlayer(linkPlayerName);        //player should always be in the graph because AddPlayersFromTeam goes first
                curPlayer.AddTeamLink(teamName, linkPlayer);
            }
        }

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
