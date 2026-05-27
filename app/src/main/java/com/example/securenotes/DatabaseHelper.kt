package com.example.securenotes
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SecureNotesDB.db"
        private const val DATABASE_VERSION = 4

        //Tabla Usuario
        const val TABLE_USUARIO = "Usuario"
        const val COLUMN_USER_ID = "IdUsuario"
        const val COLUMN_USER_NOMBRE = "NombreUsuario"
        const val COLUMN_USER_PASSWORD = "Contrasenia"

        //Tabla Contrasenia
        const val TABLE_CONTRASENIAS = "Contrasenia"
        const val COLUMN_PASS_ID = "IdContrasenia"
        const val COLUMN_PASS_TITULO = "Titulo"
        const val COLUMN_PASS_USER_CUENTA = "UsuarioCuenta"
        const val COLUMN_PASS_VALOR_CUENTA = "ContraseniaCuenta"
        const val COLUMN_PASS_COMENTARIOS = "Comentarios"
        const val COLUMN_PASS_INDIVIDUAL = "ContraseniaIndividual"

        //Tabla Nota
        const val TABLE_NOTAS = "Nota"
        const val COLUMN_NOTA_ID = "IdNota"
        const val COLUMN_NOTA_TITULO = "Titulo"
        const val COLUMN_NOTA_CONTENIDO = "Contenido"
        const val COLUMN_NOTA_COMENTARIOS = "Comentarios"
        const val COLUMN_NOTA_INDIVIDUAL = "ContraseniaIndividual"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableUsuario = ("CREATE TABLE $TABLE_USUARIO (" +
                "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USER_NOMBRE TEXT NOT NULL, " +
                "$COLUMN_USER_PASSWORD TEXT NOT NULL)")

        val createTableContrasenia = ("CREATE TABLE $TABLE_CONTRASENIAS (" +
                "$COLUMN_PASS_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "IdUsuario INTEGER DEFAULT 1, " + // <-- La llave foránea
                "$COLUMN_PASS_TITULO TEXT NOT NULL, " +
                "$COLUMN_PASS_USER_CUENTA TEXT NOT NULL, " +
                "$COLUMN_PASS_VALOR_CUENTA TEXT NOT NULL, " +
                "$COLUMN_PASS_COMENTARIOS TEXT, " +
                "$COLUMN_PASS_INDIVIDUAL TEXT, " +
                "FOREIGN KEY(IdUsuario) REFERENCES $TABLE_USUARIO($COLUMN_USER_ID))")

        val createTableNotas = ("CREATE TABLE $TABLE_NOTAS (" +
                "$COLUMN_NOTA_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "IdUsuario INTEGER DEFAULT 1, " + // <-- La llave foránea
                "$COLUMN_NOTA_TITULO TEXT NOT NULL, " +
                "$COLUMN_NOTA_CONTENIDO TEXT NOT NULL, " +
                "$COLUMN_NOTA_COMENTARIOS TEXT, " +
                "$COLUMN_NOTA_INDIVIDUAL TEXT, " +
                "FOREIGN KEY(IdUsuario) REFERENCES $TABLE_USUARIO($COLUMN_USER_ID))")

        db.execSQL(createTableUsuario)
        db.execSQL("INSERT INTO $TABLE_USUARIO ($COLUMN_USER_NOMBRE, $COLUMN_USER_PASSWORD) VALUES ('admin', '1234')")

        db.execSQL(createTableContrasenia)
        db.execSQL(createTableNotas)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTRASENIAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTAS")
        onCreate(db)
    }
}