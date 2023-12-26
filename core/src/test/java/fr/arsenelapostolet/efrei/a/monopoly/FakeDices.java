package fr.arsenelapostolet.efrei.a.monopoly;

public class FakeDices implements Dices {

    private int score;

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int throwTwoSixSidedDices() {
        return score;
    }
}
