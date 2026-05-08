package ttps.spring.models.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Date;

public record RegistroUsuarioRequest(
        @Schema(description = "Apodo del usuario para login", example = "Luis1980", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @NotBlank
        String nombreUsuario,

        @Schema(description = "Nombre del usuario", example = "Luis", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @NotBlank
        String nombre,

        @Schema(description = "Apellido del usuario", example = "Gomez", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @NotBlank
        String apellido,

        @Schema(description = "Correo electrónico del usuario", example = "alan123@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @Email @NotBlank
        String email,

        @Schema(description = "Contraseña del usuario", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @NotBlank
        String password,

        @Schema(description = "Telefono del usuario", example = "3512345678", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @NotBlank
        String telefono,

        @Schema(description = "Genero del usuario", example = "Masculino", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        String genero,

        @Schema(description = "Fecha de nacimiento del usuario", example = "8/5/2026", requiredMode = Schema.RequiredMode.REQUIRED)
        Date fechaNacimiento,

        @Schema(description = "Provincia del usuario", example = "Cordoba", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @NotBlank
        String provincia,

        @Schema(description = "Municipio del usuario", example = "Cordoba Capital", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @NotBlank
        String municipio,

        @Schema(description = "Departamento del usuario", example = "Capital", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @NotBlank
        String departamento
) {}
