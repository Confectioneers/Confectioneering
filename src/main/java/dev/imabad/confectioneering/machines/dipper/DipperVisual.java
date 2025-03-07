package dev.imabad.confectioneering.machines.dipper;

import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.PosedInstance;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.imabad.confectioneering.client.ConfectionPartialModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class DipperVisual extends SingleAxisRotatingVisual<DipperBlockEntity> implements SimpleDynamicVisual {

    protected final PosedInstance grate;

    private float lastProgress = Float.NaN;

    public DipperVisual(VisualizationContext context, DipperBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick,
                Models.partial(ConfectionPartialModels.DIPPER_AXLE));

        grate = this.instancerProvider().instancer(InstanceTypes.POSED, Models.partial(ConfectionPartialModels.DIPPER_GRATE)).createInstance();
        pivotGrate();
    }

    @Override
    public void updateLight(float partialTicks) {
        super.updateLight(partialTicks);
        relight(grate);
    }

    @Override
    protected void _delete() {
        super._delete();
        this.grate.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(grate);
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
        DipperRenderer.applyGrateAngle(blockEntity, angle, yOffset, grate.setIdentityTransform().translate(getVisualPosition()));
        grate.setChanged();
    }

    @Override
    public void beginFrame(Context context) {
        float grateProgress = getGrateProgress();
        DipperBlockEntity.State state = getGrateState();

        if (Mth.equal(grateProgress, lastProgress)) return;

        pivotGrate(grateProgress, state);
        lastProgress = grateProgress;
    }
}
