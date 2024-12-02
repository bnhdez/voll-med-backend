package med.voll.api.direccion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosDireccion(
        @NotBlank
        String calle,
        @NotNull
        Integer numero,
        @NotBlank
        String complemento,
        @NotBlank
        String distrito,
        @NotBlank
        String ciudad
) {
}
