package sprunth.dosfootball;

import java.util.*;

public class DosGraph {

    public Map<String, Player> PlayerIndex;
    public ArrayList<String> PlayerNames;

    public DosGraph() {
        PlayerIndex = new HashMap<>();
        PlayerNames = new ArrayList<>();
    }

    public Player AddPlayer(String playerName)
    {
        Player p = new Player(playerName);
        PlayerIndex.put(playerName, p);
        PlayerNames.add(playerName);
        return p;
    }

    public Player GetPlayer(String playerName)
    {
        return PlayerIndex.get(playerName);
    }

    public ArrayList<Player> FindDegreesOfSeparation(Player p1, Player p2)
    {
        ArrayList<Player> path = BFSSearch(p1, p2);

        // clear the path after every search
        PlayerIndex.forEach((s, player) -> player.Visited = false);

        return path;
    }

    private ArrayList<Player> BFSSearch(Player p1, Player p2)
    {
        // returns empty list if no possible path

        Queue<ArrayList<Player>> queue = new LinkedList<>();
        ArrayList<Player> initialPath = new ArrayList<Player>();
        initialPath.add(p1);
        queue.add(initialPath);
        while (!queue.isEmpty()) {
            // grab the next path
            ArrayList<Player> path = queue.remove();

            Player p = path.get(path.size() - 1);
            p.Visited = true;

            if (p.equals(p2))
                return path;

            for (Player adjacent : p.TeamLinks.keySet())
            {
                if (adjacent.Visited)
                    continue;

                ArrayList<Player> newPath = (ArrayList)path.clone();
                newPath.add(adjacent);
                queue.add(newPath);
            }
        }

        return new ArrayList<>();
    }
}
