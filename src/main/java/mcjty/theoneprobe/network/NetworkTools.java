package mcjty.theoneprobe.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

public class NetworkTools {

    /**
     * Reads an NBTTagCompound from the given ByteBuf.
     *
     * @param dataIn The ByteBuf to read from.
     * @return The NBTTagCompound read from the buffer, or null if an error occurred.
     */
    public static NBTTagCompound readNBT(ByteBuf dataIn) {
        PacketBuffer buf = new PacketBuffer(dataIn);
        try {
            return buf.readCompoundTag();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Writes an NBTTagCompound to the given ByteBuf.
     *
     * @param dataOut The ByteBuf to write to.
     * @param nbt The NBTTagCompound to write.
     */
    public static void writeNBT(ByteBuf dataOut, NBTTagCompound nbt) {
        PacketBuffer buf = new PacketBuffer(dataOut);
        try {
            buf.writeCompoundTag(nbt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads an ItemStack from the given ByteBuf.
     * Supports ItemStacks with more than 64 items.
     *
     * @param dataIn The ByteBuf to read from.
     * @return The ItemStack read from the buffer, or ItemStack.EMPTY if an error occurred.
     */
    public static ItemStack readItemStack(ByteBuf dataIn) {
        PacketBuffer buf = new PacketBuffer(dataIn);
        try {
            NBTTagCompound nbt = buf.readCompoundTag();
            ItemStack stack = new ItemStack(Objects.requireNonNull(nbt));
            stack.setCount(buf.readInt());
            return stack;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ItemStack.EMPTY;
    }

    /**
     * Writes an ItemStack to the given ByteBuf.
     * Supports ItemStacks with more than 64 items.
     *
     * @param dataOut The ByteBuf to write to.
     * @param itemStack The ItemStack to write.
     */
    public static void writeItemStack(ByteBuf dataOut, ItemStack itemStack) {
        PacketBuffer buf = new PacketBuffer(dataOut);
        NBTTagCompound nbt = new NBTTagCompound();
        itemStack.writeToNBT(nbt);
        try {
            buf.writeCompoundTag(nbt);
            buf.writeInt(itemStack.getCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeStringCompact(ByteBuf buf, String str) {
        if (str == null) {
            writeVarInt(buf, 0); // length = 0 means empty/null
            return;
        }

        byte[] bytes = str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    public static String readStringCompact(ByteBuf buf) {
        int length = readVarInt(buf);
        if (length == 0) return "";
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }


    /**
     * Reads a BlockPos from the given ByteBuf.
     *
     * @param dataIn The ByteBuf to read from.
     * @return The BlockPos read from the buffer.
     */
    public static BlockPos readPos(ByteBuf dataIn) {
        return new BlockPos(dataIn.readInt(), dataIn.readInt(), dataIn.readInt());
    }

    /**
     * Writes a BlockPos to the given ByteBuf.
     *
     * @param dataOut The ByteBuf to write to.
     * @param pos The BlockPos to write.
     */
    public static void writePos(ByteBuf dataOut, BlockPos pos) {
        dataOut.writeInt(pos.getX());
        dataOut.writeInt(pos.getY());
        dataOut.writeInt(pos.getZ());
    }

    /**
     * Writes an enum value to the given ByteBuf.
     *
     * @param buf The ByteBuf to write to.
     * @param value The enum value to write.
     * @param nullValue The value to write if the actual value is null.
     * @param <T> The enum type.
     */
    public static <T extends Enum<T>> void writeEnum(ByteBuf buf, T value, T nullValue) {
        if (value == null) {
            buf.writeInt(nullValue.ordinal());
        } else {
            buf.writeInt(value.ordinal());
        }
    }

    /**
     * Reads an enum value from the given ByteBuf.
     *
     * @param buf The ByteBuf to read from.
     * @param values The array of possible enum values.
     * @param <T> The enum type.
     * @return The enum value read from the buffer.
     */
    public static <T extends Enum<T>> T readEnum(ByteBuf buf, T[] values) {
        return values[buf.readInt()];
    }

    /**
     * Writes a collection of enum values to the given ByteBuf.
     *
     * @param buf The ByteBuf to write to.
     * @param collection A {@link Collection} of enum values to write.
     * @param <T> The enum type.
     */
    public static <T extends Enum<T>> void writeEnumCollection(ByteBuf buf, Collection<T> collection) {
        buf.writeInt(collection.size());
        for (T type : collection) {
            buf.writeInt(type.ordinal());
        }
    }

    /**
     * Reads a collection of enum values from the given ByteBuf.
     *
     * @param buf The ByteBuf to read from.
     * @param collection A {@link Collection} to populate with the enum values.
     * @param values The array of possible enum values.
     * @param <T> The enum type.
     */
    public static <T extends Enum<T>> void readEnumCollection(ByteBuf buf, Collection<T> collection, T[] values) {
        collection.clear();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            collection.add(values[buf.readInt()]);
        }
    }

    /**
     * Writes a Float value to the given ByteBuf.
     *
     * @param buf The ByteBuf to write to.
     * @param value The Float value to write, or null to indicate no value.
     */
    public static void writeFloat(ByteBuf buf, @Nullable Float value) {
        if (value != null) {
            buf.writeBoolean(true);
            buf.writeFloat(value);
        } else {
            buf.writeBoolean(false);
        }
    }

    /**
     * Reads a Float value from the given ByteBuf.
     *
     * @param buf The ByteBuf to read from.
     * @return The Float value read from the buffer, or null if no value was written.
     */
    public static Float readFloat(ByteBuf buf) {
        if (buf.readBoolean()) {
            return buf.readFloat();
        } else {
            return null;
        }
    }

    public static void writeVarInt(ByteBuf buf, int value) {
        while ((value & 0xFFFFFF80) != 0L) {
            buf.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buf.writeByte(value & 0x7F);
    }

    public static int readVarInt(ByteBuf buf) {
        int numRead = 0;
        int result = 0;
        byte read;

        do {
            read = buf.readByte();
            result |= (read & 0x7F) << (7 * numRead);

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((read & 0x80) != 0);

        return result;
    }

}