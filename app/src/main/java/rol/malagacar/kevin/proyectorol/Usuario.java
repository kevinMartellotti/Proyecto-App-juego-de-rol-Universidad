package rol.malagacar.kevin.proyectorol;

public class Usuario {
    private String nombre;
    private String juegosFavoritos;
    private String formasDeContacto;
    private boolean baneado;

    public Usuario() {
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getJuegosFavoritos() {
        return juegosFavoritos;
    }

    public void setJuegosFavoritos(String juegosFavoritos) {
        this.juegosFavoritos = juegosFavoritos;
    }

    public String getFormasDeContacto() {
        return formasDeContacto;
    }

    public void setFormasDeContacto(String formasDeContacto) {
        this.formasDeContacto = formasDeContacto;
    }

    public boolean isBaneado() {
        return baneado;
    }

    public void setBaneado(boolean baneado) {
        this.baneado = baneado;
    }

    public Usuario(String nombre, String juegosFavoritos, String formasDeContacto, boolean baneado) {

        this.nombre = nombre;
        this.juegosFavoritos = juegosFavoritos;
        this.formasDeContacto = formasDeContacto;
        this.baneado = baneado;
    }
}
