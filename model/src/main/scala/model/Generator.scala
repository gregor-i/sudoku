package model

import scala.util.Random
import scala.util.chaining._

object Generator {
  private type Permutation = (Position, Position)

  def apply(dim: Dimensions, seed: Int, difficulty: Difficulty): OpenSudokuBoard = {
    val random = new Random(seed)
    initialBoard(dim)
      .pipe(permute(random.nextInt(), _))
      .pipe(makePuzzle(random.nextInt(), _, difficulty))
  }

  // See: https://gamedev.stackexchange.com/a/138228
  def initialValue(dim: Dimensions)(pos: Position): Int = {
    val shift = (1 to pos._2).map {
      case i if i % dim.height == 0 => 1
      case _ => dim.width
    }.sum

    (pos._1 + shift) % dim.blockSize + 1
  }

  def initialBoard(dim: Dimensions): SolvedSudokuBoard =
    SudokuBoard.fill(dim)(initialValue(dim))

  def permute[S](seed: Int, board: SudokuBoard[S]): SudokuBoard[S] =
    permutations(seed, board.dim).foldLeft(board) {
      case (b, (pos1, pos2)) =>
        b.set(pos1, b.get(pos2)).set(pos2, b.get(pos1))
    }

  private def permutations(seed: Int, dim: Dimensions): Seq[Permutation] = {
    def permutationsOfColumns(c1: Int, c2: Int, dim: Dimensions): Seq[Permutation] =
      SudokuBoard.column(c1)(dim) zip SudokuBoard.column(c2)(dim)

    def permutationsOfColumnsInsideOfBlocks(random: Random, dim: Dimensions): Seq[Permutation] =
      for {
        block <- 0 until dim.height
        shuffled = random.shuffle((0 until dim.width): IndexedSeq[Int])
        column <- 0 until dim.width
        swapWith = shuffled(column)
        if swapWith > column
        permutations <- permutationsOfColumns(block * dim.width + column, block * dim.width + swapWith, dim)
      } yield permutations

    def permutationsOfColumnsOfBlocks(random: Random, dim: Dimensions): Seq[Permutation] = {
      val shuffled = random.shuffle((0 until dim.height): IndexedSeq[Int])
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

    def permutationsOfRowsInsideOfBlocks(random: Random, dim: Dimensions): Seq[Permutation] =
      transposeGenerator(permutationsOfColumnsInsideOfBlocks(random, _), dim)

    def permutationsOfRowsOfBlocks(random: Random, dim: Dimensions): Seq[Permutation] =
      transposeGenerator(permutationsOfColumnsOfBlocks(random, _), dim)

    val random = new Random(seed)
    permutationsOfColumnsInsideOfBlocks(random, dim) ++
      permutationsOfColumnsOfBlocks(random, dim) ++
      permutationsOfRowsInsideOfBlocks(random, dim) ++
      permutationsOfRowsOfBlocks(random, dim)
  }

  private def makePuzzle(
      seed: Int,
      solvedBoard: SolvedSudokuBoard,
      difficulty: Difficulty
  ): OpenSudokuBoard = {
    val random            = new Random(seed)
    val shuffledPositions = SudokuBoard.positions(solvedBoard.dim).pipe(random.shuffle(_))
    val board             = solvedBoard.map[Option[Int]](Some.apply)
    val solver            = Solver.forDifficulty(difficulty)

    shuffledPositions.foldLeft(board) { (board, position) =>
      val reducedBoard = board.set(position, None)
      if (solver(reducedBoard).uniqueSolution.isDefined)
        reducedBoard
      else
        board
    }
  }
}
