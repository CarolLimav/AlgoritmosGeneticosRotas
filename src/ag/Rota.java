package ag;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rota {// Classe que representa a rota de um veículo, contendo os clientes a serem atendidos
    private Veiculo veiculo;  // Veículo responsável pela rota
    private List<Cliente> clientes;  // Clientes atendidos nessa rota

    public Rota(Veiculo veiculo) {
        this.veiculo = veiculo;
        this.clientes = new ArrayList<>();
    }
    
    // Adiciona um cliente à rota
    public void adicionarCliente(Cliente cliente) {
        this.clientes.add(cliente);
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public int calcularDemandaTotal() {  // Calcula a soma total da demanda (quantidade de pacotes) de todos os clientes na rota
        int total = 0;
        for (Cliente c : clientes) {
            total += c.getDemanda();
        }
        return total;
    }

    public boolean excedeCapacidade() { // Verifica se a demanda total ultrapassa a capacidade do veículo
        return calcularDemandaTotal() > veiculo.getCapacidade();
    }

    public double calcularDistanciaTotal() {  // Calcula a distância total da rota entre todos os clientes (de um para o outro)
        double distancia = 0.0;
        for (int i = 0; i < clientes.size() - 1; i++) {
            Cliente a = clientes.get(i);
            Cliente b = clientes.get(i + 1);
            distancia += calcularDistancia(a, b);
        }
        return distancia;
    }

    private double calcularDistancia(Cliente a, Cliente b) {   // Calcula distância Euclidiana entre dois clientes
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Verifica se a rota respeita as janelas de tempo de entrega e o tempo disponível do veículo
    public boolean respeitaJanelasDeTempo() {
        int tempoAtual = 8; // exemplo: início do expediente às 8h
        int tempoRestante = veiculo.getTempoDisponivel(); // Tempo máximo disponível para essa rota
        
        for (Cliente cliente : clientes) {
        	  // Se chegar antes da janela, espera até o início
            if (tempoAtual < cliente.getJanelaInicio()) {
                tempoAtual = cliente.getJanelaInicio();
            }
            
            // Se passou da janela de tempo ou do tempo disponível do veículo, rota inválida
            if (tempoAtual > cliente.getJanelaFim() || tempoAtual > tempoRestante) {
                return false;
            }

            tempoAtual += 1; // Tempo fixo de 1 hora estimada para entregar no cliente
        }

        //Verifica se a rota não ultrapassa o expediente do veículo (8h + tempoDisponivel)
        return tempoAtual <= (8 + tempoRestante);
    }

    // Representação da rota para impressão (ex.: Veículo: Moto -> Loja A -> Loja B)
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Veículo: " + veiculo.getTipo() + "\n");
        for (Cliente cliente : clientes) {
            sb.append("  -> ").append(cliente.getNome()).append("\n");
        }
        return sb.toString();
    }
}
