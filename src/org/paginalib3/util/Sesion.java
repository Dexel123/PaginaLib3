package org.paginalib3.util;

import org.paginalib3.model.Usuario;

public final class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {
    }

    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean haySesion() {
        return usuarioActual != null;
    }

    public static void cerrar() {
        usuarioActual = null;
    }
}
