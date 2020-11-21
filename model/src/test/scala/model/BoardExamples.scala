package model

object BoardExamples {
  val Some(completedBoard) = SudokuBoard.fromString(Dimensions(2, 2)) {
    """
      |1 2 3 4
      |3 4 1 2
      |2 3 4 1
      |4 1 2 3
      |""".stripMargin
  }

  val Some(boardErrorBlock) = SudokuBoard.fromString(Dimensions(2, 2)) {
    """
      |1 _ _ _
      |_ 1 _ _
      |_ _ _ _
      |_ _ 1 _
      |""".stripMargin
  }

  val Some(boardErrorRow) = SudokuBoard.fromString(Dimensions(2, 2)) {
    """
      |1 _ _ 1
      |_ _ _ _
      |_ _ _ _
      |_ _ 1 _
      |""".stripMargin
  }

  val Some(boardErrorColumn) = SudokuBoard.fromString(Dimensions(2, 2)) {
    """
      |1 _ _ _
      |_ _ _ _
      |1 _ _ _
      |_ _ 1 _
      |""".stripMargin
  }
}
