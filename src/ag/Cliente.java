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
    private double x; 
    private double y; 
    private int demanda; 
    private int janelaInicio;
    private int janelaFim;    
}