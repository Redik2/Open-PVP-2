package Structs;

import Managers.RulesManager;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Cache
{
    public static Map<String, Structs.PlayerInfo> players_info;
    public static Map<Integer, Structs.TeamInfo> teams_info;
    public static long game_started;

    public static void set()
    {
        players_info = new HashMap<String, Structs.PlayerInfo>();
        teams_info = new HashMap<Integer, Structs.TeamInfo>();
        game_started = System.currentTimeMillis();
        RulesManager.fakeRules = Vars.state.rules.copy();
    }

    public static void reset()
    {
        set();

        Groups.player.forEach(player -> {
            players_info.put(player.uuid(), new PlayerInfo());
        });

        for (Team team : Team.all)
        {
            teams_info.put(team.id, new TeamInfo());
        }
    }
    public static long time_since_start()
    {
        return System.currentTimeMillis() - game_started;
    }

    public static PlayerInfo getPlayer(String uuid)
    {
        return players_info.get(uuid);
    }
    public static PlayerInfo getPlayer(Player player)
    {
        return players_info.get(player.uuid());
    }
    public static TeamInfo getTeam(int team_id)
    {
        return teams_info.get(team_id);
    }
}
