package minicraft.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.core.io.Sound;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Point;
import minicraft.gfx.Screen;
import minicraft.saveload.Save;
import minicraft.screen.entry.ListEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.entry.StringEntry;
import minicraft.util.Advancement;

public class AdvancementsDisplay extends Display {

    private static final ArrayList<Advancement> advancements = new ArrayList<>();

    private static Advancement selectedAdvancement;
    private static int advancementScore;

    private static final ArrayList<ListEntry> stringEntries = new ArrayList<>();

    static {

        // Get achievements from a json filed stored in resources. Relative to project root.
        String advancementJson = "";
        try (InputStream stream = Game.class.getResourceAsStream("/resources/advancements.json")) {
            assert stream != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            advancementJson = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            Logger.error("Could not read achievements from json file.");

            ex.printStackTrace();
        }

        // Read the json and put it in the achievements list.
        JSONArray json = new JSONArray(advancementJson);
        for (Object object : json) {
            JSONObject obj = (JSONObject) object;

            Advancement a = new Advancement(
                    Localization.getLocalized(obj.getString("id")),
                    Localization.getLocalized(obj.getString("desc")),
                    obj.getInt("score")
            );

            advancements.add(a);

            SelectEntry entry = new SelectEntry(a.name, null, false);

            stringEntries.add(entry);
        }
    }

    public AdvancementsDisplay() {
        super(true, true,
                new Menu.Builder(false, 2,RelPos.CENTER, stringEntries).setSize(48, 48).createMenu(),
                new Menu.Builder(false, 2, RelPos.BOTTOM, new StringEntry("")).setSize(200, 32).setPositioning(new Point(Screen.w / 2, Screen.h / 2 + 32), RelPos.BOTTOM).createMenu());
    }

    @Override
    public void init(@Nullable Display parent) {
        super.init(parent);

        selectedAdvancement = advancements.get(menus[0].getSelection());
    }

    @Override
    public void tick(InputHandler input) {
        super.tick(input);

        selectedAdvancement = advancements.get(menus[0].getSelection());
    }

    @Override
    protected void onSelectionChange(int oldSel, int newSel) {
        super.onSelectionChange(oldSel, newSel);
    }

    /**
     * Use this to lock or unlock an achievement.
     * @param id Achievement ID
     */
    public static void setAchievement(int id, boolean unlocked){
        Advancement a = advancements.get(id);
        a.setUnlocked(unlocked);

        if (unlocked) {
        	advancementScore += a.score;
        } else {
        	advancementScore -= a.score;
        }
        new Save();
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);

        // Title.
        Font.drawCentered(Localization.getLocalized("Advancements"), screen, 8, Color.YELLOW);

        // Achievement score.
        Font.drawCentered(Localization.getLocalized("Advancement Score:") + " " + advancementScore, screen, 32, Color.GRAY);

        // Render Achievement Info.
        if (selectedAdvancement.getUnlocked()){
            Font.drawCentered(Localization.getLocalized("Earned!"), screen, 48, Color.GREEN);
        } else {
            Font.drawCentered(Localization.getLocalized("Not Earned"), screen, 48, Color.RED);
        }

        // Achievement description.
        menus[1].setEntries(StringEntry.useLines(Font.getLines(selectedAdvancement.description, menus[1].getBounds().getSize().width, menus[1].getBounds().getSize().height, 2)));

        // Help text.
        Font.drawCentered("Use " + Game.input.getMapping("cursor-down") + " and " + Game.input.getMapping("cursor-up") + " to move.", screen, Screen.h - 8, Color.DARK_GRAY);
    }

    @Override
    public void onExit() {
        // Play confirm sound.
        Sound.Menu_confirm.play();
        new Save();
    }
}