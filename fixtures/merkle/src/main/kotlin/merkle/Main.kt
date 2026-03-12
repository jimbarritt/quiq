package merkle

import merkle.adapters.cli.CliInputAdapter
import merkle.adapters.cli.Command
import merkle.adapters.output.StdoutOutputAdapter
import merkle.application.BuildTreeUseCase
import merkle.application.GenerateProofUseCase
import merkle.application.VerifyProofUseCase
import merkle.domain.MerkleHash
import merkle.domain.MerkleProof
import merkle.domain.ProofStep
import merkle.domain.Side

fun main(args: Array<String>) {
    val stdin = generateSequence(::readLine).toList()
    val command = CliInputAdapter.parse(args, stdin)

    val buildTree = BuildTreeUseCase()
    val generateProof = GenerateProofUseCase()
    val verifyProof = VerifyProofUseCase()

    when (command) {
        is Command.Root -> {
            val tree = buildTree.execute(command.items)
            StdoutOutputAdapter.printRoot(tree.root)
        }
        is Command.Proof -> {
            val tree = buildTree.execute(command.items)
            val proof = generateProof.execute(tree, command.item)
            StdoutOutputAdapter.printProof(proof)
        }
        is Command.Verify -> {
            val tree = buildTree.execute(command.items)
            val steps = command.proof.split(",").map { step ->
                val (side, hash) = step.split(":", limit = 2)
                ProofStep(MerkleHash(hash), Side.valueOf(side.uppercase()))
            }
            val proof = MerkleProof(steps)
            val valid = verifyProof.execute(proof, command.item, tree.root)
            StdoutOutputAdapter.printVerification(valid)
        }
    }
}
