package ag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Solucao { // Classe que representa uma solução do problema, que é composta por várias rotas
    private List<Rota> rotas; // Lista de rotas atribuídas a veículos

    public Solucao() {
        this.rotas = new ArrayList<>();
    }
    // Adiciona uma nova rota à solução
    public void adicionarRota(Rota rota) {
        this.rotas.add(rota);
    }
 // Calcula o custo total da solução somando as distâncias de todas as rotas
    public double calcularCustoTotal() {
        double custo = 0;
        for (Rota rota : rotas) {
            custo += rota.calcularDistanciaTotal();
        }
        return custo;
    }

    // Calcula a distância total de uma rota somando distâncias entre clientes consecutivos
   
    public boolean solucaoValida(List<Cliente> todosClientes) {
        Set<Integer> atendidos = new HashSet<>();
        for (Rota rota : rotas) {
            if (rota.excedeCapacidade() || !rota.respeitaJanelasDeTempo()) return false;
            for (Cliente c : rota.getClientes()) {
                atendidos.add(c.getId());
            }
        }
        return atendidos.size() == todosClientes.size();
    }

    
    public boolean atendeTodosClientes(List<Cliente> clientes) {
        Set<Integer> atendidos = new HashSet<>();
        for (Rota rota : rotas) {
            for (Cliente c : rota.getClientes()) atendidos.add(c.getId());
        }
        return atendidos.size() == clientes.size();
    }

    
    
}