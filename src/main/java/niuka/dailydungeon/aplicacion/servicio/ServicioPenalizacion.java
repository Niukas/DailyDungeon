package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioHeroe;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioMision;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioPenalizacion;
import niuka.dailydungeon.dominio.modelo.Atributo;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Penalizacion;
import niuka.dailydungeon.dominio.reglas.ReglasJuego;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ServicioPenalizacion {

    private final RepositorioHeroe repositorioHeroe;
    private final RepositorioMision repositorioMision;
    private final RepositorioPenalizacion repositorioPenalizacion;

    public ServicioPenalizacion(RepositorioHeroe repositorioHeroe, RepositorioMision repositorioMision,
                                RepositorioPenalizacion repositorioPenalizacion) {
        this.repositorioHeroe = repositorioHeroe;
        this.repositorioMision = repositorioMision;
        this.repositorioPenalizacion = repositorioPenalizacion;
    }

    // Se llama al iniciar sesión y al abrir un tablero. Devuelve vacío si no hubo nada que cobrar.
    public Optional<Penalizacion> revisarVencimientos(int idUsuario) {
        Optional<Heroe> encontrado = repositorioHeroe.buscarPorUsuario(idUsuario);
        if (encontrado.isEmpty()) {
            return Optional.empty();
        }
        Heroe heroe = encontrado.get();

        LocalDate hoy = LocalDate.now();
        // Traigo todas las misiones del héroe y me quedo solo con las vencidas.
        List<Mision> vencidas = repositorioMision.listarPorHeroe(heroe.id()).stream()
                .filter(mision -> mision.estaVencida(hoy))
                .toList();
        if (vencidas.isEmpty()) {
            return Optional.empty();
        }

        int danioTotal = 0;
        boolean cayo = false;
        for (Mision mision : vencidas) {
            // El daño lo calculo en cada vuelta porque si el héroe cae, su Resistencia baja.
            int danio = ReglasJuego.danioPorVencimiento(heroe.valorDe(Atributo.RESISTENCIA));
            danioTotal += danio;
            if (heroe.recibirDanio(danio)) {
                cayo = true;
            }
            mision.marcarPenalizada();
        }

        repositorioPenalizacion.guardar(heroe, vencidas);
        return Optional.of(new Penalizacion(vencidas.size(), danioTotal, cayo));
    }
}
