package model.solver

import algorithms.TreeTraversal
import model.*

import scala.util.Random

object PerfectSolver extends Solver {
  override def apply(puzzle: OpenSudokuBoard): SolverResult = {
    val root = SolverNode.initial(puzzle)

    SolverResult.fromLazyList(
      TreeTraversal.traverseLeaves(root, children).flatMap(t => Validate(t.board))
    )
  }

  def withShuffle(seed: Int) = new Solver {
    override def apply(puzzle: OpenSudokuBoard): SolverResult = {
      val random = new Random(seed)
      val root   = SolverNode.initial(puzzle)

      SolverResult.fromLazyList(
        TreeTraversal.traverseLeaves(root, children andThen shuffle(random)).flatMap(t => Validate(t.board))
      )
    }

    private final def shuffle[A](random: Random)(nodes: List[A]): List[A] =
      if (nodes.sizeCompare(1) > 0)
        random.shuffle(nodes)
      else
        nodes
  }

  private final def children(node: SolverNode): List[SolverNode] =
    node.openPositions
      .minByOption(node.options.get(_).size)
      .toList
      .flatMap {
        pos =>
          for (option <- node.options.get(pos))
            yield SolverNode.setValue(node, pos, option)
      }

}
