package dev.imabad.confectioneering.config;

import net.createmod.catnip.config.ConfigBase;

public class ConfectionCommon  extends ConfigBase {

    public final ConfectionKinetics kinetics = nested(0, ConfectionKinetics::new,Comments.kinetics);


    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        static String kinetics = "Modify Confectioneering blocks components";

    }
}