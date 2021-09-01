import model.{Difficulty, Dimensions, Generator, Solver, SolverResult, Subset, SudokuBoard}

import scala.util.chaining._

object InfinitSudoku {

  val dim        = Dimensions(3, 3)
  val difficulty = Difficulty.Hard
  val seed       = 1

  val openBoard   = Generator(dim, seed, difficulty)
  val solvedBoard = Solver.perfectSolver(openBoard).uniqueSolution.get

  def setSupset[A](sudokuBoard: SudokuBoard[A], value: A, subset: Subset): SudokuBoard[A] =
    subset.foldLeft(sudokuBoard) { (board, pos) =>
      board.set(pos, value)
    }

  def main(args: Array[String]): Unit = {
    val clearedBoard = solvedBoard
      .map[Option[Int]](Some.apply)
      .pipe(setSupset(_, None, SudokuBoard.column(0)(dim)))
      .pipe(setSupset(_, None, SudokuBoard.column(1)(dim)))
      .pipe(setSupset(_, None, SudokuBoard.column(2)(dim)))

    val secondPuzzle = Generator.makePuzzle(seed, solvedBoard, difficulty)

    val mergedBoard =
      SudokuBoard.fill(dim) { pos =>
        if (pos._1 < 3)
          secondPuzzle.get(pos)
        else
          openBoard.get(pos)
      }

    println("openBoard")
    println(SudokuBoard.column(0)(dim).map(openBoard.get))
    println(SudokuBoard.column(1)(dim).map(openBoard.get))
    println(SudokuBoard.column(2)(dim).map(openBoard.get))
    println("mergedBoard")
    println(SudokuBoard.column(0)(dim).map(mergedBoard.get))
    println(SudokuBoard.column(1)(dim).map(mergedBoard.get))
    println(SudokuBoard.column(2)(dim).map(mergedBoard.get))

//    val countedSolutions = Solver.perfectSolver(clearedBoard) match {
//      case SolverResult.CouldNotSolve                => 0
//      case SolverResult.NoSolution                   => 0
//      case SolverResult.UniqueSolution(solution)     => 1
//      case SolverResult.MultipleSolutions(solutions) => solutions.size
//    }
//
//    println(countedSolutions)
  }
}
