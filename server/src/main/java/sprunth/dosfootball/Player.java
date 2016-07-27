package sprunth.dosfootball;

import java.util.*;

public class Player {

    public boolean Visited;
    public String Name;

    public Map<Player, String> TeamLinks;

    public Player(String playerName) {
        Name = playerName;
        Visited = false;
        TeamLinks = new HashMap<>();
    }

    public void AddTeamLink(String team, Player p)
    {
        TeamLinks.put(p, team);
    }
}
