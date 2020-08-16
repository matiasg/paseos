import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GraphInR2Test {

    @org.junit.jupiter.api.Test
    fun getEdge() {
        val g = GraphInR2(10, NoHistory(), startInTheMiddle = true)
        assertEquals(g.pos, Pair(5, 5))
        g.graph[5][5][1] = 3
        assertEquals(3, g.getEdge(5, 5, 1))
        assertEquals(3, g.getEdge(Pair(5, 5), 1))
        assertEquals(3, g.getEdge(Pair(5, 5), Pair(6, 5)))
    }

    @org.junit.jupiter.api.Test
    fun testMoved() {
        val g = GraphInR2(10, NoHistory())
        assertEquals(Pair(3, 5), g.moved(Pair(3, 4), 2))
        assertEquals(Pair(4, 4), g.moved(Pair(3, 4), 1))
        assertEquals(Pair(2, 4), g.moved(Pair(3, 4), 3))
        assertEquals(Pair(3, 3), g.moved(Pair(3, 4), 4))
    }

    @Test
    fun testAdd() {
        val g = GraphInR2(10, NoHistory())
        g.add(Pair(3, 3), Pair(4, 3))
        assertEquals(1, g.getNode(4, 3))
        assertEquals(1, g.getEdge(Pair(3, 3), Pair(4, 3)))
        assertEquals(1, g.getEdge(Pair(4, 3), Pair(3, 3)))
    }

    fun sumOfNodes(g: GraphInR2): Int {
        return g.graph.map { row -> row.map { col -> col[0]}.sum()}.sum()
    }

    @Test
    fun testAddForget() {
        val g = ForgetfulGraphInR2(10, 5, NoHistory(), startInTheMiddle = false)
        g.move()
        assertEquals(1, g.path.size)
        assertEquals(1, sumOfNodes(g))
        g.move()
        assertEquals(2, g.path.size)
        assertEquals(2, sumOfNodes(g))
        g.move()
        assertEquals(3, g.path.size)
        assertEquals(3, sumOfNodes(g))
        g.move()
        assertEquals(4, g.path.size)
        assertEquals(4, sumOfNodes(g))
        g.move()
        assertEquals(5, g.path.size)
        assertEquals(5, sumOfNodes(g))
        g.move()
        assertEquals(5, g.path.size)
        assertEquals(5, sumOfNodes(g))
    }

    @Test
    fun testCenter() {
        val g = ForgetfulGraphInR2(10, 5, NoHistory(), startInTheMiddle = true)
        assertEquals(Pair(5.0, 5.0), g.center)
        g.add(Pair(5, 5), Pair(5, 6))
        assertEquals(Pair(5.0, 6.0), g.center)
        g.add(Pair(5, 6), Pair(5, 7), 3)
        assertEquals(Pair(5.0, 6.75), g.center)
    }

}