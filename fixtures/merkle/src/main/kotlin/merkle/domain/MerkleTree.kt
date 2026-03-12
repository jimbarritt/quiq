package merkle.domain

@ConsistentCopyVisibility
data class MerkleTree private constructor(
    private val levels: List<List<MerkleHash>>
) {
    val root: MerkleHash get() = levels.last().single()

    companion object {
        fun from(items: List<String>): MerkleTree {
            require(items.isNotEmpty()) { "Cannot build tree from empty list" }
            val leaves = items.map { MerkleHash.of(it) }
            val levels = mutableListOf(leaves)
            var current = leaves
            while (current.size > 1) {
                val padded = if (current.size % 2 == 1) current + current.last() else current
                current = padded.chunked(2) { (left, right) -> MerkleHash.combine(left, right) }
                levels.add(current)
            }
            return MerkleTree(levels)
        }
    }

    fun proofFor(item: String): MerkleProof? {
        val target = MerkleHash.of(item)
        var index = levels[0].indexOf(target)
        if (index == -1) return null

        val steps = mutableListOf<ProofStep>()
        for (level in levels.dropLast(1)) {
            val padded = if (level.size % 2 == 1) level + level.last() else level
            val isLeft = index % 2 == 0
            val siblingIndex = if (isLeft) index + 1 else index - 1
            val side = if (isLeft) Side.RIGHT else Side.LEFT
            steps.add(ProofStep(padded[siblingIndex], side))
            index /= 2
        }
        return MerkleProof(steps)
    }
}
