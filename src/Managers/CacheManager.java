package Managers;

import Structs.*;
import arc.Events;
import mindustry.game.EventType;
import mindustry.game.Team;

public class CacheManager {
    public static void init()
    {
        Events.on(EventType.PlayerConnect.class, event -> {
            if (!Cache.players_info.containsKey(event.player.uuid()))
            {
                Cache.players_info.put(event.player.uuid(), new PlayerInfo());
            }
        });
        Events.on(EventType.PlayerLeave.class, event -> {
            if (Cache.players_info.get(event.player.uuid()).team == Team.all[0])
            {
                Cache.players_info.put(event.player.uuid(), new PlayerInfo());
            }
        });
    }
}
