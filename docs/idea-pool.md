# Idea Pool

## Infinit Sudoku

* make the game go on forever
* if a certain part of the board is filled (and correct), replace this part with a new sudoku.
  * the part may be something like the first 3 rows or last 3 columns 
  * get inspiration from tetris. completed rows are removed and all blocks above move down.  
* to motivate the player:
  * keep a score of replaced parts
  * give the player a nice animation after replacing the filled part
* open questions:
  * what to do about mistakes?
  * what seed should be used for the new sudoku-part?
  * how big must the filled part be, to create a new (and different) sudoku-part?

## tutorial

* create a tutorial to explain the game. maybe make it interactive.

## ux enhancements

* the context menu should fade out

## technical stuff

* Page.acceptsState uses reflection. remove that and make something nicer.
* write a custom json Codex for Sudoku boards
