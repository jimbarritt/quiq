package merkle.steps

import com.google.common.truth.Truth.assertThat
import io.cucumber.java8.En

class RootSteps(private val ctx: ScenarioContext) : En {
    init {
        When("I compute the Merkle root") {
            ctx.root = ctx.tree!!.root
        }

        When("I compute the Merkle root twice") {
            ctx.rootA = ctx.buildTree.execute(ctx.items).root
            ctx.rootB = ctx.buildTree.execute(ctx.items).root
        }

        When("I compute both Merkle roots") {
            ctx.rootA = ctx.buildTree.execute(ctx.items).root
            ctx.rootB = ctx.buildTree.execute(ctx.itemsB).root
        }

        Then("the root is a 64 character hex string") {
            assertThat(ctx.root!!.hex).hasLength(64)
            assertThat(ctx.root!!.hex).matches("[0-9a-f]+")
        }

        Then("both roots are equal") {
            assertThat(ctx.rootA).isEqualTo(ctx.rootB)
        }

        Then("the roots are different") {
            assertThat(ctx.rootA).isNotEqualTo(ctx.rootB)
        }
    }
}
