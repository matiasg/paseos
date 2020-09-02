import kotlin.math.absoluteValue
import kotlin.math.pow


interface FilmedObject {
    fun step()
    fun update(t: Double)
    fun getGraphs(): Map<Int, ForgetfulGraphInR2>
    fun getColors(): Map<Int, Pair<Array<Int>, Array<Int>>>
}

class Edge(val node1: Int, val node2: Int, val k: Double)


open class FilmedObjectWithChangingG() : Actoress() {
    val Gperiods = 2.0

    override fun updateG(t: Double) {
        G = fixedG * (Math.sin(2 * Math.PI * t * Gperiods) + 0.8)
    }

}

open class Actoress() : FilmedObject {

    val theActors = mutableMapOf<Int, ForgetfulGraphInR2>()
    val movingPoints: MutableMap<Int, MovingPoint> = mutableMapOf()
    val actorsToActors: MutableList<Edge> = mutableListOf()
    val actorsToPoints: MutableList<Edge> = mutableListOf()
    val theColors: MutableMap<Int, Pair<Array<Int>, Array<Int>>> = mutableMapOf()

    // TODO: reify this
    val fixedG: Double = 2e2
    var G: Double = fixedG
    val minDist: Double = 6.0

    fun addGraph(graph: ForgetfulGraphInR2, col1: Array<Int>, col2: Array<Int>): Int {
        val id = theActors.size
        theActors.put(id, graph)
        theColors.put(id, Pair(col1, col2))
        return id
    }

    fun addMovingPoint(point: MovingPoint): Int {
        val id = movingPoints.size
        movingPoints.put(id, point)
        return id
    }

    fun addEdgeAtoA(id1: Int, id2: Int, k: Double) {
        actorsToActors.add(Edge(id1, id2, k))
    }

    fun addEdgeAtoP(idg: Int, idp: Int, k: Double) {
        actorsToPoints.add(Edge(idg, idp, k))
    }

    fun changeStrategy(graphId: Int, newStrategy: StrategyWithAim) {
        theActors[graphId]?.strategy = newStrategy
    }

    private  fun getDirection(ownPos: Pair<Double, Double>, otherCenter: Pair<Double, Double>, factor: Double): Pair<Double, Double> {
        val diff = Pair<Double, Double>(ownPos.first - otherCenter.first, ownPos.second - otherCenter.second)
        val dist3 =
            ((diff.first.absoluteValue + minDist).pow(2) + (diff.second.absoluteValue + minDist).pow(2)).pow(1.5)
        return Pair(G * factor * diff.first / dist3, G * factor * diff.second / dist3)
    }

    override fun update(t: Double) {
        changeAim(t)
        updateG(t)
    }

    override fun getGraphs(): Map<Int, ForgetfulGraphInR2> {
        return theActors
    }

    override fun getColors(): Map<Int, Pair<Array<Int>, Array<Int>>> {
        return theColors
    }

    fun changeAim(t: Double) {
        val aims: Array<Pair<Double, Double>> = Array(theActors.size, { Pair(0.0, 0.0) })
        val positions: Array<Pair<Double, Double>> = Array(theActors.size, { id -> Pair(theActors[id]!!.pos.first.toDouble(), theActors[id]!!.pos.second.toDouble()) })

        for (edge in actorsToActors) {
            val edgeAim = getDirection(positions[edge.node1], positions[edge.node2], edge.k)
            aims[edge.node1] = Pair(aims[edge.node1].first + edgeAim.first , aims[edge.node1].second + edgeAim.second)
        }

        for (edge in actorsToPoints) {
            val point = movingPoints[edge.node2]!!
            val edgeAim = getDirection(positions[edge.node1], point.trajectory.pos(t), edge.k * point.mass)
            aims[edge.node1] = Pair(aims[edge.node1].first + edgeAim.first , aims[edge.node1].second + edgeAim.second)
        }

        for (id in 0 until theActors.size) {
            theActors[id]!!.strategy.setAim(GraphInR2.toDirWeights(aims[id]))
        }
    }

    open fun updateG(t: Double) {}

    override fun step() {
        for (g in theActors.values) {
            g.move()
        }
    }

}

class Take(
    val size: Int,
    val totalSteps: Int,
    val stepsForaAimChange: Int
) {
    val graphsCollections = mutableListOf<FilmedObject>()
    var steps = 0
    val changes = mutableListOf<GraphChange>()

    fun addObject(c: FilmedObject) {
        graphsCollections.add((c))
    }

    fun addChange(ch: GraphChange) {
        changes.add(ch)
    }


    fun step() {
        for (collection in graphsCollections) {
            collection.step()
        }
        steps++

        val timeForAimChange = steps % stepsForaAimChange == 0
        if (timeForAimChange) {
            for (collection in graphsCollections) {
                collection.update(steps.toDouble() / totalSteps)
            }
        }

        for (ch in changes) {
            ch.doChange(steps)
        }
    }

}

class GraphChange(
    time: Double,
    totalSteps: Int,
    val graphs: FilmedObject,
    val change: (FilmedObject) -> Unit
) {
    val stepToChange = (time * totalSteps).toInt()

    fun doChange(step: Int) {
        if (step == stepToChange) {
            change(graphs)
        }
    }
}