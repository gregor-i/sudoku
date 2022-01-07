package model

import org.scalatest.compatible.Assertion
import org.scalatest.funsuite.AnyFunSuite

class PatternTest extends AnyFunSuite {
  val dim = Dimensions(3, 3)
  test("NoPattern") {
    assert(NoPattern.groups(dim).size == dim.boardSize)
  }

  test("MirrorXAxis") {
    val groups = MirrorXAxis.groups(dim)
    assert(groups.contains(List((0, 0), (8, 0))))
    assert(groups.contains(List((4, 0))))
  }

  test("MirrorYAxis") {
    val groups = MirrorYAxis.groups(dim)
    assert(groups.contains(List((0, 0), (0, 8))))
    assert(groups.contains(List((0, 4))))
  }

  test("RotationalSymmetry (even)") {
    val groups = RotationalSymmetry.groups(Dimensions(2, 2))
    assert(groups.contains(List((0, 0), (0, 3), (3, 3), (3, 0))))
    assert(groups.contains(List((1, 0), (0, 2), (2, 3), (3, 1))))
  }

  test("RotationalSymmetry (odd)") {
    val groups = RotationalSymmetry.groups(dim)
    assert(groups.contains(List((0, 0), (0, 8), (8, 8), (8, 0))))
    assert(groups.contains(List((4, 4))))
  }

  for {
    pattern <- Pattern.allPatterns
  } test(s"check soundness of $pattern") {
    for (dim <- DimensionExamples.examples) {
      val groups = pattern.groups(dim)
      // no position is repeated
      assert(groups.flatten.distinct == groups.flatten)
      // all postions are present
      assert(groups.flatten.size == dim.boardSize)
      // the groups together form the whole board
      assert(groups.flatten.sorted == SudokuBoard.positions(dim).sorted)
    }
  }
}
