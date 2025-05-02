package mcjty.theoneprobe.apiimpl;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import mcjty.theoneprobe.apiimpl.elements.ElementVertical;

import java.util.ArrayList;
import java.util.List;

public class ProbeInfo extends ElementVertical {

    public List<IElement> getElements() {
        return children;
    }

    public void fromBytes(ByteBuf buf) {
        children = createElements(buf);
    }

    public ProbeInfo() {
        super(null, 2, ElementAlignment.ALIGN_TOPLEFT);
    }

    public static List<IElement> createElements(ByteBuf buf) {
        int size = buf.readUnsignedShort();  // 2 bytes for size (0–65535)
        List<IElement> elements = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int id = buf.readUnsignedShort();  // 2 bytes per element ID (0–65535)
            IElementFactory factory = TheOneProbe.theOneProbeImp.getElementFactory(id);
            IElement element = factory.createElement(buf);
            elements.add(element);
        }
        return elements;
    }

    public static void writeElements(List<IElement> elements, ByteBuf buf) {
        int size = elements == null ? 0 : elements.size();
        buf.writeShort(size);  // 2 bytes for count
        for (int i = 0; i < size; i++) {
            IElement element = elements.get(i);
            buf.writeShort(element.getID());  // 2 bytes for ID
            element.toBytes(buf);
        }
    }

    public void removeElement(IElement element) {
        this.getElements().remove(element);
    }
}
