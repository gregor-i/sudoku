class SudokuBoard[F[_]](val dim: Dimensions, val data: Vector[F[Int]]) {
  import dim._
  require(blockSize * blockSize == data.length, s"required size is ${blockSize * blockSize}, but is actually ${data.length}")

  private def toIndex(x: Int, y: Int): Int = {
    require(0 <= x)
    require(0 <= y)
    require(x < blockSize)
    require(y < blockSize)
    y * blockSize + x
  }

  def get(x: Int, y: Int): F[Int] =
    data(toIndex(x, y))

  def set(x: Int, y: Int, value: F[Int]): SudokuBoard[F] =
    new SudokuBoard[F](dim, data.updated(toIndex(x, y), value))
}

object SudokuBoard {
  def empty(dim: Dimensions): SudokuBoard[Option] =
    new SudokuBoard[Option](dim, Vector.fill[Option[Int]](dim.blockSize * dim.blockSize)(None))

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

  // todo: these functions are quite dumb
  def rowOf(position: (Int, Int))(dim: Dimensions): Seq[(Int, Int)]    = rows(dim).find(_.contains(position)).get
  def columnOf(position: (Int, Int))(dim: Dimensions): Seq[(Int, Int)] = columns(dim).find(_.contains(position)).get
  def blockOf(position: (Int, Int))(dim: Dimensions): Seq[(Int, Int)]  = blocks(dim).find(_.contains(position)).get
}
