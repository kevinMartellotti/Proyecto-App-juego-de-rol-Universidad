package rol.malagacar.kevin.proyectorol;

public class Juego {
    private  String nombre ="";
    private  String descripcion ="";
    private  String jugadoresMaximos ="";

    public Juego() {
    }

    public Juego(String nombre, String descripcion, String jugadoresMaximos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.jugadoresMaximos = jugadoresMaximos;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getJugadoresMaximos() {
        return jugadoresMaximos;
    }

    public void setJugadoresMaximos(String jugadoresMaximos) {
        this.jugadoresMaximos = jugadoresMaximos;
    }
}
