package mcjty.theoneprobe.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * HitBox's are basically buttons. They can be used by a GUI for actions when pressed
 *
 * @since 11/10/2016
 * @author McJty
 */
@SideOnly(Side.CLIENT)
class HitBox {
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final Runnable runnable;

    /**
     * Constructs a HitBox with specified coordinates and an action to execute when clicked.
     *
     * @param x1       The x-coordinate of the top-left corner of the hit area.
     * @param y1       The y-coordinate of the top-left corner of the hit area.
     * @param x2       The x-coordinate of the bottom-right corner of the hit area (exclusive).
     * @param y2       The y-coordinate of the bottom-right corner of the hit area (exclusive).
     * @param runnable The action to execute when the hit area is clicked.
     */
    public HitBox(int x1, int y1, int x2, int y2, Runnable runnable) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.runnable = runnable;
    }

    /**
     * Checks if the given coordinates are within the hit area.
     *
     * @param xx The x-coordinate to check.
     * @param yy The y-coordinate to check.
     * @return true if the coordinates are within the hit area, false otherwise.
     */
    public boolean isHit(int xx, int yy) {
        return xx >= x1 && xx < x2 && yy >= y1 && yy < y2;
    }

    /**
     * Executes the action associated with this HitBox.
     */
    public void call() {
        runnable.run();
    }
}
