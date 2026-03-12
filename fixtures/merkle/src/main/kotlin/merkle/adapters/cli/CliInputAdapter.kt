package merkle.adapters.cli

sealed interface Command {
    data class Root(val items: List<String>) : Command
    data class Proof(val items: List<String>, val item: String) : Command
    data class Verify(val items: List<String>, val item: String, val proof: String) : Command
}

object CliInputAdapter {

    fun parse(args: Array<String>, stdin: List<String>): Command {
        require(args.isNotEmpty()) { "Usage: merkle <root|proof|verify> [options]" }
        return when (args[0]) {
            "root" -> Command.Root(stdin)
            "proof" -> {
                val item = requireArg(args, "--item")
                Command.Proof(stdin, item)
            }
            "verify" -> {
                val item = requireArg(args, "--item")
                val proof = requireArg(args, "--proof")
                Command.Verify(stdin, item, proof)
            }
            else -> error("Unknown command: ${args[0]}")
        }
    }

    private fun requireArg(args: Array<String>, flag: String): String {
        val index = args.indexOf(flag)
        require(index >= 0 && index + 1 < args.size) { "Missing $flag argument" }
        return args[index + 1]
    }
}
