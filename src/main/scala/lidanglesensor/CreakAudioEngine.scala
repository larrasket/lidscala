package lidanglesensor
import javax.sound.sampled._

class Audio {
  private var line: SourceDataLine = _
  private var data: Array[Byte] = _
  private var pos = 0
  private var vol = 0f
  
  def init(): Boolean = try {
    val s = getClass.getResourceAsStream("/CREAK_LOOP.wav")
    val a = AudioSystem.getAudioInputStream(new java.io.BufferedInputStream(s))
    val fmt = a.getFormat
    data = a.readAllBytes()
    val info = new DataLine.Info(classOf[SourceDataLine], fmt)
    line = AudioSystem.getLine(info).asInstanceOf[SourceDataLine]
    line.open(fmt, 512)
    line.start()
    true
  } catch { case _: Exception => false }
  
  def setVol(v: Float): Unit = vol = v
  
  def write(): Unit = if (line != null && data != null) {
    val avail = line.available()
    if (avail > 0) {
      val len = math.min(avail, data.length - pos)
      val chunk = new Array[Byte](len)
      var i = 0; while (i < len) { chunk(i) = (data(pos + i) * vol).toByte; i += 1 }
      line.write(chunk, 0, len)
      pos = (pos + len) % data.length
    }
  }
  
  def close(): Unit = if (line != null) { line.stop(); line.close() }
}
