package model

import scala.util.chaining._
import scala.util.Random
import scala.annotation.tailrec

object ContinuePuzzle {
  def maybeContinue(puzzle: DecoratedBoard, seed: Int, difficulty: Difficulty): DecoratedBoard = {
    def columnBlocks = SudokuBoard.columns(puzzle.dim).grouped(puzzle.dim.width).map(_.flatten)
    def rowBlocks    = SudokuBoard.rows(puzzle.dim).grouped(puzzle.dim.height).map(_.flatten)

    val solution = Solver
      .perfectSolver(puzzle.map {
        case DecoratedCell.Given(value) => Some(value)
        case _                          => None
      })
      .uniqueSolution
      .get
    def subsetIsCompleted(subset: Subset): Boolean = subset.forall(pos => puzzle.get(pos).toOption.contains(solution.get(pos)))

    columnBlocks
      .concat(rowBlocks)
      .find(subsetIsCompleted)
      .map(completedSubset => apply(solution, puzzle, completedSubset, seed, difficulty)._2)
      .getOrElse(puzzle)
  }

  def apply(
      solution: SolvedSudokuBoard,
      puzzle: DecoratedBoard,
      positions: Seq[Position],
      seed: Int,
      difficulty: Difficulty
  ): (SolvedSudokuBoard, DecoratedBoard) = {
    require(solution.dim == puzzle.dim)
    require(positions.forall(pos => puzzle.get(pos).toOption.contains(solution.get(pos))))

    val random = Random(seed)

    val alternativeSolution = solution
      .map(Option.apply)
      .pipe(clearPositions(_, positions.toList))
      .pipe(Solver.perfectSolver.apply(_))
      .pipe {
        case SolverResult.MultipleSolutions(solutions) =>
          println(s"found ${solutions.length} alternatives")
          solutions.foreach(println)
          solutions(random.nextInt(solutions.length))
        case _ => ???
      }

    val continuedPuzzle =
      Generator
        .makePuzzle(
          random = random,
          positions = random.shuffle(positions),
          board = puzzle.map {
            case DecoratedCell.Given(value) => Some(value)
            case _                          => None
          },
          solver = Solver.forDifficulty(difficulty)
        )
        .map {
          case Some(value) => DecoratedCell.Given(value)
          case None        => DecoratedCell.Empty
        }

    (merge(solution, alternativeSolution, positions), merge(puzzle, continuedPuzzle, positions))
  }

  @tailrec
  private def clearPositions(board: OpenSudokuBoard, positions: List[Position]): OpenSudokuBoard =
    positions match {
      case Nil          => board
      case head :: tail => clearPositions(board.mod(head, _ => None), tail)
    }

  private def merge[A](left: SudokuBoard[A], right: SudokuBoard[A], positions: Seq[Position]): SudokuBoard[A] = {
    require(left.dim == right.dim)
    SudokuBoard.fill[A](left.dim) {
      pos =>
        if (positions.contains(pos))
          right.get(pos)
        else
          left.get(pos)
    }
  }
}
