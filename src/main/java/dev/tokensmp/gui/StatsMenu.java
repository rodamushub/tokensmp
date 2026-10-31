package dev.tokensmp.gui;

import dev.tokensmp.TokenSMPPlugin;
import dev.tokensmp.data.PlayerData;
import dev.tokensmp.util.Fmt;
import dev.tokensmp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Locale;

public final class StatsMenu extends Menu {

    public StatsMenu(TokenSMPPlugin plugin, Player player) {
        super(plugin, player);
    }

    @Override
    protected String title() {
        return "Your statistics";
    }

    @Override
    protected int rows() {
        return 3;
    }

    @Override
    protected void build() {
        PlayerData d = plugin.economy().get(player.getUniqueId());
        double kd = d.deaths == 0 ? d.kills : (double) d.kills / d.deaths;
        long bounty = plugin.bounties().total(player.getUniqueId());

        set(10, ItemBuilder.of(Material.GOLD_INGOT).name("<gold>Tokens")
                .lore("<white>" + symbol() + " " + Fmt.num(d.balance),
                        "<gray>Rank <white>#" + plugin.economy().rankByBalance(player.getUniqueId())).build());
        set(11, ItemBuilder.of(Material.SKELETON_SKULL).name("<red>Kills").lore("<white>" + Fmt.num(d.kills)).build());
        set(12, ItemBuilder.of(Material.WITHER_ROSE).name("<dark_red>Deaths to players").lore("<white>" + Fmt.num(d.deaths)).build());
        set(13, ItemBuilder.of(Material.COMPARATOR).name("<yellow>K/D ratio")
                .lore("<white>" + String.format(Locale.US, "%.2f", kd)).build());
        set(14, ItemBuilder.of(Material.EMERALD).name("<green>Bounties claimed").lore("<white>" + Fmt.num(d.bountiesClaimed)).build());
        set(15, ItemBuilder.of(Material.WRITABLE_BOOK).name("<aqua>Bounties placed").lore("<white>" + Fmt.num(d.bountiesPlaced)).build());
        set(16, ItemBuilder.of(Material.TARGET).name("<red>Bounty on you")
                .lore(bounty > 0 ? "<gold>" + symbol() + " " + Fmt.num(bounty) : "<gray>None").build());
        set(22, ItemBuilder.of(Material.ARROW).name("<yellow>Back").build(), e -> new MainMenu(plugin, player).open());
    }
}
