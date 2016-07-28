package sprunth.dosfootball;

import java.util.*;

public class DosGraph {

    public Map<String, Player> PlayerIndex;
    public ArrayList<String> PlayerNamesWithQuotes;

    public DosGraph() {
        PlayerIndex = new HashMap<>();
        PlayerNamesWithQuotes = new ArrayList<>();
    }

    public Player AddPlayer(String playerName)
    {
        Player p = new Player(playerName);
        PlayerIndex.put(playerName, p);
        PlayerNamesWithQuotes.add("\"" + playerName + "\"");
        return p;
    }

    public Player GetPlayer(String playerName)
    {
        return PlayerIndex.get(playerName);
    }

    public ArrayList<Player> FindDegreesOfSeparation(Player p1, Player p2)
    {
        ArrayList<Player> path = BidirectionalBFS(p1, p2);

        return path;
    }

//    private ArrayList<Player> BFSSearch(Player p1, Player p2)
//    {
//        // returns empty list if no possible path
//
//        Queue<ArrayList<Player>> queue = new LinkedList<>();
//        ArrayList<Player> initialPath = new ArrayList<>();
//        initialPath.add(p1);
//        queue.add(initialPath);
//        while (!queue.isEmpty()) {
//            // grab the next path
//            ArrayList<Player> path = queue.remove();
//
//            Player p = path.get(path.size() - 1);
//            p.Visited = true;
//
//            if (p.equals(p2))
//                return path;
//
//            for (Player adjacent : p.TeamLinks.keySet())
//            {
//                if (adjacent.Visited)
//                    continue;
//
//                ArrayList<Player> newPath = (ArrayList)path.clone();
//                newPath.add(adjacent);
//                queue.add(newPath);
//            }
//        }
//
//        return new ArrayList<>();
//    }

    private ArrayList<Player> BidirectionalBFS(Player p1, Player p2)
    {
        Queue<ArrayList<Player>> queue1 = new LinkedList<>();
        Queue<ArrayList<Player>> queue2 = new LinkedList<>();
        ArrayList<Player> initialPath1 = new ArrayList<>();
        ArrayList<Player> initialPath2 = new ArrayList<>();
        Set<Player> visited1 = new HashSet<>();
        Set<Player> visited2 = new HashSet<>();
        Map<Player, ArrayList<Player>> pathToPlayer1 = new HashMap<>();
        Map<Player, ArrayList<Player>> pathToPlayer2 = new HashMap<>();

        initialPath1.add(p1);
        initialPath2.add(p2);
        queue1.add(initialPath1);
        queue2.add(initialPath2);

        Player intersectingPlayer;
        while (true)
        {
            ArrayList<Player> path1 = queue1.remove();
            ArrayList<Player> path2 = queue2.remove();

            Player player1 = path1.get(path1.size()-1);
            Player player2 = path2.get(path2.size()-1);

            pathToPlayer1.put(player1, path1);
            pathToPlayer2.put(player2, path2);

            visited1.add(player1);
            visited2.add(player2);

            if (visited1.contains(player2))
            {    intersectingPlayer = player2;
                break;
            }
            if (visited2.contains(player1)) {
                intersectingPlayer = player1;
                break;
            }

            for (Player adjacent : player1.TeamLinks.keySet())
            {
                if (visited1.contains(adjacent))
                    break;

                ArrayList<Player> newPath = (ArrayList)path1.clone();
                newPath.add(adjacent);
                queue1.add(newPath);
            }

            for (Player adjacent : player2.TeamLinks.keySet())
            {
                if (visited2.contains(adjacent))
                    break;

                ArrayList<Player> newPath = (ArrayList)path2.clone();
                newPath.add(adjacent);
                queue2.add(newPath);
            }

            if (path1.size() > 6)
                return new ArrayList<>();
        }

        ArrayList<Player> path1 = pathToPlayer1.get(intersectingPlayer);
        ArrayList<Player> path2 = pathToPlayer2.get(intersectingPlayer);

        //since path2 is in reverse, flip it
        Collections.reverse(path2);
        // since both contain the intersecting player, remove from one of the lists
        path1.remove(path1.size()-1);

        path1.addAll(path2);
        return path1;
    }
}
