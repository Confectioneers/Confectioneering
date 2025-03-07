package dev.imabad.confectioneering.client;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.imabad.confectioneering.Confectioneering;

public class ConfectionPartialModels {

    public static final PartialModel DIPPER_GRATE = block("dipper/grate");
    public static final PartialModel DIPPER_AXLE = block("dipper/axle");
    public static final PartialModel ENROBER_AXLE = block("enrober/axle");

    public static final PartialModel LARGE_CARDBOARD_BOX_LID_SIDE = block("large_cardboard_box/lid");

    private static PartialModel block(String path) {
        return PartialModel.of(Confectioneering.location("block/" + path));
    }


    public static void init() {}
}
