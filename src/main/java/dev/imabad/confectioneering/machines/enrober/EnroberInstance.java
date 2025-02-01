package dev.imabad.confectioneering.machines.enrober;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import dev.imabad.confectioneering.client.ConfectionPartialModels;
import net.minecraft.util.Mth;

public class EnroberInstance extends BlockEntityInstance<EnroberBlockEntity> implements DynamicInstance {


    private float lastProgress = Float.NaN;

    public EnroberInstance(MaterialManager materialManager, EnroberBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    public void beginFrame() {
    }

    @Override
    protected void remove() {

    }

    @Override
    public void updateLight() {
        super.updateLight();
    }
}
