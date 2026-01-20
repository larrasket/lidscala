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
    line.open(fmt, 4096)
    line.start()
    true
  } catch { case _: Exception => false }
  
  def setVol(v: Float): Unit = vol = v
  
  def write(): Unit = if (line != null && data != null) {
    if (line.available() > 256) {
      val chunk = new Array[Byte](256)
      var i = 0
      while (i < 256) {
        val p = (pos + i) % data.length
        val pNext = (p + 1) % data.length
        
        val s = ((data(pNext) << 8) | (data(p) & 0xFF)).toShort
        val v = (s * vol).toInt.toShort
        
        chunk(i) = (v & 0xFF).toByte
        chunk(i + 1) = ((v >> 8) & 0xFF).toByte
        i += 2
      }
      line.write(chunk, 0, 256)
      pos = (pos + 256) % data.length
    }
  }
  
  def close(): Unit = if (line != null) { line.stop(); line.close() }
}
