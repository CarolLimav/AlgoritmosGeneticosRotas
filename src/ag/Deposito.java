package ag;


public class Deposito extends Cliente {
 public Deposito(int id, String nome, double x, double y) {
     super(id, nome, x, y, 0, 0, 24); 
 }

 @Override
 public String toString() {
     return "Depósito: " + getNome();
 }
}