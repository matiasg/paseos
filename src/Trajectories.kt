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
    val timePoints: List<TimePoint>
) : Trajectory {
    // first timePoint coord t should be 0.0. Last should be 1.0

    var idx = 0

    override fun pos(t: Double): Pair<Double, Double> {
        // WARN: we assume this is called with increasing t's
        if (t >= timePoints[idx + 1].t) idx++

        val dt = timePoints[idx + 1].t - timePoints[idx].t
        val x =
            (timePoints[idx + 1].t - t) * timePoints[idx].p.first + (t - timePoints[idx].t) * timePoints[idx + 1].p.first
        val y =
            (timePoints[idx + 1].t - t) * timePoints[idx].p.second + (t - timePoints[idx].t) * timePoints[idx + 1].p.second
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