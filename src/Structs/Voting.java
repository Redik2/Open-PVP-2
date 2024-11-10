package Structs;


import Constants.OpenEvents;
import arc.Events;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import java.util.ArrayList;
import java.util.List;

public class Voting {
    public enum Type {
        restart
    }
    public long time_starts, time_ends, duration;
    public int voted_yes, voted_no, max_votes;
    public Type type;
    public List<Player> alerady_voted;

    public Voting(long duration, Type type)
    {
        this.time_starts = System.currentTimeMillis();
        this.time_ends = time_starts + duration;
        this.duration = duration;
        this.voted_no = 0;
        this.voted_yes = 0;
        this.type = type;
        this.alerady_voted = new ArrayList<>();
        this.max_votes = Groups.player.size();
    }

    public int total_votes()
    {
        return this.voted_no + this.voted_yes;
    }

    public boolean ended()
    {
        return this.time_ends < System.currentTimeMillis();
    }

    public boolean vote_yes(Player player)
    {
        if (this.alerady_voted.contains(player)) return false;
        this.voted_yes++;
        this.alerady_voted.add(player);
        Events.fire(new OpenEvents.PlayerVoted(player,true));
        return true;
    }

    public long time_left()
    {
        return this.time_ends - System.currentTimeMillis();
    }

    public boolean vote_no(Player player)
    {
        if (this.alerady_voted.contains(player)) return false;
        this.voted_no++;
        this.alerady_voted.add(player);
        Events.fire(new OpenEvents.PlayerVoted(player,false));
        return true;
    }
}
