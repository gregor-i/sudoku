package object model {
  type Position = (Int, Int)
  type Subset   = Seq[Position]

  type FilledSudokuBoard    = SudokuBoard[Int]
  type OpenSudokuBoard      = SudokuBoard[Option[Int]]
  type OptionsSudokuBoard   = SudokuBoard[Set[Int]]
  type SudokuPuzzle         = SudokuBoard[PuzzleCell]
  type FreshSudokuPuzzle    = SudokuBoard[PuzzleCell.Given | PuzzleCell.Empty]
  type FinishedSudokuPuzzle = SudokuBoard[PuzzleCell.Given | PuzzleCell.CorrectInput]
}
