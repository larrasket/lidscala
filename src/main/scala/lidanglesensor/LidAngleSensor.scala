package lidanglesensor
import com.sun.jna.Pointer

class Sensor {
  private var dev: Pointer = _; private var m: Pointer = _
  def init(): Boolean = {
    m = IOKit.mgr; if (m == null || IOKit.open(m) != 0) return false
    IOKit.matching(m, 0x05AC, 0x8104, 0x0020, 0x008A)
    for (d <- IOKit.devices(m)) if (IOKit.openDev(d) == 0 && IOKit.report(d).isDefined) { dev = IOKit.retain(d); return true }
    IOKit.close(m); false
  }
  def read: Option[Int] = if (dev != null) IOKit.report(dev) else None
  def close(): Unit = { if (dev != null) { IOKit.closeDev(dev); IOKit.release(dev) }; if (m != null) IOKit.close(m) }
}
