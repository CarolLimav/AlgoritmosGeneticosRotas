🚀 Visão Geral do Projeto
Este projeto aborda o desafio de otimização de rotas para a empresa de entregas urgentes "Entrega Rápida". Com o crescimento da demanda e a necessidade de eficiência, a empresa busca aprimorar o planejamento logístico de sua frota. O problema se torna complexo devido a múltiplos fatores: uma base de clientes geograficamente dispersa, capacidade e tempo disponível variados entre os diferentes tipos de veículos, e restrições de janelas de tempo para a entrega em clientes específicos.

Tradicionais algoritmos de roteirização mostram-se insuficientes para a complexidade e variedade de cenários. Por isso, este projeto explora a aplicação de Algoritmos Genéticos como uma solução robusta para encontrar planos de rota que não apenas minimizem a distância total percorrida, mas também atendam rigorosamente a todas as restrições de capacidade dos veículos e janelas de tempo dos clientes, garantindo a satisfação do cliente e a competitividade da "Entrega Rápida".

💡 O Problema de Negócio
A "Entrega Rápida", uma empresa de entregas urgentes em uma grande cidade, está em expansão. No entanto, o aumento da demanda e a frota diversificada (motos, carros, caminhões com capacidades distintas) impõem desafios complexos ao planejamento de rotas. Cada motorista tem um tempo limitado de trabalho, e muitos clientes exigem entregas dentro de janelas de tempo específicas. A ineficiência no roteamento resulta em custos operacionais elevados e pode comprometer a promessa de entregas rápidas. Este projeto visa resolver essas dores de forma automatizada e inteligente.

🧬 O Algoritmo Genético em Ação
O cerne da solução reside na implementação de um Algoritmo Genético (AG), uma técnica de otimização inspirada na evolução biológica. Ele opera da seguinte forma:

Cromossomo: Uma Solucao representa um plano completo de rotas para toda a frota de veículos.

Gene: Cada Rota individual (o caminho de um único veículo) é um gene dentro do cromossomo.

Alelo: Os Clientes em uma rota, e sua ordem, são os alelos.

População: Um conjunto de múltiplos planos de rota (Solucoes) que o AG evolui a cada ciclo.

Fitness: A "qualidade" de um plano de rota, calculada como a distância total percorrida, acrescida de penalidades pesadas por violações de capacidade, janelas de tempo ou clientes não atendidos. O AG busca minimizar este valor.

Geração: Cada ciclo de evolução, onde a população é avaliada e aprimorada.

Operadores Genéticos:

Seleção: Escolhe os "melhores" planos de rota (menor fitness) para serem "pais".

Crossover: Combina as características de dois planos "pais" (recombinando clientes e reconstruindo rotas) para gerar novos planos "filhos".

Mutação: Introduz pequenas alterações aleatórias nos planos (como troca de clientes ou ordem de visita) para explorar novas possibilidades.

Através desses princípios, o algoritmo "aprende" e converge para soluções cada vez mais eficientes e válidas.

🛠️ Tecnologias Utilizadas
Java: Linguagem de programação principal para o desenvolvimento do algoritmo.

📚 Informações da Disciplina
Instituição: IFBA – Instituto Federal de Educação, Ciência e Tecnologia da Bahia
Curso: Graduação Tecnológica em Análise e Desenvolvimento de Sistemas
Disciplina: INF032 – Inteligência Artificial
Professor: Marcelo Vera Cruz Diniz
