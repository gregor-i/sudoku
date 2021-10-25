package model

import scala.util.Try

case class SudokuBoard[+A](dim: Dimensions, data: Vector[A]) {
  import dim._
  require(boardSize == data.length, s"required size is ${boardSize}, but is actually ${data.length}")

  private def toIndex(x: Int, y: Int): Int = {
    require(0 <= x)
    require(0 <= y)
    require(x < blockSize)
    require(y < blockSize)
    y * blockSize + x
  }

  def map[S](f: A => S): SudokuBoard[S] = new SudokuBoard[S](dim, data.map(f))

  def get(x: Int, y: Int): A = data(toIndex(x, y))

  def get(pos: Position): A = data(toIndex(pos._1, pos._2))

  def set[AA >: A](pos: Position, value: AA): SudokuBoard[AA] =
    new SudokuBoard[AA](dim, data.updated(toIndex(pos._1, pos._2), value))

  def mod[AA >: A](pos: Position, f: A => AA): SudokuBoard[AA] = set(pos, f(get(pos)))
}

object SudokuBoard {
  def empty(dim: Dimensions): OpenSudokuBoard = fill(dim)(_ => None)

  def fill[A](dim: Dimensions)(f: Position => A): SudokuBoard[A] =
    new SudokuBoard[A](dim, rows(dim).flatten.map(f).toVector)

  def fromString(dimensions: Dimensions)(string: String): Option[OpenSudokuBoard] =
    Try {
      new OpenSudokuBoard(
        dimensions,
        string
          .split("\\s")
          .map(_.trim)
          .filter(_.nonEmpty)
          .map(_.toIntOption)
          .toVector
      )
    }.toOption

  def row(y: Int)(dim: Dimensions): Subset    = for (x <- 0 until dim.blockSize) yield (x, y)
  def column(x: Int)(dim: Dimensions): Subset = for (y <- 0 until dim.blockSize) yield (x, y)
  def block(i: Int)(dim: Dimensions): Subset =
    for {
      y <- 0 until dim.height
      x <- 0 until dim.width
    } yield (x + (i % dim.height) * dim.width, y + (i / dim.height) * dim.height)

  def rows(dim: Dimensions): Seq[Subset]       = for (y <- 0 until dim.blockSize) yield row(y)(dim)
  def columns(dim: Dimensions): Seq[Subset]    = for (x <- 0 until dim.blockSize) yield column(x)(dim)
  def blocks(dim: Dimensions): Seq[Subset]     = for (i <- 0 until dim.blockSize) yield block(i)(dim)
  def allSubsets(dim: Dimensions): Seq[Subset] = rows(dim) ++ columns(dim) ++ blocks(dim)

  def rowOf(position: Position)(dim: Dimensions): Subset    = row(position._2)(dim)
  def columnOf(position: Position)(dim: Dimensions): Subset = column(position._1)(dim)
  def blockOf(position: Position)(dim: Dimensions): Subset =
    block((position._2 / dim.height) * dim.height + (position._1 / dim.width) % dim.height)(dim)
  def allSubsetsOf(position: Position)(dim: Dimensions): Seq[Subset] =
    Seq(rowOf(position)(dim), columnOf(position)(dim), blockOf(position)(dim))

  def columnBlock(i: Int, dim: Dimensions): Subset =
    (dim.width * i until dim.width * (i + 1))
      .flatMap(column(_)(dim))

  def rowBlock(i: Int, dim: Dimensions): Subset =
    (dim.height * i until dim.height * (i + 1))
      .flatMap(row(_)(dim))

  def columnBlocks(dim: Dimensions): Seq[Subset] = for (i <- 0 until dim.height) yield columnBlock(i, dim)
  def rowBlocks(dim: Dimensions): Seq[Subset]    = for (i <- 0 until dim.width) yield rowBlock(i, dim)

  def positions(dim: Dimensions): Seq[Position] =
    for {
      x <- 0 until dim.blockSize
      y <- 0 until dim.blockSize
    } yield (x, y)

  def values(dim: Dimensions): Range = 1 to dim.blockSize
}
