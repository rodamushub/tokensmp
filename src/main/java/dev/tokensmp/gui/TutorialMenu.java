package dev.tokensmp.gui;

import dev.tokensmp.TokenSMPPlugin;
import dev.tokensmp.util.ItemBuilder;
import dev.tokensmp.util.Msg;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/** Paged tutorial. The text lives in messages.yml under tutorial.pages. */
public final class TutorialMenu extends Menu {

    private int index;

    public TutorialMenu(TokenSMPPlugin plugin, Player player, int index) {
        super(plugin, player);
        this.index = index;
    }

    @Override
    protected String title() {
        return "Tutorial";
    }

    @Override
    protected int rows() {
        return 3;
    }

    @Override
    protected void build() {
        List<Msg.Page> pages = plugin.msg().tutorialPages();
        if (pages.isEmpty()) {
            set(13, ItemBuilder.of(Material.BARRIER).name("<red>No tutorial pages configured").build());
            return;
        }
        index = Math.max(0, Math.min(index, pages.size() - 1));
        Msg.Page page = pages.get(index);

        List<String> lore = new ArrayList<>();
        for (String line : page.lines()) {
            lore.add(line.replace("<symbol>", symbol())
                    .replace("<reward>", String.valueOf(plugin.settings().killReward))
                    .replace("<steal>", stealText()));
        }
        set(13, ItemBuilder.of(Material.WRITTEN_BOOK).name("<gold>" + page.title())
                .lore(lore).build());

        set(4, ItemBuilder.of(Material.PAPER).name("<white>Page " + (index + 1) + " of " + pages.size()).build());

        if (index > 0) {
            set(11, ItemBuilder.of(Material.ARROW).name("<yellow>Previous").build(), e -> {
                index--;
                render();
            });
        }
        if (index < pages.size() - 1) {
            set(15, ItemBuilder.of(Material.ARROW).name("<yellow>Next").build(), e -> {
                index++;
                render();
            });
        } else {
            set(15, ItemBuilder.of(Material.LIME_DYE).name("<green>Finish").lore("<gray>Back to the main menu.").build(),
                    e -> new MainMenu(plugin, player).open());
        }
        set(22, ItemBuilder.of(Material.BARRIER).name("<red>Close").build(), e -> player.closeInventory());
        set(18, ItemBuilder.of(Material.ARROW).name("<yellow>Main menu").build(), e -> new MainMenu(plugin, player).open());
    }

    private String stealText() {
        double d = plugin.settings().stealPercent;
        return d == Math.floor(d) ? String.valueOf((long) d) : String.valueOf(d);
    }
}
