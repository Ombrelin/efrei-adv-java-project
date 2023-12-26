package fr.arsenelapostolet.efrei.a.monopoly;

public interface Location {

    String getName();

    public enum Kind {
        START,
        PROPERTY,
        TAX,
        STATION,
        CARD,
        PRISON,
        COMPANY,
        FREE_PARK,
        GO_TO_JAIL
    }

}
