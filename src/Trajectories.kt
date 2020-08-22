import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MovingPoint(val mass: Double, val trajectory: Trajectory)

interface Trajectory {
    fun pos(t: Double): Pair<Double, Double>
}


class FixedTrajectory(
    x: Int,
    y: Int
) : Trajectory {
    private val pos_ = Pair(x.toDouble(), y.toDouble())

    override fun pos(t: Double): Pair<Double, Double> = pos_
}

class LinearTrajectory(
    val start: Pair<Double, Double>,
    val end: Pair<Double, Double>
) : Trajectory {
    override fun pos(t: Double): Pair<Double, Double> {
        return Pair((1 - t) * start.first + t * end.first, (1 - t) * start.second + t * end.second)
    }
}

class CircularTrajectory(
    val center: Pair<Double, Double>,
    val radius: Double,
    val startAngle: Double,
    val endAngle: Double
) : Trajectory {
    var steps = 0

    override fun pos(t: Double): Pair<Double, Double> {
        val angle = (1 - t) * startAngle + t * endAngle
        return Pair(center.first + radius * cos(angle), center.second + radius * sin(angle))
    }
}

class GeneralTrajectory(
    val xy: (t: Double) -> Pair<Double, Double>
) : Trajectory {
    override fun pos(t: Double): Pair<Double, Double> {
        return xy(t)
    }
}


class TimePoint(val t: Double, val p: Pair<Double, Double>)

class Polygonal(
    val timePoints: List<TimePoint> // first timePoint coord t should be 0.0. Last should be 1.0
) : Trajectory {
    override fun pos(t: Double): Pair<Double, Double> {
        if (t == 1.0) return timePoints.last().p
        val idx = ((0..timePoints.size-1) zip timePoints).filter { it.second.t > t }.minBy { it.second.t }!!.first
        val t0 = timePoints[idx - 1].t
        val t1 = if (idx < timePoints.size) timePoints.get(idx).t else 2.0
        val dt = t1 - t0
        val x = (t1 - t) / dt * timePoints[idx - 1].p.first + (t - t0) / dt * timePoints[idx].p.first
        val y = (t1 - t) / dt * timePoints[idx - 1].p.second + (t - t0) / dt * timePoints[idx].p.second
        return Pair(x, y)
    }
}

class DisplacedMovingPoint(
    val mp: Trajectory,
    val deltaX: Double,
    val deltaY: Double
) : Trajectory {
    override fun pos(t: Double): Pair<Double, Double> {
        val xy = mp.pos(t)
        return Pair(xy.first + deltaX, xy.second + deltaY)
    }
}

class VibratingTrajectory(
    val pos0: Pair<Double, Double>,
    val pos1: Pair<Double, Double>,
    val frequency: Double
) : Trajectory {
    override fun pos(t: Double): Pair<Double, Double> {
        val s = (cos(2 * PI * frequency * t) + 1) / 2
        val x = s * pos0.first + (1 - s) * pos1.first
        val y = s * pos0.second + (1 - s) * pos1.second
        return Pair(x, y)
    }
}

class Composition(
    val changes: List<Double>,
    val subTrajectories: List<Trajectory>
) : Trajectory {
    override fun pos(t: Double): Pair<Double, Double> {
        val idx = ((0..changes.size-1) zip changes).filter { it.second > t }.minBy { it.second }?.first ?: changes.size
        val t0 = if (idx > 0) changes[idx - 1] else 0.0
        val t1 = if (idx < changes.size) changes[idx] else 1.0
        return subTrajectories[idx].pos((t - t0) / (t1 - t0))
    }
}