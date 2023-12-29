package fr.arsenelapostolet.efrei.monopoly;

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
