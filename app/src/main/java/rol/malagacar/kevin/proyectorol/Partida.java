package rol.malagacar.kevin.proyectorol;

public class Partida {
    private String nombrePartida;
    private int numeroJugadoresActuales;

    public Partida() {
    }

    public Partida(String nombrePartida, int numeroJugadoresActuales) {

        this.nombrePartida = nombrePartida;
        this.numeroJugadoresActuales = numeroJugadoresActuales;
    }

    public String getNombrePartida() {
        return nombrePartida;
    }

    public void setNombrePartida(String nombrePartida) {
        this.nombrePartida = nombrePartida;
    }

    public int getNumeroJugadoresActuales() {
        return numeroJugadoresActuales;
    }

    public void setNumeroJugadoresActuales(int numeroJugadoresActuales) {
        this.numeroJugadoresActuales = numeroJugadoresActuales;
    }
}
