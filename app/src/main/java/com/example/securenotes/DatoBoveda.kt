package com.example.securenotes

data class DatoBoveda(
    val id: Int,
    val titulo: String,
    val tipo: TipoDato
)

enum class TipoDato {
    NOTA, PASSWORD
}
