import java.lang.Exception
import kotlin.random.Random

interface Strategy {
    fun getDir(nodeAndEdges: IntArray): Int
}

interface StrategyWithAim : Strategy {
    fun setAim(ds: DoubleArray) {}
}

class NoHistory : StrategyWithAim {
    override fun getDir(nodeAndEdges: IntArray): Int {
        return (1..4).random()
    }
}

class LinearWeight : Strategy {
    override fun getDir(nodeAndEdges: IntArray): Int {
        val edgesSum: Int = nodeAndEdges.slice(1..4).sum() + 4 // weight = 1 + edge
        var rdir = (1..edgesSum).random()
        for (dir in (1..4)) {
            if (rdir <= 1 + nodeAndEdges[dir])
                return dir
            rdir -= (1 + nodeAndEdges[dir])
        }
        throw Exception("finished with dirs")
    }
}

fun dirFromWeights(weights: List<Double>): Int {
    val weightsSum = weights.fold(0.0) { s, v -> s + v }
    var rdir = Random.nextDouble(weightsSum)
    for (dir in (1..4)) {
        if (rdir < weights[dir - 1])
            return dir
        rdir -= weights[dir - 1]
    }
    throw Exception("finished with directions")
}

open class OneOrA(var a: Double) : Strategy, StrategyWithAim {
    var aim_: DoubleArray = DoubleArray(4)

    override fun getDir(nodeAndEdges: IntArray): Int {
        val weights: List<Double> = nodeAndEdges.slice(1..4).mapIndexed { d, v -> strategy(v, d) + aim_[d] }
        return dirFromWeights(weights)
    }

    open fun strategy(v: Int, dir: Int) = if (v == 0) 1.0 else a

    override fun setAim(ds: DoubleArray) {
        aim_ = ds
    }
}

class Oblong(a: Double, val rx: Double, val ry: Double) : OneOrA(a) {
    override fun strategy(v: Int, dir: Int): Double {
        return (if (v == 0) 1.0 else a) * (if (dir % 2 == 0) rx else ry)
    }
}
