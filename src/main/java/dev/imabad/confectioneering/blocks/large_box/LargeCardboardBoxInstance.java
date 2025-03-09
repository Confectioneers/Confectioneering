package dev.imabad.confectioneering.blocks.large_box;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import dev.imabad.confectioneering.client.ConfectionPartialModels;
import net.minecraft.util.Mth;

public class LargeCardboardBoxInstance extends BlockEntityInstance<LargeCardboardBoxBlockEntity> implements DynamicInstance {

    protected final ModelData[] lidSides = new ModelData[4];

    private float lastProgress;

    public LargeCardboardBoxInstance(MaterialManager materialManager, LargeCardboardBoxBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        getTransformMaterial().getModel(ConfectionPartialModels.LARGE_CARDBOARD_BOX_LID_SIDE).createInstances(lidSides);
        for (int i = 0; i < lidSides.length; i++) {
            pivotLid(i, getLidProgress());
        }
    }

    @Override
    public void beginFrame() {
        float lidProgress = getLidProgress();

        if (Mth.equal(lidProgress, lastProgress)) return;

        for (int i = 0; i < lidSides.length; i++) {
            pivotLidMapped(i, lidProgress);
        }

        lastProgress = lidProgress;
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, lidSides);
    }

    @Override
    protected void remove() {
        for (ModelData lidSide : lidSides) {
            lidSide.delete();
        }
    }

    private void pivotLidMapped(int index, float lidProgress) {
        pivotLid(index, lidProgress);
    }

    private float getLidProgress() {
        return blockEntity.getLidProgress(AnimationTickHolder.getPartialTicks());
    }

    private void pivotLid(int index, float lidProgress) {
        float local = (lidProgress - (index * 0.25f)) / (1.0F - (index * 0.25f));

        // Clamp it between 0..1
        if (local < 0.0F) local = 0.0F;
        if (local > 1.0F) local = 1.0F;

        float angle = local * (225f );

        LargeCardboardBoxRenderer.applyLidAngle(blockEntity, index, angle, local, lidSides[index].loadIdentity().translate(getInstancePosition()));
        if(local >= 0.8f && (index == 0 || index == 2)){
            lidSides[index].setColor((byte)0, (byte)0, (byte)0, (byte) 0);
        } else {
            lidSides[index].setColor((byte)255, (byte)255, (byte)255, (byte) 255);
        }
    }

}
