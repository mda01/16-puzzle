fun main() {
//    val initB = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
//    val initB = arrayOf(4, 13, 7, 11, 5, 10, 3, 8, 9, 12, 16, 2, 6, 14, 1, 15)
//    val initB = arrayOf(1, 2, 7, 4, 5, 6, 3, 8, 9, 10, 11, 12, 13, 14, 15, 16)
    val initB = arrayOf(5, 11, 14, 6, 2, 8, 12, 16, 10, 15, 4, 13, 3, 1, 9, 7)


    val b = Board("puzzle/puzzle4x4-78.txt")
    println(b)
//    println(b.colConflict(2))
    val s = Solver(b)
    s.solve()
    print(s)
}