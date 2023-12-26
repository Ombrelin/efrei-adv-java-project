package fr.arsenelapostolet.efrei.a.monopoly;

import java.util.Map;
import java.util.UUID;

public interface Monopoly {

    void submitOrder(UUID playerId, OrderKind order);
    Map<UUID, Location> getPlayersLocation();

}
