import kotlin.math.*

fun getStart(center: MovingPoint) = Pair(center.trajectory.pos(0.0).first.toInt(), center.trajectory.pos(0.0).second.toInt())

class Phantom(
    size: Int,
    center: MovingPoint,
    color: Array<Int>,
    oblongNess: Pair<Double, Double>? = null,
    memory: Int = 24_000
) : Actoress() {
    val body: Int

    init {
        val start = getStart(center)
        body = addGraph(
            ForgetfulGraphInR2(
                size,
                memory,
                if (oblongNess == null) OneOrA(1.5) else Oblong(1.5, oblongNess.first, oblongNess.second),
                start
            ), arrayOf(color[0] / 30, color[1] / 30, color[2] / 40), color
        )
        val eye1 = addGraph(
            ForgetfulGraphInR2(size, 6_000, OneOrA(4.5), start),
            color, toColor("ffffff")
        )
        val eye2 = addGraph(
            ForgetfulGraphInR2(size, 6_000, OneOrA(4.5), start),
            color, toColor("ffffff")
        )

        val bodyPoint = addMovingPoint(center)
        val eye1Point =
            addMovingPoint(MovingPoint(6 * center.mass, DisplacedTrajectory(center.trajectory, -10.0, -8.0)))
        val eye2Point =
            addMovingPoint(MovingPoint(6 * center.mass, DisplacedTrajectory(center.trajectory, -10.0, 8.0)))

        addEdgeAtoP(body, bodyPoint, -1.5)
        addEdgeAtoP(eye1, eye1Point, -1.5)
        addEdgeAtoP(eye2, eye2Point, -1.5)
    }

    private fun getBody(): GraphInR2 {
        return theActors[body]!!
    }

    fun changeOblongness (newObx: Double, newOby: Double) {
        getBody().strategy = Oblong(1.5, newObx, newOby)
    }

}


class PersonFromBehind(
    size: Int,
    center: MovingPoint,
    color: Array<Int>,
    a: Double,
    memory: Int = 24_000,
    lookingToLeft: Boolean
) : Actoress() {
    init {
        val start = getStart(center)
        val body = addGraph(
            ForgetfulGraphInR2(size, memory, Oblong(a, 1.8, 1.0), start),
            arrayOf(color[0] / 30, color[1] / 30, color[2] / 40), color
        )
        val eye = addGraph(
            ForgetfulGraphInR2(size, 6_000, OneOrA(4.5), start),
            color, toColor("ffffff")
        )

        val bodyPoint = addMovingPoint(center)
        val eye1Point = addMovingPoint(MovingPoint(6 * center.mass,
            DisplacedTrajectory(center.trajectory, -10.0, (if (lookingToLeft) -8.0 else 8.0))
        ))

        addEdgeAtoP(body, bodyPoint, -1.5)
        addEdgeAtoP(eye, eye1Point, -1.5)
    }

}

class Bug(
    size: Int,
    center: MovingPoint,
    color: Array<Int>,
    val memory: Int
) : Actoress() {
    val body: Int

    init {
        val start = getStart(center)
        body = addGraph(
            ForgetfulGraphInR2(
                size,
                memory,
                Oblong(4.5, 1.0, 3.0),
                start
            ), arrayOf(color[0] / 30, color[1] / 30, color[2] / 40), color
        )
        val eyes = addGraph(
            ForgetfulGraphInR2(size, memory / 2, OneOrA(4.5), start),
            color, toColor("ffffff")
        )

        val bodyPoint = addMovingPoint(center)
        val eyesPoint =
            addMovingPoint(MovingPoint(6 * center.mass, DisplacedTrajectory(center.trajectory, -9.0, 0.0)))

        addEdgeAtoP(body, bodyPoint, -1.5)
        addEdgeAtoP(eyes, eyesPoint, -1.5)
    }

    fun explode() {
        theActors[body]!!.strategy = Oblong(1.0, 17.0, 17.0)
        theActors[body]!!.memory = memory * 3
    }
}


class Ball(
    size: Int,
    center: MovingPoint,
    a: Double,
    color: Array<Int>,
    memory: Int
) : Actoress() {
    val body: Int

    init {
        val start = getStart(center)
        body = addGraph(
            ForgetfulGraphInR2(
                size,
                memory,
                OneOrA(a),
                start
            ), arrayOf(color[0] / 10, color[1] / 10, color[2] / 10), color
        )
        val bodyPoint = addMovingPoint(center)
        addEdgeAtoP(body, bodyPoint, -1.0)
    }

}


class Tree(
    size: Int,
    bottomLeft: Pair<Double, Double>,
    topRight: Pair<Double, Double>
) : Actoress() {
    init {
        val trunk = addGraph(
            ForgetfulGraphInR2(
                size,
                240_000,
                OneOrA(7.5),
                Pair(bottomLeft.first.toInt(), bottomLeft.second.toInt())
            ), toColor("604020"), toColor("e0e040")
        )

        val oneTrunkContour = Composition(
            listOf(0.2, 0.5, 0.7),
            listOf(
                LinearTrajectory(bottomLeft, Pair(bottomLeft.first, topRight.second)),
                Quadratic(
                    Pair(bottomLeft.first, topRight.second),
                    Pair(
                        bottomLeft.first * 0.5 + topRight.first * 0.5,
                        bottomLeft.second * 0.2 + topRight.second * 0.8
                    ),
                    topRight
                ),
                LinearTrajectory(topRight, Pair(topRight.first, bottomLeft.second)),
                Quadratic(
                    Pair(topRight.first, bottomLeft.second),
                    Pair(
                        bottomLeft.first * 0.5 + topRight.first * 0.5,
                        bottomLeft.second * 0.8 + topRight.second * 0.2
                    ),
                    bottomLeft
                )
            )
        )
        val contour = addMovingPoint(
            MovingPoint(2.0, Repetition(oneTrunkContour, 107))
        )
        addEdgeAtoP(trunk, contour, -2.8)


        val center = Pair(topRight.first, topRight.second * 0.5 + bottomLeft.second * 0.5)
        val leavesGraph = addGraph(ForgetfulGraphInR2(size, 280_000, OneOrA(4.0), Pair(center.first.toInt(), center.second.toInt())),
            toColor("206010"), toColor("30f020")
        )
        val leaves = mutableListOf<Trajectory>()
        val length = 0.40 * sqrt((topRight.first - bottomLeft.first).pow(2) + (topRight.second - bottomLeft.second).pow(2))
        for (i in 2 until 11) {
            val end = Pair(center.first + length * cos(i.toDouble() / 6 * PI), center.second + length * sin(i.toDouble() / 6 * PI))
            leaves.add(LinearTrajectory(center, end))
        }
        val leavesTrajectory = addMovingPoint(MovingPoint(2.0,
            Repetition(Composition((1 until 9).map { it -> it.toDouble() / 9}, leaves), 27)
        ))

        addEdgeAtoP(leavesGraph, leavesTrajectory, -2.0)

    }
}