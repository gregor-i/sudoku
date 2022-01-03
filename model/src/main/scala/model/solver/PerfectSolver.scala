package model.solver

import algorithms.TreeTraversal
import model.*
import model.solver.PerfectSolver.allSolutions

import scala.util.Random

trait PerfectSolver {
  def allSolutions(puzzle: OpenSudokuBoard): LazyList[FilledSudokuBoard]

  final def uniqueSolution(puzzle: OpenSudokuBoard): Option[FilledSudokuBoard] =
    allSolutions(puzzle) match {
      case LazyList(solution) => Some(solution)
      case _                  => None
    }
}

object PerfectSolver extends PerfectSolver {
  def allSolutions(puzzle: OpenSudokuBoard): LazyList[FilledSudokuBoard] = {
    val root = SolverNode.initial(puzzle)

    TreeTraversal.traverseLeaves(root, children).flatMap(t => Validate(t.board))
  }

  def withShuffle(seed: Long) = new PerfectSolver {
    override def allSolutions(puzzle: OpenSudokuBoard): LazyList[FilledSudokuBoard] = {
      val random = new Random(seed)
      val root   = SolverNode.initial(puzzle)

      TreeTraversal.traverseLeaves(root, children andThen shuffle(random)).flatMap(t => Validate(t.board))
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
