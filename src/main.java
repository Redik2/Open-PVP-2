import Constants.BlockTypes;
import Constants.GameModeRules;
import Constants.Utilities;
import Lang.LanguageManager;
import Managers.*;
import Structs.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.world.Tile;

import java.util.concurrent.TimeUnit;

public class main extends Plugin
{
    @Override
    public void init(){
        Cache.set();
        CoresManager.init();
        CacheManager.init();
        PlayersManager.init();
        InfoManager.init();
        LanguageManager.init();
        RulesManager.init();
        GameManager.init();
        DataManager.init();
        PayloadManager.init();
        UnitCapManager.init();
        VoteManager.init();
        MenuManager.setupMenus();
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("host", "[map...]", "Open the server", args -> {
            if (Vars.state.isPlaying()) {
                Log.err("Already hosting. Type 'stop' to stop hosting first.");
                return;
            }

            var map = args.length > 0 ?
                    Vars.maps.all().find(other -> other.plainName().equalsIgnoreCase(Strings.stripColors(args[0]))) :
                    Vars.maps.getNextMap(Gamemode.pvp, Vars.state.map);

            if (args.length == 0) {
                Log.info("Randomized next map to be @.", map.plainName());
            } else if (map == null) {
                Log.err("No map with name '@' found.", args[0]);
                return;
            }

            try {
                Log.info("Loading map...");
                Vars.world.loadMap(map);
                Vars.world.tiles.forEach(tile -> {
                    if (BlockTypes.cores.contains(tile.block()))
                    {
                        tile.removeNet();
                    }
                });


                Vars.state.rules = map.applyRules(Gamemode.pvp);

                RulesManager.ruleSetter.get(Vars.state.rules);

                Vars.logic.play();
                Log.info("Map loaded.");

                Cache.reset();

                Vars.netServer.openServer();


            } catch (Exception exception) {
                Log.err("@: @", map.plainName(), exception.getLocalizedMessage());
            }
        });
    } // переопределяем команду хост

    @Override
    public void registerClientCommands(CommandHandler handler){
        handler.<Player>register("destroy", " ", "Взрывает любую вашу постройку под вами(Не возвращает ресурсы)", (args, player) -> {
            Tile tile = player.tileOn();
            if (tile.build != null && tile.build.team() == player.team()) {
                tile.build.kill();
                return;
            } else {
                return;
            }
        });

        handler.<Player>register("spectate", " ", "Делает вашу базу заброшенной, а вас наблюдателем", (args, player) -> {
            if (player.team() != Team.all[0]) {
                if (player.team().data().players.size <= 1) player.team().data().destroyToDerelict();;
                Cache.getPlayer(player).team = Team.derelict;
                player.team(Team.all[0]);
                if (player.unit().spawnedByCore()) Call.unitDespawn(player.unit());
            } else {
                player.sendMessage(LanguageManager.getLocalisedMessage(player, "spectator.err.already_spectator"));
            }
        });

        handler.<Player>register("setting", "[param_name] [new_value]", "Позволяет изменить персональные настройки на сервере. Для помощи с настройками не указывайте параметры команды.", (args, player) -> {
            if (args.length == 0) {player.sendMessage(LanguageManager.getLocalisedMessage(player, "setting")); return;}
            if (!DataManager.available_params.containsKey(args[0])) return;
            if (args.length == 1)
            {
                player.sendMessage(LanguageManager.getLocalisedMessage(player, "setting." + args[0]).formatted(DataManager.get_default_value(args[0]), DataManager.get_data(player.uuid(), args[0])));
                return;
            }
            Object value = null;
            if (args.length == 2)
            {
                switch (DataManager.available_params.get(args[0]))
                {
                    case digit -> {
                        try {
                            value = Long.parseLong(args[1]);
                        } catch (NumberFormatException e)
                        {
                            player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_int").formatted(args[1]));
                            return;
                        }
                    }
                    case text -> {
                        value = args[1];
                    }
                    case float_digit -> {
                        try {
                            value = Double.parseDouble(args[1]);
                        } catch (NumberFormatException e)
                        {
                            player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_float").formatted(args[1]));
                            return;
                        }
                    }
                }
                if (value == null) return;
                DataManager.edit_data(player.uuid(), args[0], value);
                DataManager.save_data();
                player.sendMessage(LanguageManager.getLocalisedMessage(player, "setting.changed").formatted(args[0]) + args[1]);
                return;
            }
        });

        handler.<Player>register("ban", "<name> [minutes] [hours] [days]", "(Только для админов) Отправляет игрока в бан", (args, player) -> {
            if (!player.admin()) {player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_admin")); return;}
            var ban_player = Utilities.find_by_name(args[0]);
            if (ban_player == null) {player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.low_name_acc")); return;}

            int minutes = 0, hours = 0, days = 0;
            if (args.length == 2)
            {
                try {
                    minutes = Integer.parseInt(args[1]);
                } catch (NumberFormatException e)
                {
                    player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_int").formatted(args[1]));
                    return;
                }
            }
            if (args.length == 3)
            {
                try {
                    hours = Integer.parseInt(args[2]);
                } catch (NumberFormatException e)
                {
                    player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_int").formatted(args[2]));
                    return;
                }
            }
            if (args.length == 4)
            {
                try {
                    days = Integer.parseInt(args[3]);
                } catch (NumberFormatException e)
                {
                    player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_int").formatted(args[3]));
                    return;
                }
            }
            long total_time = TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.HOURS.toMillis(hours) + TimeUnit.DAYS.toMillis(days);
            DataManager.edit_data(ban_player.uuid(), "ban_time_end", System.currentTimeMillis() + total_time);


            player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.ban.success").formatted(ban_player.coloredName(), Utilities.timestamp(total_time)));
            ban_player.kick(LanguageManager.getLocalisedMessage(player, "kick_reason.ban_by").formatted(player.coloredName(), Utilities.timestamp(total_time)), 10000);
        });

        handler.<Player>register("mute", "<name> [minutes] [hours] [days]", "(Только для админов) Отправляет игрока в мут", (args, player) -> {
            if (!player.admin()) {player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_admin")); return;}
            var ban_player = Utilities.find_by_name(args[0]);
            if (ban_player == null) {player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.low_name_acc")); return;}

            int minutes = 0, hours = 0, days = 0;
            if (args.length == 2)
            {
                try {
                    minutes = Integer.parseInt(args[1]);
                } catch (NumberFormatException e)
                {
                    player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_int").formatted(args[1]));
                    return;
                }
            }
            if (args.length == 3)
            {
                try {
                    hours = Integer.parseInt(args[2]);
                } catch (NumberFormatException e)
                {
                    player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_int").formatted(args[2]));
                    return;
                }
            }
            if (args.length == 4)
            {
                try {
                    days = Integer.parseInt(args[3]);
                } catch (NumberFormatException e)
                {
                    player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_int").formatted(args[3]));
                    return;
                }
            }
            long total_time = TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.HOURS.toMillis(hours) + TimeUnit.DAYS.toMillis(days);
            DataManager.edit_data(ban_player.uuid(), "mute_time_end", System.currentTimeMillis() + total_time);

            player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.mute.success").formatted(ban_player.coloredName(), Utilities.timestamp(total_time)));
            ban_player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.mute.muted").formatted(player.coloredName(), Utilities.timestamp(total_time)));
        });

        handler.<Player>register("team", "<team_id>", "(Только для админов) Меняет команду на выбранную", (args, player) -> {
            if (!player.admin()) {player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_admin")); return;}
            Team team_to_change;
            try {
                team_to_change = Team.all[Integer.parseInt(args[0])];
            } catch (NumberFormatException e)
            {
                player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.err.not_int").formatted(args[0]));
                return;
            }
            Cache.getPlayer(player).team = team_to_change;
            player.team(team_to_change);
            player.sendMessage(LanguageManager.getLocalisedMessage(player, "command.team.success").formatted(args[0]));
        });

        handler.<Player>register("yes", "", "Голосовать \"за\" в последнем опросе", (args, player) -> {
            if (VoteManager.now_on_vote == null) {player.sendMessage(LanguageManager.getLocalisedMessage(player, "voting.vote.err.no_voting")); return;}
            boolean has_voted_already = !VoteManager.now_on_vote.vote_yes(player);
            if (has_voted_already) player.sendMessage(LanguageManager.getLocalisedMessage(player, "voting.vote.err.has_voted_already"));
            else player.sendMessage(LanguageManager.getLocalisedMessage(player, "voting.vote.yes"));
        });

        handler.<Player>register("no", "", "Голосовать \"против\" в последнем опросе", (args, player) -> {
            if (VoteManager.now_on_vote == null) {player.sendMessage(LanguageManager.getLocalisedMessage(player, "voting.vote.err.no_voting")); return;}
            boolean has_voted_already = !VoteManager.now_on_vote.vote_no(player);
            if (has_voted_already) player.sendMessage(LanguageManager.getLocalisedMessage(player, "voting.vote.err.has_voted_already"));
            else player.sendMessage(LanguageManager.getLocalisedMessage(player, "voting.vote.no"));
        });

        handler.<Player>register("restart", "", "Начинать голосование за перезапуск", (args, player) -> {
            if (VoteManager.now_on_vote != null) {player.sendMessage(LanguageManager.getLocalisedMessage(player, "voting.vote.err.already_voting")); return;}
            VoteManager.now_on_vote = new Voting(30 * 1000, Voting.Type.restart);
            Groups.player.each(player1 -> player1.sendMessage(LanguageManager.getLocalisedMessage(player1, "voting.restart.started")));
            player.sendMessage(LanguageManager.getLocalisedMessage(player, "voting.vote.yes"));
            VoteManager.now_on_vote.vote_yes(player);
        });

        handler.<Player>register("rules", "", "Открыть правила сервера", (args, player) -> {
            MenuManager.callRulesMenu(player);
        });

        handler.<Player>register("discord", "", "Получить приглашение на дискорд сервер", (args, player) -> {
            Call.openURI(player.con(), GameModeRules.discord_link);
        });
    }
}
