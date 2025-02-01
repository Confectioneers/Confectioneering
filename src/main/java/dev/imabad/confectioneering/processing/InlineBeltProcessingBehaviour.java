package dev.imabad.confectioneering.processing;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;

public class InlineBeltProcessingBehaviour extends BeltProcessingBehaviour {
    public static final BehaviourType<InlineBeltProcessingBehaviour> TYPE = new BehaviourType<>();
    public InlineBeltProcessingBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
