package object model {
  type Position = (Int, Int)
  type Subset   = Seq[Position]

  type SolvedSudokuBoard  = SudokuBoard[Int]
  type OpenSudokuBoard    = SudokuBoard[Option[Int]]
  type OptionsSudokuBoard = SudokuBoard[Set[Int]]
  type SudokuPuzzle       = SudokuBoard[PuzzleCell]
}
