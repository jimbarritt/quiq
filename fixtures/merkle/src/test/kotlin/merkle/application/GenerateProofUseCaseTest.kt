package merkle.application

import com.google.common.truth.Truth.assertThat
import merkle.domain.MerkleTree
import org.junit.jupiter.api.Test

class GenerateProofUseCaseTest {

    private val useCase = GenerateProofUseCase()
    private val tree = MerkleTree.from(listOf("alice", "bob", "carol", "dave"))

    @Test
    fun `returns proof for known item`() {
        assertThat(useCase.execute(tree, "alice")).isNotNull()
    }

    @Test
    fun `returns null for unknown item`() {
        assertThat(useCase.execute(tree, "eve")).isNull()
    }
}
