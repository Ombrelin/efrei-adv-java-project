package fr.arsenelapostolet.efrei.monopoly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseMonopolyTests {

    private final String player1 = "player 1";
    private final String player2 = "player 2";
    private final String player3 = "player 3";
    private final String player4 = "player 4";

    private Monopoly monopoly;
    private final Dices fakeDices = mock(Dices.class);

    public abstract Monopoly createMonopoly(Dices dices, List<String> playerIds);

    @BeforeEach
    public void setUp() {
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(3);

        monopoly = createMonopoly(fakeDices, List.of(player1, player2, player3, player4));
    }

    @Test
    public void start_firstPlayerMoved() {
        // Then
        final var locations = monopoly.getPlayersLocation();
        assertEquals(4, locations.size());

        assertPlayerIsOnLocation(locations, player1, "Rue Victor Hugo", Location.LocationKind.PROPERTY);
        assertPlayerIsOnLocation(locations, player2, "Départ", Location.LocationKind.START);
        assertPlayerIsOnLocation(locations, player3, "Départ", Location.LocationKind.START);
        assertPlayerIsOnLocation(locations, player4, "Départ", Location.LocationKind.START);
    }

    @Test
    public void start_whenInvalidOrderIsIssued_thenOrderIsIgnored() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(5);

        // When
        monopoly.submitOrder(player3, OrderKind.IDLE);

        // Then
        final var locations = monopoly.getPlayersLocation();

        assertPlayerIsOnLocation(locations, player1, "Rue Victor Hugo", Location.LocationKind.PROPERTY);
        assertPlayerIsOnLocation(locations, player2, "Départ", Location.LocationKind.STATION);
        assertPlayerIsOnLocation(locations, player3, "Départ", Location.LocationKind.START);
        assertPlayerIsOnLocation(locations, player4, "Départ", Location.LocationKind.START);
    }

    @Test
    public void start_whenFirstPlayerIssuesIdleOrder_secondPlayerMoved() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(5);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE); // P1 acts, advances P2 is moved by 5

        // Then
        final var locations = monopoly.getPlayersLocation();

        assertPlayerIsOnLocation(locations, player1, "Rue Victor Hugo", Location.LocationKind.PROPERTY);
        assertPlayerIsOnLocation(locations, player2, "Villejuif - Léo Lagrange", Location.LocationKind.STATION);
        assertPlayerIsOnLocation(locations, player3, "Départ", Location.LocationKind.START);
        assertPlayerIsOnLocation(locations, player4, "Départ", Location.LocationKind.START);
    }

    @Test
    public void fullRound_everyPlayerIssuesIdleOrder_everyPlayerMoves() {
        // When
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(5)
                .thenReturn(6)
                .thenReturn(2);

        monopoly.submitOrder(player1, OrderKind.IDLE);  // P1 acts, advances P2 by 5
        monopoly.submitOrder(player2, OrderKind.IDLE);  // P2 acts, advances P3 by 6
        monopoly.submitOrder(player3, OrderKind.IDLE); // P3 acts, advances P4 by 2

        // Then
        final var locations = monopoly.getPlayersLocation();

        assertPlayerIsOnLocation(locations, player1, "Rue Victor Hugo", Location.LocationKind.PROPERTY);
        assertPlayerIsOnLocation(locations, player2, "Villejuif - Léo Lagrange", Location.LocationKind.STATION);
        assertPlayerIsOnLocation(locations, player3, "Rue Jean Jaurès", Location.LocationKind.PROPERTY);
        assertPlayerIsOnLocation(locations, player4, "Caisse de Communauté", Location.LocationKind.CARD);
    }

    @Test
    public void fullBoard_continueToNextBoardTour() {
        // When

        monopoly.submitOrder(player1, OrderKind.IDLE);  // P1 acts, advances P2 by 3
        monopoly.submitOrder(player2, OrderKind.IDLE);  // P2 acts, advances P3 by 3
        monopoly.submitOrder(player3, OrderKind.IDLE); // P3 acts, advances P4 by 3

        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(10);
        for (var i = 0; i < 5; ++i) {
            monopoly.submitOrder(player4, OrderKind.IDLE); // P4 acts, advances P1 by 5 * 10
            monopoly.submitOrder(player1, OrderKind.IDLE); // P1 acts, advances P1 by 5 * 10
            monopoly.submitOrder(player2, OrderKind.IDLE); // P2 acts, advances P1 by 5 * 10
            monopoly.submitOrder(player3, OrderKind.IDLE); // P3 acts, advances P1 by 5 * 10
        }

        // Every player advanced by 53

        // Then
        final var locations = monopoly.getPlayersLocation();

        assertPlayerIsOnLocation(locations, player1, "Avenue de la République", Location.LocationKind.PROPERTY);
        assertPlayerIsOnLocation(locations, player2, "Avenue de la République", Location.LocationKind.PROPERTY);
        assertPlayerIsOnLocation(locations, player3, "Avenue de la République", Location.LocationKind.PROPERTY);
        assertPlayerIsOnLocation(locations, player4, "Avenue de la République", Location.LocationKind.PROPERTY);
    }


    @Test
    public void twoRounds_everyPlayerIssuesIdleOrder_everyPlayerMovesTwice() {
        // When
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(5)
                .thenReturn(6)
                .thenReturn(2)
                .thenReturn(5)
                .thenReturn(3)
                .thenReturn(2)
                .thenReturn(1);

        monopoly.submitOrder(player1, OrderKind.IDLE); // P1 acts, advances P2 by 5
        monopoly.submitOrder(player2, OrderKind.IDLE); // P2 acts, advances P3 by 6
        monopoly.submitOrder(player3, OrderKind.IDLE); // P3 acts, advances P4 by 2
        monopoly.submitOrder(player4, OrderKind.IDLE); // P4 acts, advances P1 by 5
        monopoly.submitOrder(player1, OrderKind.IDLE); // P1 acts, advances P2 by 3
        monopoly.submitOrder(player2, OrderKind.IDLE); // P2 acts, advances P3 by 2
        monopoly.submitOrder(player3, OrderKind.IDLE); // P3 acts, advances P4 by 1

        // Then
        final var locations = monopoly.getPlayersLocation();

        assertPlayerIsOnLocation(locations, player1, "Boulevard Maxime Gorki", Location.LocationKind.PROPERTY); // P1 advanced 8
        assertPlayerIsOnLocation(locations, player2, "Boulevard Maxime Gorki", Location.LocationKind.PROPERTY); // P2 advanced 8
        assertPlayerIsOnLocation(locations, player3, "Boulevard Maxime Gorki", Location.LocationKind.PROPERTY); // P3 advanced 8
        assertPlayerIsOnLocation(locations, player4, "Rue Victor Hugo", Location.LocationKind.PROPERTY); // P4 advanced 3
    }

    private void assertPlayerIsOnLocation(Map<String, Location> locations, String player, String expectedLocationName, Location.LocationKind expectedLocationKind) {
        final var playerLocation = locations.get(player);
        assertThat(playerLocation.getName()).isEqualTo(expectedLocationName);
        assertThat(playerLocation.getKind()).isEqualTo(expectedLocationKind);
    }

}