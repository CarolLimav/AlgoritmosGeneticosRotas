// ag/Main.java
package ag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        // Criar o depósito central (ponto de partida e retorno dos veículos)
        Deposito deposito = new Deposito(0, "Depósito Central", 0, 0); // ID 0, nome, coordenadas (0,0)

        // Criar alguns clientes com (id, nome, x, y, demanda, janelaInicio, janelaFim)
        List<Cliente> clientes = Arrays.asList(
            new Cliente(1, "Loja A", 10, 20, 2, 8, 12),
            new Cliente(2, "Loja B", 15, 30, 3, 9, 11),
            new Cliente(3, "Loja C", 20, 25, 1, 10, 13),
            new Cliente(4, "Loja D", 18, 22, 4, 8, 14),
            new Cliente(5, "Loja E", 25, 15, 5, 9, 12),
            new Cliente(6, "Loja F", 5, 10, 2, 8, 10),
            new Cliente(7, "Loja G", 30, 40, 3, 11, 15),
            new Cliente(8, "Loja H", 22, 18, 1, 9, 13)
        );

        // Criar veículos com (id, tipo, capacidade, tempoDisponivel)
        List<Veiculo> veiculos = Arrays.asList(
            new Veiculo(1, "Moto", 5, 8),      // Capacidade 5 pacotes, 8 horas
            new Veiculo(2, "Carro", 8, 8),     // Capacidade 8 pacotes, 8 horas
            new Veiculo(3, "Caminhão", 15, 10) // Capacidade 15 pacotes, 10 horas
        );

        // Executar o algoritmo genético, passando o depósito
        AlgoritmoGenetico ag = new AlgoritmoGenetico(clientes, veiculos, deposito);
        Solucao melhorSolucao = ag.executar();

        System.out.println("\n=== Melhor solução encontrada ===");
        if (melhorSolucao != null) {
            if (melhorSolucao.solucaoValida(clientes)) {
                System.out.println("Solução válida! Custo Total (distância): " + String.format("%.2f", melhorSolucao.calcularCustoTotal()) + " km");
            } else {
                System.out.println("Solução encontrada (pode conter penalidades):");
                System.out.println("Custo com penalidades: " + String.format("%.2f", ag.fitness(melhorSolucao)));
                System.out.println("Custo Total (distância): " + String.format("%.2f", melhorSolucao.calcularCustoTotal()) + " km");
            }

            for (Rota rota : melhorSolucao.getRotas()) {
                System.out.println(rota);
            }
        } else {
            System.out.println("Nenhuma solução válida foi encontrada.");
        }
    }
}