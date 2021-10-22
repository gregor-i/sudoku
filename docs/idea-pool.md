# Idea Pool

## Infinite Sudoku

* to motivate the player:
  * keep a score of replaced parts
  * give the player a nice animation after replacing the filled part

## help with wrong inputs

* the player might have made a wrong input, and found that out too late. assist him in that situation.
* ideas:
  * add a stronger assistance mode, where all wrong inputs (not conflicting inputs) are highlighted
  * make the hint highlight such an input

## dark mode
 
* implement a dark mode 
  * use the browser / system property to select the theme

## tutorial

* create a tutorial to explain the game. maybe make it interactive.

## technical stuff

* write a custom json Codex for Sudoku boards
* puzzle state should know the solution to its puzzle
  * make the generator return the solution and the puzzle
  * use the solution for infinite sudoku checks. continue puzzle should return the new solution.
* better / smarter saving of the current puzzle
  * ie: starting a puzzle, exiting the add, reopening the add should show the same puzzle.
* evaluate performance of PerfectSolver.withShuffle vs. Generator.{swaps, shuffleValues}