import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StrategiesTest {
    @Test
    fun testOblong() {
        val o = Oblong(3.0, 2.0, 1.0)
        assertEquals(3.0, o.strategy(1, 1))
        assertEquals(6.0, o.strategy(1, 2))
    }

    @Test
    fun testOblong2() {
        val o = Oblong(3.0, 0.5, 1.0)
        val aims = DoubleArray(4, { 1.0 })
        val visits = IntArray(5, {1})
        o.setAim(aims)
        val directions = IntArray(5, {0})
        for (i in 0 until 10000) {
            val dir = o.getDir(visits)
            directions[dir]++
        }

        assertTrue(1500 < directions[1] && directions[1] < 2500)
        assertTrue(1500 < directions[3] && directions[3] < 2500)
        assertTrue(2500 < directions[2] && directions[2] < 3500)
        assertTrue(2500 < directions[4] && directions[4] < 3500)
    }
}