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

  val Some(easyExample) = SudokuBoard.fromString(Dimensions(3, 3)) {
    """
      |_ 5 6 _ _ 9 1 _ _
      |_ 8 9 _ _ _ 4 5 _
      |_ _ _ 4 _ _ _ _ _
      |_ _ _ 3 4 _ _ _ _
      |_ _ 5 6 7 _ _ 1 2
      |_ _ _ _ _ _ 3 4 5
      |_ _ _ _ _ _ _ _ 1
      |_ _ _ _ 9 _ 2 3 _
      |8 9 1 _ _ _ _ _ 7
      |""".stripMargin
  }
  val Some(mediumExample) = SudokuBoard.fromString(Dimensions(3, 3)) {
    """
      |_ _ _ 8 _ 1 _ 6 _
      |5 6 7 _ _ _ _ 9 _
      |_ _ _ _ _ _ _ _ _
      |4 _ _ _ _ _ _ _ 9
      |_ _ _ _ _ 6 _ 2 3
      |1 _ _ _ 8 _ 4 _ _
      |9 1 _ _ _ _ 3 _ _
      |_ _ 5 9 _ _ _ 7 _
      |6 _ 8 _ _ _ _ 1 2
      |""".stripMargin
  }

  val Some(hardExample) = SudokuBoard.fromString(Dimensions(3, 3)) {
    """
      |8 _ _ _ _ _ _ _ _
      |_ _ 3 6 _ _ _ _ _
      |_ 7 _ _ 9 _ 2 _ _
      |_ 5 _ _ _ 7 _ _ _
      |_ _ _ _ 4 5 7 _ _
      |_ _ _ 1 _ _ _ 3 _
      |_ _ 1 _ _ _ _ 6 8
      |_ _ 8 5 _ _ _ 1 _
      |_ 9 _ _ _ _ 4 _ _
      |""".stripMargin
  }
}
