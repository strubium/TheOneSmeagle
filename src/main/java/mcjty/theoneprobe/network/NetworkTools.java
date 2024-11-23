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

    /**
     * Reads a String from the given ByteBuf.
     *
     * @param dataIn The ByteBuf to read from.
     * @return The String read from the buffer, or null if the length was -1, or an empty string if the length was 0.
     */
    public static String readString(ByteBuf dataIn) {
        int s = dataIn.readInt();
        if (s == -1) {
            return null;
        }
        if (s == 0) {
            return "";
        }
        byte[] dst = new byte[s];
        dataIn.readBytes(dst);
        return new String(dst);
    }

    /**
     * Writes a String to the given ByteBuf.
     *
     * @param dataOut The ByteBuf to write to.
     * @param str The String to write.
     */
    public static void writeString(ByteBuf dataOut, String str) {
        if (str == null) {
            dataOut.writeInt(-1);
            return;
        }
        byte[] bytes = str.getBytes();
        dataOut.writeInt(bytes.length);
        if (bytes.length > 0) {
            dataOut.writeBytes(bytes);
        }
    }

    /**
     * Reads a UTF-8 encoded String from the given ByteBuf.
     *
     * @param dataIn The ByteBuf to read from.
     * @return The String read from the buffer, or null if the length was -1, or an empty string if the length was 0.
     */
    public static String readStringUTF8(ByteBuf dataIn) {
        int length = dataIn.readInt();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return "";
        }
        byte[] dst = new byte[length];
        dataIn.readBytes(dst);
        return new String(dst, java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Writes a UTF-8 encoded String to the given ByteBuf.
     *
     * @param dataOut The ByteBuf to write to.
     * @param str The String to write.
     */
    public static void writeStringUTF8(ByteBuf dataOut, String str) {
        if (str == null) {
            dataOut.writeInt(-1);
            return;
        }
        byte[] bytes = str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        dataOut.writeInt(bytes.length);
        if (bytes.length > 0) {
            dataOut.writeBytes(bytes);
        }
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
}