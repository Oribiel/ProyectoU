package negocio;

import dtos.AlumnoLecturaDTO;
import dtos.AlumnoTablaDTO;
import dtos.GuardarAlumnoDTO;
import dtos.EditarAlumnoDTO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlumnoNegocio implements IAlumnoNegocio {

    private final String URL = "jdbc:mysql://localhost:3306/Escuela";
    private final String USUARIO = "root";
    private final String CONTRASEÑA = "1234";

    public AlumnoNegocio() {
        // Constructor
    }

    private Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
    }

    @Override
    public List<AlumnoTablaDTO> buscarAlumnosTabla(int limite, int pagina) throws NegocioException {
        List<AlumnoTablaDTO> alumnos = new ArrayList<>();
        try (Connection conexion = obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement("SELECT * FROM alumnos WHERE eliminado = 0 LIMIT ?, ?")) {
            consulta.setInt(1, (pagina - 1) * limite);
            consulta.setInt(2, limite);
            try (ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    int idAlumno = resultado.getInt("idAlumno");
                    String nombres = resultado.getString("nombres");
                    String apellidoPaterno = resultado.getString("apellidoPaterno");
                    String apellidoMaterno = resultado.getString("apellidoMaterno");
                    String estatus = resultado.getBoolean("activo") ? "Activo" : "Inactivo";
                    alumnos.add(new AlumnoTablaDTO(idAlumno, nombres, apellidoPaterno, apellidoMaterno, estatus));
                }
            }
        } catch (SQLException ex) {
            throw new NegocioException("Error al buscar alumnos en la tabla: " + ex.getMessage());
        }
        return alumnos;
    }

    @Override
    public AlumnoLecturaDTO obtenerPorId(int id) throws NegocioException {
        try (Connection conexion = obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement("SELECT * FROM alumnos WHERE idAlumno = ? AND eliminado = 0")) {
            consulta.setInt(1, id);
            try (ResultSet resultado = consulta.executeQuery()) {
                if (resultado.next()) {
                    String nombres = resultado.getString("nombres");
                    String apellidoPaterno = resultado.getString("apellidoPaterno");
                    String apellidoMaterno = resultado.getString("apellidoMaterno");
                    boolean activo = resultado.getBoolean("activo");
                    return new AlumnoLecturaDTO(id, nombres, apellidoPaterno, apellidoMaterno, activo);
                } else {
                    throw new NegocioException("Alumno no encontrado");
                }
            }
        } catch (SQLException ex) {
            throw new NegocioException("Error al obtener alumno por ID: " + ex.getMessage());
        }
    }

    @Override
    public void insertar(GuardarAlumnoDTO nuevoAlumno) throws NegocioException {
        try (Connection conexion = obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement("INSERT INTO alumnos (nombres, apellidoPaterno, apellidoMaterno) VALUES (?, ?, ?)")) {
            consulta.setString(1, nuevoAlumno.getNombres());
            consulta.setString(2, nuevoAlumno.getApellidoPaterno());
            consulta.setString(3, nuevoAlumno.getApellidoMaterno());
            consulta.executeUpdate();
        } catch (SQLException ex) {
            throw new NegocioException("Error al insertar nuevo alumno: " + ex.getMessage());
        }
    }

    @Override
    public void editar(EditarAlumnoDTO alumno) throws NegocioException {
        try (Connection conexion = obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement("UPDATE alumnos SET nombres = ?, apellidoPaterno = ?, apellidoMaterno = ?, activo = ? WHERE idAlumno = ?")) {
            consulta.setString(1, alumno.getNombres());
            consulta.setString(2, alumno.getApellidoPaterno());
            consulta.setString(3, alumno.getApellidoMaterno());
            consulta.setBoolean(4, alumno.isActivo());
            consulta.setInt(5, alumno.getIdAlumno());
            int filasAfectadas = consulta.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NegocioException("Alumno no encontrado");
            }
        } catch (SQLException ex) {
            throw new NegocioException("Error al editar alumno: " + ex.getMessage());
        }
    }
}
