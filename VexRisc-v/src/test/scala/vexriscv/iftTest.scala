/*

import spinal.sim._
import spinal.core._
import spinal.core.sim._
import vexriscv.VexRiscvConfig
import vexriscv.VexRiscv
import vexriscv.plugin._
//import vexriscv.plugin.InstructionCacheConfig
//import vexriscv.Riscv.IMPL

object IFTPluginTest {
  def main(args: Array[String]): Unit = {
    val config = VexRiscvConfig(
      plugins = List(
        // Add other plugins you're using
        new InformationFlowTrackingPlugin // Enable the VexRiscv Full CPU configuration
      )
    )

    SimConfig.allOptimisation.compile(new VexRiscv(config)).doSim { dut =>
      val mainClkPeriod = 10000 // 10ns // Define your main clock period (use 10ns)

      // Import the CPU service
      val cpu = dut.service(classOf[CPU])

      // Get the clock domain from the CPU service
      val clockDomain = ClockDomain(cpu.service(classOf[ClockDomainWithFrequency]).get.clock, cpu.io.asyncReset)
      clockDomain.forkStimulus(mainClkPeriod)

      // Your test case logic here

      // Here, you can start the simulation, wait for it to complete, and then check the IFT result
      val simulationCycles = 3 // Define the number of simulation cycles to run

      for (_ <- 0 until simulationCycles) {
        // Advance simulation by one clock cycle
        clockDomain.waitSampling()
      }

      // Check the result of the IFT
      val maliciousDetected = dut.plugins[InformationFlowTrackingPlugin].maliciousDetected.toBoolean

      if (maliciousDetected) {
        println("Malicious activity detected.")
      } else {
        println("No malicious activity detected.")
      }
    }
  }
}
*/