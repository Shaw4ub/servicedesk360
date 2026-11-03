package sv.edu.itca.servicedesk360.persistence;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class PruebaConexion {

    public static void main(String[] args) throws Exception {
        try (Connection cn = ConexionBD.abrir()) {
            DatabaseMetaData md = cn.getMetaData();
            System.out.println("Conexión válida: " + cn.isValid(3));
            System.out.println("DBMS: " + md.getDatabaseProductName());
            System.out.println("Versión: " + md.getDatabaseProductVersion());
            System.out.println("Driver: " + md.getDriverName());
        }
    }
}
