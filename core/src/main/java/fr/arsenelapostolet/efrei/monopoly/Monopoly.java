package fr.arsenelapostolet.efrei.monopoly;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface Monopoly {

    void submitOrder(String playerId, OrderKind order, Map<String, Object> orderParameters);
    Map<String, Location> getPlayersLocation();
    Map<String, BigDecimal> getPlayersBalance();
    List<Location> getBoard();

}
