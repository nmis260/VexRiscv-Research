package vexriscv

import spinal.sim._
import spinal.core._
import spinal.core.sim._
import vexriscv.demo.GenFull
import vexriscv.plugin._

object IFTPluginTest {
  def main(args: Array[String]): Unit = {
    //val config = // Your VexRiscv configuration with the IFT plugin
    val config = VexRiscvConfig()

    config.plugins ++= List(
    new IBusSimplePlugin(resetVector = 0x80000000l,
                        cmdForkOnSecondStage = true,
                        cmdForkPersistence = false),
    new DBusSimplePlugin,
    new CsrPlugin(CsrPluginConfig.smallest),
    new DecoderSimplePlugin,
    new InformationFlowTrackingPlugin,
    //new NBitsPlugin(n: Int),
    new RegFilePlugin(regFileReadyKind = plugin.SYNC),
    new IntAluPlugin,
    new SrcPlugin,
    new MulDivIterativePlugin(
      mulUnrollFactor = 4,
      divUnrollFactor = 1
    ),
    new FullBarrelShifterPlugin,
    new HazardSimplePlugin,
    new BranchPlugin(
      earlyBranch = false
    ),
    new YamlPlugin("cpu0.yaml")
    ) 

    // Load the configuration from the specified file
    val withMemoryStage = true   // or false, based on your needs
    val withWriteBackStage = true // or false, based on your needs
    val configInstance = VexRiscvConfig(withMemoryStage, withWriteBackStage, config.plugins)
    SimConfig.allOptimisation.compile(rtl = new VexRiscv(configInstance)).doSim { dut =>  
    //SimConfig.allOptimisation.compile(new VexRiscvConfig()).doSim { dut =>
      val mainClkPeriod = 100000 // 100ns, define your main clock period
      val clockDomain = ClockDomain(dut.io.mainClk, dut.io.asyncReset)
      clockDomain.forkStimulus(mainClkPeriod)

      // Load your compiled program with the IFT-triggering instruction into the instruction memory
      val program = Array[BigInt](BigInt("b00000110000000000000110011001100", 2)) 
      for (i <- program.indices) {
        dut.io.instructionMemory(i) #= program(i)
      }
      val simulationCycles = 3 // Define the number of simulation cycles to run
      for (_ <- 0 until simulationCycles) {
        // Advance the simulation by one clock cycle
        clockDomain.waitSampling()
      }
      // Check the result of the IFT
      val maliciousDetected = dut.maliciousDetected.toBoolean

      if (maliciousDetected) {
        println("Malicious activity detected.")
      } else {
        println("No malicious activity detected.")
      }
    }
  }
}
