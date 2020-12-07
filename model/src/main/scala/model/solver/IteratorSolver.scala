package model.solver

import model._

private[model] object IteratorSolver extends Solver {

  def apply(board: OpenSudokuBoard): SolverResult = {
    type State = (OpenSudokuBoard, OptionsSudokuBoard, List[Position], List[Int])

    def initialState: State = {
      val positions = SudokuBoard.positions(board.dim).filter(board.get(_).isEmpty).toList
      val options   = OptionsSudokuBoard(board)
      (board, options, positions, positions.headOption.fold(List.empty[Int])(options.get(_).toList))
    }

//    @tailrec
    def loop(now: State, stack: List[State]): LazyList[SolvedSudokuBoard] = {
      now match {
        case (board, optionsBoard, pos :: Nil, option :: _) =>
          loop(now = (board.set(pos, Some(option)), optionsBoard.map(_ => Set.empty), Nil, Nil), stack = stack.prepended(now))

        case (board, optionsBoard, pos :: posTail, option :: _) =>
          val newOptionsBoard = OptionsSudokuBoard.set(optionsBoard, pos, option)
          val nextPos         = posTail.minBy(newOptionsBoard.get(_).size)
          val newOptions      = newOptionsBoard.get(nextPos).toList

          loop(
            stack = stack.prepended(now),
            now = (board.set(pos, Some(option)), newOptionsBoard, nextPos :: (posTail.filter(_ != nextPos)), newOptions)
          )

        case (board, _, _, Nil) =>
          Validate(board) match {
            case Some(solution) =>
              if (stack.isEmpty) {
                LazyList(solution)
              } else {
                val tnow = stack.head
                solution #:: loop(
                  stack = stack.tail,
                  now = (tnow._1, tnow._2, tnow._3, tnow._4.drop(1))
                )
              }
            case None =>
              if (stack.isEmpty) {
                LazyList.empty
              } else {
                val tnow = stack.head
                loop(
                  stack = stack.tail,
                  now = (tnow._1, tnow._2, tnow._3, tnow._4.drop(1))
                )
              }
          }

        case (_, _, Nil, _) =>
          LazyList.empty
      }
    }

    if (Validate.noError(board))
      SolverResult.fromLazyList(loop(initialState, Nil))
    else
      SolverResult.NoSolution
  }
}
