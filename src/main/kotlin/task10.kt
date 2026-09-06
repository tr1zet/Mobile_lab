open class Vehicle {
    open var speed: Int = 0
    open var name: String = "Vehicle"

    open fun start() {
        println("$name started moving at $speed km/h")
    }

    open fun stop() {
        println("$name stopped")
    }
}

class Boat : Vehicle() {
    override var speed: Int = 50
    override var name: String = "Boat"

    override fun start() {
        println("$name started sailing at $speed km/h")
    }
}

class Plane : Vehicle() {
    override var speed: Int = 400
    override var name: String = "Plane"

    override fun start() {
        println("$name started flying at $speed km/h")
    }
}

class Tank : Vehicle() {
    override var speed: Int = 20
    override var name: String = "Tank"

    override fun start() {
        println("$name started driving at $speed km/h")
    }
}

fun main() {
    val boat = Boat()
    val plane = Plane()
    val tank = Tank()

    boat.start()
    boat.stop()
    println()

    plane.start()
    plane.stop()
    println()

    tank.start()
    tank.stop()
}