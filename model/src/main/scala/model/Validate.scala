package model

import scala.annotation.tailrec
import scala.util.chaining.scalaUtilChainingOps

object Validate {
  def apply(board: OpenSudokuBoard): Option[SolvedSudokuBoard] =
    if (board.data.forall(_.isDefined))
      Some(board.map(_.get)).filter(correct)
    else
      None

  def noError(board: OpenSudokuBoard, pos: Position): Boolean =
    SudokuBoard
      .allSubsetsOf(pos)(board.dim)
      .forall(_.flatMap(board.get).pipe(noDuplicate))

  def hasError(board: OpenSudokuBoard, pos: Position): Boolean =
    !noError(board, pos)

  def correct(board: SolvedSudokuBoard): Boolean =
    SudokuBoard.allSubsets(board.dim).forall {
      _.map(board.get).sorted == SudokuBoard.values(board.dim)
    }

  private def noDuplicate(values: Seq[Int]): Boolean = {
    @tailrec
    def loop(acc: Set[Int], remaining: List[Int]): Boolean =
      remaining match {
        case head :: _ if acc.contains(head) => false
        case head :: tail                    => loop(acc + head, tail)
        case Nil                             => true
      }

    loop(Set.empty, values.toList)
  }
}
