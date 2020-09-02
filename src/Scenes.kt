import java.io.File


class Scene(
    val label: String,
    val totalSteps: Int,
    val stepsForPics: Int
) {
    val takes = mutableListOf<Take>()
    val changes = mutableMapOf<Double, Int>(0.0 to 0)

    init {
        directory().mkdir()
    }

    fun addTake(take: Take): Int {
        val id = takes.size
        takes.add(take)
        return id
    }


    fun action() {
        check(takes.size > 0) {"No takes, no action!! Did you forget to use addTake()?"}
        val changesTimes = changes.keys.sorted()
        var currentTakeChange = 0

        for (steps in 0..totalSteps) {

            for (take in takes) {
                take.step()
            }

            val timeForPicture = steps % stepsForPics == 0 && steps > 0
            if (timeForPicture) {
                val time = steps.toDouble() / totalSteps
                while ((currentTakeChange < changesTimes.size - 1) && (time >= changesTimes[currentTakeChange + 1])) {
                    currentTakeChange++
                }
                val pic = steps / stepsForPics - 1
                val pngFile = pngFile(pic)
                val takeId = changes[changesTimes[currentTakeChange]]!!
                val take = takes[takeId]
                println("going for pic $pngFile with take id $takeId")
                pngFile.outputStream().use {
                    dumpPng(take.graphsCollections, take.size, it)
                }
            }
        }
    }

    fun directory(): File {
        val name = "graphs.$label"
        return File(name)
    }

    fun pngFile(pic: Int): File {
        val name = "graph.$label.%04d.png".format(pic)
        return File(directory(), name)
    }

}