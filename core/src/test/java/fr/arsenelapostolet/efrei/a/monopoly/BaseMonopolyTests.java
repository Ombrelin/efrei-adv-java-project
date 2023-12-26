package fr.arsenelapostolet.efrei.a.monopoly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseMonopolyTests {

    private UUID player1 = UUID.randomUUID();
    private UUID player2 = UUID.randomUUID();
    private UUID player3 = UUID.randomUUID();
    private UUID player4 = UUID.randomUUID();

    private Monopoly monopoly;
    private FakeDices fakeDices = new FakeDices();

    public abstract Monopoly createMonopoly(Dices dices, List<UUID> playerIds);

    @BeforeEach
    public void setUp(){
        fakeDices.setScore(3);
        monopoly = createMonopoly(fakeDices, List.of(player1, player2, player3, player4));
    }

    @Test
    public void start_firstPlayerMoved(){
        // When
        final var locations = monopoly.getPlayersLocation();

        // Then
        assertEquals(4, locations.size());
    }



}
