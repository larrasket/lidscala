package lidanglesensor

object Main extends App {
  val s = new Sensor; if (!s.init()) { println("No sensor"); sys.exit(1) }
  val a = new Audio; a.init()
  sys.addShutdownHook { a.close(); s.close() }
  var prev = 0.0; var vel = 0.0; var t = System.nanoTime()
  while (true) {
    s.read.foreach { ang =>
      val now = System.nanoTime(); val dt = (now - t) / 1e9
      if (dt > 0.001 && dt < 1) { vel = 0.3 * math.abs(ang - prev) / dt + 0.7 * vel; prev = ang; t = now }
      a.setVol(if (vel < 1) 0 else if (vel > 100) 0 else (1 - vel / 100).toFloat)
    }
    a.write()
  }
}
