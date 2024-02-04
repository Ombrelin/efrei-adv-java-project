package fr.arsenelapostolet.efrei.monopoly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    // Livrable 1

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

    // Livrable 2

    @Test
    public void start_balance_playerHaveStartingMoney() {
        // When
        final var balances = monopoly.getPlayersBalance();

        // Then
        assertThat(balances.values())
                .allMatch(balance -> balance.equals(new BigDecimal(1500)));
    }

    @Test
    public void buy_propertyIsOwnedByBuyerAndTheirMoneyIsSpent() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(10);

        // When
        monopoly.submitOrder(player1, OrderKind.BUY);
        final var player1Balance = monopoly.getPlayersBalance().get(player1);
        final var boughtProperty = findLocationByName("Rue Victor Hugo");

        // Then
        assertThat(player1Balance).isEqualTo(new BigDecimal(1500 - 60));
        assertThat(boughtProperty.getOwner()).isEqualTo(player1);
    }

    @Test
    public void rent_whenPropertyIsOwned_rentIsPaidToOwner() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(3);
        monopoly.submitOrder(player1, OrderKind.BUY);

        // When
        final var player1Balance = monopoly.getPlayersBalance().get(player1);
        final var player2Balance = monopoly.getPlayersBalance().get(player2);

        // Then
        assertThat(player1Balance).isEqualTo(new BigDecimal(1500 - 60 + 4));
        assertThat(player2Balance).isEqualTo(new BigDecimal(1500 - 4));
    }

    @Test
    public void buy_whenPropertyIsStation_propertyIsOwnedByBuyerAndTheirMoneyIsSpent() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(5)
                .thenReturn(10);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);

        final var player2Balance = monopoly.getPlayersBalance().get(player2);
        final var boughtProperty = findLocationByName("Villejuif - Léo Lagrange");

        // Then
        assertThat(player2Balance).isEqualTo(new BigDecimal(1500 - 200));
        assertThat(boughtProperty.getOwner()).isEqualTo(player2);
    }

    @Test
    public void rent_whenPropertyIsStation_rentIsPaidToOwner() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(5);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);

        final var player2Balance = monopoly.getPlayersBalance().get(player2);
        final var player3Balance = monopoly.getPlayersBalance().get(player3);

        // Then
        assertThat(player2Balance).isEqualTo(new BigDecimal(1500 - 200 + 25));
        assertThat(player3Balance).isEqualTo(new BigDecimal(1500 - 25));
    }


    @Test
    public void buy_whenPropertyIsCompany_propertyIsOwnedByBuyerAndTheirMoneyIsSpent() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(12)
                .thenReturn(10);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);

        final var player2Balance = monopoly.getPlayersBalance().get(player2);
        final var boughtProperty = findLocationByName("Electricité de Villejuif");

        // Then
        assertThat(player2Balance).isEqualTo(new BigDecimal(1500 - 150));
        assertThat(boughtProperty.getOwner()).isEqualTo(player2);
    }

    @Test
    public void rent_whenPropertyIsCompanyAndOwnerOwnsOnlyOne_rentIsPaidToOwner() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(12)
                .thenReturn(12)
                .thenReturn(10);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);

        final var player2Balance = monopoly.getPlayersBalance().get(player2);
        final var player3Balance = monopoly.getPlayersBalance().get(player3);

        // Then
        assertThat(player2Balance).isEqualTo(new BigDecimal(1500 - 150 + (12 * 4)));
        assertThat(player3Balance).isEqualTo(new BigDecimal(1500 - (12 * 4)));
    }

    @Test
    public void rent_whenPropertyIsCompanyAndOwnerOwnsTwo_rentIsPaidToOwner() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(12)
                .thenReturn(3)
                .thenReturn(3)
                .thenReturn(3)
                .thenReturn(16)
                .thenReturn(25);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);
        monopoly.submitOrder(player3, OrderKind.IDLE);
        monopoly.submitOrder(player4, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);

        final var player2Balance = monopoly.getPlayersBalance().get(player2);
        final var player3Balance = monopoly.getPlayersBalance().get(player3);

        // Then
        assertThat(player2Balance).isEqualTo(new BigDecimal((1500 - (150 * 2)) + (25 * 10)));
        assertThat(player3Balance).isEqualTo(new BigDecimal(1500 - (25 * 10)));
    }

    @Test
    public void rent_whenPlayerHasLessThan0Money_playerIsRemovedFromGame() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(12)
                .thenReturn(3)
                .thenReturn(3)
                .thenReturn(3)
                .thenReturn(16)
                .thenReturn(25 + (40 * 7));

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);
        monopoly.submitOrder(player3, OrderKind.IDLE);
        monopoly.submitOrder(player4, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);

        /*
        Player 2 buys the second company, and Player 3 is moved by 305 on the company location, they are confronted with
        a rent of 3050 which they cannot pay (Player 3 has 1500 + 7*200, which is 2900)
        */

        final var balances = monopoly.getPlayersBalance();
        final var locations = monopoly.getPlayersLocation();

        // Then
        assertThatPlayerIsRemovedFromGame(balances, locations, player3);
        assertThat(balances.get(player2)).isEqualTo(new BigDecimal((1500 - (2 * 150)) + (1500 + 7*200)));
    }

    @Test
    public void rent_whenLessThanTwoPlayers_exceptionIsThrown() {
        // Given
        monopoly = createMonopoly(fakeDices, List.of(player1, player2));
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(3)
                .thenReturn(9)
                .thenReturn(3)
                .thenReturn(16)
                .thenReturn(22 + (40 * 7))
                .thenReturn(1);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.BUY);
        monopoly.submitOrder(player2, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.BUY);

        final var balances = monopoly.getPlayersBalance();
        final var locations = monopoly.getPlayersLocation();

        // Then
        assertThatPlayerIsRemovedFromGame(balances, locations, player2);
        assertThatThrownBy(() -> monopoly.submitOrder(player1, OrderKind.IDLE))
                .isInstanceOf(GameFinishedException.class);
        assertThat(balances.get(player1)).isEqualTo(new BigDecimal((1500 - (2 * 150)) + (1500 + 7*200)));
    }

    // Livrable 3

    @Test
    public void prison_landsOnGoToPrison_playerGoesToPrison() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(30);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);

        final var locations = monopoly.getPlayersLocation();

        // Then
        assertPlayerIsOnLocation(locations, player2, "Prison", Location.LocationKind.JAIL);

    }

    @Test
    public void prison_playerInPrisonAndIdles_thenPlayerDoesntMove() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(30)
                .thenReturn(3);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.IDLE);
        monopoly.submitOrder(player3, OrderKind.IDLE);
        monopoly.submitOrder(player4, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.IDLE);

        final var locations = monopoly.getPlayersLocation();

        // Then
        assertPlayerIsOnLocation(locations, player2, "Prison", Location.LocationKind.JAIL);
    }

    @Test
    public void prison_playerInPrisonCantPayOnInitialRound_thenPlayerDoesntMove() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(30)
                .thenReturn(3);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.PAY_PRISON);

        final var locations = monopoly.getPlayersLocation();
        final var balances = monopoly.getPlayersBalance();

        // Then
        assertPlayerIsOnLocation(locations, player2, "Prison", Location.LocationKind.JAIL);
        assertThat(balances.get(player2)).isEqualTo(new BigDecimal(1500));
    }

    @Test
    public void prison_playerInPrisonAndPaysOnFirstPrisonRound_thenPlayerMoves() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(30)
                .thenReturn(3);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.IDLE);
        monopoly.submitOrder(player3, OrderKind.IDLE);
        monopoly.submitOrder(player4, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.PAY_PRISON);

        final var locations = monopoly.getPlayersLocation();
        final var balances = monopoly.getPlayersBalance();

        // Then
        assertPlayerIsOnLocation(locations, player2, "Avenue de la République", Location.LocationKind.PROPERTY);
        assertThat(balances.get(player2)).isEqualTo(new BigDecimal(1500 - 50));
    }

    @Test
    public void prison_playerInPrisonAndPaysOnSecondPrisonRound_thenPlayerMoves() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(30)
                .thenReturn(3);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.IDLE);
        monopoly.submitOrder(player3, OrderKind.IDLE);
        monopoly.submitOrder(player4, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.PAY_PRISON);

        final var locations = monopoly.getPlayersLocation();
        final var balances = monopoly.getPlayersBalance();

        // Then
        assertPlayerIsOnLocation(locations, player2, "Avenue de la République", Location.LocationKind.PROPERTY);
        assertThat(balances.get(player2)).isEqualTo(new BigDecimal(1500 - 50));
    }

    @Test
    public void prison_playerInPrison_thirdPrisonRound_thenPlayerPaysThenMoves() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(30)
                .thenReturn(3);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.IDLE);
        monopoly.submitOrder(player3, OrderKind.IDLE);
        monopoly.submitOrder(player4, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.IDLE);
        monopoly.submitOrder(player3, OrderKind.IDLE);
        monopoly.submitOrder(player4, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.IDLE);

        final var locations = monopoly.getPlayersLocation();
        final var balances = monopoly.getPlayersBalance();

        // Then
        assertPlayerIsOnLocation(locations, player2, "Avenue de la République", Location.LocationKind.PROPERTY);
        assertThat(balances.get(player2)).isEqualTo(new BigDecimal(1500 - 50));
    }

    @Test
    public void start_passByStart_Gets200() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(43);

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);

        final var balances = monopoly.getPlayersBalance();

        // Then
        assertThat(balances.get(player2)).isEqualTo(new BigDecimal(1700));

    }

    @Test
    public void rent_stationRent_scalesWithNumberOfStationsOwned() {
        // Given
        when(fakeDices.throwTwoSixSidedDices())
                .thenReturn(5) // Player 2
                .thenReturn(3) // Player 3
                .thenReturn(5) // Player 4
                .thenReturn(3) // Player 1
                .thenReturn(10) // Player 2
                .thenReturn(3) // Player 3
                .thenReturn(10) // Player 4
                .thenReturn(3) // Player 1
                .thenReturn(10) // Player 2
                .thenReturn(3) // Player 3
                .thenReturn(10) // Player 4
                .thenReturn(3) // Player 1
                .thenReturn(10) // Player 2
                .thenReturn(3) // Player 3
                .thenReturn(10); // Player 4

        // When
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);

        final var player2BalanceAfterBuyingFirstStation = monopoly.getPlayersBalance().get(player2);

        monopoly.submitOrder(player3, OrderKind.IDLE);

        final var player4BalanceAfterLandingOnFirstStation = monopoly.getPlayersBalance().get(player4);
        final var player2BalanceAfterCollectingFirstStationRent = monopoly.getPlayersBalance().get(player2);

        monopoly.submitOrder(player4, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);

        final var player2BalanceAfterBuyingSecondStation = monopoly.getPlayersBalance().get(player2);

        monopoly.submitOrder(player3, OrderKind.IDLE);

        final var player4BalanceAfterLandingOnSecondStation = monopoly.getPlayersBalance().get(player4);
        final var player2BalanceAfterCollectingSecondStationRent = monopoly.getPlayersBalance().get(player2);

        monopoly.submitOrder(player4, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);

        final var player2BalanceAfterBuyingThirdStation = monopoly.getPlayersBalance().get(player2);

        monopoly.submitOrder(player3, OrderKind.IDLE);

        final var player4BalanceAfterLandingOnThirdStation = monopoly.getPlayersBalance().get(player4);
        final var player2BalanceAfterCollectingThirdStationRent = monopoly.getPlayersBalance().get(player2);

        monopoly.submitOrder(player4, OrderKind.IDLE);
        monopoly.submitOrder(player1, OrderKind.IDLE);
        monopoly.submitOrder(player2, OrderKind.BUY);

        final var player2BalanceAfterBuyingLastStation = monopoly.getPlayersBalance().get(player2);

        monopoly.submitOrder(player3, OrderKind.IDLE);

        final var player4BalanceAfterLandingOnLastStation = monopoly.getPlayersBalance().get(player4);
        final var player2BalanceAfterCollectingLastStationRent = monopoly.getPlayersBalance().get(player2);

        // Then
        assertThat(player2BalanceAfterBuyingFirstStation).isEqualTo(new BigDecimal(1500 - 200));
        assertThat(player4BalanceAfterLandingOnFirstStation).isEqualTo(new BigDecimal(1500 - 25));
        assertThat(player2BalanceAfterCollectingFirstStationRent).isEqualTo(new BigDecimal(1500 - 200 + 25));

        assertThat(player2BalanceAfterBuyingSecondStation).isEqualTo(new BigDecimal(1500 - (2 * 200) + 25));
        assertThat(player4BalanceAfterLandingOnSecondStation).isEqualTo(new BigDecimal(1500 - 25 - 50));
        assertThat(player2BalanceAfterCollectingSecondStationRent).isEqualTo(new BigDecimal(1500 - (2 * 200) + 25 + 50));

        assertThat(player2BalanceAfterBuyingThirdStation).isEqualTo(new BigDecimal(1500 - (3 * 200) + 25 + 50));
        assertThat(player4BalanceAfterLandingOnThirdStation).isEqualTo(new BigDecimal(1500 - 25 - 50 - 100));
        assertThat(player2BalanceAfterCollectingThirdStationRent).isEqualTo(new BigDecimal(1500 - (3 * 200) + 25 + 50 + 100));

        assertThat(player2BalanceAfterBuyingLastStation).isEqualTo(new BigDecimal(1500 - (4 * 200) + 25 + 50 + 100));
        assertThat(player4BalanceAfterLandingOnLastStation).isEqualTo(new BigDecimal(1500 - 25 - 50 - 100 - 200));
        assertThat(player2BalanceAfterCollectingLastStationRent).isEqualTo(new BigDecimal(1500 - (4 * 200) + 25 + 50 + 100 + 200));
    }

    private void assertThatPlayerIsRemovedFromGame(Map<String, BigDecimal> balances, Map<String, Location> locations, String player) {
        assertThat(balances.containsKey(player)).isFalse();
        assertThat(locations.containsKey(player)).isFalse();
    }

    private void assertPlayerIsOnLocation(Map<String, Location> locations, String player, String expectedLocationName, Location.LocationKind expectedLocationKind) {
        final var playerLocation = locations.get(player);
        assertThat(playerLocation.getName()).isEqualTo(expectedLocationName);
        assertThat(playerLocation.getKind()).isEqualTo(expectedLocationKind);
    }

    private Location findLocationByName(String anObject) {
        return monopoly
                .getBoard()
                .stream()
                .filter(property -> property.getName().equals(anObject))
                .findFirst()
                .orElseThrow();
    }

}