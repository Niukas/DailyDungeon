package niuka.dailydungeon.infraestructura.repositorio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioClase;
import niuka.dailydungeon.dominio.modelo.Atributo;
import niuka.dailydungeon.dominio.modelo.Clase;
import niuka.dailydungeon.infraestructura.bd.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RepositorioClaseJDBC implements RepositorioClase {

    private static final String ERROR_BD = "Error al leer las clases";

    private final ConexionBD conexionBD;

    // Una fila de la tabla clase_atributo.
    private record FilaAtributo(int idClase, Atributo atributo, int valorInicial, int crecimientoPorNivel) {
    }

    public RepositorioClaseJDBC(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public List<Clase> listarTodas() {
        try (Connection conexion = conexionBD.obtenerConexion()) {
            List<FilaAtributo> filas = leerAtributos(conexion);
            return leerClases(conexion, filas);
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    private List<FilaAtributo> leerAtributos(Connection conexion) throws SQLException {
        String sql = "SELECT id_clase, id_atributo, valor_inicial, crecimiento_por_nivel FROM clase_atributo";
        List<FilaAtributo> filas = new ArrayList<>();
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                filas.add(new FilaAtributo(
                        resultado.getInt("id_clase"),
                        Atributo.desdeId(resultado.getInt("id_atributo")),
                        resultado.getInt("valor_inicial"),
                        resultado.getInt("crecimiento_por_nivel")));
            }
        }
        return filas;
    }

    private List<Clase> leerClases(Connection conexion, List<FilaAtributo> filas) throws SQLException {
        String sql = "SELECT id_clase, nombre, descripcion FROM clase ORDER BY id_clase";
        List<Clase> clases = new ArrayList<>();
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                clases.add(armarClase(
                        resultado.getInt("id_clase"),
                        resultado.getString("nombre"),
                        resultado.getString("descripcion"),
                        filas));
            }
        }
        return clases;
    }

    private Clase armarClase(int idClase, String nombre, String descripcion, List<FilaAtributo> filas) {
        Map<Atributo, Integer> valoresIniciales = new EnumMap<>(Atributo.class);
        Map<Atributo, Integer> crecimientos = new EnumMap<>(Atributo.class);
        for (FilaAtributo fila : filas) {
            if (fila.idClase() == idClase) {
                valoresIniciales.put(fila.atributo(), fila.valorInicial());
                crecimientos.put(fila.atributo(), fila.crecimientoPorNivel());
            }
        }
        return new Clase(idClase, nombre, descripcion, valoresIniciales, crecimientos);
    }
}
