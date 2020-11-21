package model

import scala.util.Try

class SudokuBoard[A](val dim: Dimensions, val data: Vector[A]) {
  import dim._
  require(boardSize == data.length, s"required size is ${boardSize}, but is actually ${data.length}")

  private def toIndex(x: Int, y: Int): Int = {
    require(0 <= x)
    require(0 <= y)
    require(x < blockSize)
    require(y < blockSize)
    y * blockSize + x
  }

  def map[S](f: A => S): SudokuBoard[S] =
    new SudokuBoard[S](dim, data.map(f))

  def get(x: Int, y: Int): A =
    data(toIndex(x, y))

  def get(pos: (Int, Int)): A =
    get(pos._1, pos._2)

  def set(x: Int, y: Int, value: A): SudokuBoard[A] =
    new SudokuBoard[A](dim, data.updated(toIndex(x, y), value))

  def set(pos: (Int, Int), value: A): SudokuBoard[A] =
    set(pos._1, pos._2, value)
}

object SudokuBoard {
  def empty(dim: Dimensions): OpenSudokuBoard =
    new OpenSudokuBoard(dim, Vector.fill[Option[Int]](dim.blockSize * dim.blockSize)(None))

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

  def row(y: Int)(dim: Dimensions): Seq[(Int, Int)]    = for (x <- 0 until dim.blockSize) yield (x, y)
  def column(x: Int)(dim: Dimensions): Seq[(Int, Int)] = for (y <- 0 until dim.blockSize) yield (x, y)
  def block(i: Int)(dim: Dimensions): Seq[(Int, Int)] =
    for {
      y <- 0 until dim.height
      x <- 0 until dim.width
    } yield (x + (i % dim.height) * dim.width, y + (i / dim.height) * dim.height)

  def rows(dim: Dimensions): Seq[Seq[(Int, Int)]]       = for (y <- 0 until dim.blockSize) yield row(y)(dim)
  def columns(dim: Dimensions): Seq[Seq[(Int, Int)]]    = for (x <- 0 until dim.blockSize) yield column(x)(dim)
  def blocks(dim: Dimensions): Seq[Seq[(Int, Int)]]     = for (i <- 0 until dim.blockSize) yield block(i)(dim)
  def allSubsets(dim: Dimensions): Seq[Seq[(Int, Int)]] = rows(dim) ++ columns(dim) ++ blocks(dim)

  def rowOf(position: (Int, Int))(dim: Dimensions): Seq[(Int, Int)]    = row(position._2)(dim)
  def columnOf(position: (Int, Int))(dim: Dimensions): Seq[(Int, Int)] = column(position._1)(dim)
  def blockOf(position: (Int, Int))(dim: Dimensions): Seq[(Int, Int)] =
    block((position._2 / dim.height) * dim.height + (position._1 / dim.width) % dim.height)(dim)

  def positions(dim: Dimensions): Seq[(Int, Int)] =
    for {
      x <- 0 until dim.blockSize
      y <- 0 until dim.blockSize
    } yield (x, y)

  def values(dim: Dimensions): Range = 1 to dim.blockSize
}
