package exposed.hydrogen.nightclub.util;

import co.aikar.commands.CommandIssuer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CrossCompatUtil {
    List<CrossCompatPlayer> getListOfPlayers();

    @Nullable CrossCompatPlayer getPlayer(CommandIssuer commandIssuer);
}
