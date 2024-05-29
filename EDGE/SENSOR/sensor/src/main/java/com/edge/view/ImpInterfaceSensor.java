package com.edge.view;

public class ImpInterfaceSensor implements InterfaceSensor {

    @Override
    public void inicio() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        imprimir("Inicio Exitoso del Programa.");
        System.out.println();
        System.out.println();
    }

    @Override
    public void imprimir(String texto) {
        int anchoMaximo = 64;
        int inicio = 0;
        int fin = Math.min(anchoMaximo, texto.length());
        String bordeSuperiorInferior = "   +" + "-".repeat(anchoMaximo) + "+";
        while (inicio < texto.length()) {
            String linea = texto.substring(inicio, fin).trim();
            if (!linea.isEmpty()) {
                int paddingIzquierda = (anchoMaximo - linea.length()) / 2;
                int paddingDerecha = anchoMaximo - linea.length() - paddingIzquierda;
                System.out.println("   |" + " ".repeat(paddingIzquierda) + linea + " ".repeat(paddingDerecha) + "|");
            }
            inicio = fin;
            fin = Math.min(inicio + anchoMaximo, texto.length());
        }
    }

    @Override
    public void finalizar() {
        System.out.println();
        System.out.println();
        String[] banner = {
                "   +-----------------------------------------------------------------------+   ",
                "   |                                                                       |   ",
                "   |                             FIN DEL PROGRAMA                          |   ",
                "   |                                                                       |   ",
                "   +-----------------------------------------------------------------------+   "
        };

        for (String line : banner) {
            System.out.println(line);
        }
        System.out.println();
        System.out.println();
    }
}
