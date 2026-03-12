package merkle.application

import com.google.common.truth.Truth.assertThat
import merkle.domain.MerkleHash
import org.junit.jupiter.api.Test

class BuildTreeUseCaseTest {

    private val useCase = BuildTreeUseCase()

    @Test
    fun `builds tree and exposes root`() {
        val tree = useCase.execute(listOf("alice", "bob"))
        val expected = MerkleHash.combine(MerkleHash.of("alice"), MerkleHash.of("bob"))
        assertThat(tree.root).isEqualTo(expected)
    }

    @Test
    fun `same inputs always produce same root`() {
        val items = listOf("alice", "bob", "carol")
        assertThat(useCase.execute(items).root).isEqualTo(useCase.execute(items).root)
    }
}
