package mcjty.theoneprobe.playerdata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;

@Setter
@Getter
@NoArgsConstructor
public class PlayerGotNote {

    private boolean playerGotNote = false;

    public void copyFrom(PlayerGotNote source) {
        playerGotNote = source.playerGotNote;
    }

    public void saveNBTData(NBTTagCompound compound) {
        compound.setBoolean("gotNote", playerGotNote);
    }

    public void loadNBTData(NBTTagCompound compound) {
        playerGotNote = compound.getBoolean("gotNote");
    }
}
