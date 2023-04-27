import agente.Acao;
import agente.Agente;
import agente.Posicao;
import ambiente.Sala;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var sala = new Sala(20);
        var agente = new Agente(sala, new Posicao(1, 1));
        System.out.println("Ambiente inicial:\n");
        sala.exibirAmbienteInicial(agente);
        while (agente.isLimpezaPendente()) {
            Acao acaoPendente = agente.decidirAcao();
            System.out.println("Ação: " + acaoPendente);
            agente.executarAcao(acaoPendente);
            if (acaoPendente == Acao.Limpar) {
                System.out.println("Novo Ambiente:");
                sala.exibirAmbiente();
//                Thread.sleep(7000);
            }
        }
        System.out.println("\nQuantidade de movimentos: " + agente.getContagemMovimentos());
        System.out.println("Terminou");
    }
}