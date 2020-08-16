import kotlin.math.cos
import kotlin.math.sin

class ForgetfulGraphInR2(
    size: Int,
    val memory: Int,
    override val strategy: StragegyWithAim,
    startInTheMiddle: Boolean = false
) : GraphInR2(size, strategy, startInTheMiddle) {
    val path = ArrayList<Pair<Int, Int>>(memory)
    var memStart: Int = 0


    override fun move() {
        val dir = randomDir()
        val newPos = moved(pos, dir)
        add(pos, newPos)
        if (path.size >= memory) {
            val s = path[memStart]
            val memNext = (memStart + 1) % memory
            val t = path[memNext]
            add(t, s, -1)
            path.set(memStart, newPos)
            memStart = memNext
        } else {
            path.add(newPos)
        }
        pos = newPos
    }
}

interface MovingPoint {
    val mass: Double

    fun getPos() : Pair<Double, Double>
    fun move()
}

class FixedPoint(
    x: Int,
    y: Int,
    override val mass: Double
): MovingPoint {
    private val pos = Pair(x.toDouble(), y.toDouble())

    override fun getPos(): Pair<Double, Double> {
        return pos
    }

    override fun move() {}
}

class LinearlyMovingPoint(
    val start: Pair<Double, Double>,
    val end: Pair<Double, Double>,
    val totalSteps: Int,
    override val mass: Double
): MovingPoint {
    var steps = 0

    override fun getPos(): Pair<Double, Double> {
        val t = steps.toDouble() / totalSteps
        return Pair((1 - t) * start.first + t * end.first, (1 - t) * start.second + t * end.second)
    }

    override fun move() {
        steps++
    }
}

class CircularMovingPoint(
    val center: Pair<Double, Double>,
    val radius: Double,
    val startAngle: Double,
    val endAngle: Double,
    val totalSteps: Int,
    override val mass: Double
): MovingPoint {
    var steps = 0

    override fun getPos(): Pair<Double, Double> {
        val t = steps.toDouble() / totalSteps
        val angle = (1 - t) * startAngle + t * endAngle
        return Pair(center.first + radius * cos(angle), center.second + radius * sin(angle))
    }

    override fun move() {
        steps++
    }
}

class FunctionMovingPoint(
    val xy: (t: Double) -> Pair<Double, Double>,
    val totalSteps: Int,
    override val mass: Double
): MovingPoint {
    var steps = 0

    override fun getPos(): Pair<Double, Double> {
        return xy(steps.toDouble() / totalSteps)
    }

    override fun move() {
        steps++
    }
}