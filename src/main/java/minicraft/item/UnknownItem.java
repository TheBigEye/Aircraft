package minicraft.item;

import minicraft.graphic.Sprite;

public class UnknownItem extends StackableItem {

    protected UnknownItem(String name) {
        super(name, Sprite.missingTexture(1, 1));
    }

    @Override
    public UnknownItem clone() {
        return new UnknownItem(getName());
    }
}
