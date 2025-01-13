package dev.imabad.confectioneering.client;

import com.jozufozu.flywheel.core.PartialModel;
import dev.imabad.confectioneering.Confectioneering;

public class ConfectionPartialModels {

    public static final PartialModel DIPPER_GRATE = block("dipper/grate");
    public static final PartialModel DIPPER_AXLE = block("dipper/axle");

    public static final PartialModel LARGE_CARDBOARD_BOX_LID_SIDE = block("large_cardboard_box/lid");

    private static PartialModel block(String path) {
        return new PartialModel(Confectioneering.location("block/" + path));
    }


    public static void init() {}
}
