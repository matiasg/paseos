import java.lang.Exception
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.random.Random.Default.nextDouble

open class GraphInR2(
    val size: Int,
    open val strategy: Strategy,
    startAt: Pair<Int, Int>? = null
) {
    val graph = Array(size, { Array(size, { IntArray(5) }) })
    var pos: Pair<Int, Int> = startAt ?: Pair((0..size - 1).random(), (0..size - 1).random())
    var totalMass: Int = 0
    var center: Pair<Double, Double> = Pair(pos.first.toDouble(), pos.second.toDouble())

    fun dist1(s: Pair<Int, Int>, t: Pair<Int, Int>): Int {
        return abs(s.first - t.first) + abs(s.second - t.second)
    }
    fun getNode(x: Int, y: Int): Int {
        return graph[x][y][0]
    }

    fun getNode(xy: Pair<Int, Int>): Int {
        return getNode(xy.first, xy.second)
    }

    fun maxNode(): Int {
        val maxByRow: List<Int> = graph.map { row ->
            row.map { pos ->
                pos[0]
            }.max()
        }.filterNotNull()
        return maxByRow.max() ?: 0
    }

    fun maxEdge(): Int {
        val maxByRow: List<Int> = graph.map { row ->
            row.map { pos ->
                max(pos[1], max(pos[2], max(pos[3], pos[4])))
            }.max()
        }.filterNotNull()
        return maxByRow.max() ?: 0
    }

    fun getEdge(x: Int, y: Int, dir: Int): Int {
        return graph[x][y][dir]
    }

    fun getEdge(xy: Pair<Int,Int>, dir: Int): Int {
        return graph[xy.first][xy.second][dir]
    }

    fun diff(s: Pair<Int, Int>, t: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(s.first - t.first, s.second - t.second)
    }

    fun add(s: Pair<Int, Int>, t: Pair<Int, Int>) {
        add(s, t, 1)
    }

    fun add(s: Pair<Int, Int>, t: Pair<Int, Int>, qty: Int) {
        graph[t.first][t.second][0] += qty
        graph[s.first][s.second][dir(s, t)] += qty
        graph[t.first][t.second][dir(t, s)] += qty

        val centerx = (center.first * totalMass + t.first * qty) / (totalMass + qty)
        val centery = (center.second * totalMass + t.second * qty) / (totalMass + qty)
        center = Pair(centerx, centery)
        totalMass += qty
    }

    fun dir(s: Pair<Int, Int>, t: Pair<Int, Int>): Int {
        assert(dist1(s, t) == 1)
        return when (diff(t, s)) {
            Pair(1, 0) -> 1
            Pair(0, 1) -> 2
            Pair(-1, 0) -> 3
            Pair(0, -1) -> 4
            else -> 0  // this can't happen
        }
    }

    fun getEdge(s: Pair<Int, Int>, t: Pair<Int, Int>): Int {
        assert(dist1(s, t) == 1)
        return getEdge(s, dir(s, t))
    }

    fun moved(s: Pair<Int, Int>, dir: Int): Pair<Int, Int> {
        return when (dir) {
            1 -> Pair(s.first + 1, s.second)
            2 -> Pair(s.first, s.second + 1)
            3 -> Pair(s.first - 1, s.second)
            4 -> Pair(s.first, s.second - 1)
            else -> throw Exception("direction must be in [1..4]")
        }
    }

    fun inside(pos: Pair<Int, Int>): Boolean {
        return (0 <= pos.first) and (pos.first < size) and (0 <= pos.second) and (pos.second < size)
    }

    fun randomDir(): Int {
        var dir = strategy.getDir(graph[pos.first][pos.second])
        var newPos = moved(pos, dir)
        while (!inside(newPos)) {
            dir = strategy.getDir(graph[pos.first][pos.second])
            newPos = moved(pos, dir)
        }
        return dir
    }

    open fun move() {
        val dir = randomDir()
        val newPos = moved(pos, dir)
        add(pos, newPos)
        pos = newPos
    }

    fun show() {
        for (i in (0..size-1)) {
            for (j in (0..size-1)) {
                print("%4d".format(getNode(i, j)))
            }
            println()
        }
    }

    companion object {
        fun toDirWeights(dir: Pair<Double, Double>): DoubleArray {
            val ret = DoubleArray(4)
            if (dir.first > 0) ret[0] = dir.first
            else ret[2] = -dir.first

            if (dir.second > 0) ret[1] = dir.second
            else ret[3] = -dir.second

            return ret
        }

    }

}