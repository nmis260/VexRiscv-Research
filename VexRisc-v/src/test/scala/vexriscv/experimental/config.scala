package vexriscv.experimental

import spinal.core.SpinalVerilog
import vexriscv.ip.InstructionCacheConfig
import vexriscv.{VexRiscv, VexRiscvConfig, plugin}
import vexriscv.plugin._

import scala.collection.mutable.ArrayBuffer

object Presentation extends App{

  val config = VexRiscvConfig()

  config.plugins ++= List(
    new IBusSimplePlugin(resetVector = 0x80000000l,
                        cmdForkOnSecondStage = true,
                        cmdForkPersistence = false),
    new DBusSimplePlugin,
    new CsrPlugin(CsrPluginConfig.smallest),
    new DecoderSimplePlugin,
    new InformationFlowTrackingPlugin,
    new NBitsPlugin(n: Int),
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

  new VexRiscv(config)
}

