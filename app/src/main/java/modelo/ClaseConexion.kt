package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {
    fun cadenaConexion(): Connection? {
        try {
            val url = "jdbc:oracle:thin:@10.10.1.108:1521:xe"
            val usuario = "system"
            val contrasena = "ITR2024"

            val connection = DriverManager.getConnection(url, usuario, contrasena)
            return connection
        } catch (e: Exception) {
            println("Este es el error en la cadena de conexion: $e")
            return null
        }
    }
}