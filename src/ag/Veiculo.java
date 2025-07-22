package ag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Veiculo {
	 private int id;
	 private String tipo; 
	 private int capacidade; 
	 private int tempoDisponivel; 
}