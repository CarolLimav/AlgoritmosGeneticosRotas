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
    private Veiculo veiculo;  
    private List<Cliente> clientes;  
    private Deposito deposito;
  
    public Rota(Veiculo veiculo, Deposito deposito) {
        this.veiculo = veiculo;
        this.clientes = new ArrayList<>();
        this.deposito = deposito; 
    }
    
    public void adicionarCliente(Cliente cliente) {
        this.clientes.add(cliente);
    }

    public int calcularDemandaTotal() {  
        int total = 0;
        for (Cliente c : clientes) {
            total += c.getDemanda();
        }
        return total;
    }
    
    private double calcularDistancia(Cliente a, Cliente b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public double calcularDistanciaTotal() {
        double distancia = 0.0;
        Cliente anterior = deposito; 

        for (Cliente atual : clientes) {
            distancia += calcularDistancia(anterior, atual);
            anterior = atual;
        }
      
        if (!clientes.isEmpty()) {
            distancia += calcularDistancia(anterior, deposito);
        }
        return distancia;
    }
    
    public double calcularTempoExecutado() {
        double tempoAtual = 8.0; 
        double velocidadeMedia = 40.0; 

        Cliente anterior = deposito;

        if (!clientes.isEmpty()) {
            double distanciaDepositoPrimeiroCliente = calcularDistancia(deposito, clientes.get(0));
            tempoAtual += distanciaDepositoPrimeiroCliente / velocidadeMedia;
        }

        for (Cliente cliente : clientes) {
            if (anterior != deposito) {
                double distancia = calcularDistancia(anterior, cliente);
                double tempoDeslocamento = distancia / velocidadeMedia;
                tempoAtual += tempoDeslocamento;
            }

            if (tempoAtual < cliente.getJanelaInicio()) {
                tempoAtual = cliente.getJanelaInicio();
            }

            tempoAtual += 0.25; 
            anterior = cliente;
        }

        if (!clientes.isEmpty()) {
            double distanciaUltimoClienteAoDeposito = calcularDistancia(anterior, deposito);
            tempoAtual +=distanciaUltimoClienteAoDeposito / velocidadeMedia;
        }
        return tempoAtual - 8.0;
    }
    
    public boolean excedeCapacidade() { 
        return calcularDemandaTotal() > veiculo.getCapacidade();
    }

    public boolean respeitaJanelasDeTempo() {
        double tempoAtual = 8.0;
        double tempoLimite = 8.0 + veiculo.getTempoDisponivel();
        double velocidadeMedia = 40.0; 

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

            if (tempoAtual < cliente.getJanelaInicio()) {
                tempoAtual = cliente.getJanelaInicio();
            }

            if (tempoAtual > cliente.getJanelaFim() || tempoAtual > tempoLimite) {
                return false;
            }

            tempoAtual += 0.25; 
            anterior = cliente;
        }

        if (!clientes.isEmpty()) {
            double distanciaLastToDepot = calcularDistancia(anterior, deposito);
            tempoAtual += distanciaLastToDepot / velocidadeMedia;
        }

        return tempoAtual <= tempoLimite;
    }
    
   
    @Override
    public String toString() {
    	  StringBuilder sb = new StringBuilder("--- Rota do Veículo: " + veiculo.getTipo() + " ---\n");
          sb.append("  Capacidade do Veículo: ").append(veiculo.getCapacidade()).append(" pacotes\n");
          sb.append("  Demanda Atendida na Rota: ").append(calcularDemandaTotal()).append(" pacotes\n");
          sb.append("  Tempo Disponível do Veículo: ").append(veiculo.getTempoDisponivel()).append(" horas\n");
          sb.append("  Tempo Executado na Rota: ").append(String.format("%.2f", calcularTempoExecutado())).append(" horas\n");
          sb.append("  Início: ").append(deposito.getNome()).append(" (").append(deposito.getX()).append(", ").append(deposito.getY()).append(")\n");
          for (Cliente cliente : clientes) {
              sb.append("  -> ").append(cliente.getNome())
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