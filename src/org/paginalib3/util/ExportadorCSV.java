package org.paginalib3.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public final class ExportadorCSV {
    private ExportadorCSV() {}

    public static void guardar(File archivo, List<String[]> filas) throws IOException {
        StringBuilder sb = new StringBuilder("\uFEFF");
        for (String[] fila : filas) {
            for (int i = 0; i < fila.length; i++) {
                if (i > 0) sb.append(';');
                sb.append(escapar(fila[i]));
            }
            sb.append(System.lineSeparator());
        }
        Files.writeString(archivo.toPath(), sb.toString(), StandardCharsets.UTF_8);
    }

    private static String escapar(String valor) {
        String s = valor == null ? "" : valor;
        if (s.contains(";") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
