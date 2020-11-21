package model

import scala.util.Random
import scala.util.chaining._

object Generator {
  type Permutation = ((Int, Int), (Int, Int))

  def apply(dim: Dimensions, seed: Int): OpenSudokuBoard = {
    val random = new Random(seed)
    initialBoard(dim)
      .pipe(permutate(random.nextInt(), _))
      .pipe(makePuzzle(random.nextInt(), _))
  }

  def initialBoard(dim: Dimensions): SolvedSudokuBoard = Solver.apply(SudokuBoard.empty(dim)).head

  def permutate[S](seed: Int, board: SudokuBoard[S]): SudokuBoard[S] =
    permutations(seed, board.dim).foldLeft(board) {
      case (b, (pos1, pos2)) =>
        b.set(pos1, b.get(pos2)).set(pos2, b.get(pos1))
    }

  private def permutations(seed: Int, dim: Dimensions): Seq[Permutation] = {
    def permutationsOfColumns(c1: Int, c2: Int, dim: Dimensions): Seq[Permutation] =
      SudokuBoard.column(c1)(dim) zip SudokuBoard.column(c2)(dim)

    def permutationsOfColumnsInsideOfBlocks(seed: Int, dim: Dimensions): Seq[Permutation] =
      for {
        block <- 0 until dim.height
        shuffled = new Random(seed).shuffle((0 until dim.width): IndexedSeq[Int])
        column <- 0 until dim.width
        swapWith = shuffled(column)
        if swapWith > column
        permutations <- permutationsOfColumns(block * dim.width + column, block * dim.width + swapWith, dim)
      } yield permutations

    def permutationsOfColumnsOfBlocks(seed: Int, dim: Dimensions): Seq[Permutation] = {
      val shuffled = new Random(seed).shuffle((0 until dim.height): IndexedSeq[Int])
      for {
        block <- 0 until dim.height
        swapWith = shuffled(block)
        if swapWith > block
        column      <- 0 until dim.width
        permutation <- permutationsOfColumns(block * dim.width + column, swapWith * dim.width + column, dim)
      } yield permutation
    }

    def transposeGenerator(generator: Dimensions => Seq[Permutation], dim: Dimensions): Seq[Permutation] =
      generator(Dimensions(width = dim.height, height = dim.width))
        .map { case (p1, p2) => (p1.swap, p2.swap) }

    def permutationsOfRowsInsideOfBlocks(seed: Int, dim: Dimensions): Seq[Permutation] =
      transposeGenerator(permutationsOfColumnsInsideOfBlocks(seed, _), dim)

    def permutationsOfRowsOfBlocks(seed: Int, dim: Dimensions): Seq[Permutation] =
      transposeGenerator(permutationsOfColumnsOfBlocks(seed, _), dim)

    val random = new Random(seed)
    permutationsOfColumnsInsideOfBlocks(random.nextInt(), dim) ++
      permutationsOfColumnsOfBlocks(random.nextInt(), dim) ++
      permutationsOfRowsInsideOfBlocks(random.nextInt(), dim) ++
      permutationsOfRowsOfBlocks(random.nextInt(), dim)
  }

  private def makePuzzle(seed: Int, solvedBoard: SolvedSudokuBoard): OpenSudokuBoard = {
    val random            = new Random(seed)
    val shuffledPositions = SudokuBoard.positions(solvedBoard.dim).pipe(random.shuffle(_))
    val board             = solvedBoard.map[Option[Int]](Some.apply)

    shuffledPositions.foldLeft(board) { (board, position) =>
      val reducedBoard = board.set(position, None)
      if (Solver(reducedBoard).sizeCompare(1) == 0)
        reducedBoard
      else
        board
    }
  }
}
