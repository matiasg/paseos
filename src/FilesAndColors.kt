import ar.com.hjg.pngj.ImageInfo
import ar.com.hjg.pngj.ImageLineHelper
import ar.com.hjg.pngj.ImageLineInt
import ar.com.hjg.pngj.PngWriter
import java.io.FileOutputStream


fun interpolate(col0: Array<Int>, col1: Array<Int>, visits: Int, maxVisits: Int): IntArray {
    if (visits == 0) {
        return IntArray(3)
    }
    val t = visits.toDouble() / maxVisits
    return IntArray(3, { i -> (t * col0[i] + (1 - t) * col1[i]).toInt() })
}


class GCMV(val graph: GraphInR2, val col1: Array<Int>, val col2: Array<Int>, val maxVal: Int)

fun dumpPng(collections: List<FilmedObject>, size: Int, file: FileOutputStream) {
    /*
    TODO: Poner aca un array con toda la info y hacer
    for collection
    for i
    for j
    y al final dump
     */

    val picMatrix = Array(size) { Array(size) { IntArray(3) } }

    for (collection in collections) {
        val gs = collection.getGraphs()
        val cols = collection.getColors()
        val graphsNumber = gs.size
        val info = (0..graphsNumber - 1).map { id ->
            GCMV(
                gs[id]!!,
                cols[id]!!.first,
                cols[id]!!.second,
                gs[id]!!.maxNode()
            )
        }
        for (i in (0..size - 1)) {
            for (j in (0..size - 1)) {
                for (gcmv in info) {
                    val thisGraphColor = interpolate(gcmv.col1, gcmv.col2, gcmv.graph.getNode(i, j), gcmv.maxVal)
                    for (kc in (0..2)) {
                        picMatrix[i][j][kc] += thisGraphColor[kc]
                    }
                }

            }
        }
    }

    val imi = ImageInfo(size, size, 8, false)
    val png = PngWriter(file, imi)
    for (i in (0..size - 1)) {
        val iline = ImageLineInt(imi)
        for (j in (0..size - 1)) {
            ImageLineHelper.setPixelRGB8(iline, j, picMatrix[i][j][0], picMatrix[i][j][1], picMatrix[i][j][2])
        }
        png.writeRow(iline)
    }
    png.end()
}


fun toColor(rgb: String): Array<Int> {
    return arrayOf(
        rgb.slice((0..1)).toInt(radix = 16),
        rgb.slice((2..3)).toInt(radix = 16),
        rgb.slice((4..5)).toInt(radix = 16)
    )
}

