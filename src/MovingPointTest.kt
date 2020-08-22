import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MovingPointTest {
    @Test
    fun testPolygonal() {
        val p = Polygonal(
            listOf(
                TimePoint(0.0, Pair(0.0, 1.0)),
                TimePoint(0.5, Pair(1.0, 1.0)),
                TimePoint(1.0, Pair(1.0, 0.0))
            )
        )
        assertEquals(Pair(0.0, 1.0), p.pos(0.0))
        assertEquals(Pair(0.5, 1.0), p.pos(0.25))
        assertEquals(Pair(1.0, 1.0), p.pos(0.5))
        assertEquals(Pair(1.0, 0.5), p.pos(0.75))
        assertEquals(Pair(1.0, 0.0), p.pos(1.0))
    }

    @Test
    fun testComposition() {
        val c = Composition(
            listOf(0.25, 0.75),
            listOf(LinearTrajectory(Pair(0.0, 1.0), Pair(1.0, 1.0)),
                LinearTrajectory(Pair(1.0, 1.0), Pair(2.0, 2.0)),
                LinearTrajectory(Pair(2.0, 2.0), Pair(0.0, 0.0))
            )
        )
        assertEquals(Pair(0.0, 1.0), c.pos(0.0))
        assertEquals(Pair(1.0, 1.0), c.pos(0.25))
        assertEquals(Pair(1.5, 1.5), c.pos(0.50))
        assertEquals(Pair(2.0, 2.0), c.pos(0.75))
        assertEquals(Pair(1.0, 1.0), c.pos(0.875))
        assertEquals(Pair(0.0, 0.0), c.pos(1.0))
    }
}