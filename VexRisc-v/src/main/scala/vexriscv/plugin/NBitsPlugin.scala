/*
package vexriscv.plugin

import spinal.core._
import vexriscv.plugin.Plugin
import vexriscv.{Stageable, DecoderService, VexRiscv}



// Identity takes n bits in a and gives them back in z
class NBitsPlugin(n: Int) extends Component{
  val io = new Bundle {
    val a = in Bits(n bits)
    val z = out Bits(n bits)
  }

  io.z := io.a
}
*/