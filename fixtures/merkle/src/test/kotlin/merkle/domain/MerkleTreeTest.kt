package merkle.domain

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MerkleTreeTest {

    @Test
    fun `single item tree has root equal to hash of that item`() {
        val tree = MerkleTree.from(listOf("alice"))
        assertThat(tree.root).isEqualTo(MerkleHash.of("alice"))
    }

    @Test
    fun `two item tree root is hash of combined leaf hashes`() {
        val tree = MerkleTree.from(listOf("alice", "bob"))
        val expected = MerkleHash.combine(MerkleHash.of("alice"), MerkleHash.of("bob"))
        assertThat(tree.root).isEqualTo(expected)
    }

    @Test
    fun `same items always produce same root`() {
        val items = listOf("alice", "bob", "carol")
        assertThat(MerkleTree.from(items).root).isEqualTo(MerkleTree.from(items).root)
    }

    @Test
    fun `different items produce different roots`() {
        assertThat(MerkleTree.from(listOf("alice", "bob")).root)
            .isNotEqualTo(MerkleTree.from(listOf("alice", "carol")).root)
    }

    @Test
    fun `odd number of items is handled by duplicating last leaf`() {
        // Three items — last leaf is duplicated at the first level
        val tree = MerkleTree.from(listOf("alice", "bob", "carol"))
        assertThat(tree.root).isNotNull()
    }

    @Test
    fun `empty list throws`() {
        assertThrows<IllegalArgumentException> { MerkleTree.from(emptyList()) }
    }

    @Test
    fun `proof is null for item not in tree`() {
        val tree = MerkleTree.from(listOf("alice", "bob"))
        assertThat(tree.proofFor("dave")).isNull()
    }

    @Test
    fun `proof is non-null for item in tree`() {
        val tree = MerkleTree.from(listOf("alice", "bob", "carol"))
        assertThat(tree.proofFor("alice")).isNotNull()
    }
}
