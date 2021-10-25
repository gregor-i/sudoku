import AnalyseGenerator.dim
import model.Difficulty.{Easy, Hard, Medium}
import model.{Dimensions, SudokuBoard}
import model.infinite.ContinuationOptions
import model.solver.PerfectSolver

object AnalyseContinuationOptions {

  def countOptions(amount: Int, dim: Dimensions): Unit = {
    val sizes =
      for (board <- PerfectSolver.allSolutions(SudokuBoard.empty(dim)).take(amount)) yield {
        val area = SudokuBoard.columnBlock(1, dim)

        val options = ContinuationOptions(board, area = area.toSet, seed = 5)

        options.length
      }

    println(
      f"""analysis of continuation options (how many new puzzles can be generated, when replacing a single block column)
         |dim: ${dim}
         |""".stripMargin
    )

    sizes
      .groupBy(identity)
      .toList
      .sortBy(_._1)
      .foreach((numberOfOptions, list) => println(s"${numberOfOptions} ${list.size.toDouble / amount * 100}%"))
  }

  def main(args: Array[String]): Unit = {
    countOptions(100, Dimensions(2, 2))
    countOptions(100, Dimensions(2, 3))
    countOptions(100, Dimensions(3, 3))
  }
}
