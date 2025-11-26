package demo.cmp.x

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform