package fr.arsenelapostolet.efrei.monopoly;

public interface Location {

    String getName();

    LocationKind getKind();

    String getOwner();

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
