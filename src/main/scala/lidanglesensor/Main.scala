package lidanglesensor

object Main extends App {
  val s = new Sensor; if (!s.init()) { println("No sensor"); sys.exit(1) }
  val a = new Audio; a.init()
  sys.addShutdownHook { a.close(); s.close() }
  var prev = 0.0; var vel = 0.0; var t = System.nanoTime()
  while (true) {
    val now = System.nanoTime()
    if (now - t > 16000000) { 
      s.read.foreach { ang =>
        val dt = (now - t) / 1e9
        val inst = math.abs(ang - prev) / dt
        if (inst > 0 || dt < 0.1) {
           vel = 0.3 * inst + 0.7 * vel
        } else {
           vel = 0 
        }
        prev = ang
      }
      a.setVol(if (vel < 1) 0 else if (vel > 100) 0 else (1 - vel / 100).toFloat)
      t = now
    }
    a.write()
  }
}
