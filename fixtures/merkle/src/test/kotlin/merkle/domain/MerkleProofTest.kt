package merkle.domain

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class MerkleProofTest {

    private val items = listOf("alice", "bob", "carol", "dave")
    private val tree = MerkleTree.from(items)

    @Test
    fun `valid proof verifies successfully`() {
        items.forEach { item ->
            val proof = tree.proofFor(item)!!
            assertThat(proof.verify(item, tree.root)).isTrue()
        }
    }

    @Test
    fun `proof for wrong item does not verify`() {
        val proof = tree.proofFor("alice")!!
        assertThat(proof.verify("bob", tree.root)).isFalse()
    }

    @Test
    fun `proof against wrong root does not verify`() {
        val proof = tree.proofFor("alice")!!
        val wrongRoot = MerkleTree.from(listOf("x", "y")).root
        assertThat(proof.verify("alice", wrongRoot)).isFalse()
    }

    @Test
    fun `round trip generate and verify for odd-sized tree`() {
        val oddTree = MerkleTree.from(listOf("alice", "bob", "carol"))
        listOf("alice", "bob", "carol").forEach { item ->
            val proof = oddTree.proofFor(item)!!
            assertThat(proof.verify(item, oddTree.root)).isTrue()
        }
    }

    @Test
    fun `single item tree proof verifies`() {
        val tree = MerkleTree.from(listOf("alice"))
        val proof = tree.proofFor("alice")!!
        assertThat(proof.verify("alice", tree.root)).isTrue()
    }

    @Test
    fun `single item proof has no steps`() {
        val tree = MerkleTree.from(listOf("alice"))
        val proof = tree.proofFor("alice")!!
        assertThat(proof.steps).isEmpty()
    }
}
