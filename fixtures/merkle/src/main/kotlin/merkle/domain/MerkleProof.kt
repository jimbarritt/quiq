package merkle.domain

enum class Side { LEFT, RIGHT }

data class ProofStep(val hash: MerkleHash, val side: Side)

data class MerkleProof(val steps: List<ProofStep>) {

    fun verify(item: String, root: MerkleHash): Boolean {
        var current = MerkleHash.of(item)
        for (step in steps) {
            current = when (step.side) {
                Side.LEFT -> MerkleHash.combine(step.hash, current)
                Side.RIGHT -> MerkleHash.combine(current, step.hash)
            }
        }
        return current == root
    }
}
