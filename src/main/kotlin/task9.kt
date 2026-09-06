class Vector(val x: Double, val y: Double, val z: Double) {

    fun length(): Double {
        return Math.sqrt(x * x + y * y + z * z)
    }

    fun dot(other: Vector): Double {
        return this.x * other.x + this.y * other.y + this.z * other.z
    }

    infix fun dotProduct(other: Vector): Double {
        return this.x * other.x + this.y * other.y + this.z * other.z
    }

    operator fun times(other: Vector): Double {
        return this.x * other.x + this.y * other.y + this.z * other.z
    }
}

fun dotProduct(v1: Vector, v2: Vector): Double {
    return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z
}

fun main() {
    val v1 = Vector(1.0, 2.0, 3.0)
    val v2 = Vector(3.0, 2.0, 1.0)

    println("Vector 1: (${v1.x}, ${v1.y}, ${v1.z})")
    println("Vector 2: (${v2.x}, ${v2.y}, ${v2.z})")
    println("Length of vector 1: ${v1.length()}")
    println("Length of vector 2: ${v2.length()}")
    println("Dot product (method): ${v1.dot(v2)}")
    println("Dot product (infix): ${v1 dotProduct v2}")
    println("Dot product (operator *): ${v1 * v2}")
    println("Dot product (function): ${dotProduct(v1, v2)}")
}