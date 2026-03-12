package merkle.steps

import com.google.common.truth.Truth.assertThat
import io.cucumber.java8.En

class VerifySteps(private val ctx: ScenarioContext) : En {
    init {
        When("I generate and verify the proof for {string}") { item: String ->
            val proof = ctx.generateProof.execute(ctx.tree!!, item)!!
            ctx.verificationResult = ctx.verifyProof.execute(proof, item, ctx.tree!!.root)
        }

        When("I generate a proof for {string} but verify it for {string}") { generated: String, verified: String ->
            val proof = ctx.generateProof.execute(ctx.tree!!, generated)!!
            ctx.verificationResult = ctx.verifyProof.execute(proof, verified, ctx.tree!!.root)
        }

        When("I generate and verify the proof for each item") {
            ctx.verificationResults = ctx.items.associateWith { item ->
                val proof = ctx.generateProof.execute(ctx.tree!!, item)!!
                ctx.verifyProof.execute(proof, item, ctx.tree!!.root)
            }
        }

        Then("the proof is valid") {
            assertThat(ctx.verificationResult).isTrue()
        }

        Then("the proof is invalid") {
            assertThat(ctx.verificationResult).isFalse()
        }

        Then("all proofs are valid") {
            ctx.verificationResults.values.forEach { assertThat(it).isTrue() }
        }
    }
}
