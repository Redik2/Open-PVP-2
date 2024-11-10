package Managers;

import Constants.BlockTypes;
import Constants.GameModeRules;
import Constants.OpenEvents;
import Lang.LanguageManager;
import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;
import mindustry.world.Build;
import mindustry.world.Tile;
import Structs.*;

public class CoresManager {
    private static Team takeNewTeam ()
    {
        for (Team team : Team.all)
        {
            if (!team.active() && team.id > 5)
            {
                return team;
            }
        }
        return Team.all[0];
    }
    public static Boolean valid_test(Tile tile, Team team) {
        int r = 128;
        for (int x_add = -r; x_add <= r; x_add+=3)
        {
            for (int y_add = -r; y_add <= r; y_add+=3)
            {
                if (Math.sqrt((x_add * x_add) + (y_add * y_add)) < r)
                {
                    Tile test_tile = Vars.world.tile(tile.x + x_add, tile.y + y_add);
                    if ((test_tile != null) && (Constants.BlockTypes.cores.contains(test_tile.block())) && (test_tile.build.team() != team))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void init()
    {
        Events.on(EventType.TapEvent.class, event -> {
            Player player = event.player;
            Tile tile = event.tile;

            if (Cache.getPlayer(player).team != Team.all[0]) {
                return;
            }
            if (System.currentTimeMillis() - Cache.getPlayer(player).last_respawned < 30000)
            {
                Events.fire(new OpenEvents.Notification(player, tile, String.format(LanguageManager.getLocalisedMessage(player, "place_core.error.too_fast"), 30 - (System.currentTimeMillis() - Cache.getPlayer(player).last_respawned) / 1000), 1.5f));
                return;
            }
            if (!CoresManager.valid_test(tile, Team.all[0])) {
                Events.fire(new OpenEvents.Notification(player, tile, LanguageManager.getLocalisedMessage(player, "place_core.error.close_enemy"), 1.5f));
                return;
            }
            if (BlockTypes.nonvalid_floors.contains(tile.floor()) || !Build.validPlace(Blocks.vault, Team.all[0], tile.x, tile.y, 0)) {
                Events.fire(new OpenEvents.Notification(player, tile, LanguageManager.getLocalisedMessage(player, "place_core.error.invalid_place"), 1.5f));
                return;
            }

            Team new_team = takeNewTeam();
            Events.fire(new OpenEvents.NewTeamSpawn(player, tile, new_team));
        });
        Events.on(OpenEvents.NewTeamSpawn.class, event -> {
            event.tile.setNet(Blocks.coreShard, event.team, 0);
        });
        Vars.netServer.admins.addActionFilter(action ->
        {
            if (action.type != Administration.ActionType.placeBlock) return true;
            if (action.block != Blocks.vault) return true;

            if (!valid_test(action.tile, action.player.team())) {
                Events.fire(new OpenEvents.Notification(action.player, action.tile, LanguageManager.getLocalisedMessage(action.player, "place_core.error.close_enemy"), 1.5f));
                action.tile.setNet(Blocks.air);
                return false;
            }

            int coreCount = action.player.team().cores().count(coreBuild -> {return coreBuild.block == Blocks.coreShard;});

            if (coreCount >= GameModeRules.coreShardLimit)
            {
                Events.fire(new OpenEvents.Notification(action.player, action.tile, String.format(LanguageManager.getLocalisedMessage(action.player, "place_core.shard.error.count"), coreCount, GameModeRules.coreShardLimit), 1.5f));
                return false;
            }
            return true;
        });
        Vars.netServer.admins.addActionFilter(action ->
        {
            if (action.type != Administration.ActionType.placeBlock) return true;
            if (action.block != Blocks.coreNucleus) return true;
            int coreCount = action.player.team().cores().count(coreBuild -> {return coreBuild.block == Blocks.coreNucleus;});
            Events.fire(new OpenEvents.Notification(action.player, action.tile, String.format(LanguageManager.getLocalisedMessage(action.player, "place_core.nucleus.info.count"), coreCount + 1), 1.5f));
            return true;

        });

        Vars.netServer.admins.addActionFilter(action ->
        {
            if (action.type != Administration.ActionType.placeBlock) return true;
            if (action.block != Blocks.coreFoundation) return true;

            int coreCount = action.player.team().cores().count(coreBuild -> {return coreBuild.block == Blocks.coreFoundation;});

            if (coreCount >= GameModeRules.coreFoundationLimit)
            {
                Events.fire(new OpenEvents.Notification(action.player, action.tile, String.format(LanguageManager.getLocalisedMessage(action.player, "place_core.foundation.error.count"), coreCount, GameModeRules.coreFoundationLimit), 1.5f));
                return false;
            }
            Events.fire(new OpenEvents.Notification(action.player, action.tile, String.format(LanguageManager.getLocalisedMessage(action.player, "place_core.foundation.info.count"), coreCount + 1, GameModeRules.coreFoundationLimit), 1.5f));
            return true;
        });
        Events.on(EventType.BlockBuildEndEvent.class, event -> {
            if (!event.breaking && event.tile.block() == Blocks.vault)
            {
                Core.app.post(() -> {
                    event.tile.setNet(Blocks.coreShard, event.tile.build.team(), 0);
                    InfoManager.sync_unitCap(event.team);
                });
                int coreCount = event.tile.build.team().cores().count(coreBuild -> {return coreBuild.block == Blocks.coreFoundation;});

                Groups.player.forEach(player -> {
                    if (player.team() == event.tile.build.team())
                    {
                        Events.fire(new OpenEvents.Notification(player, event.tile, String.format(LanguageManager.getLocalisedMessage(player, "place_core.shard.info.count"), coreCount + 1, GameModeRules.coreShardLimit), 1.5f));
                    }
                });
            }
        });
    }
}
