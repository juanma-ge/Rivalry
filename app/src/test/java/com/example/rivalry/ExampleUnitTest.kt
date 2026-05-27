package com.example.rivalry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class LogicaRivalryTest {

    @Test
    fun `Un equipo gana 3 puntos por victoria y 1 por empate`() {
        val golesMiEquipo = 3
        val golesRival = 1

        val puntos = if (golesMiEquipo > golesRival) 3 else if (golesMiEquipo == golesRival) 1 else 0

        assertEquals(3, puntos)
    }

    @Test
    fun `El correo electronico debe tener formato valido`() {
        val correoCorrecto = "usuario@rivalry.com"
        val correoIncorrecto = "usuariorivalry.com"

        assertTrue(correoCorrecto.contains("@") && correoCorrecto.contains("."))
        assertFalse(correoIncorrecto.contains("@"))
    }

    @Test
    fun `El limite de jugadores en futbol 11 debe permitir 22 personas`() {
        val maxJugadoresFutbol11 = 22
        val jugadoresApuntados = 21

        assertTrue("Aún debería haber hueco", jugadoresApuntados < maxJugadoresFutbol11)
    }

    @Test
    fun `Un equipo gana 0 puntos por derrota`() {
        val golesMiEquipo = 1
        val golesRival = 2
        val puntos = if (golesMiEquipo > golesRival) 3 else if (golesMiEquipo == golesRival) 1 else 0
        assertEquals(0, puntos)
    }

    @Test
    fun `El apodo del jugador debe tener al menos 3 caracteres`() {
        val apodo = "Dani"
        assertTrue("El apodo es muy corto", apodo.length >= 3)
    }

}