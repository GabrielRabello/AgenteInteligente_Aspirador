package agente;

import ambiente.Estado;
import ambiente.Sala;
import java.util.HashMap;
import java.util.Map;

public class Agente {
    private final Sala sala;
    private final Posicao posXY;
    private boolean limpezaPendente;
    private int contagemMovimentos;
    private int pilhaRotinaFinalCima;
    private int pilhaRotinaFinalBaixo;
    private Acao inicioMovimentoRotinaFinal;

    public Agente(Sala sala, Posicao posInicial) {
        if (sala.tam - 1 < posInicial.getPosX() || sala.tam - 1 < posInicial.getPosY()) {
            throw new IllegalArgumentException("Posição inicial deve estar dentro dos limites do ambiente");
        }
        this.sala = sala;
        this.posXY = posInicial;
        this.limpezaPendente = true;
        this.pilhaRotinaFinalCima = 0;
        this.pilhaRotinaFinalBaixo = 0;
        this.inicioMovimentoRotinaFinal = Acao.Nenhuma;
        this.contagemMovimentos = 0;

    }

    public Acao decidirAcao() {
        if (!this.limpezaPendente) return Acao.Nenhuma;
        Estado estadoAtual = this.sala.getEstado(this.posXY);
        if (estadoAtual == Estado.Sujo) {
            return Acao.Limpar;
        }
        boolean isRotinaFinal = pilhaRotinaFinalBaixo != 0 || pilhaRotinaFinalCima != 0;
        Map<Acao, Integer> arredores = verificarArredores(isRotinaFinal);
        int maxVal = Integer.MIN_VALUE;
        Acao acaoPrioridade = Acao.Nenhuma;
        for (var entry : arredores.entrySet()) {
            int v = entry.getValue();
            Acao k = entry.getKey();
            // Esquerda e direita tem precedência quando a sujeira é igual.
            if (k == Acao.Esquerda || k == Acao.Direita) {
                if (maxVal <= v) {
                    maxVal = v;
                    acaoPrioridade = entry.getKey();
                }
            } else if (maxVal < v) {
                maxVal = v;
                acaoPrioridade = entry.getKey();
            };
        }
        // O código dessa parte ficou bem meiote
        if (arredores.isEmpty() && inicioMovimentoRotinaFinal == Acao.Nenhuma) {
            inicioMovimentoRotinaFinal = prepararRotinaFinal();
            isRotinaFinal = true;
        }
        // Desce até o fim do espaço e então sobe até o início. A cada movimento, acontecem as verificações do espaço horizontal para verificar por sujeira.
        if (isRotinaFinal && arredores.isEmpty() && inicioMovimentoRotinaFinal == Acao.Baixo) {
            if (pilhaRotinaFinalBaixo > 0) {
                acaoPrioridade = Acao.Baixo;
                pilhaRotinaFinalBaixo--;
            } else if (pilhaRotinaFinalCima > 0) {
                acaoPrioridade = Acao.Cima;
                pilhaRotinaFinalCima--;
            }
        } // Ou sobe até o inicio e depois desce
        else if (isRotinaFinal && arredores.isEmpty() && inicioMovimentoRotinaFinal == Acao.Cima) {
            if (pilhaRotinaFinalCima > 0) {
                acaoPrioridade = Acao.Cima;
                pilhaRotinaFinalCima--;
            } else if (pilhaRotinaFinalBaixo > 0) {
                acaoPrioridade = Acao.Baixo;
                pilhaRotinaFinalBaixo--;
            }
        }
        this.contagemMovimentos++;
        return acaoPrioridade;
    }

    /**
     * Faz a contagem da sujeira no espaço horizontal e/ou vertical a partir do ponto onde o agente está.
     */
    private Map<Acao, Integer> verificarArredores(boolean apenasColunas)
    {
        final int posX = this.posXY.getPosX();
        final int posY = this.posXY.getPosY();
        Map<Acao, Integer> direcoesSujeiraCount = new HashMap<Acao, Integer>();
        // colunas Esquerdas
        for (int i = posY+1; i < this.sala.tam; i++) {
            if (this.sala.getEstado(new Posicao(posX, i)) == Estado.Sujo) {
                direcoesSujeiraCount.merge(Acao.Direita, 1, Integer::sum);
            }
        }
        // Colunas direitas
        for (int i = posY-1; i >= 0; i--) {
            if (this.sala.getEstado(new Posicao(posX, i)) == Estado.Sujo) {
                direcoesSujeiraCount.merge(Acao.Esquerda, 1, Integer::sum);
            }
        }
        if (!apenasColunas) {
            // Linhas acima
            for (int i = posX-1; i >= 0; i--) {
                if (this.sala.getEstado(new Posicao(i, posY)) == Estado.Sujo) {
                    direcoesSujeiraCount.merge(Acao.Cima, 1, Integer::sum);
                }
            }
            // Linhas abaixo
            for (int i = posX+1; i < this.sala.tam; i++) {
                if (this.sala.getEstado(new Posicao(i, posY)) == Estado.Sujo) {
                    direcoesSujeiraCount.merge(Acao.Baixo, 1, Integer::sum);
                }
            }
        }
        return direcoesSujeiraCount;
    }

    /**
     * Após o agente não encontrar sujeira no seu campo de visão, ele prepara uma ultima procura vertical antes de encerrar.
     * @implNote A rota vertical é calculada levando em consideração o caminho mais curto de forma que todas as linhas sejam verificadas.
     * @return Acao
     */
    public Acao prepararRotinaFinal() {
        if (posXY.getPosX() < sala.tam / 2) {
            pilhaRotinaFinalCima = posXY.getPosX();
            pilhaRotinaFinalBaixo = sala.tam - 1;
            return Acao.Cima;
        }
        pilhaRotinaFinalCima = sala.tam - 1;
        pilhaRotinaFinalBaixo = sala.tam - 1 - posXY.getPosX();
        return Acao.Baixo;
    }
    public void executarAcao(Acao acao) {
        switch(acao) {
            case Limpar -> sala.limpar(posXY);
            case Esquerda -> posXY.setPosY(posXY.getPosY() - 1);
            case Direita -> posXY.setPosY(posXY.getPosY() + 1);
            case Cima -> posXY.setPosX(posXY.getPosX() - 1);
            case Baixo -> posXY.setPosX(posXY.getPosX() + 1);
            case Nenhuma -> limpezaPendente = false;
            default -> throw new IllegalArgumentException("Ação inválida");
        }
        sala.updateLocAgente(acao, posXY);
    }

    public boolean isLimpezaPendente() { return this.limpezaPendente; }
    public int getContagemMovimentos() { return this.contagemMovimentos; }
    public Posicao getPosicao() { return this.posXY; }
}
