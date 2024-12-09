package med.voll.api.domain.consulta.validaciones;

import med.voll.api.domain.ValidacionException;
import med.voll.api.domain.consulta.DatosReservaConsulta;
import med.voll.api.domain.paciente.PacienteRepository;

public class ValidadorPacienteActivo {

    private PacienteRepository repository;

    public void validar(DatosReservaConsulta datos){
        var pacienteEstadoActivo = repository.findActivoById(datos.idPaciente());
        if (!pacienteEstadoActivo){
            throw new ValidacionException("Consulta no puede ser reservada con paciente excluido.");
        }
    }

}
