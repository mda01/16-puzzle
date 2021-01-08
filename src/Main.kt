fun main() {
//    val initB = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
//    val initB = arrayOf(4, 13, 7, 11, 5, 10, 3, 8, 9, 12, 16, 2, 6, 14, 1, 15)
//    val initB = arrayOf(1, 2, 7, 4, 5, 6, 3, 8, 9, 10, 11, 12, 13, 14, 15, 16)
//    val initB = arrayOf(7, 5, 12, 15, 1, 11, 8, 4, 2, 14, 16, 6, 9, 10, 3, 13)


    val b = Board("puzzle/puzzlemystery.txt")
    println(b)
//    println(b.colConflict(2))
    val s = Solver(b)
    s.solve()
    print(s)
}