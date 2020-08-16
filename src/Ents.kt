import java.io.File

class Phantom(
    size: Int,
    val center: (t: Double) -> Pair<Double, Double>,
    totalSteps: Int,
    color: Array<Int>
): GraphsWithRestraints() {
    init {
        val body = addGraph(
            ForgetfulGraphInR2(size, 24_000, OneOrA(1.5)),
            arrayOf(color[0] / 30, color[1] / 30, color[2] / 40), color
        )
        val eye1 = addGraph(
            ForgetfulGraphInR2(size, 6_000, OneOrA(4.5)),
            color, toColor("ffffff")
        )
        val eye2 = addGraph(
            ForgetfulGraphInR2(size, 6_000, OneOrA(4.5)),
            color, toColor("ffffff")
        )

        val bodyPoint = addMovingPoint(FunctionMovingPoint({t -> center(t)}, totalSteps, 2.0))
        val eye1Point = addMovingPoint(FunctionMovingPoint({t -> Pair(center(t).first - 10.0, center(t).second - 8.0)}, totalSteps, 12.0))
        val eye2Point = addMovingPoint(FunctionMovingPoint({t -> Pair(center(t).first - 10.0, center(t).second + 8.0)}, totalSteps, 12.0))

        addEdgeGtoP(body, bodyPoint, -1.5)
        addEdgeGtoP(eye1, eye1Point, -1.5)
        addEdgeGtoP(eye2, eye2Point, -1.5)
    }

}