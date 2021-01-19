package algorithms

import org.scalatest.funsuite.AnyFunSuite

class TreeTraversalTest extends AnyFunSuite {
  sealed trait BinaryTree[A] { def value: A }
  case class Node[A](value: A, left: BinaryTree[A], right: BinaryTree[A]) extends BinaryTree[A]
  case class Leaf[A](value: A)                                            extends BinaryTree[A]

  val exampleTree: BinaryTree[String] = Node("1.1", Node("2.1", Leaf("3.1"), Leaf("3.2")), Node("2.2", Leaf("3.3"), Leaf("3.4")))

  def children[A](binaryTree: BinaryTree[A]): Seq[BinaryTree[A]] = binaryTree match {
    case Node(_, left, right) => Seq(left, right)
    case Leaf(_)              => Seq.empty
  }

  test("depth first search") {
    val dfs = TreeTraversal.depthFirstSearch[BinaryTree[String]](exampleTree, children)
    assert(dfs.map(_.value) == LazyList("1.1", "2.1", "3.1", "3.2", "2.2", "3.3", "3.4"))
  }

  test("traverse leaves") {
    val dfs = TreeTraversal.traverseLeaves[BinaryTree[String]](exampleTree, children)
    assert(dfs.map(_.value) == LazyList("3.1", "3.2", "3.3", "3.4"))
  }

  test("breadth first search") {
    val dfs = TreeTraversal.breadthFirstSearch[BinaryTree[String]](exampleTree, children)
    assert(dfs.map(_.value) == LazyList("1.1", "2.1", "2.2", "3.1", "3.2", "3.3", "3.4"))
  }
}
