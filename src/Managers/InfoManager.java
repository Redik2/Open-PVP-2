package Managers;

import Constants.BlockTypes;
import Constants.GameModeRules;
import Constants.OpenEvents;
import Constants.Utilities;
import Lang.LanguageManager;
import Structs.Cache;
import Structs.TeamInfo;
import arc.Core;
import arc.Events;
import arc.util.Align;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.net.Administration;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InfoManager {
    static Map<String, Integer> align_namings = new HashMap<>() {{
        put("top", Align.top);
        put("top_left", Align.topLeft);
        put("top_right", Align.topRight);
        put("center", Align.center);
        put("left", Align.left);
        put("right", Align.right);
        put("bottom", Align.bottom);
        put("bottom_left", Align.bottomLeft);
        put("bottom_right", Align.bottomRight);
    }};
    public static void init()
    {
        Administration.Config.serverName.set("[#ffd37f]Open PvP II   [gray]Beta");
        Events.on(OpenEvents.Notification.class, event ->
        {
            Call.label(event.player.con(), event.text, event.duration, event.tile.x * 8, event.tile.y * 8);
        });

        Events.on(EventType.PlayerJoin.class, event -> {
            if (System.currentTimeMillis() - (long)DataManager.get_data(event.player.uuid(), "last_time_welcome_showed") > TimeUnit.HOURS.toMillis(18))
            {
                Log.info(DataManager.get_data(event.player.uuid(), "last_time_welcome_showed"));
                DataManager.edit_data(event.player.uuid(), "last_time_welcome_showed", System.currentTimeMillis());
                DataManager.save_data();
                MenuManager.callWelcomeMenu(event.player);
            }
        });

        Events.run(EventType.Trigger.update, () ->
        {
            if (GameManager.interval(60))
            {
                long millis = GameModeRules.game_last - Cache.time_since_start();
                String desc = "[white]Open beta test of Open PvP II\n[#bfbfbf]Restarts in [#ffd37f]" + Utilities.timestamp(millis);
                Administration.Config.desc.set(desc);
                Groups.player.forEach(player -> {
                    String text = "";
                    text += LanguageManager.getLocalisedMessage(player, "info.time_left").formatted(Utilities.timestamp(millis));

                    int unitCap = UnitCapManager.getUnitCap(player.team());
                    if (player.team() != Team.derelict) text += "\n" + LanguageManager.getLocalisedMessage(player, "info.unit_cap").formatted(Math.min(player.team().data().unitCount, unitCap), unitCap);
                    else text += "\n" + LanguageManager.getLocalisedMessage(player, "info.tap_to_spawn");
                    Call.infoPopup(player.con, text, 61f / Core.graphics.getFramesPerSecond(), align_namings.getOrDefault((String)DataManager.get_data(player.uuid(), "info_align"), Align.topLeft), Long.valueOf((long)DataManager.get_data(player.uuid(), "info_y")).intValue(), Long.valueOf((long)DataManager.get_data(player.uuid(), "info_x")).intValue(), 0, 0);

                    if (VoteManager.now_on_vote != null)
                    {
                        switch (VoteManager.now_on_vote.type)
                        {
                            case restart -> {
                                String voting_text = LanguageManager.getLocalisedMessage(player, "voting.restart").formatted(VoteManager.now_on_vote.voted_yes, VoteManager.now_on_vote.voted_no, TimeUnit.MILLISECONDS.toSeconds(VoteManager.now_on_vote.time_left()));;
                                Call.infoPopup(player.con, voting_text, 61f / Core.graphics.getFramesPerSecond(), align_namings.getOrDefault((String)DataManager.get_data(player.uuid(), "voting_align"), Align.topLeft), Long.valueOf((long)DataManager.get_data(player.uuid(), "voting_y")).intValue(), Long.valueOf((long)DataManager.get_data(player.uuid(), "voting_x")).intValue(), 0, 0);
                            }
                        }
                    }
                });
            }
        });

        Events.run(EventType.Trigger.update, () ->
        {
            Team team = Team.all[(int)(GameManager.tick() % 256)];
            if (!team.active()) return;
            sync_unitCap(team);
        });

        Events.on(EventType.BlockBuildEndEvent.class, event -> {
            if (event.tile.build == null) return;
            if (BlockTypes.cores.contains(event.tile.build.block()))
            {
                InfoManager.sync_unitCap(event.team);
            }
        });
    }

    public static void sync_unitCap(Team team)
    {
        TeamInfo teamInfo = Cache.getTeam(team.id);
        int vanillaUnitCap = 0;
        for (CoreBlock.CoreBuild core : team.cores())
        {
            if (core.block() == Blocks.coreShard) vanillaUnitCap += 8;
            else if (core.block() == Blocks.coreFoundation) vanillaUnitCap += 16;
            else if (core.block() == Blocks.coreNucleus) vanillaUnitCap += 24;
        }
        teamInfo.vanillaUnitCap = vanillaUnitCap;
        RulesManager.fakeRules.unitCap = UnitCapManager.getUnitCap(team) - Cache.getTeam(team.id).vanillaUnitCap;
        team.data().players.forEach(player ->
        {
            Call.setRules(player.con(), RulesManager.fakeRules);
        });
    }
}
