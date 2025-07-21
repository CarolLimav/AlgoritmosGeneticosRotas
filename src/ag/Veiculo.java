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
	 private String tipo; // Moto, Carro, Caminhão
	 private int capacidade; //capacidade maxima de carga (quantidade de pacotes)
	 private int tempoDisponivel; // tempo total disponível para realizar entregas (horas)
}