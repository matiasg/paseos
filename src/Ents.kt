class Phantom(
    size: Int,
    val center: MovingPoint,
    color: Array<Int>
): JointGraphsWithRestraints() {
    init {
        val start = Pair(center.trajectory.pos(0.0).first.toInt(), center.trajectory.pos(0.0).second.toInt())
        val body = addGraph(
            ForgetfulGraphInR2(size, 24_000, OneOrA(1.5), start),
            arrayOf(color[0] / 30, color[1] / 30, color[2] / 40), color
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
        val eye1Point = addMovingPoint(MovingPoint(6 * center.mass, DisplacedMovingPoint(center.trajectory, -10.0, -8.0)))
        val eye2Point = addMovingPoint(MovingPoint(6 * center.mass, DisplacedMovingPoint(center.trajectory, -10.0, 8.0)))

        addEdgeGtoP(body, bodyPoint, -1.5)
        addEdgeGtoP(eye1, eye1Point, -1.5)
        addEdgeGtoP(eye2, eye2Point, -1.5)
    }

}