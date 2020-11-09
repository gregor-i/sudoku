case class Dimensions(width: Int, height: Int) {
  require(width > 0)
  require(height > 0)
  val blockSize: Int = width * height

  def positions: Seq[(Int, Int)] =
    for {
      x <- 0 until blockSize
      y <- 0 until blockSize
    } yield (x, y)

  def values: Seq[Int] = 1 to blockSize
}
