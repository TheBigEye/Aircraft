package minicraft.entity.furniture.statue;

import minicraft.entity.furniture.Furniture;
import minicraft.gfx.Sprite;

public class AirWizardStatue extends Furniture {

    public AirWizardStatue() {
        super("AirWizardStatue", new Sprite(14, 28, 2, 2, 2), 3, 2);

    }

    @Override
    public int getLightRadius() {
        return 6;
    }

    @Override
    public Furniture clone() {
        return new AirWizardStatue();
    }

}
