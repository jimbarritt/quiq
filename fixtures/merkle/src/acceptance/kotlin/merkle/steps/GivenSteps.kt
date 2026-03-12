package merkle.steps

import io.cucumber.java8.En

class GivenSteps(private val ctx: ScenarioContext) : En {
    init {
        Given("the items {string} and {string}") { a: String, b: String ->
            ctx.items = listOf(a, b)
            ctx.tree = ctx.buildTree.execute(ctx.items)
        }

        Given("the items {string}, {string}, and {string}") { a: String, b: String, c: String ->
            ctx.items = listOf(a, b, c)
            ctx.tree = ctx.buildTree.execute(ctx.items)
        }

        Given("the items {string}, {string}, {string}, and {string}") { a: String, b: String, c: String, d: String ->
            ctx.items = listOf(a, b, c, d)
            ctx.tree = ctx.buildTree.execute(ctx.items)
        }

        Given("the items {string} and {string} as second set") { a: String, b: String ->
            ctx.itemsB = listOf(a, b)
        }
    }
}
