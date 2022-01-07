import model.Difficulty.{Easy, Hard, Medium}
import model._

object AnalyseGenerator {
  val dim = Dimensions(3, 3)

  def streamPuzzles(difficulty: Difficulty, pattern: Pattern): LazyList[OpenSudokuBoard] =
    LazyList
      .iterate(0)(_ + 1)
      .map(seed => Generator(seed = seed, dim = dim, difficulty = difficulty, pattern = pattern))
      .map(_.map {
        case PuzzleCell.Given(value) => Some(value)
        case PuzzleCell.Empty(_)     => None
      })

  def streamWithName(difficulty: Difficulty, pattern: Pattern): (String, LazyList[OpenSudokuBoard]) =
    (s"$difficulty, $pattern", streamPuzzles(difficulty, pattern))

  def actuallyEasy(puzzle: OpenSudokuBoard): Boolean =
    Solver.forDifficulty(Difficulty.Easy).canSolve(puzzle)

  def actuallyMedium(puzzle: OpenSudokuBoard): Boolean =
    Solver.forDifficulty(Difficulty.Medium).canSolve(puzzle)

  def analyseDifficulty(n: Int, streams: Seq[(String, LazyList[OpenSudokuBoard])]): Unit = {
    val longestName = streams.map(_._1.length).max.max("Stream".length)
    val columnSize  = 6

    extension (s: String) def padToRight(n: Int, char: Char): String = s.reverse.padTo(n, char).reverse
    extension (d: Double) def format: String                         = f"${d}%2.2f%%"

    def row(name: String, easy: String, medium: String, hard: String): Unit = {
      val line = Seq(
        name.padTo(longestName, ' '),
        easy.padToRight(columnSize, ' '),
        medium.padToRight(columnSize, ' '),
        hard.padToRight(columnSize, ' ')
      ).mkString(" | ")
      println(line)
    }

    println(s"analyse actual difficulty distribution (n = $n, dim = ${dim})")
    row("Stream", Easy.toString, Medium.toString, Hard.toString)
    for ((name, stream) <- streams) {
      val difficultyDistribution = stream
        .take(n)
        .map {
          case puzzle if actuallyEasy(puzzle)   => Easy
          case puzzle if actuallyMedium(puzzle) => Medium
          case _                                => Hard
        }
        .groupBy(identity)
        .withDefaultValue(Seq.empty)

      row(
        name,
        (difficultyDistribution(Easy).size * 100d / n).format,
        (difficultyDistribution(Medium).size * 100d / n).format,
        (difficultyDistribution(Hard).size * 100d / n).format
      )
    }
  }

  def analyseDensity(n: Int, difficulty: Difficulty, pattern: Pattern): Unit = {
    val densityStatistics = streamPuzzles(difficulty, pattern)
      .take(n)
      .map(_.data.count(_.isDefined))

    println(
      s"""analyse generator (density)
         |Difficulty = ${difficulty}, n = $n, dim = ${dim}
         |
         |Min given fields: ${densityStatistics.min}
         |Max given fields: ${densityStatistics.max}
         |Avg given fields: ${densityStatistics.sum.toDouble / n}
         |""".stripMargin
    )
  }

  def main(args: Array[String]): Unit = {
    analyseDifficulty(
      n = 100,
      streams = Seq(
        streamWithName(Hard, NoPattern),
        streamWithName(Hard, RotationalSymmetry),
        streamWithName(Hard, MirrorXYAxis),
        streamWithName(Medium, NoPattern),
        streamWithName(Medium, RotationalSymmetry),
        streamWithName(Medium, MirrorXYAxis)
      )
    )
//    analyseDensity(100, Hard)
//    analyseDensity(100, Medium)
//    analyseDensity(100, Easy)
  }
}
