package ag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {
    private int id;
    private String nome;
    private double x; // coordenada X da localização do cliente
    private double y; // coordenada Y da localização do cliente
    private int demanda; // quantidade de pacotes a serem entregues
    private int janelaInicio; // horário mínimo de entrega (ex: 9 = 9h)
    private int janelaFim;    // horário máximo de entrega (ex: 12 = 12h)

    // construtor, getters e setters
}