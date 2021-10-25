import model.Difficulty.{Easy, Hard, Medium}
import model._

object AnalyseGenerator {
  val dim = Dimensions(3, 3)

  def streamPuzzles(difficulty: Difficulty): LazyList[OpenSudokuBoard] =
    LazyList
      .iterate(0)(_ + 1)
      .map(seed => Generator(seed = seed, dim = dim, difficulty = difficulty))

  def actuallyEasy(puzzle: OpenSudokuBoard): Boolean =
    Solver.forDifficulty(Difficulty.Easy).canSolve(puzzle)

  def actuallyMedium(puzzle: OpenSudokuBoard): Boolean =
    Solver.forDifficulty(Difficulty.Medium).canSolve(puzzle)

  def analyseDifficulty(n: Int, difficulty: Difficulty): Unit = {
    val difficultyDistribution = streamPuzzles(difficulty)
      .take(n)
      .map {
        case puzzle if actuallyEasy(puzzle)   => Easy
        case puzzle if actuallyMedium(puzzle) => Medium
        case _                                => Hard
      }
      .groupBy(identity)
      .withDefaultValue(Seq.empty)

    println(
      f"""analysis generator (desired difficulty with vs. actual difficulty)
         |Difficulty = ${difficulty}, n = $n, dim = ${dim}
         |
         |Easy:   ${difficultyDistribution(Easy).size * 100d / n}%2.2f%%
         |Medium: ${difficultyDistribution(Medium).size * 100d / n}%2.2f%%
         |Hard:   ${difficultyDistribution(Hard).size * 100d / n}%2.2f%%
         |""".stripMargin
    )
  }

  def analyseDensity(n: Int, difficulty: Difficulty): Unit = {
    val densityStatistics = streamPuzzles(difficulty)
      .take(n)
      .map(_.data.count(_.isDefined))

    println(
      s"""analysis generator (density)
         |Difficulty = ${difficulty}, n = $n, dim = ${dim}
         |
         |Min given fields: ${densityStatistics.min}
         |Max given fields: ${densityStatistics.max}
         |Avg given fields: ${densityStatistics.sum.toDouble / n}
         |""".stripMargin
    )
  }

  def main(args: Array[String]): Unit = {
    analyseDifficulty(100, Hard)
    analyseDifficulty(100, Medium)
    analyseDensity(100, Hard)
    analyseDensity(100, Medium)
    analyseDensity(100, Easy)
  }
}
