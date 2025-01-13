package dev.imabad.confectioneering.machines.dipper;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import dev.imabad.confectioneering.client.ConfectionPartialModels;
import net.minecraft.util.Mth;

public class DipperInstance extends ShaftInstance<DipperBlockEntity> implements DynamicInstance {

    protected final ModelData grate;

    private float lastProgress = Float.NaN;

    public DipperInstance(MaterialManager materialManager, DipperBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        grate = getTransformMaterial().getModel(ConfectionPartialModels.DIPPER_GRATE, blockEntity.getBlockState()).createInstance();

        pivotGrate();
    }

    @Override
    public void beginFrame() {
        float grateProgress = getGrateProgress();
        DipperBlockEntity.State state = getGrateState();

        if (Mth.equal(grateProgress, lastProgress)) return;

        pivotGrate(grateProgress, state);
        lastProgress = grateProgress;
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, grate);
    }

    @Override
    public void remove() {
        super.remove();
        grate.delete();
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(ConfectionPartialModels.DIPPER_AXLE);
    }

    private void pivotGrate() {
        pivotGrate(getGrateProgress(), getGrateState());
    }

    private float getGrateProgress() {
        return blockEntity.getGrateProgress(AnimationTickHolder.getPartialTicks());
    }
    private DipperBlockEntity.State getGrateState() {
        return blockEntity.getState();
    }

    private void pivotGrate(float grateProgress, DipperBlockEntity.State grateState) {
        float angle = 0;
        float yOffset = 0;
        if(grateState == DipperBlockEntity.State.FLIPPING || grateState == DipperBlockEntity.State.RESOLVING_FLIPPING) {
            angle = grateProgress * 70f;
        } else if (grateState == DipperBlockEntity.State.DIPPING || grateState == DipperBlockEntity.State.RESOLVING_DIPPING) {
            yOffset = grateProgress * blockEntity.getFluidLevel();
        }

        DipperRenderer.applyGrateAngle(blockEntity, angle, yOffset, grate.loadIdentity().translate(getInstancePosition()));
    }
}
