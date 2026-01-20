package lidanglesensor
import com.sun.jna._, ptr.IntByReference

object IOKit {
  private val io = Native.load("IOKit", classOf[IO])
  private val cf = Native.load("CoreFoundation", classOf[CF])
  trait CF extends Library {
    def CFSetGetCount(s: Pointer): Long
    def CFSetGetValues(s: Pointer, v: Array[Pointer]): Unit
    def CFRelease(p: Pointer): Unit
    def CFRetain(p: Pointer): Pointer
    def CFDictionaryCreate(a: Pointer, k: Array[Pointer], v: Array[Pointer], n: Long, c1: Pointer, c2: Pointer): Pointer
    def CFNumberCreate(a: Pointer, t: Long, p: Pointer): Pointer
    def CFStringCreateWithCString(a: Pointer, s: String, e: Int): Pointer
  }
  trait IO extends Library {
    def IOHIDManagerCreate(a: Pointer, o: Int): Pointer
    def IOHIDManagerOpen(m: Pointer, o: Int): Int
    def IOHIDManagerClose(m: Pointer, o: Int): Int
    def IOHIDManagerSetDeviceMatching(m: Pointer, d: Pointer): Unit
    def IOHIDManagerCopyDevices(m: Pointer): Pointer
    def IOHIDDeviceOpen(d: Pointer, o: Int): Int
    def IOHIDDeviceClose(d: Pointer, o: Int): Int
    def IOHIDDeviceGetReport(d: Pointer, t: Int, id: Long, r: Array[Byte], l: IntByReference): Int
  }
  def mgr: Pointer = io.IOHIDManagerCreate(null, 0)
  def open(m: Pointer): Int = io.IOHIDManagerOpen(m, 0)
  def close(m: Pointer): Int = io.IOHIDManagerClose(m, 0)
  def matchSensor(m: Pointer): Unit = {
    def str(s: String) = cf.CFStringCreateWithCString(null, s, 0x08000100)
    def num(n: Int) = { val p = new Memory(4); p.setInt(0, n); cf.CFNumberCreate(null, 9, p) }
    io.IOHIDManagerSetDeviceMatching(m, cf.CFDictionaryCreate(null, 
      Array(str("VendorID"), str("UsagePage"), str("Usage")),
      Array(num(0x05AC), num(0x0020), num(0x008A)), 3, null, null))
  }
  def devices(m: Pointer): Array[Pointer] = {
    val s = io.IOHIDManagerCopyDevices(m); if (s == null) return Array()
    val n = cf.CFSetGetCount(s).toInt; if (n == 0) { cf.CFRelease(s); return Array() }
    val d = new Array[Pointer](n); cf.CFSetGetValues(s, d); cf.CFRelease(s); d
  }
  def openDev(d: Pointer): Int = io.IOHIDDeviceOpen(d, 0)
  def closeDev(d: Pointer): Int = io.IOHIDDeviceClose(d, 0)
  def retain(d: Pointer): Pointer = cf.CFRetain(d)
  def release(d: Pointer): Unit = cf.CFRelease(d)
  def report(d: Pointer): Option[Int] = {
    val r = new Array[Byte](8); val l = new IntByReference(8)
    if (io.IOHIDDeviceGetReport(d, 2, 1, r, l) == 0 && l.getValue >= 3) Some(((r(2)&0xFF)<<8)|(r(1)&0xFF)) else None
  }
}
