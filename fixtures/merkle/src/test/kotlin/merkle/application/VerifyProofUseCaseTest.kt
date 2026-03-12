package merkle.application

import com.google.common.truth.Truth.assertThat
import merkle.domain.MerkleTree
import org.junit.jupiter.api.Test

class VerifyProofUseCaseTest {

    private val buildTree = BuildTreeUseCase()
    private val generateProof = GenerateProofUseCase()
    private val verifyProof = VerifyProofUseCase()

    @Test
    fun `valid proof returns true`() {
        val tree = buildTree.execute(listOf("alice", "bob", "carol", "dave"))
        val proof = generateProof.execute(tree, "carol")!!
        assertThat(verifyProof.execute(proof, "carol", tree.root)).isTrue()
    }

    @Test
    fun `tampered item returns false`() {
        val tree = buildTree.execute(listOf("alice", "bob", "carol", "dave"))
        val proof = generateProof.execute(tree, "alice")!!
        assertThat(verifyProof.execute(proof, "mallory", tree.root)).isFalse()
    }

    @Test
    fun `proof verifies for all items in tree`() {
        val items = listOf("alice", "bob", "carol", "dave", "eve")
        val tree = buildTree.execute(items)
        items.forEach { item ->
            val proof = generateProof.execute(tree, item)!!
            assertThat(verifyProof.execute(proof, item, tree.root)).isTrue()
        }
    }
}
