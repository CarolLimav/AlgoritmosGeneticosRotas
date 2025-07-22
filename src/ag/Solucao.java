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
public class Solucao { 
    private List<Rota> rotas; 

    public Solucao() {
        this.rotas = new ArrayList<>();
    }
  
    public void adicionarRota(Rota rota) {
        this.rotas.add(rota);
    }
 
    public double calcularCustoTotal() {
        double custo = 0;
        for (Rota rota : rotas) {
            custo += rota.calcularDistanciaTotal();
        }
        return custo;
    }
   
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
}