package frontend.pages

import model.{Dimensions, OpenSudokuBoard, SolvedSudokuBoard, SudokuBoard, Validate}

object QPHelper {
  object OpenSudoku {
    def toQP(board: OpenSudokuBoard): Map[String, String] =
      Map(
        "width"  -> board.dim.width.toString,
        "height" -> board.dim.height.toString,
        "board"  -> board.data.map(_.fold("_")(_.toString)).mkString(",")
      )

    def unapply(qp: Map[String, String]): Option[OpenSudokuBoard] =
      for {
        width  <- qp.get("width").flatMap(_.toIntOption)
        height <- qp.get("height").flatMap(_.toIntOption)
        dim = Dimensions(width, height)
        board <- qp.get("board").map(_.replaceAll(",", " ")).flatMap(SudokuBoard.fromString(dim))
      } yield board
  }

  object SolvedSudoku {
    def toQP(board: SolvedSudokuBoard): Map[String, String] =
      Map(
        "width"  -> board.dim.width.toString,
        "height" -> board.dim.height.toString,
        "board"  -> board.data.map(_.toString).mkString(",")
      )

    def unapply(qp: Map[String, String]): Option[SolvedSudokuBoard] =
      for {
        width  <- qp.get("width").flatMap(_.toIntOption)
        height <- qp.get("height").flatMap(_.toIntOption)
        dim = Dimensions(width, height)
        board          <- qp.get("board").map(_.replaceAll(",", " ")).flatMap(SudokuBoard.fromString(dim))
        validatedBoard <- Validate(board)
      } yield validatedBoard
  }
}
