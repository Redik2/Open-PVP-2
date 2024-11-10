package Managers;

import Constants.BlockTypes;
import arc.Core;
import arc.Events;
import arc.util.Log;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.type.Liquid;
import mindustry.world.Build;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.modules.LiquidModule;

import java.util.*;

public class PayloadManager {
    static List<Building> storage_payloads = new ArrayList<Building>();
    static List<Building> liquid_payloads = new ArrayList<Building>();

    public static void init() {
//        UnitTypes.poly.constructor = UnitTypes.quad.constructor;
//        UnitTypes.poly.payloadCapacity = 64;
//        UnitTypes.obviate.constructor = UnitTypes.quell.constructor;
//        UnitTypes.obviate.payloadCapacity = 400;
//        UnitTypes.avert.constructor = UnitTypes.quell.constructor;
//        UnitTypes.avert.payloadCapacity = 128;
//        UnitTypes.quell.payloadCapacity = 1024;

        Events.run(EventType.Trigger.update, () -> {
            Groups.unit.each(unit -> {
                if (unit.type.payloadCapacity <= 8) return;
                PayloadUnit payloadUnit = (PayloadUnit) unit;
                var uitem = unit.item();
                storage_payloads.clear();
                liquid_payloads.clear();
                payloadUnit.payloads.each(payload -> {
                    if (payload.getClass() != BuildPayload.class) return;
                    Building build = ((BuildPayload) payload).build;
                    if (BlockTypes.storage_payloads.contains(build.block))
                    {
                        storage_payloads.add(build);
                    };
                    if (BlockTypes.liquid_payloads.contains(build.block))
                    {
                        liquid_payloads.add(build);
                    };
                });
                int load_count = ((PayloadUnit) unit).payloads.size - storage_payloads.size() - liquid_payloads.size();
                payloadUnit.payloads.each(payload -> {
                    if (payload.getClass() != BuildPayload.class) return;
                    Building build = ((BuildPayload) payload).build;


                    if (!liquid_payloads.contains(build) && build.block.liquidCapacity > 0)
                    {
                        for (Building liquid_storage : liquid_payloads)
                        {
                            liquid_storage.liquids.each((liquid, amount) -> {
                                if (!build.acceptLiquid(null, liquid)) return;
                                float transfer = Math.min(build.block.liquidCapacity - build.liquids.get(liquid), amount);
                                liquid_storage.transferLiquid(build, transfer, liquid);
                            });
                        }
                    }

                    if (!storage_payloads.contains(build))
                    {
                        int transfer = build.acceptStack(uitem, unit.stack().amount, null);
                        if (transfer > 0)
                        {
                            build.handleStack(uitem, transfer, null);

                            unit.stack.amount -= transfer;
                            return;
                        }
                    }

                    if (!storage_payloads.contains(build))
                    {
                        for (Building storage : storage_payloads) {
                            storage.items.each((item, amount) -> {
                                int transfer = build.acceptStack(item, amount, null);
                                if (transfer > 0) {
                                    build.handleStack(item, transfer, null);

                                    storage.removeStack(item, transfer);
                                }
                            });
                        }
                    }
                });
            });
        });
    }
}
