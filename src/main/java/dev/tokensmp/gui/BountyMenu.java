package dev.tokensmp.gui;

import dev.tokensmp.TokenSMPPlugin;
import dev.tokensmp.data.PlayerData;
import dev.tokensmp.sound.SoundType;
import dev.tokensmp.util.Fmt;
import dev.tokensmp.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BountyMenu extends PagedMenu {

    public enum Mode { ALL, MINE }

    private Mode mode;

    public BountyMenu(TokenSMPPlugin plugin, Player player, Mode mode) {
        super(plugin, player);
        this.mode = mode;
    }

    @Override
    protected String title() {
        return mode == Mode.ALL ? "Active bounties" : "My bounties";
    }

    @Override
    protected void header() {
        set(1, ItemBuilder.of(Material.EMERALD).name("<green>Place a bounty")
                .lore("<gray>Pick an online player and", "<gray>put tokens on their head.", "", "<yellow>Click to choose").build(),
                e -> new PlayerPickerMenu(plugin, player).open());

        set(3, ItemBuilder.of(Material.COMPASS).name("<yellow>Search for a player")
                .lore("<gray>Type any player's name in chat,", "<gray>online or offline.", "", "<yellow>Click to search").build(),
                e -> plugin.prompts().ask(player, "prompt-search", text -> {
                    PlayerData target = plugin.economy().find(text);
                    if (target == null) {
                        plugin.fail(player, "unknown-player", "player", text);
                        return;
                    }
                    new AmountMenu(plugin, player, target.uuid, () -> new BountyMenu(plugin, player, Mode.ALL)).open();
                }));

        boolean all = mode == Mode.ALL;
        set(5, ItemBuilder.of(all ? Material.WRITABLE_BOOK : Material.BOOK)
                .name(all ? "<aqua>Show my bounties" : "<aqua>Show all bounties")
                .lore("<gray>Switch between every bounty", "<gray>and the ones you funded.", "", "<yellow>Click to switch").build(),
                e -> {
                    mode = all ? Mode.MINE : Mode.ALL;
                    page = 0;
                    render();
                });

        set(7, ItemBuilder.of(Material.ARROW).name("<yellow>Back").build(), e -> new MainMenu(plugin, player).open());
    }

    @Override
    protected List<Entry> entries() {
        List<Entry> out = new ArrayList<>();
        if (mode == Mode.ALL) {
            for (Map.Entry<UUID, Long> b : plugin.bounties().ranked()) {
                UUID target = b.getKey();
                out.add(entry(target, b.getValue(),
                        "<gray>Contributors: <white>" + plugin.bounties().contributors(target)));
            }
        } else {
            for (Map.Entry<UUID, Long> mine : plugin.bounties().contributedBy(player.getUniqueId()).entrySet()) {
                UUID target = mine.getKey();
                out.add(entry(target, plugin.bounties().total(target),
                        "<gray>Your contribution: <white>" + symbol() + " " + Fmt.num(mine.getValue())));
            }
        }
        return out;
    }

    private Entry entry(UUID target, long total, String extraLine) {
        String name = plugin.economy().nameOf(target);
        boolean self = target.equals(player.getUniqueId());
        boolean online = Bukkit.getPlayer(target) != null;
        var item = ItemBuilder.of(Material.PLAYER_HEAD).head(Bukkit.getOfflinePlayer(target))
                .name("<red>" + name)
                .lore("<gray>Bounty: <gold>" + symbol() + " " + Fmt.num(total),
                        extraLine,
                        "<gray>Status: " + (online ? "<green>online" : "<dark_gray>offline"),
                        "",
                        self ? "<dark_gray>This is you." : "<yellow>Click to add to this bounty")
                .build();
        return new Entry(item, e -> {
            if (self) {
                plugin.sounds().play(player, SoundType.DECLINED);
                return;
            }
            new AmountMenu(plugin, player, target, () -> new BountyMenu(plugin, player, mode)).open();
        });
    }
}
