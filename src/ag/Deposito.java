package ag;

//Importante: certifique-se de que Cliente já está no mesmo pacote ou importe-o
public class Deposito extends Cliente {
 // O depósito não tem demanda de pacotes para serem entregues nem janelas de tempo específicas de cliente
 // Definimos demanda como 0 e janelas de tempo amplas (ex: 0 a 24) para que não restrinjam o cálculo do tempo da rota.
 public Deposito(int id, String nome, double x, double y) {
     super(id, nome, x, y, 0, 0, 24); // Exemplo: 0 demanda, 0-24h janela (ou valores que façam sentido para seu contexto)
 }

 @Override
 public String toString() {
     return "Depósito: " + getNome();
 }
}