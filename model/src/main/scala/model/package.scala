package object model {
  type Position = (Int, Int)

  type SolvedSudokuBoard  = SudokuBoard[Int]
  type OpenSudokuBoard    = SudokuBoard[Option[Int]]
  type OptionsSudokuBoard = SudokuBoard[Set[Int]]
}
