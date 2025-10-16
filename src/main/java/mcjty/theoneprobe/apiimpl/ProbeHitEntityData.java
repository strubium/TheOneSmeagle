package mcjty.theoneprobe.apiimpl;

import lombok.AllArgsConstructor;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import net.minecraft.util.math.Vec3d;

@AllArgsConstructor
public class ProbeHitEntityData implements IProbeHitEntityData {

    private final Vec3d hitVec;

    @Override
    public Vec3d getHitVec() {
        return hitVec;
    }
}