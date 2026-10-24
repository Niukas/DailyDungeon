package niuka.dailydungeon.dominio.modelo;

import niuka.dailydungeon.dominio.reglas.ReglasJuego;

import java.util.EnumMap;
import java.util.Map;

public class Heroe {

    private final int id;
    private final int idUsuario;
    private final Clase clase;
    private final String nombre;
    private int nivel;
    private int xp;
    private int hpActual;
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

    // Así se crea un héroe recién hecho: nivel 1, sin XP, con los atributos
    // iniciales de su clase y la vida llena.
    public static Heroe nuevo(int idUsuario, String nombre, Clase clase) {
        Map<Atributo, Integer> atributosIniciales = clase.valoresIniciales();
        int vidaInicial = ReglasJuego.vidaMaxima(atributosIniciales.get(Atributo.VITALIDAD));
        return new Heroe(0, idUsuario, clase, nombre, ReglasJuego.NIVEL_INICIAL, 0, vidaInicial, atributosIniciales);
    }

    public Heroe conId(int idAsignado) {
        return new Heroe(idAsignado, idUsuario, clase, nombre, nivel, xp, hpActual, atributos);
    }

    public void ganarXp(int xpGanada) {
        xp += xpGanada;
        // Mientras la XP alcance para subir, sube de nivel.
        // Por eso si gana mucha XP de golpe puede subir varios niveles.
        while (xp >= xpParaSubirDeNivel()) {
            xp -= xpParaSubirDeNivel();
            subirDeNivel();
        }
    }

    // Es privado: la única forma de subir de nivel es ganando XP.
    private void subirDeNivel() {
        // Cada atributo suma lo que dice su clase (el Guerrero suma más Vitalidad, por ejemplo).
        nivel++;
        for (Atributo atributo : Atributo.values()) {
            atributos.put(atributo, valorDe(atributo) + clase.crecimientosPorNivel().get(atributo));
        }
        hpActual = vidaMaxima();
    }

    // Resta vida sin dejarla negativa. Devuelve true si llegó a 0 y el héroe cayó,
    // así el servicio sabe qué mensaje mostrar.
    public boolean recibirDanio(int danio) {
        hpActual = Math.max(0, hpActual - danio);
        if (hpActual == 0) {
            caer();
            return true;
        }
        return false;
    }

    // Pasa cuando la vida llega a 0. Es lo contrario de subir de nivel: baja un nivel (nunca del 1),
    // resta lo que habían crecido los atributos (sin bajar del valor inicial) y deja la XP en 0.
    private void caer() {
        if (nivel > ReglasJuego.NIVEL_INICIAL) {
            nivel--;
            for (Atributo atributo : Atributo.values()) {
                int valorReducido = valorDe(atributo) - clase.crecimientosPorNivel().get(atributo);
                atributos.put(atributo, Math.max(valorReducido, clase.valoresIniciales().get(atributo)));
            }
        }
        xp = 0;
        // La vida va al final porque la vida máxima depende de la Vitalidad, que acaba de bajar.
        hpActual = vidaMaxima() / 2;
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
