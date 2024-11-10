package Managers;

import arc.Core;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.net.Administration;
import mindustry.type.UnitType;
import mindustry.world.blocks.storage.CoreBlock;

import java.lang.reflect.Field;
import java.util.Arrays;

public class UnitCapManager {
    public static void init()
    {
        Events.run(EventType.Trigger.update, () -> {
            for (Team team : Team.all)
            {
                boolean limit = team.data().unitCount >= getUnitCap(team);
                team.data().unitCap = limit ? -Vars.state.rules.unitCap : Vars.state.rules.unitCap;
            }
        });
    }

    public static int getUnitCap(Team team)
    {
        int unitCap = 0;
        for (CoreBlock.CoreBuild core : team.cores())
        {
            unitCap += core.block.unitCapModifier;
        }
        return unitCap;
    }
}
