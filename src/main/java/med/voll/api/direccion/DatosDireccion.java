package med.voll.api.direccion;

public record DatosDireccion(
        String calle,
        String distrito,
        String ciudad,
        Integer numero,
        String complemento
) {
}
