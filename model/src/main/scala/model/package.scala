package object model {
  type SolvedSudokuBoard = SudokuBoard[Int]
  type OpenSudokuBoard   = SudokuBoard[Option[Int]]
}
