package fr.arsenelapostolet.efrei.monopoly;

import java.util.UUID;

public interface Location {

    String getName();

    LocationKind getKind();

    UUID getOwner();

    enum LocationKind {
        START,
        PROPERTY,
        TAX,
        STATION,
        CARD,
        JAIL,
        COMPANY,
        FREE_PARK,
        GO_TO_JAIL
    }

}
