package merkle.steps

import com.google.common.truth.Truth.assertThat
import io.cucumber.java8.En

class ProofSteps(private val ctx: ScenarioContext) : En {
    init {
        When("I generate a proof for {string}") { item: String ->
            ctx.proof = ctx.generateProof.execute(ctx.tree!!, item)
        }

        When("I generate a proof for each item") {
            ctx.proofs = ctx.items.associateWith { ctx.generateProof.execute(ctx.tree!!, it) }
        }

        Then("a proof is returned") {
            assertThat(ctx.proof).isNotNull()
        }

        Then("no proof is returned") {
            assertThat(ctx.proof).isNull()
        }

        Then("a proof is returned for each item") {
            ctx.proofs.values.forEach { assertThat(it).isNotNull() }
        }
    }
}
