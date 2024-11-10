package Managers;

import Constants.OpenEvents;
import Lang.LanguageManager;
import Structs.Voting;
import arc.Events;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;

public class VoteManager {
    public static Voting now_on_vote;

    static public void init() {
        Events.on(OpenEvents.PlayerVoted.class, event -> {
            switch (now_on_vote.type) // on somebody voted
            {
                case restart -> {
                    if (now_on_vote.voted_no > Groups.player.size() / 2)
                    {
                        Groups.player.each(player -> {
                            player.sendMessage(String.format(LanguageManager.getLocalisedMessage(player, "voting.restart.half_no")));
                        });
                        now_on_vote = null;

                    } else if (now_on_vote.voted_yes > Groups.player.size() / 2) {
                        now_on_vote = null;
                        Groups.player.each(player -> {
                            player.sendMessage(String.format(LanguageManager.getLocalisedMessage(player, "voting.restart.half_yes")));
                        });
                        GameManager.gameOver();
                    }
                }
            }
        });

        Events.run(EventType.Trigger.update, () -> {
            if (now_on_vote == null) return;
            if (!now_on_vote.ended()) return;
            switch (now_on_vote.type) // on voting ends
            {
                case restart -> {
                    if (now_on_vote.voted_no >= now_on_vote.voted_yes)
                    {
                        Groups.player.each(player -> {
                            player.sendMessage(String.format(LanguageManager.getLocalisedMessage(player, "voting.restart.more_no")));
                        });
                        now_on_vote = null;

                    } else {
                        now_on_vote = null;
                        Groups.player.each(player -> {
                            player.sendMessage(String.format(LanguageManager.getLocalisedMessage(player, "voting.restart.more_yes")));
                        });
                        GameManager.gameOver();
                    }
                }
            }
        });
    }
}
