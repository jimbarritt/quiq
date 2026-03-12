package merkle.application

import merkle.domain.MerkleTree

class BuildTreeUseCase {
    fun execute(items: List<String>): MerkleTree = MerkleTree.from(items)
}
