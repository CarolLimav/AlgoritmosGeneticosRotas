// ag/AlgoritmoGenetico.java
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
public class AlgoritmoGenetico {
    private List<Cliente> clientes;
    private List<Veiculo> veiculos;
    private Deposito deposito;

    private int tamanhoPopulacao = 100;
    private int geracoes = 300;
    private double taxaCrossover = 0.8;
    private double taxaMutacao = 0.15;
    private int tamanhoTorneio = 5;

    private Random random = new Random();

    public AlgoritmoGenetico(List<Cliente> clientes, List<Veiculo> veiculos, Deposito deposito) {
        this.clientes = clientes;
        this.veiculos = veiculos;
        this.deposito = deposito;
    }

    public Solucao executar() {
        List<Solucao> populacao = gerarPopulacaoInicial();

        System.out.println("--- Início da Evolução Genética ---");
        System.out.println("Geração inicial, Melhor Custo (Fitness): " + String.format("%.2f", fitness(melhor(populacao))));

        for (int geracao = 0; geracao < geracoes; geracao++) {
            List<Solucao> novaPopulacao = new ArrayList<>();

            Solucao melhorAnterior = melhor(populacao);
            novaPopulacao.add(melhorAnterior);

            while (novaPopulacao.size() < tamanhoPopulacao) {
                Solucao pai1 = selecionar(populacao);
                Solucao pai2 = selecionar(populacao);

                Solucao filho;
                if (random.nextDouble() < taxaCrossover) {
                    filho = crossover(pai1, pai2);
                } else {
                    filho = copiar(pai1);
                }

                if (random.nextDouble() < taxaMutacao) {
                    mutar(filho);
                }
                novaPopulacao.add(filho);
            }
            while(novaPopulacao.size() > tamanhoPopulacao) {
                // Remove o pior elemento se a nova população exceder o tamanho
                // Isso requer que a lista esteja ordenada ou que se remova o de pior fitness
                // Para simplificar, vamos apenas remover o último, assumindo que a seleção já priorizou os melhores
                novaPopulacao.remove(novaPopulacao.size() - 1);
            }
            populacao = novaPopulacao;

            // Imprime o melhor fitness da geração para monitorar o progresso
            System.out.println("Geração " + (geracao + 1) + ", Melhor Custo (Fitness): " + String.format("%.2f", fitness(melhor(populacao))));
        }
        System.out.println("--- Fim da Evolução Genética ---");

        Solucao melhorSolucao = melhor(populacao);
        if (melhorSolucao != null) {
            if (!melhorSolucao.solucaoValida(clientes)) {
                System.out.println("\nAviso: A melhor solução final pode conter penalidades por violação de restrições.");
                System.out.println("Fitness da melhor solução final (com penalidades): " + String.format("%.2f", fitness(melhorSolucao)));
                System.out.println("Custo real da melhor solução final (distância sem penalidades): " + String.format("%.2f", melhorSolucao.calcularCustoTotal()) + " km");
            } else {
                System.out.println("\nSolução final encontrada é válida e livre de penalidades.");
                System.out.println("Custo Total (distância): " + String.format("%.2f", melhorSolucao.calcularCustoTotal()) + " km");
            }
            return melhorSolucao;
        } else {
            System.out.println("Nenhuma solução válida foi encontrada.");
            return null;
        }
    }

    private List<Solucao> gerarPopulacaoInicial() {
        List<Solucao> populacao = new ArrayList<>();

        int tentativas = 0;
        final int MAX_TENTATIVAS = tamanhoPopulacao * 10; // Aumentado para dar mais chance de gerar soluções válidas inicialmente

        while (populacao.size() < tamanhoPopulacao && tentativas < MAX_TENTATIVAS) {
            Solucao solucao = new Solucao();
            List<Cliente> clientesNaoAtendidos = new ArrayList<>(clientes);
            Collections.shuffle(clientesNaoAtendidos);

            List<Veiculo> veiculosDisponiveis = new ArrayList<>(veiculos);
            Collections.shuffle(veiculosDisponiveis);

            for (Veiculo veiculo : veiculosDisponiveis) {
                Rota rota = new Rota(veiculo, deposito);
                int capacidadeRestante = veiculo.getCapacidade();

                Iterator<Cliente> iterator = clientesNaoAtendidos.iterator();
                List<Cliente> clientesParaAdicionar = new ArrayList<>(); // Clientes que serão adicionados nesta rota após validação
                
                while (iterator.hasNext()) {
                    Cliente cliente = iterator.next();
                    if (capacidadeRestante >= cliente.getDemanda()) {
                        // Tenta adicionar o cliente para verificar a janela de tempo
                        rota.getClientes().add(cliente); // Adiciona temporariamente
                        if (rota.respeitaJanelasDeTempo()) {
                            clientesParaAdicionar.add(cliente); // Adiciona na lista de clientes válidos para essa rota
                            capacidadeRestante -= cliente.getDemanda();
                            iterator.remove(); // Remove da lista de clientes não atendidos
                        } else {
                            // Se não respeita, remove o cliente temporário e tenta o próximo
                            rota.getClientes().remove(cliente);
                        }
                    }
                }
                // Adiciona os clientes que passaram pela validação de capacidade e tempo
                rota.getClientes().clear(); // Limpa clientes temporários
                rota.getClientes().addAll(clientesParaAdicionar); // Adiciona os válidos

                if (!rota.getClientes().isEmpty()) {
                    solucao.adicionarRota(rota);
                }
            }
            
            // Só adiciona à população se todos os clientes foram atendidos e a solução é válida inicialmente
            if (clientesNaoAtendidos.isEmpty() && solucao.solucaoValida(clientes)) {
                populacao.add(solucao);
            }
            tentativas++;
        }

        // Se, após as tentativas, a população ainda não estiver cheia de soluções válidas,
        // preencha o restante gerando soluções aleatórias (que podem ser inválidas, mas serão penalizadas)
        while (populacao.size() < tamanhoPopulacao) {
            Solucao solucaoInvalida = new Solucao();
            List<Cliente> clientesRestantes = new ArrayList<>(clientes); // Reinicia com todos os clientes
            Collections.shuffle(clientesRestantes);

            List<Veiculo> veiculosDisponiveisParaGeracao = new ArrayList<>(veiculos);
            Collections.shuffle(veiculosDisponiveisParaGeracao);

            for (Veiculo veiculo : veiculosDisponiveisParaGeracao) {
                Rota rota = new Rota(veiculo, deposito);
                int capacidadeRestante = veiculo.getCapacidade();

                Iterator<Cliente> iterator = clientesRestantes.iterator();
                while (iterator.hasNext()) {
                    Cliente cliente = iterator.next();
                    if (capacidadeRestante >= cliente.getDemanda()) {
                        // Não faz validação de janela de tempo aqui para garantir que algo seja gerado
                        rota.adicionarCliente(cliente);
                        capacidadeRestante -= cliente.getDemanda();
                        iterator.remove();
                    }
                }
                if (!rota.getClientes().isEmpty()) {
                    solucaoInvalida.adicionarRota(rota);
                }
            }
            populacao.add(solucaoInvalida);
        }
        return populacao;
    }

    private Solucao selecionar(List<Solucao> populacao) {
        Solucao melhor = null;
        for (int i = 0; i < tamanhoTorneio; i++) {
            Solucao s = populacao.get(random.nextInt(populacao.size()));
            if (melhor == null || fitness(s) < fitness(melhor)) {
                melhor = s;
            }
        }
        return melhor;
    }

    private Solucao crossover(Solucao pai1, Solucao pai2) {
        Solucao filho = new Solucao();
        List<Cliente> clientesFilho = new ArrayList<>();

        Set<Cliente> clientesAdicionados = new HashSet<>();

        for (Rota rota : pai1.getRotas()) {
            for (Cliente cliente : rota.getClientes()) {
                if (!clientesAdicionados.contains(cliente)) {
                    clientesAdicionados.add(cliente);
                    clientesFilho.add(cliente);
                }
            }
        }
        for (Rota rota : pai2.getRotas()) {
            for (Cliente cliente : rota.getClientes()) {
                if (!clientesAdicionados.contains(cliente)) {
                    clientesAdicionados.add(cliente);
                    clientesFilho.add(cliente);
                }
            }
        }

        Collections.shuffle(clientesFilho);

        int indiceCliente = 0;
        for (Veiculo veiculo : veiculos) {
            Rota rota = new Rota(veiculo, deposito);
            int capacidadeRestante = veiculo.getCapacidade();

            List<Cliente> clientesRotaAtual = new ArrayList<>();
            while (indiceCliente < clientesFilho.size() &&
                   capacidadeRestante >= clientesFilho.get(indiceCliente).getDemanda()) {
                
                // Tenta adicionar o cliente para verificar janelas de tempo
                clientesRotaAtual.add(clientesFilho.get(indiceCliente));
                // Cria uma rota temporária para testar as janelas de tempo
                Rota rotaTemp = new Rota(veiculo, deposito);
                rotaTemp.getClientes().addAll(clientesRotaAtual);

                if (rotaTemp.respeitaJanelasDeTempo()) {
                    rota.adicionarCliente(clientesFilho.get(indiceCliente));
                    capacidadeRestante -= clientesFilho.get(indiceCliente).getDemanda();
                    indiceCliente++;
                } else {
                    // Se a adição deste cliente quebra as janelas de tempo, não o adiciona
                    clientesRotaAtual.remove(clientesRotaAtual.size() - 1); // Remove do temp
                    // Avança para o próximo cliente na lista geral para não ficar preso
                    indiceCliente++; // Avança o índice, mas não adiciona este cliente a esta rota
                }
            }
            if (!rota.getClientes().isEmpty()) {
                filho.adicionarRota(rota);
            }
        }
        return filho;
    }

    private void mutar(Solucao solucao) {
        List<Rota> rotas = solucao.getRotas();
        if (rotas.isEmpty()) return;

        // Se houver apenas uma rota, tenta mutação intra-rota (swap)
        if (rotas.size() == 1) {
            Rota r = rotas.get(0);
            if (r.getClientes().size() < 2) return; // Precisa de pelo menos 2 clientes para swap

            int i1 = random.nextInt(r.getClientes().size());
            int i2;
            do {
                i2 = random.nextInt(r.getClientes().size());
            } while (i2 == i1);

            // Testar se a troca não quebra as janelas de tempo (complexo, mas importante)
            // Por simplicidade aqui, fazemos a troca e a penalidade no fitness cuida.
            Collections.swap(r.getClientes(), i1, i2);
            return;
        }

        // Caso contrário (duas ou mais rotas), decide entre intra-rota e inter-rota
        if (random.nextDouble() < 0.7) { // 70% de chance de mutação intra-rota
            int idxRota = random.nextInt(rotas.size());
            Rota r = rotas.get(idxRota);
            if (r.getClientes().size() < 2) return; // Não há o que mutar se só tem 0 ou 1 cliente

            int i1 = random.nextInt(r.getClientes().size());
            int i2;
            do {
                i2 = random.nextInt(r.getClientes().size());
            } while (i2 == i1);

            Collections.swap(r.getClientes(), i1, i2);

        } else { // 30% de chance de mutação inter-rota (troca entre rotas)
            int idx1 = random.nextInt(rotas.size());
            int idx2;
            do {
                idx2 = random.nextInt(rotas.size());
            } while (idx2 == idx1);

            Rota r1 = rotas.get(idx1);
            Rota r2 = rotas.get(idx2);

            if (r1.getClientes().isEmpty() || r2.getClientes().isEmpty()) return;

            int i1 = random.nextInt(r1.getClientes().size());
            int i2 = random.nextInt(r2.getClientes().size());

            Cliente c1 = r1.getClientes().get(i1);
            Cliente c2 = r2.getClientes().get(i2);

            // Verificar se a troca é viável em termos de capacidade
            int demandaR1AposTroca = r1.calcularDemandaTotal() - c1.getDemanda() + c2.getDemanda();
            int demandaR2AposTroca = r2.calcularDemandaTotal() - c2.getDemanda() + c1.getDemanda();

            // Adicionalmente, verificar se a troca não viola as janelas de tempo
            // Esta é uma verificação mais complexa, pois envolve o novo cliente na rota.
            // Para ser exato, precisaríamos criar rotas temporárias e testar.
            // Por simplicidade, assumimos que o fitness penalizará depois.
            
            if (demandaR1AposTroca <= r1.getVeiculo().getCapacidade() &&
                demandaR2AposTroca <= r2.getVeiculo().getCapacidade()) {
                
                // Realiza a troca e verifica as janelas de tempo
                // Cuidado: Esta é uma operação in-place, pode ser arriscado se não clonar.
                // Uma forma mais segura seria:
                // List<Cliente> tempClientes1 = new ArrayList<>(r1.getClientes());
                // tempClientes1.set(i1, c2);
                // Rota tempRota1 = new Rota(r1.getVeiculo(), r1.getDeposito());
                // tempRota1.getClientes().addAll(tempClientes1);

                // ... e fazer o mesmo para r2. Depois, se ambos forem válidos, aplicar a mudança.
                
                // Para manter a simplicidade do seu código original, vamos aplicar a troca
                // e deixar a função fitness cuidar das penalidades se as janelas forem violadas.
                r1.getClientes().set(i1, c2);
                r2.getClientes().set(i2, c1);
            }
        }
    }
    
    public double fitness(Solucao solucao) {
        double custo = solucao.calcularCustoTotal();

        Set<Integer> clientesAtendidosIds = new HashSet<>();
        for (Rota rota : solucao.getRotas()) {
            for(Cliente c : rota.getClientes()) {
                clientesAtendidosIds.add(c.getId());
            }
        }
        if (clientesAtendidosIds.size() < clientes.size()) {
            custo += 1000000 * (clientes.size() - clientesAtendidosIds.size());
        }

        for (Rota rota : solucao.getRotas()) {
            if (rota.excedeCapacidade()) {
                custo += 5000 * (rota.calcularDemandaTotal() - rota.getVeiculo().getCapacidade());
            }
            if (!rota.respeitaJanelasDeTempo()) {
                // Penalidade por violação da janela de tempo pode ser proporcional ao atraso
                // ou um valor fixo se o objetivo é apenas respeitar ou não.
                custo += 2000;
            }
        }

        return custo;
    }

    private Solucao melhor(List<Solucao> populacao) {
        if (populacao.isEmpty()) return null;
        Solucao melhor = populacao.get(0);
        for (Solucao s : populacao) {
            if (fitness(s) < fitness(melhor)) {
                melhor = s;
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
        Rota nova = new Rota(original.getVeiculo(), original.getDeposito());
        nova.getClientes().addAll(original.getClientes());
        return nova;
    }

    public double fitnessSemPenalidade(Solucao solucao) {
        return solucao.calcularCustoTotal();
    }
}