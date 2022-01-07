package model

trait Pattern {
  def groups(dim: Dimensions): Seq[Seq[Position]]
}

object Pattern {
  val allPatterns = Seq(NoPattern, MirrorXAxis, MirrorYAxis, MirrorXYAxis, RotationalSymmetry)
}

case object NoPattern extends Pattern {
  def groups(dim: Dimensions): Seq[Seq[Position]] =
    SudokuBoard.positions(dim).map(List(_))
}

case object MirrorXAxis extends Pattern {
  def groups(dim: Dimensions): Seq[List[Position]] =
    for
      (x, y) <- SudokuBoard.positions(dim)
      x2 = dim.blockSize - 1 - x
      if x <= x2
    yield
      if x == x2 then List((x, y))
      else List((x, y), (x2, y))
}

case object MirrorYAxis extends Pattern {
  def groups(dim: Dimensions): Seq[List[Position]] =
    for
      (x, y) <- SudokuBoard.positions(dim)
      y2 = dim.blockSize - 1 - y
      if y <= y2
    yield
      if y == y2 then List((x, y))
      else List((x, y), (x, y2))
}

case object MirrorXYAxis extends Pattern {
  def groups(dim: Dimensions): Seq[Seq[Position]] =
    for
      (x, y) <- SudokuBoard.positions(dim)
      x2 = dim.blockSize - 1 - x
      y2 = dim.blockSize - 1 - y
      if x <= x2 && y <= y2
    yield
      if x == x2 && y == y2 then List((x, y))
      else if x == x2 then List((x, y), (x, y2))
      else if y == y2 then List((x, y), (x2, y))
      else List((x, y), (x2, y), (x, y2), (x2, y2))
}

case object RotationalSymmetry extends Pattern {
  def groups(dim: Dimensions): Seq[Seq[Position]] = {
    if dim.blockSize % 2 == 0 then {
      val center = dim.blockSize / 2 - 1
      for
        (x, y) <- SudokuBoard.positions(dim)
        dx = center - x
        dy = center - y
        if dx >= 0 && dy >= 0
      yield Seq(
        (center - dx, center - dy),
        (center - dy, center + 1 + dx),
        (center + 1 + dx, center + 1 + dy),
        (center + 1 + dy, center - dx)
      )
    } else {
      val center = dim.blockSize / 2
      val rotated = for
        (x, y) <- SudokuBoard.positions(dim)
        dx = center - x
        dy = center - y
        if dx >= 0 && dy > 0
      yield Seq(
        (center - dx, center - dy),
        (center - dy, center + dx),
        (center + dx, center + dy),
        (center + dy, center - dx)
      )
      List((center, center)) +: rotated
    }
  }
}
