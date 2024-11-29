package med.voll.api.direccion;

public record DatosDireccion(
        String calle,
        Integer numero,
        String complemento,
        String distrito,
        String ciudad
) {
}
