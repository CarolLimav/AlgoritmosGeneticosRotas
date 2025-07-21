package ag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        // Criar alguns clientes com (id, nome, x, y, demanda, janelaInicio, janelaFim)
        List<Cliente> clientes = Arrays.asList(
            new Cliente(1, "Loja A", 10, 20, 2, 8, 12),
            new Cliente(2, "Loja B", 15, 30, 3, 9, 11),
            new Cliente(3, "Loja C", 20, 25, 1, 10, 13),
            new Cliente(4, "Loja D", 18, 22, 4, 8, 14),
            new Cliente(5, "Loja E", 25, 15, 5, 9, 12)
        );

        // Criar veículos com (id, tipo, capacidade, tempoDisponivel)
        List<Veiculo> veiculos = Arrays.asList(
            new Veiculo(1, "Moto", 5, 8),
            new Veiculo(2, "Carro", 8, 8),
            new Veiculo(3, "Caminhão", 10, 8)
        );

        // Executar o algoritmo genético
        AlgoritmoGenetico ag = new AlgoritmoGenetico(clientes, veiculos);
        Solucao melhorSolucao = ag.executar();

        // Exibir resultado
        System.out.println("\n=== Melhor solução encontrada ===");
        if (melhorSolucao != null) {
            for (Rota rota : melhorSolucao.getRotas()) {
                System.out.println(rota);
            }
            System.out.printf("Custo total: %.2f\n", melhorSolucao.calcularCustoTotal());
        } else {
            System.out.println("Nenhuma solução encontrada.");
        }
    }
}