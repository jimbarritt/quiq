package merkle.domain

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class MerkleHashTest {

    @Test
    fun `hash of same input produces same hash`() {
        assertThat(MerkleHash.of("alice")).isEqualTo(MerkleHash.of("alice"))
    }

    @Test
    fun `hash of different inputs produces different hashes`() {
        assertThat(MerkleHash.of("alice")).isNotEqualTo(MerkleHash.of("bob"))
    }

    @Test
    fun `hash is 64 character hex string`() {
        val hash = MerkleHash.of("alice")
        assertThat(hash.hex).hasLength(64)
        assertThat(hash.hex).matches("[0-9a-f]+")
    }

    @Test
    fun `combine is deterministic`() {
        val left = MerkleHash.of("alice")
        val right = MerkleHash.of("bob")
        assertThat(MerkleHash.combine(left, right)).isEqualTo(MerkleHash.combine(left, right))
    }

    @Test
    fun `combine is not commutative`() {
        val left = MerkleHash.of("alice")
        val right = MerkleHash.of("bob")
        assertThat(MerkleHash.combine(left, right)).isNotEqualTo(MerkleHash.combine(right, left))
    }
}
