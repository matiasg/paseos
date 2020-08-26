import kotlin.math.*

class Phantom(
    size: Int,
    val center: MovingPoint,
    color: Array<Int>,
    oblongNess: Pair<Double, Double>? = null
) : JointGraphsWithRestraints() {
    val body: Int

    init {
        val start = Pair(center.trajectory.pos(0.0).first.toInt(), center.trajectory.pos(0.0).second.toInt())
        body = addGraph(
            ForgetfulGraphInR2(
                size,
                24_000,
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

        addEdgeGtoP(body, bodyPoint, -1.5)
        addEdgeGtoP(eye1, eye1Point, -1.5)
        addEdgeGtoP(eye2, eye2Point, -1.5)
    }

    fun getBody(): GraphInR2 {
        return theGraphs[body]!!
    }

}

class Bug(
    size: Int,
    val center: MovingPoint,
    color: Array<Int>,
    val memory: Int
) : JointGraphsWithRestraints() {
    init {
        val start = Pair(center.trajectory.pos(0.0).first.toInt(), center.trajectory.pos(0.0).second.toInt())
        val body = addGraph(
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

        addEdgeGtoP(body, bodyPoint, -1.5)
        addEdgeGtoP(eyes, eyesPoint, -1.5)
    }

}

class Tree(
    size: Int,
    bottomLeft: Pair<Double, Double>,
    topRight: Pair<Double, Double>
) : JointGraphsWithRestraints() {
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
        addEdgeGtoP(trunk, contour, -2.8)


        val center = Pair(topRight.first, topRight.second * 0.5 + bottomLeft.second * 0.5)
        val leavesGraph = addGraph(ForgetfulGraphInR2(size, 280_000, OneOrA(4.0), Pair(center.first.toInt(), center.second.toInt())),
            toColor("206010"), toColor("30f020")
        )
        var leaves = mutableListOf<Trajectory>()
        val length = 0.40 * sqrt((topRight.first - bottomLeft.first).pow(2) + (topRight.second - bottomLeft.second).pow(2))
        for (i in 2 until 11) {
            val end = Pair(center.first + length * cos(i.toDouble() / 6 * PI), center.second + length * sin(i.toDouble() / 6 * PI))
            leaves.add(LinearTrajectory(center, end))
        }
        val leavesTrajectory = addMovingPoint(MovingPoint(2.0,
            Repetition(Composition((1 until 9).map { it -> it.toDouble() / 9}, leaves), 27)
        ))

        addEdgeGtoP(leavesGraph, leavesTrajectory, -2.0)

    }
}