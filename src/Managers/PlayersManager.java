package Managers;

import Constants.OpenEvents;
import Constants.Utilities;
import Lang.LanguageManager;
import Structs.Cache;
import Structs.PlayerInfo;
import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import java.util.concurrent.TimeUnit;

public class PlayersManager {
    public static void ban_kick(Player player)
    {
        player.kick(String.format(LanguageManager.getLocalisedMessage(player, "kick_reason.ban"), Utilities.timestamp((long) DataManager.get_data(player.uuid(), "ban_time_end") - System.currentTimeMillis())), 10000);
    }

    public static void init()
    {
        Events.on(OpenEvents.NewTeamSpawn.class, event -> {
            Cache.getPlayer(event.player).team = event.team;
            Cache.getPlayer(event.player).last_respawned = System.currentTimeMillis();
            event.player.team(event.team);
        });
        Events.on(EventType.PlayerJoin.class, event -> {
            event.player.team(Cache.getPlayer(event.player).team);
        });
        Events.on(EventType.PlayerConnect.class, event -> {
            if ((long)DataManager.get_data(event.player.uuid(), "ban_time_end") > System.currentTimeMillis())
            {
                ban_kick(event.player);
            }
        });
        Events.run(EventType.Trigger.update, () -> {
            if (!GameManager.interval(60)) return;
            for (Team team : Team.all)
            {
                if (team == Team.derelict) continue;
                if (!team.active())
                {
                    team.data().players.forEach(player -> {
                        kill_player(player);
                        player.team(Team.all[0]);
                    });
                }
            }
        });
        Vars.netServer.admins.addChatFilter((player, text) -> {
            if ((long)DataManager.get_data(player.uuid(), "mute_time_end") > System.currentTimeMillis())
            {
                player.sendMessage(LanguageManager.getLocalisedMessage(player, "message_send.muted").formatted(Utilities.timestamp((long)DataManager.get_data(player.uuid(), "mute_time_end") - System.currentTimeMillis())));
                return null;
            }

            return text;
        });
    }

    public static void kill_player(Player player)
    {
        Cache.players_info.put(player.uuid(), new PlayerInfo());
        Cache.getPlayer(player.uuid()).last_respawned = System.currentTimeMillis();
        player.unit().kill();
    }
}
