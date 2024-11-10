package Managers;

import Constants.BlockTypes;
import Constants.GameModeRules;
import Constants.OpenEvents;
import Lang.LanguageManager;
import Structs.Cache;
import arc.Core;
import arc.Events;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.io.SaveIO;
import mindustry.maps.Map;
import mindustry.net.WorldReloader;

import static mindustry.Vars.*;
import static mindustry.Vars.player;

public class GameManager {
    static int tick;
    public static void init()
    {
        tick = 0;
        Events.on(EventType.PlayEvent.class, event -> {
            RulesManager.ruleSetter.get(state.rules);
        });

        Events.on(OpenEvents.ReloadGame.class, event -> {
            reload();
        });

        Events.run(EventType.Trigger.update, () -> {
            tick++;
            if (interval(60))
            {
                if (Cache.time_since_start() > GameModeRules.game_last)
                {
                    Events.fire(new OpenEvents.ReloadGame());
                }
            }
        });
    }

    public static Boolean interval(int tick_interval)
    {
        return tick() % tick_interval == 0;
    }

    public static long tick() {return tick;}
    public static void reload() {
        LanguageManager.init();

        Map map = maps.getNextMap(Gamemode.pvp, Vars.state.map);

        var reloader = new WorldReloader();
        reloader.begin();

        logic.reset();

        Cache.reset();
        tick = 0;

        Vars.world.loadMap(map);

        logic.play();
        reloader.end();

        Vars.world.tiles.forEach(tile -> {
            if (BlockTypes.cores.contains(tile.block()))
            {
                tile.removeNet();
            }
        });
    }

    public static void gameOver()
    {
        logic.pause();
        Time.run(Core.graphics.getFramesPerSecond() * 10, GameManager::reload);
        Groups.player.each(player -> {Call.infoMessage(player.con(), LanguageManager.getLocalisedMessage(player, "gameover.message"));});
    }
}
