package dev.tokensmp.gui;

import dev.tokensmp.TokenSMPPlugin;
import dev.tokensmp.data.PlayerData;
import dev.tokensmp.ui.LeaderboardService.Board;
import dev.tokensmp.util.Fmt;
import dev.tokensmp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class MainMenu extends Menu {

    public MainMenu(TokenSMPPlugin plugin, Player player) {
        super(plugin, player);
    }

    @Override
    protected String title() {
        return "Main menu";
    }

    @Override
    protected int rows() {
        return 4;
    }

    @Override
    protected void build() {
        PlayerData d = plugin.economy().get(player.getUniqueId());
        long bounty = plugin.bounties().total(player.getUniqueId());

        set(10, ItemBuilder.of(Material.GOLD_INGOT).name("<gold>Your tokens")
                .lore("<white>" + symbol() + " " + Fmt.num(d.balance),
                        "<gray>Rank: <white>#" + plugin.economy().rankByBalance(player.getUniqueId()),
                        "",
                        "<gray>Earn tokens by winning PvP fights.",
                        "<gray>Send tokens with <white>/tokens pay</white>.").build());

        set(12, ItemBuilder.of(Material.TARGET).name("<red>Bounties")
                .lore("<gray>See active bounties, search for a",
                        "<gray>player or put tokens on someone's head.",
                        "",
                        bounty > 0 ? "<red>There is a bounty on you: <white>" + symbol() + " " + Fmt.num(bounty) : "<gray>No bounty on you.",
                        "",
                        "<yellow>Click to open").build(),
                e -> new BountyMenu(plugin, player, BountyMenu.Mode.ALL).open());

        set(14, ItemBuilder.of(Material.GOLD_BLOCK).name("<yellow>Leaderboards")
                .lore("<gray>Richest, most kills, highest bounties,",
                        "<gray>bounties claimed and placed.",
                        "",
                        "<yellow>Click to open").build(),
                e -> new LeaderboardMenu(plugin, player, Board.RICHEST).open());

        set(16, ItemBuilder.of(Material.PLAYER_HEAD).head(player).name("<aqua>Your statistics")
                .lore("<gray>Kills, deaths, bounties and more.", "", "<yellow>Click to open").build(),
                e -> new StatsMenu(plugin, player).open());

        set(20, ItemBuilder.of(Material.KNOWLEDGE_BOOK).name("<green>Tutorial")
                .lore("<gray>A quick walkthrough of how", "<gray>Token SMP works.", "", "<yellow>Click to open").build(),
                e -> new TutorialMenu(plugin, player, 0).open());

        set(22, ItemBuilder.of(Material.COMPARATOR).name("<white>Settings")
                .lore("<gray>Sounds, notifications and", "<gray>interface preferences.", "", "<yellow>Click to open").build(),
                e -> new SettingsMenu(plugin, player).open());

        set(24, ItemBuilder.of(Material.BOOK).name("<white>Command list")
                .lore("<gray>Print every command in chat.", "", "<yellow>Click to show").build(),
                e -> {
                    player.closeInventory();
                    plugin.msg().sendList(player, "help.commands");
                });

        set(31, ItemBuilder.of(Material.BARRIER).name("<red>Close").build(), e -> player.closeInventory());
    }
}
