# Chess - Assignment 1
**Course:** CS 213  

## Project Overview
A two-player text-based Chess game implemented in Java 21. This project focuses on Object-Oriented Design (OOD) and follows specific constraints for automated grading via Autolab.

## Features Implemented
- **Basic Movement:** All pieces (Pawn, Rook, Knight, Bishop, Queen, King).
- **Special Moves:**
    - Castling (King-side and Queen-side).
    - Enpassant.
    - Pawn Promotion (Defaults to Queen).
- **Game Logic:**
    - Check detection.
    - Checkmate detection.
    - Illegal move handling (including moving into check).
    - Resignation and Draw offers.

## Directory Structure
The autograder requires a specific package structure. Ensure all source files reside in the `chess` package.

```text
.
├── README.md
└── chess/
    ├── Chess.java            
    ├── ReturnPiece.java      
    ├── ReturnPlay.java       
    ├── PlayChess.java
    ├── Pieces.java
    ├── (Rook, Queen, Pawn, Bishop, Knight).java         
    └── GameLogic.java
```
## Compile and Run
```bash
javac chess/*.java
```
```bash
java chess.PlayChess
```
