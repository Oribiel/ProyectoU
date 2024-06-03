/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import dtos.AlumnoLecturaDTO;
import dtos.AlumnoTablaDTO;
import dtos.GuardarAlumnoDTO;
import dtos.EditarAlumnoDTO;
import java.util.List;

public interface IAlumnoNegocio {
    List<AlumnoTablaDTO> buscarAlumnosTabla(int limite, int pagina) throws NegocioException;
    AlumnoLecturaDTO obtenerPorId(int id) throws NegocioException;
    void insertar(GuardarAlumnoDTO alumno) throws NegocioException;
    void editar(EditarAlumnoDTO alumno) throws NegocioException;
}
