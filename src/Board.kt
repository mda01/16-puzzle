import java.io.File
import kotlin.math.abs

class Board {
    // les 16 uint
    private val tiles: Array<UInt>
    private val goalBoard: Array<UInt> =
        arrayOf(1u, 2u, 4u, 8u, 16u, 32u, 64u, 128u, 256u, 512u, 1024u, 2048u, 4096u, 8192u, 16384u, 32768u)
    /*
0 1 2 3
4 5 6 7
8 9 10 11
12 13 14 15
     */

    constructor () {
        val line1 = readLine()
        val line2 = readLine()
        val line3 = readLine()
        val line4 = readLine()
        val initBoard = ArrayList<Int>()
        line1!!.split(" ").forEach { initBoard.add(it.toInt() - 1) }
        line2!!.split(" ").forEach { initBoard.add(it.toInt() - 1) }
        line3!!.split(" ").forEach { initBoard.add(it.toInt() - 1) }
        line4!!.split(" ").forEach { initBoard.add(it.toInt() - 1) }

        tiles = arrayOf(0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u)

        for (i in 0..15) {
            tiles[initBoard[i]] = 1u shl i
        }
    }

    constructor(tiles: Array<UInt>) {
        this.tiles = tiles
    }

    constructor(tiles: Array<Int>) {
        this.tiles = goalBoard.clone()
        for (i in 0..15) {
            this.tiles[tiles[i] - 1] = 1u shl i
        }
    }

    constructor(filePath: String) {
        val f = File(filePath)
        val ar = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        var i = 0
        for (line in f.readLines()) {
            val l = line.trim().split("  ", " ")
            if (l.size == 4) {
                for (el in l) {
                    if(el.toInt() != 0)
                        ar[i] = el.toInt()
                    else
                        ar[i] = 16
                    i++
                }
            }
        }
        this.tiles = Board(ar).tiles
    }

    override fun toString(): String {
        val sb: StringBuilder = java.lang.StringBuilder()
        for (i in 0..3) {
            sb.append("| ")
            for (j in 0..3) {
                for (tile in tiles) {
                    if (tile.countTrailingZeroBits() == 4 * i + j) {
                        val ind = tiles.indexOf(tile) + 1
                        if (ind < 10)
                            sb.append("0")
                        if (ind == 16)
                            sb.append("XX")
                        else
                            sb.append(ind)
                        sb.append(" | ")
                    }
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    private fun moveLeft(): Board? {
        val t2 = tiles.clone()
        t2[15] = t2[15] shr 1
        return if (t2[15] and 0b1111011101110111u == 0u) {
            null
        } else {
            for (i in 0..14) if (t2[i] and t2[15] != 0u) {
                t2[i] = t2[i] shl 1
                return Board(t2)
            }
            throw InvalidBoardException("Illegal left move")
        }
    }

    private fun moveRight(): Board? {
        val t2 = tiles.clone()
        t2[15] = t2[15] shl 1
        return if (t2[15] and 0b1110111011101110u != 0u) {
            for (i in 0..14) if (t2[i] and t2[15] != 0u) {
                t2[i] = t2[i] shr 1
                return Board(t2)
            }
            throw InvalidBoardException("Illegal right move")
        } else {
            null
        }
    }

    private fun moveTop(): Board? {
        val t2 = tiles.clone()
        t2[15] = t2[15] shr 4
        return if (t2[15] != 0u) {
            for (i in 0..14) if (t2[i] and t2[15] != 0u) {
                t2[i] = t2[i] shl 4
                return Board(t2)
            }
            throw InvalidBoardException("Illegal top move")
        } else {
            null
        }
    }

    private fun moveBot(): Board? {
        val t2 = tiles.clone()
        t2[15] = t2[15] shl 4
        return if (t2[15] and 0b1111111111111111u != 0u) {
            for (i in 0..14) if (t2[i] and t2[15] != 0u) {
                t2[i] = t2[i] shr 4
                return Board(t2)
            }
            throw InvalidBoardException("Illegal bot move")
        } else {
            null
        }
    }

    fun children(): ArrayList<Board> {
        val res = ArrayList<Board>()
        val ml = moveLeft()
        val mr = moveRight()
        val mt = moveTop()
        val mb = moveBot()
        if (ml != null)
            res += ml
        if (mr != null)
            res += mr
        if (mt != null)
            res += mt
        if (mb != null)
            res += mb
        return res
    }

    fun isGoal(): Boolean {
        for (i in 0..14)
            if (tiles[i] and goalBoard[i] == 0u) return false
        return true
    }

    private fun computeX(tile: UInt): Int = when {
        tile and 0b0001000100010001u != 0u -> 0
        tile and 0b0010001000100010u != 0u -> 1
        tile and 0b0100010001000100u != 0u -> 2
        else -> 3
    }

    private fun computeY(tile: UInt): Int = when {
        tile and 0b1111u != 0u -> 0
        tile and 0b11110000u != 0u -> 1
        tile and 0b111100000000u != 0u -> 2
        else -> 3
    }

    private fun manhattan(tile: UInt, goal: UInt): Int {
        if (tile == goal) return 0
        val xTile = computeX(tile)
        val xGoal = computeX(goal)
        val yTile = computeY(tile)
        val yGoal = computeY(goal)
        return abs(xGoal - xTile) + abs(yGoal - yTile)
    }

    private fun manhattanHeuristic(): Int {
        var res = 0
        for (i in 0..14) {
            res += manhattan(tiles[i], goalBoard[i])
        }
        return res
    }

    private fun linConflict(lineNum: Int): Int {
        val n = 4 * lineNum
        var res = 0
        val mask = 0b1111u shl 4 * lineNum
        if (lineNum != 3) {
            val t2: UInt = (tiles[n + 1] or tiles[n + 2] or tiles[n + 3]) and mask
            if (tiles[n] and mask != 0u && t2 != 0u && tiles[n] > t2) res += 2

            val t1: UInt = (tiles[n + 2] or tiles[n + 3]) and mask
            if (tiles[n + 1] and mask != 0u && t1 != 0u && tiles[n + 1] > t1) res += 2

            if (tiles[n + 2] and mask != 0u && tiles[n + 3] and mask != 0u && tiles[n + 2] > tiles[n + 3]) res += 2
        } else {
            val t1: UInt = (tiles[n + 1] or tiles[n + 2]) and mask
            if (tiles[n] and mask != 0u && t1 != 0u && tiles[n] > t1) res += 2

            if (tiles[n + 1] and mask != 0u && tiles[n + 2] and mask != 0u && tiles[n + 1] > tiles[n + 2]) res += 2
        }
        return res
    }

    fun colConflict(colNum: Int): Int {
        var res = 0
        val mask = 0b1000100010001u shl colNum
        if (colNum != 3) {
            val t2: UInt = (tiles[colNum + 4] or tiles[colNum + 8] or tiles[colNum + 12]) and mask
            if (tiles[colNum] and mask != 0u && t2 != 0u && tiles[colNum] > t2) res += 2

            val t1: UInt = (tiles[colNum + 8] or tiles[colNum + 12]) and mask
            if (tiles[colNum + 4] and mask != 0u && t1 != 0u && tiles[colNum + 4] > t1) res += 2

            if (tiles[colNum + 8] and mask != 0u && tiles[colNum + 12] and mask != 0u && tiles[colNum + 8] > tiles[colNum + 12]) res += 2
        } else {
            val t1: UInt = (tiles[colNum + 4] or tiles[colNum + 8]) and mask
            if (tiles[colNum] and mask != 0u && t1 != 0u && tiles[colNum] > t1) res += 2

            if (tiles[colNum + 4] and mask != 0u && tiles[colNum + 4] and mask != 0u && tiles[colNum + 4] > tiles[colNum + 8]) res += 2
        }
        return res
    }

    private fun linConflictsHeuristic() = (linConflict(0) + linConflict(1) + linConflict(2) + linConflict(3))
    //+ colConflict(0) + colConflict(1) + colConflict(2) + colConflict(3))

    fun heuristic(): Int {
        return manhattanHeuristic() + linConflictsHeuristic()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (!tiles.contentEquals(other.tiles)) return false

        return true
    }

    override fun hashCode(): Int {
        return tiles.contentHashCode()
    }
}


