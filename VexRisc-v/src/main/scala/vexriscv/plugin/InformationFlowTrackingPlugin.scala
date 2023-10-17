package vexriscv.plugin

import spinal.core._
import vexriscv.plugin.Plugin
import vexriscv.{Stageable, DecoderService, VexRiscv}

// Define Check Tags
object CheckTags {
  val HIGH = 1
  val LOW = 0
}

// Define a new Stageable signal for the checkTag
object CHECK_TAG extends Stageable(Bits(1 bits))

// Define custom instructions for checking tags
object CHECK_TAG_INST extends Stageable(Bool)

class InformationFlowTrackingPlugin extends Plugin[VexRiscv] {
  // Register to indicate if malicious activity is detected
  val maliciousDetected = RegInit(False)

  override def setup(pipeline: VexRiscv): Unit = {
    import pipeline.config._

    // Retrieve the DecoderService instance
    val decoderService = pipeline.service(classOf[DecoderService])

    // Specify the default value for CHECK_TAG when instructions are decoded
    decoderService.addDefault(CHECK_TAG, CheckTags.LOW)
    decoderService.addDefault(CHECK_TAG_INST, False)

    // Add custom decoding rules
    decoderService.add(
      // For demonstration, let's say any instruction with this pattern is considered "high risk"
      key = M"0000011----------000-----0110011",
      List(
        CHECK_TAG -> CheckTags.HIGH
      )
    )

    // Custom instruction for checking tags
    decoderService.add(
      key = M"0000010----------000-----0110011",
      List(
        CHECK_TAG_INST -> True
      )
    )
  }

  override def build(pipeline: VexRiscv): Unit = {
    import pipeline._
    import pipeline.config._

    // Execute stage
    execute plug new Area {
      val currentTag = execute.input(CHECK_TAG)
      val checkTagInst = execute.input(CHECK_TAG_INST)

      // If the custom instruction for checking tags is executed
      when(checkTagInst) {
        when(currentTag === CheckTags.HIGH) {
          maliciousDetected := True
        }
      }
    }

    // Memory stage
    memory plug new Area {
      val currentTag = memory.input(CHECK_TAG)
      val checkTagInst = memory.input(CHECK_TAG_INST)

      // If the custom instruction for checking tags is executed
      when(checkTagInst) {
        when(currentTag === CheckTags.HIGH) {
          maliciousDetected := True
        }
      }
    }

    // WriteBack stage
    writeBack plug new Area {
      val currentTag = writeBack.input(CHECK_TAG)
      val checkTagInst = writeBack.input(CHECK_TAG_INST)

      // If the custom instruction for checking tags is executed
      when(checkTagInst) {
        when(currentTag === CheckTags.HIGH) {
          maliciousDetected := True
        }
      }
    }
  }
}