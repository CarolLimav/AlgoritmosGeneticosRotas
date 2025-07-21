package ag;

import java.util.ArrayList;
import java.util.List;

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
            custo += calcularDistanciaRota(rota);
        }
        return custo;
    }

    // Calcula a distância total de uma rota somando distâncias entre clientes consecutivos
    private double calcularDistanciaRota(Rota rota) {
        double distancia = 0;
        List<Cliente> clientes = rota.getClientes();
        Cliente anterior = null;

        for (Cliente atual : clientes) {
            if (anterior != null) {
                distancia += calcularDistancia(anterior, atual);
            }
            anterior = atual;
        }
        return distancia;
    }
 // Calcula a distância Euclidiana entre dois clientes (pontos)
    private double calcularDistancia(Cliente c1, Cliente c2) {
        double dx = c1.getX() - c2.getX();
        double dy = c1.getY() - c2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}