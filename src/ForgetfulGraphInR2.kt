import java.awt.geom.Path2D
import java.sql.Time
import kotlin.math.cos
import kotlin.math.sin

class ForgetfulGraphInR2(
    size: Int,
    val memory: Int,
    override val strategy: StragegyWithAim,
    startAt: Pair<Int, Int>? = null
) : GraphInR2(size, strategy, startAt) {
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