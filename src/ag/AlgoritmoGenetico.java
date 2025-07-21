package ag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlgoritmoGenetico {// Implementação do Algoritmo Genético para otimização das rotas
    private List<Cliente> clientes; // Lista de clientes a atender
    private List<Veiculo> veiculos;  // Frota de veículos disponíveis
    private int tamanhoPopulacao = 100; // Número de soluções na população
    private int geracoes = 3; // Quantidade de gerações para evolução
    private double taxaCrossover = 0.8; //Probabilidade de cruzamento
    private double taxaMutacao = 0.1; // Probabilidade de mutação

    private Random random = new Random();

    public AlgoritmoGenetico(List<Cliente> clientes, List<Veiculo> veiculos) {
        this.clientes = clientes;
        this.veiculos = veiculos;
    }

    public Solucao executar() {  // Método principal que executa o algoritmo genético e retorna a melhor solução
        List<Solucao> populacao = gerarPopulacaoInicial(); // Gera população inicial aleatória
        // Loop de evolução das gerações
        for (int geracao = 0; geracao < geracoes; geracao++) {
            List<Solucao> novaPopulacao = new ArrayList<>();
            // Preenche a nova população até o tamanho definido
            while (novaPopulacao.size() < tamanhoPopulacao) {
                Solucao pai1 = selecionar(populacao); // Seleção de pai 1
                Solucao pai2 = selecionar(populacao); // Seleção de pai 2

                Solucao filho;
                // Decide se vai aplicar crossover com base na taxa
                if (random.nextDouble() < taxaCrossover) {
                    filho = crossover(pai1, pai2);
                } else {
                    filho = copiar(pai1);
                }
                // Aplica mutação com base na taxa
                if (random.nextDouble() < taxaMutacao) {
                    mutar(filho);
                }

                novaPopulacao.add(filho); // Adiciona filho à nova população
            }
            // Exibe o melhor fitness da geração para monitorar evolução
            System.out.printf("Geração %d - Melhor fitness (sem penalidade): %.2f\n", geracao, fitnessSemPenalidade(melhor(populacao)));
            // Elitismo: mantém o melhor da geração anterior na nova população
            Solucao melhorAnterior = melhor(populacao);
            novaPopulacao.set(0, melhorAnterior); // Mantém o melhor da geração anterior
            populacao = novaPopulacao;  // Atualiza a população para próxima geração
        }
        // Retorna a melhor solução encontrada após todas as gerações
        return melhor(populacao);
    }
    
    // Gera a população inicial com soluções válidas distribuindo clientes aleatoriamente
    private List<Solucao> gerarPopulacaoInicial() {
        List<Solucao> populacao = new ArrayList<>();
        
        for (int i = 0; i < tamanhoPopulacao; i++) {
            Solucao solucao = new Solucao();
            List<Cliente> clientesNaoAtendidos = new ArrayList<>(clientes);
            Collections.shuffle(clientesNaoAtendidos); // Embaralha clientes para diversidade
            // Distribui clientes entre veículos respeitando capacidade
            for (Veiculo veiculo : veiculos) {
                Rota rota = new Rota(veiculo);
                int capacidadeRestante = veiculo.getCapacidade();
                
                Iterator<Cliente> iterator = clientesNaoAtendidos.iterator();
                while (iterator.hasNext()) {
                    Cliente cliente = iterator.next();
                    if (capacidadeRestante >= cliente.getDemanda()) {
                        rota.adicionarCliente(cliente);
                        capacidadeRestante -= cliente.getDemanda();
                        iterator.remove(); // Remove cliente atendido da lista
                    }
                }
                
                if (!rota.getClientes().isEmpty()) {
                    solucao.adicionarRota(rota);
                }
            }
            
            // Garante que todos os clientes foram atendidos antes de adicionar a solução
            if (clientesNaoAtendidos.isEmpty()) {
                populacao.add(solucao);
            } else {
                i--; // Tentar novamente- // Repetir tentativa para preencher a população
            }
        }
        return populacao;
    }

    private Solucao selecionar(List<Solucao> populacao) {
        // Torneio de 3 soluções
        Solucao melhor = null;
        for (int i = 0; i < 3; i++) {
            Solucao s = populacao.get(random.nextInt(populacao.size()));  // Seleciona aleatoriamente
            if (melhor == null || fitness(s) < fitness(melhor)) { // Escolhe o de menor custo (melhor fitness)
                melhor = s;
            }
        }
        return melhor;
    }

    private Solucao crossover(Solucao pai1, Solucao pai2) {
        Solucao filho = new Solucao();
        List<Cliente> clientesFilho = new ArrayList<>();
        
        // Adicionar clientes únicos de ambos os pais -  // Conjunto para rastrear clientes já adicionados (evita duplicatas)
        Set<Cliente> clientesAdicionados = new HashSet<>();
        // Adiciona clientes únicos do pai1
        for (Rota rota : pai1.getRotas()) {
            for (Cliente cliente : rota.getClientes()) {
                if (!clientesAdicionados.contains(cliente)) {
                    clientesAdicionados.add(cliente);
                    clientesFilho.add(cliente);
                }
            }
        }
        // Adiciona clientes únicos do pai2 (sem repetir os do pai1)
        for (Rota rota : pai2.getRotas()) {
            for (Cliente cliente : rota.getClientes()) {
                if (!clientesAdicionados.contains(cliente)) {
                    clientesAdicionados.add(cliente);
                    clientesFilho.add(cliente);
                }
            }
        }
        
        // Distribuir os clientes nos veículos do filho = // Embaralha a lista para distribuir aleatoriamente
        Collections.shuffle(clientesFilho);
        int indiceCliente = 0;
        // Tenta distribuir os clientes nos veículos respeitando a capacidade
        for (Veiculo veiculo : veiculos) {
            Rota rota = new Rota(veiculo);
            int capacidadeRestante = veiculo.getCapacidade();
            // Adiciona clientes enquanto houver capacidade no veículo
            while (indiceCliente < clientesFilho.size() && 
                   capacidadeRestante >= clientesFilho.get(indiceCliente).getDemanda()) {
                rota.adicionarCliente(clientesFilho.get(indiceCliente));
                capacidadeRestante -= clientesFilho.get(indiceCliente).getDemanda();
                indiceCliente++;
            }
            // Adiciona a rota à solução, se ela tiver pelo menos um cliente
            if (!rota.getClientes().isEmpty()) {
                filho.adicionarRota(rota);
            }
        }
        
        return filho;
    }

    private void mutar(Solucao solucao) {
        List<Rota> rotas = solucao.getRotas();
        // Garante que há pelo menos duas rotas para trocar
        if (rotas.size() < 2) return;
        
        // Escolher dois indices de rotas  diferentes
        int idx1 = random.nextInt(rotas.size());
        int idx2;
        do {
            idx2 = random.nextInt(rotas.size());
        } while (idx2 == idx1 && rotas.size() > 1);
        
        Rota r1 = rotas.get(idx1);
        Rota r2 = rotas.get(idx2);
     // Garante que ambas rotas têm clientes
        if (r1.getClientes().isEmpty() || r2.getClientes().isEmpty()) return;
        
        // Trocar clientes entre rotas se possível -  // Escolhe um cliente aleatório de cada rota
        int i1 = random.nextInt(r1.getClientes().size());
        int i2 = random.nextInt(r2.getClientes().size());
        
        Cliente c1 = r1.getClientes().get(i1);
        Cliente c2 = r2.getClientes().get(i2);
        
        // Verificar se a troca é viável em termos de capacidade
        int demandaR1 = r1.calcularDemandaTotal() - c1.getDemanda() + c2.getDemanda();
        int demandaR2 = r2.calcularDemandaTotal() - c2.getDemanda() + c1.getDemanda();
        
        if (demandaR1 <= r1.getVeiculo().getCapacidade() && 
            demandaR2 <= r2.getVeiculo().getCapacidade()) {
        	   // Realiza a troca
            r1.getClientes().set(i1, c2);
            r2.getClientes().set(i2, c1);
        }
    }

    private double fitness(Solucao solucao) {
        double custo = solucao.calcularCustoTotal();
        
        // Verificar se todos os clientes foram atendidos
        Set<Cliente> clientesAtendidos = new HashSet<>();
        for (Rota rota : solucao.getRotas()) {
            clientesAtendidos.addAll(rota.getClientes());
        }
        // Penalidade alta para cada cliente não atendido
        if (clientesAtendidos.size() < clientes.size()) {
            custo += 10000 * (clientes.size() - clientesAtendidos.size());
        }
        
        // Penalidades por restrições violadas
        for (Rota rota : solucao.getRotas()) {
            if (rota.excedeCapacidade()) {
            	   // Penalidade proporcional ao excesso de demanda
                custo += 1000 * (rota.calcularDemandaTotal() - rota.getVeiculo().getCapacidade());
            }
            if (!rota.respeitaJanelasDeTempo()) {
            	// Penalidade fixa por violar a janela de tempo
                custo += 1000;
            }
        }
        
        return custo;
    }

    private Solucao melhor(List<Solucao> populacao) {
        Solucao melhor = populacao.get(0); // Assume que a primeira é a melhor
        for (Solucao s : populacao) {
            if (fitness(s) < fitness(melhor)) {
                melhor = s;// Atualiza se encontrar solução com menor custo
            }
        }
        return melhor;
    }

    private Solucao copiar(Solucao original) {
        Solucao copia = new Solucao();
        for (Rota rota : original.getRotas()) {
            copia.adicionarRota(copiarRota(rota));
        }
        return copia;
    }

    private Rota copiarRota(Rota original) {
        Rota nova = new Rota(original.getVeiculo());
        nova.getClientes().addAll(original.getClientes());
        return nova;
    }
    
    private double fitnessSemPenalidade(Solucao solucao) {
        return solucao.calcularCustoTotal();
    }
    
}
