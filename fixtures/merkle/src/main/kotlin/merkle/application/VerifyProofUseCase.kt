package merkle.application

import merkle.domain.MerkleHash
import merkle.domain.MerkleProof

class VerifyProofUseCase {
    fun execute(proof: MerkleProof, item: String, root: MerkleHash): Boolean =
        proof.verify(item, root)
}
