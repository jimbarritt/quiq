package merkle.adapters.output

import merkle.domain.MerkleHash
import merkle.domain.MerkleProof

object StdoutOutputAdapter {

    fun printRoot(root: MerkleHash) = println(root.hex)

    fun printProof(proof: MerkleProof?) {
        if (proof == null) {
            println("item not found in tree")
            return
        }
        println(proof.steps.joinToString(",") { "${it.side.name.lowercase()}:${it.hash.hex}" })
    }

    fun printVerification(valid: Boolean) = println(if (valid) "valid" else "invalid")
}
