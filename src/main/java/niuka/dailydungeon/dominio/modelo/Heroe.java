package niuka.dailydungeon.dominio.modelo;

import niuka.dailydungeon.dominio.reglas.ReglasJuego;

import java.util.EnumMap;
import java.util.Map;

public class Heroe {

    private final int id;
    private final int idUsuario;
    private final Clase clase;
    private final String nombre;
    private final int nivel;
    private final int xp;
    private final int hpActual;
    private final Map<Atributo, Integer> atributos;

    public Heroe(int id, int idUsuario, Clase clase, String nombre,
                 int nivel, int xp, int hpActual, Map<Atributo, Integer> atributos) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.clase = clase;
        this.nombre = nombre;
        this.nivel = nivel;
        this.xp = xp;
        this.hpActual = hpActual;
        this.atributos = new EnumMap<>(atributos);
    }

    public static Heroe nuevo(int idUsuario, String nombre, Clase clase) {
        Map<Atributo, Integer> atributosIniciales = clase.valoresIniciales();
        int vidaInicial = ReglasJuego.vidaMaxima(atributosIniciales.get(Atributo.VITALIDAD));
        return new Heroe(0, idUsuario, clase, nombre, ReglasJuego.NIVEL_INICIAL, 0, vidaInicial, atributosIniciales);
    }

    public Heroe conId(int idAsignado) {
        return new Heroe(idAsignado, idUsuario, clase, nombre, nivel, xp, hpActual, atributos);
    }

    public int vidaMaxima() {
        return ReglasJuego.vidaMaxima(valorDe(Atributo.VITALIDAD));
    }

    public int xpParaSubirDeNivel() {
        return ReglasJuego.xpParaSubir(nivel);
    }

    public int valorDe(Atributo atributo) {
        return atributos.get(atributo);
    }

    public int id() {
        return id;
    }

    public int idUsuario() {
        return idUsuario;
    }

    public Clase clase() {
        return clase;
    }

    public String nombre() {
        return nombre;
    }

    public int nivel() {
        return nivel;
    }

    public int xp() {
        return xp;
    }

    public int hpActual() {
        return hpActual;
    }
}
