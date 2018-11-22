package network.palace.creative.inventory;

import java.util.Map;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class MenuButton {

    @Getter
    private final int slot;
    @Getter
    private final ItemStack itemStack;
    @Getter
    private final Map<ClickType, Consumer<Player>> actions;
}
