package model

case class Dimensions(width: Int, height: Int) {
  require(width > 0)
  require(height > 0)
  val blockSize: Int = width * height
  val boardSize: Int = blockSize * blockSize
}
