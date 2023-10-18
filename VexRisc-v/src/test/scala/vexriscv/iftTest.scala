/*
package vexriscv.plugin

import spinal.core._
import spinal.core.sim._
import vexriscv.demo.GenFull
//import vexriscv.cpu0
//import vexriscv.sim._
import vexriscv.plugin._

object InformationFlowTrackingTest extends App {
  // Compile the RISC-V CPU
  val compiled = SimConfig.withConfig(SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(resetKind = spinal.core.SYNC)))
    .withSimulators
    .compile(GenFull.cpu())

  // Run the simulation
  compiled.doSimUntilVoid("InformationFlowTrackingTest") { dut =>
    val clockDomain = dut.clockDomain
    clockDomain.forkStimulus(period = 10)

    val iftPlugin = dut.service(classOf[InformationFlowTrackingPlugin])

    // Define a function to monitor malicious activity
    def monitorMaliciousActivity() = {
      while(true) {
        if(iftPlugin.maliciousDetected.toBoolean) {
          println("Malicious activity detected!")
        }
        sleep(1)
      }
    }

    // Fork the malicious activity monitor
    fork(monitorMaliciousActivity())

    // Assume the custom instruction machine code is loaded at address 0x80000000
    // and the next instruction is at 0x80000004
    dut.memory.loadBinary("custom_instructions.bin", 0x80000000)

    // Reset the CPU
    clockDomain.waitRisingEdge()
    dut.reset #= true
    clockDomain.waitRisingEdge()
    dut.reset #= false

    // Assume the program counter starts at 0x80000000
    dut.pcSim #= 0x80000000

    // Run the simulation for a number of cycles to allow the program to execute
    clockDomain.waitSampling(100)  // adjust as needed

    // Finish the simulation
    clockDomain.waitSampling(1000)
  }
}
*/