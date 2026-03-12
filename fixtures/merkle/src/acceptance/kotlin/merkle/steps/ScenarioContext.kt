package merkle.steps

import merkle.application.BuildTreeUseCase
import merkle.application.GenerateProofUseCase
import merkle.application.VerifyProofUseCase
import merkle.domain.MerkleHash
import merkle.domain.MerkleProof
import merkle.domain.MerkleTree

class ScenarioContext {
    val buildTree = BuildTreeUseCase()
    val generateProof = GenerateProofUseCase()
    val verifyProof = VerifyProofUseCase()

    var items: List<String> = emptyList()
    var itemsB: List<String> = emptyList()
    var tree: MerkleTree? = null

    var root: MerkleHash? = null
    var rootA: MerkleHash? = null
    var rootB: MerkleHash? = null

    var proof: MerkleProof? = null
    var proofs: Map<String, MerkleProof?> = emptyMap()

    var verificationResult: Boolean = false
    var verificationResults: Map<String, Boolean> = emptyMap()
}
