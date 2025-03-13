package minicraft.entity;

import minicraft.core.io.Sound;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.graphic.Color;
import minicraft.graphic.Rectangle;
import minicraft.graphic.Screen;
import minicraft.item.Items;
import minicraft.level.tile.Tile;

import java.util.List;

public class Arrow extends Entity implements ClientTickable {
    private final Direction dir;
    private int damage;
    private final Mob owner;
    private final int speed;

    /**
     * An Arrow projectile
     *
     * @param owner  the owner of the arrow
     * @param dir    the direction of the arrow
     * @param damage the damage of the arrow
     */
    public Arrow(Mob owner, Direction dir, int damage) {
        this(owner, owner.x, owner.y, dir, damage);
    }

    /**
     * An Arrow projectile
     *
     * @param owner  the owner of the arrow
     * @param x      the x position of the arrow
     * @param y      the y position of the arrow
     * @param dir    the direction of the arrow
     * @param damage the damage of the arrow
     */
    public Arrow(Mob owner, int x, int y, Direction dir, int damage) {
        super(Math.abs(dir.getX()) + 1, Math.abs(dir.getY()) + 1);
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.damage = damage;
        this.speed = damage > 3 ? 4 : (damage >= 0 ? 3 : 2);
    }

    /**
     * Generates information about the arrow.
     *
     * @return string representation of owner, xdir, ydir and damage.
     */
    public String getData() {
        return owner.eid + ":" + dir.ordinal() + ":" + damage;
    }

    /**
     * Updates the state of the arrow.
     */
    @Override
    public void tick() {
        // Check if the projectile is out of bounds
        int xTile = x >> 4;
        int yTile = y >> 4;

        if (x < 0 || xTile > level.w || y < 0 || yTile > level.h) {
            remove();
            return;
        }

        // Update position based on direction and speed
        x += dir.getX() * speed;
        y += dir.getY() * speed;

        // Check if the projectile collides with any entities
        List<Entity> entitylist = level.getEntitiesInRect(new Rectangle(x, y, 0, 0, Rectangle.CENTER_DIMS));
        boolean criticalHit = random.nextInt(11) < 9;

        for (Entity entityHit : entitylist) {
            if (entityHit instanceof Mob && entityHit != owner) {
                Mob mob = (Mob) entityHit;
                int extraDamage = (entityHit instanceof Player ? 0 : 3) + (criticalHit ? 0 : 1);
                mob.hurt(owner, damage + extraDamage, dir);

                if (random.nextBoolean()) {
                    remove();
                }

                break; // No need to check other entities
            }
        }

        // Check if the projectile collides with a solid tile
        Tile tile = level.getTile(xTile, yTile);
        if (!tile.mayPass(level, xTile, yTile, this)) {
            // Check if the projectile collides with water
            if (!(tile.id == 16)) {

                if (random.nextBoolean() && owner instanceof Player) {
                    level.dropItem(x - (dir.getX() * 4), y - (dir.getY() * 4), Items.get("Arrow"));
                }

                Sound.playAt("genericHurt", this.x , this.y);
                remove();
            }
        }
    }

    /**
     * Returns whether or not the arrow is solid.
     */
    @Override
    public boolean isSolid() {
        return false;
    }

    /**
     * Returns whether or not the arrow can swim.
     */
    @Override
    public boolean canSwim() {
        return true;
    }

    /**
     * Renders the arrow on screen.
     */
    @Override
    public void render(Screen screen) {
        int xt = 0;
        int yt = 2;

        if (dir == Direction.LEFT) xt = 1;
        if (dir == Direction.UP) xt = 2;
        if (dir == Direction.DOWN) xt = 3;

        screen.render(x - 4, y + 2, xt + (yt << 5), 0, 0, -1, false, Color.BLACK);
        screen.render(x - 4, y - 4, xt + (yt << 5), 0, 0);
    }
}
