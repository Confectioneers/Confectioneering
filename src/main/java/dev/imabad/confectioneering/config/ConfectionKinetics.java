package dev.imabad.confectioneering.config;

import net.createmod.catnip.config.ConfigBase;

public class ConfectionKinetics extends ConfigBase {
    public final ConfectionStress stressValues = nested(1, ConfectionStress::new, Comments.stress);
    @Override
    public String getName() {
        return "kinetics";
    }
    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";
    }
}
