package com.example.securenotes
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SecureNotesDB.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NOTAS = "Nota"
        const val COLUMN_ID = "IdNota"
        const val COLUMN_TITULO = "Titulo"
        const val COLUMN_CONTENIDO = "Contenido"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableNotas = ("CREATE TABLE " + TABLE_NOTAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITULO + " TEXT NOT NULL, "
                + COLUMN_CONTENIDO + " TEXT NOT NULL)")

        db.execSQL(createTableNotas)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTAS)
        onCreate(db)
    }
}