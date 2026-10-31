package dev.tokensmp.gui;

import dev.tokensmp.TokenSMPPlugin;
import dev.tokensmp.data.PlayerData;
import dev.tokensmp.util.Fmt;
import dev.tokensmp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/** Lists online players to choose a bounty target from. */
public final class PlayerPickerMenu extends PagedMenu {

    public PlayerPickerMenu(TokenSMPPlugin plugin, Player player) {
        super(plugin, player);
    }

    @Override
    protected String title() {
        return "Choose a target";
    }

    @Override
    protected void header() {
        set(3, ItemBuilder.of(Material.COMPASS).name("<yellow>Search offline players")
                .lore("<gray>Type a name in chat.", "", "<yellow>Click to search").build(),
                e -> plugin.prompts().ask(player, "prompt-search", text -> {
                    PlayerData target = plugin.economy().find(text);
                    if (target == null) {
                        plugin.fail(player, "unknown-player", "player", text);
                        return;
                    }
                    new AmountMenu(plugin, player, target.uuid, () -> new PlayerPickerMenu(plugin, player)).open();
                }));
        set(5, ItemBuilder.of(Material.ARROW).name("<yellow>Back").build(),
                e -> new BountyMenu(plugin, player, BountyMenu.Mode.ALL).open());
    }

    @Override
    protected List<Entry> entries() {
        List<Entry> out = new ArrayList<>();
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            if (other.equals(player)) continue;
            long current = plugin.bounties().total(other.getUniqueId());
            var item = ItemBuilder.of(Material.PLAYER_HEAD).head(other).name("<white>" + other.getName())
                    .lore("<gray>Current bounty: <gold>" + symbol() + " " + Fmt.num(current), "", "<yellow>Click to choose").build();
            out.add(new Entry(item, e -> new AmountMenu(plugin, player, other.getUniqueId(),
                    () -> new PlayerPickerMenu(plugin, player)).open()));
        }
        return out;
    }
}
