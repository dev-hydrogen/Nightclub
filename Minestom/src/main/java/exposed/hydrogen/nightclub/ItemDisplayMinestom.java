package exposed.hydrogen.nightclub;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;

public class ItemDisplayMinestom {
    private Entity itemDisplay;
    private ItemDisplayMeta meta;
    public ItemDisplayMinestom() {
        itemDisplay = new Entity(EntityType.ITEM_DISPLAY);
        meta = (ItemDisplayMeta) itemDisplay.getEntityMeta();
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.HEAD);
        meta.setShadowRadius(0);
        meta.setShadowStrength(0);
    }
    public void setBrightness(int brightness) {
        meta.setBrightnessOverride(brightness);
    }
    public int getBrightness() {
        return meta.getBrightnessOverride();
    }
    public void setInterpolationDuration(int duration) {
        meta.setInterpolationDuration(duration);
    }
    public void setInterpolationStartDelta(int delta) {
        meta.setInterpolationStartDelta(delta);
    }
    public int getInterpolationDuration() {
        return meta.getInterpolationDuration();
    }
    public long getInterpolationStartDelta() {
        return meta.getInterpolationStartDelta();
    }
    public float [] getLeftRotation() {
        return meta.getLeftRotation();
    }
    public float [] getRightRotation() {
        return meta.getRightRotation();
    }
    public void setLeftRotation(float [] rotation) {
        meta.setLeftRotation(rotation);
    }
    public void setRightRotation(float [] rotation) {
        meta.setRightRotation(rotation);
    }
    public ItemStack getItemStack() {
        return meta.getItemStack();
    }
    public void setItemStack(ItemStack itemStack) {
        meta.setItemStack(itemStack);
    }
    public void setScale(Vec scale) {
        meta.setScale(scale);
    }
    public Vec getScale() {
        return meta.getScale();
    }
}
