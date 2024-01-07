package fr.arsenelapostolet.efrei.monopoly;

import java.util.Map;

public interface Monopoly {

    void submitOrder(String playerName, OrderKind order);

    Map<String, Location> getPlayersLocation();

}
