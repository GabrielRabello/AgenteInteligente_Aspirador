package agente;

public class Posicao {
    private int posX;
    private int posY;

    public Posicao() {
        this.posX = 0;
        this.posY = 0;
    }

    /**
     *
     * @param posX indexado em 0
     * @param posY indexado em 0
     */
    public Posicao(int posX, int posY) {
        if (posX < 0 || posY < 0) {
            throw new IllegalArgumentException("Posição < 0");
        }
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() { return this.posX; }
    public int getPosY() { return this.posY; }
    public void setPosX(int posX) { this.posX = posX; }
    public void setPosY(int posY) { this.posY = posY; }
}
