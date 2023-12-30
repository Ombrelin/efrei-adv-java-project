package fr.arsenelapostolet.efrei.monopoly;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface Monopoly {

    void submitOrder(UUID playerId, OrderKind order);
    Map<UUID, Location> getPlayersLocation();
    Map<UUID, BigDecimal> getPlayersBalance();

}
