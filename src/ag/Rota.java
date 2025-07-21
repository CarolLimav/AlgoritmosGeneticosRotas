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
    private Deposito deposito; // Novo atributo para o depósito

    public Rota(Veiculo veiculo) {
        this.veiculo = veiculo;
        this.clientes = new ArrayList<>();
    }
    
    public Rota(Veiculo veiculo, Deposito deposito) {
        this.veiculo = veiculo;
        this.clientes = new ArrayList<>(); // Inicializa a lista de clientes vazia
        this.deposito = deposito; // Inicializa o depósito com o valor passado
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

    public double calcularDistanciaTotal() {
        double distancia = 0.0;
        Cliente anterior = deposito; // Começa do depósito

        for (Cliente atual : clientes) {
            distancia += calcularDistancia(anterior, atual);
            anterior = atual;
        }
        // Retorna ao depósito após o último cliente
        if (!clientes.isEmpty()) {
            distancia += calcularDistancia(anterior, deposito);
        }
        return distancia;
    }

    private double calcularDistancia(Cliente a, Cliente b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean respeitaJanelasDeTempo() {
        double tempoAtual = 8.0; // Início do expediente (8h)
        double tempoLimite = 8.0 + veiculo.getTempoDisponivel();
        double velocidadeMedia = 40.0; // km/h (Você pode tornar isso um parâmetro ou constante global)

        Cliente anterior = deposito; // Começa do depósito

        // Tempo de deslocamento do depósito para o primeiro cliente
        if (!clientes.isEmpty()) {
            double distanciaDepotToFirst = calcularDistancia(deposito, clientes.get(0));
            tempoAtual += distanciaDepotToFirst / velocidadeMedia;
        }

        for (Cliente cliente : clientes) {
            // Se o cliente atual não for o primeiro, calcula o tempo de deslocamento do cliente anterior para o atual
            if (anterior != deposito) { // Só calcula se 'anterior' não for o depósito
                double distancia = calcularDistancia(anterior, cliente);
                double tempoDeslocamento = distancia / velocidadeMedia;
                tempoAtual += tempoDeslocamento;
            }

            // Espera até o início da janela se chegou cedo
            if (tempoAtual < cliente.getJanelaInicio()) {
                tempoAtual = cliente.getJanelaInicio();
            }

            // Verifica se excedeu a janela de tempo do cliente ou o tempo limite do veículo
            if (tempoAtual > cliente.getJanelaFim() || tempoAtual > tempoLimite) {
                return false;
            }

            tempoAtual += 0.25; // 15 min para realizar a entrega (Pode virar tempoServico do Cliente)
            anterior = cliente;
        }

        // Tempo de retorno do último cliente para o depósito
        if (!clientes.isEmpty()) {
            double distanciaLastToDepot = calcularDistancia(anterior, deposito);
            tempoAtual += distanciaLastToDepot / velocidadeMedia;
        }

        // Verifica se o tempo total da rota (incluindo retorno ao depósito) excede o tempo limite do veículo
        return tempoAtual <= tempoLimite;
    }
    
    public double calcularTempoExecutado() {
        double tempoAtual = 8.0; // Início do expediente (8h)
        double velocidadeMedia = 40.0; // km/h

        Cliente anterior = deposito;

        if (!clientes.isEmpty()) {
            double distanciaDepotToFirst = calcularDistancia(deposito, clientes.get(0));
            tempoAtual += distanciaDepotToFirst / velocidadeMedia;
        }

        for (Cliente cliente : clientes) {
            if (anterior != deposito) {
                double distancia = calcularDistancia(anterior, cliente);
                double tempoDeslocamento = distancia / velocidadeMedia;
                tempoAtual += tempoDeslocamento;
            }

            // Espera até o início da janela se chegou cedo
            if (tempoAtual < cliente.getJanelaInicio()) {
                tempoAtual = cliente.getJanelaInicio();
            }

            tempoAtual += 0.25; // 15 min para realizar a entrega
            anterior = cliente;
        }

        if (!clientes.isEmpty()) {
            double distanciaLastToDepot = calcularDistancia(anterior, deposito);
            tempoAtual += distanciaLastToDepot / velocidadeMedia;
        }
        return tempoAtual - 8.0; // Retorna a duração total da rota em horas desde o início do expediente
    }

    @Override
    public String toString() {
    	  StringBuilder sb = new StringBuilder("--- Rota do Veículo: " + veiculo.getTipo() + " (ID: " + veiculo.getId() + ") ---\n");
          sb.append("  Capacidade do Veículo: ").append(veiculo.getCapacidade()).append(" pacotes\n");
          sb.append("  Demanda Atendida na Rota: ").append(calcularDemandaTotal()).append(" pacotes\n");
          sb.append("  Tempo Disponível do Veículo: ").append(veiculo.getTempoDisponivel()).append(" horas\n");
          sb.append("  Tempo Executado na Rota: ").append(String.format("%.2f", calcularTempoExecutado())).append(" horas\n");
          sb.append("  Início: ").append(deposito.getNome()).append(" (").append(deposito.getX()).append(", ").append(deposito.getY()).append(")\n");
          for (Cliente cliente : clientes) {
              sb.append("  -> ").append(cliente.getNome())
                .append(" (ID: ").append(cliente.getId())
                .append(", Demanda: ").append(cliente.getDemanda())
                .append(", Janela: ").append(cliente.getJanelaInicio()).append("h-").append(cliente.getJanelaFim()).append("\n");
          }
          sb.append("  Retorno: ").append(deposito.getNome()).append("\n");
          sb.append("  Distância Total da Rota: ").append(String.format("%.2f", calcularDistanciaTotal())).append(" km\n");
          sb.append("  Excede Capacidade? ").append(excedeCapacidade() ? "SIM" : "NÃO").append("\n");
          sb.append("  Respeita Janelas de Tempo? ").append(respeitaJanelasDeTempo() ? "SIM" : "NÃO").append("\n");
          return sb.toString();
    }
}