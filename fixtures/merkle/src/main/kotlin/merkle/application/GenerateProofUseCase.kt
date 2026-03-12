package merkle.application

import merkle.domain.MerkleProof
import merkle.domain.MerkleTree

class GenerateProofUseCase {
    fun execute(tree: MerkleTree, item: String): MerkleProof? = tree.proofFor(item)
}
