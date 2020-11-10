package model

import scala.util.Try

class SudokuBoard[F[_]](val dim: Dimensions, val data: Vector[F[Int]]) {
  import dim._
  require(boardSize == data.length, s"required size is ${boardSize}, but is actually ${data.length}")

  private def toIndex(x: Int, y: Int): Int = {
    require(0 <= x)
    require(0 <= y)
    require(x < blockSize)
    require(y < blockSize)
    y * blockSize + x
  }

  def get(x: Int, y: Int): F[Int] =
    data(toIndex(x, y))

  def get(pos: (Int, Int)): F[Int] =
    get(pos._1, pos._2)

  def set(x: Int, y: Int, value: F[Int]): SudokuBoard[F] =
    new SudokuBoard[F](dim, data.updated(toIndex(x, y), value))

  def set(pos: (Int, Int), value: F[Int]): SudokuBoard[F] =
    set(pos._1, pos._2, value)
}

object SudokuBoard {
  def empty(dim: Dimensions): SudokuBoard[Option] =
    new SudokuBoard[Option](dim, Vector.fill[Option[Int]](dim.blockSize * dim.blockSize)(None))

  def fromString(dimensions: Dimensions)(string: String): Option[SudokuBoard[Option]] =
    Try {
      new SudokuBoard(
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

  def rows(dim: Dimensions): Seq[Seq[(Int, Int)]]    = for (y <- 0 until dim.blockSize) yield row(y)(dim)
  def columns(dim: Dimensions): Seq[Seq[(Int, Int)]] = for (x <- 0 until dim.blockSize) yield column(x)(dim)
  def blocks(dim: Dimensions): Seq[Seq[(Int, Int)]]  = for (i <- 0 until dim.blockSize) yield block(i)(dim)

  def rowOf(position: (Int, Int))(dim: Dimensions): Seq[(Int, Int)]    = row(position._2)(dim)
  def columnOf(position: (Int, Int))(dim: Dimensions): Seq[(Int, Int)] = column(position._1)(dim)
  def blockOf(position: (Int, Int))(dim: Dimensions): Seq[(Int, Int)] =
    block((position._2 / dim.height) * dim.height + (position._1 / dim.width) % dim.height)(dim)

  def positions(dim: Dimensions): Seq[(Int, Int)] =
    for {
      x <- 0 until dim.blockSize
      y <- 0 until dim.blockSize
    } yield (x, y)

  def values(dim: Dimensions): Seq[Int] = 1 to dim.blockSize
}
