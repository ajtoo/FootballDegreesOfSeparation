package sprunth.dosfootball;

import java.util.*;

public class Player {

    public String Name;

    public Map<Player, String> TeamLinks;

    public Player(String playerName) {
        Name = playerName;
        TeamLinks = new HashMap<>();
    }

    public void AddTeamLink(String team, Player p)
    {
        TeamLinks.put(p, team);
    }
}
