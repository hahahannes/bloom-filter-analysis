package bloomfilter

import com.panayotis.gnuplot.JavaPlot
import com.panayotis.gnuplot.plot.DataSetPlot
import com.panayotis.gnuplot.style.PlotStyle
import com.panayotis.gnuplot.style.Style
import com.panayotis.gnuplot.terminal.ImageTerminal
import java.io.IOException
import java.io.FileNotFoundException
import java.io.FileInputStream
import java.io.File
import javax.imageio.ImageIO

object Plotter {
    fun createPlotFile(imageName: String, parameter: String, metric: String, plot: JavaPlot) {
        val png = ImageTerminal()
        plot.terminal = png
        val useDockerVolume = System.getenv("use_volume").toBoolean()
        var file = File(imageName + "_$parameter" + "_$metric.png")

        if(useDockerVolume) {
            file = File("/data/" + imageName + "_$parameter" + "_$metric.png")
        }
        try {
            file.createNewFile()
            png.processOutput(FileInputStream(file))
        } catch (ex: FileNotFoundException) {
            System.err.print(ex)
        } catch (ex: IOException) {
            System.err.print(ex)
        }

        plot.plot()
        try {
            ImageIO.write(png.image, "png", file)
        } catch (ex: IOException) {
            System.err.print(ex)
        }
    }
    fun plotResults(results: Array<Result>, imageName: String, parameter: String) {
        val precisionLine = Array(results.size) { FloatArray(2) }
        val matchingTimeLine = Array(results.size) { FloatArray(2) }
        val computingTimeLine = Array(results.size) { FloatArray(2) }
        val recallLine = Array(results.size) { FloatArray(2) }

        for (i in 0..results.size-1) {
            if(parameter == "length") {
                precisionLine[i][0] = results.get(i).filterLength.toFloat()
                matchingTimeLine[i][0] = results.get(i).filterLength.toFloat()
                computingTimeLine[i][0] = results.get(i).filterLength.toFloat()
                recallLine[i][0] = results.get(i).filterLength.toFloat()
            } else if (parameter == "hash") {
                precisionLine[i][0] = results.get(i).amountOfHashFunctions.toFloat()
                matchingTimeLine[i][0] = results.get(i).amountOfHashFunctions.toFloat()
                computingTimeLine[i][0] = results.get(i).amountOfHashFunctions.toFloat()
                recallLine[i][0] = results.get(i).amountOfHashFunctions.toFloat()
            } else {
                precisionLine[i][0] = results.get(i).amountOfFolding.toFloat()
                matchingTimeLine[i][0] = results.get(i).amountOfFolding.toFloat()
                computingTimeLine[i][0] = results.get(i).amountOfFolding.toFloat()
                recallLine[i][0] = results.get(i).amountOfFolding.toFloat()
            }
            val truematch = results.get(i).trueMatch.toFloat()
            val match = results.get(i).match.toFloat()
            val precision = truematch / match
            var expectedTruePositives = System.getenv("expected_true_positives")
            if(expectedTruePositives == null) {
                expectedTruePositives = "15000"
            }
            val recall = truematch / expectedTruePositives.toFloat()

            precisionLine[i][1] = precision
            matchingTimeLine[i][1] = results.get(i).matchingTime.toFloat() / 1000
            computingTimeLine[i][1] = results.get(i).computingTime.toFloat() / 1000
            recallLine[i][1] = recall
        }
        // Precision plot
        createPlotFile(imageName, parameter, "precision", drawPlot(precisionLine, "Precision"))

        // Matching time plot
        createPlotFile(imageName, parameter, "matching_time", drawPlot(matchingTimeLine, "Match Time"))

        // Computing time plot
        createPlotFile(imageName, parameter, "computing_time", drawPlot(computingTimeLine, "Coputing Time"))

        // Recall plot
        createPlotFile(imageName, parameter, "recall", drawPlot(recallLine, "Recall"))
    }

    private fun drawPlot(dataset: Array<FloatArray>, title: String): JavaPlot {
        val plot = JavaPlot()
        val plotStyle = PlotStyle()
        plotStyle.setLineWidth(1)
        plotStyle.setStyle(Style.LINES)
        val dataSet = DataSetPlot(dataset)
        dataSet.plotStyle = plotStyle
        dataSet.setTitle(title)
        plot.addPlot(dataSet)
        return plot
    }
}