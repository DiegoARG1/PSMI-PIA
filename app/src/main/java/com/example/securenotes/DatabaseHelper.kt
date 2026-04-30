package com.example.securenotes // Asegúrate de que esto coincida con tu paquete real

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Heredamos de SQLiteOpenHelper para que Android sepa que esta clase maneja la BD
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // companion object es como decir "Variables estáticas" en C#
    companion object {
        private const val DATABASE_NAME = "SecureNotesDB.db" // El nombre de tu archivo físico
        private const val DATABASE_VERSION = 1 // Si después agregamos tablas, subimos este número

        // Definimos los nombres de la tabla y columnas como constantes para no equivocarnos al escribirlas
        const val TABLE_NOTAS = "Nota"
        const val COLUMN_ID = "IdNota"
        const val COLUMN_TITULO = "Titulo"
        const val COLUMN_CONTENIDO = "Contenido"
    }

    // Este método SOLO se ejecuta la primera vez que se instala la app. Aquí creamos las tablas.
    override fun onCreate(db: SQLiteDatabase) {
        // Tu script de SQL adaptado a SQLite (sin varchar, usando TEXT y AUTOINCREMENT)
        val createTableNotas = ("CREATE TABLE " + TABLE_NOTAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITULO + " TEXT NOT NULL, "
                + COLUMN_CONTENIDO + " TEXT NOT NULL)")

        db.execSQL(createTableNotas)
    }

    // Este método se ejecuta si cambias el DATABASE_VERSION (ej. si actualizaras la app en la PlayStore con nuevas tablas)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTAS)
        onCreate(db)
    }
}