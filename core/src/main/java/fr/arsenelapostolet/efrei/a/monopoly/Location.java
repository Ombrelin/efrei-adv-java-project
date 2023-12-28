package fr.arsenelapostolet.efrei.a.monopoly;

public interface Location {

    String getName();

    LocationKind getKind();

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
