package ambiente;

import agente.Acao;
import agente.Agente;
import java.util.Random;

import agente.Posicao;

public class Sala {
    public final int tam;
    private Estado[][] ambiente;

    public Sala(int tam) {
        this.tam = tam;
        this.construirAmbiente();
    }

    // Construir o ambiente
    private void construirAmbiente() {
        Random rng = new Random();
        ambiente = new Estado[this.tam][this.tam];
        for (int i = 0; i < this.tam; i++) {
            for (int j = 0; j < this.tam; j++) {
                this.ambiente[i][j] = rng.nextFloat() > 0.50
                        ? Estado.Sujo
                        : Estado.Limpo;
            }
        }
    }

    public void exibirAmbienteInicial(Agente agente) {
        Posicao posAgente = agente.getPosicao();
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                if (j == 0) System.out.print("|");
                if (i == posAgente.getPosX() && j == posAgente.getPosY()) {
                    System.out.print(ambiente[i][j] + "(Agente)\t|");
                } else {
                    System.out.print(ambiente[i][j] + "\t|");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    public void exibirAmbiente()
    {
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                if (j == 0) System.out.print("|");
                System.out.print(ambiente[i][j] + "\t|");
            }
            System.out.println();
        }
        System.out.println();
    }
    public Estado getEstado(Posicao pos) {
        return this.ambiente[pos.getPosX()][pos.getPosY()];
    }
    public void limpar(Posicao pos) {
        this.ambiente[pos.getPosX()][pos.getPosY()] = Estado.Agente;
    }
    public void updateLocAgente(Acao acao, Posicao novaPos) {
        switch (acao) {
            case Esquerda -> this.ambiente[novaPos.getPosX()][novaPos.getPosY() + 1] = Estado.Limpo;
            case Direita -> this.ambiente[novaPos.getPosX()][novaPos.getPosY() - 1] = Estado.Limpo;
            case Cima -> this.ambiente[novaPos.getPosX() + 1][novaPos.getPosY()] = Estado.Limpo;
            case Baixo -> this.ambiente[novaPos.getPosX() - 1][novaPos.getPosY()] = Estado.Limpo;
        }
    }
}
