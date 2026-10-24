package niuka.dailydungeon.infraestructura.repositorio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioClase;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioHeroe;
import niuka.dailydungeon.dominio.modelo.Atributo;
import niuka.dailydungeon.dominio.modelo.Clase;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.infraestructura.bd.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class RepositorioHeroeJDBC implements RepositorioHeroe {

    private static final String ERROR_BD = "Error al acceder a la tabla heroe";

    private final ConexionBD conexionBD;
    private final RepositorioClase repositorioClase;

    public RepositorioHeroeJDBC(ConexionBD conexionBD, RepositorioClase repositorioClase) {
        this.conexionBD = conexionBD;
        this.repositorioClase = repositorioClase;
    }

    @Override
    public Optional<Heroe> buscarPorUsuario(int idUsuario) {
        String sql = """
                SELECT id_heroe, id_clase, nombre, nivel, xp, hp_actual
                FROM heroe
                WHERE id_usuario = ? AND eliminado = FALSE
                """;
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, idUsuario);
            try (ResultSet resultado = consulta.executeQuery()) {
                if (!resultado.next()) {
                    return Optional.empty();
                }
                // El héroe está repartido en varias tablas: acá se junta todo
                // (sus datos, su clase y sus atributos).
                int idHeroe = resultado.getInt("id_heroe");
                return Optional.of(new Heroe(
                        idHeroe,
                        idUsuario,
                        buscarClase(resultado.getInt("id_clase")),
                        resultado.getString("nombre"),
                        resultado.getInt("nivel"),
                        resultado.getInt("xp"),
                        resultado.getInt("hp_actual"),
                        leerAtributos(conexion, idHeroe)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    @Override
    public boolean existeParaUsuario(int idUsuario) {
        String sql = "SELECT 1 FROM heroe WHERE id_usuario = ? AND eliminado = FALSE";
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, idUsuario);
            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    @Override
    public Heroe guardar(Heroe heroe) {
        try (Connection conexion = conexionBD.obtenerConexion()) {
            // Uso una transacción: si algo falla en el medio, se deshace todo
            // y no queda un héroe guardado sin sus atributos.
            conexion.setAutoCommit(false);
            try {
                int idHeroe = insertarHeroe(conexion, heroe);
                insertarAtributos(conexion, idHeroe, heroe);
                conexion.commit();
                return heroe.conId(idHeroe);
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    // La usan RepositorioRecompensaJDBC y RepositorioPenalizacionJDBC. Recibe la conexión
    // de afuera para que el cambio del héroe quede dentro de la transacción de ellos.
    static void actualizarHeroe(Connection conexion, Heroe heroe) throws SQLException {
        String sqlHeroe = "UPDATE heroe SET nivel = ?, xp = ?, hp_actual = ? WHERE id_heroe = ?";
        try (PreparedStatement actualizacion = conexion.prepareStatement(sqlHeroe)) {
            actualizacion.setInt(1, heroe.nivel());
            actualizacion.setInt(2, heroe.xp());
            actualizacion.setInt(3, heroe.hpActual());
            actualizacion.setInt(4, heroe.id());
            actualizacion.executeUpdate();
        }
        String sqlAtributo = "UPDATE heroe_atributo SET valor = ? WHERE id_heroe = ? AND id_atributo = ?";
        try (PreparedStatement actualizacion = conexion.prepareStatement(sqlAtributo)) {
            for (Atributo atributo : Atributo.values()) {
                actualizacion.setInt(1, heroe.valorDe(atributo));
                actualizacion.setInt(2, heroe.id());
                actualizacion.setInt(3, atributo.id());
                actualizacion.executeUpdate();
            }
        }
    }

    private int insertarHeroe(Connection conexion, Heroe heroe) throws SQLException {
        String sql = "INSERT INTO heroe (id_usuario, id_clase, nombre, nivel, xp, hp_actual) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insercion = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            insercion.setInt(1, heroe.idUsuario());
            insercion.setInt(2, heroe.clase().id());
            insercion.setString(3, heroe.nombre());
            insercion.setInt(4, heroe.nivel());
            insercion.setInt(5, heroe.xp());
            insercion.setInt(6, heroe.hpActual());
            insercion.executeUpdate();
            try (ResultSet claves = insercion.getGeneratedKeys()) {
                claves.next();
                return claves.getInt(1);
            }
        }
    }

    private Clase buscarClase(int idClase) {
        return repositorioClase.listarTodas().stream()
                .filter(clase -> clase.id() == idClase)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Clase desconocida: " + idClase));
    }

    private Map<Atributo, Integer> leerAtributos(Connection conexion, int idHeroe) throws SQLException {
        String sql = "SELECT id_atributo, valor FROM heroe_atributo WHERE id_heroe = ?";
        Map<Atributo, Integer> atributos = new EnumMap<>(Atributo.class);
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, idHeroe);
            try (ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    atributos.put(Atributo.desdeId(resultado.getInt("id_atributo")), resultado.getInt("valor"));
                }
            }
        }
        return atributos;
    }

    private void insertarAtributos(Connection conexion, int idHeroe, Heroe heroe) throws SQLException {
        String sql = "INSERT INTO heroe_atributo (id_heroe, id_atributo, valor) VALUES (?, ?, ?)";
        try (PreparedStatement insercion = conexion.prepareStatement(sql)) {
            for (Atributo atributo : Atributo.values()) {
                insercion.setInt(1, idHeroe);
                insercion.setInt(2, atributo.id());
                insercion.setInt(3, heroe.valorDe(atributo));
                insercion.executeUpdate();
            }
        }
    }
}
