package model

import model.solver.IteratorSolver
import org.scalatest.funsuite.AnyFunSuite

class IteratorSolverTest extends AnyFunSuite with SolverSuite {
  testSolver(IteratorSolver)
}
