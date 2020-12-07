package model

import model.solver.FPSolver
import org.scalatest.funsuite.AnyFunSuite

class FPSolverTest extends AnyFunSuite with SolverSuite {
  testSolver(FPSolver)
}
