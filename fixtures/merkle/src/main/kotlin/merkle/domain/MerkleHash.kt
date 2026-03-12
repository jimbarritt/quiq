package merkle.domain

import java.security.MessageDigest

@JvmInline
value class MerkleHash(val hex: String) {

    companion object {
        fun of(data: String): MerkleHash {
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest(data.toByteArray())
            return MerkleHash(bytes.joinToString("") { "%02x".format(it) })
        }

        fun combine(left: MerkleHash, right: MerkleHash): MerkleHash {
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest((left.hex + right.hex).toByteArray())
            return MerkleHash(bytes.joinToString("") { "%02x".format(it) })
        }
    }
}
