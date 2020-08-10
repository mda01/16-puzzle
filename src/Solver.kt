import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Solver(private val board: Board) {
    private lateinit var solNode: SearchNode
    private var elapsedTime: Long = 0

    fun solve() {
        elapsedTime = kotlin.system.measureTimeMillis {
            var threshold = -1
            var s = 4000
            while (s != -1) {
                threshold++
                print("Threshold: ")
                println(threshold)
                val initNode = SearchNode(null, board, 0)
                s = search(initNode, threshold)
            }
        }
    }

    private fun search(node: SearchNode, threshold: Int): Int {
        val h = node.board.heuristic()
        val g = node.cost + h
        if (g > threshold)
            return g
        if (node.board.isGoal()) {
            solNode = node
            return -1
        }
        var mini = 4000
        for (board in node.board.children()) {
            if (node.parent == null || board != node.parent.board) {
                val s = search(SearchNode(node, board, node.cost + 1), threshold)
                mini = min(s, mini)
                if (mini == -1) break
            }
        }
        return mini
    }

    private fun parent(node: SearchNode): String {
        return if (node.parent == null) {
            node.board.toString() + '\n'
        } else {
            parent(node.parent) + node.board.toString() + '\n'
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        if (this::solNode.isInitialized) {
            sb.append(parent(solNode))
            sb.append("Moves count: ")
            sb.append(solNode.cost)
            sb.append("\nElapsed time: ")
            val d = elapsedTime.toDuration(DurationUnit.MILLISECONDS)
            sb.append(d.inHours.toInt())
            sb.append(" hours, ")
            sb.append(d.inMinutes.toInt()%60)
            sb.append(" minutes, ")
            sb.append(d.inSeconds.toInt()%60)
            sb.append(" seconds and ")
            sb.append(d.inMilliseconds.toInt()%1000)
            sb.append(" ms.")
        } else {
            sb.append("Problem still unsolved")
        }
        return sb.toString()
    }
}
