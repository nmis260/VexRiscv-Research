package vexriscv.plugin

import spinal.core._
import spinal.lib._
import vexriscv._
import vexriscv.demo.GenFull
import vexriscv.plugin.Plugin
import vexriscv.{Stageable, DecoderService, VexRiscv}
import vexriscv.{VexRiscv, VexRiscvConfig, plugin}


// Define Check Tags
object CheckTags {
  val HIGH = 1
  val LOW = 0
}

// Define a new Stageable signal for the checkTag
object CHECK_TAG extends Stageable(Bits(1 bits))

// Define custom instructions for checking tags
object CHECK_TAG_INST extends Stageable(Bool)

// Tag propagation
object TAG_PROPAGATION extends Stageable(Bits(1 bits))

class InformationFlowTrackingPlugin extends Plugin[VexRiscv] {
  // Register to indicate if malicious activity is detected
  lazy val maliciousDetected = spinal.core.RegInit(False)

  var exceptionPort : Flow[ExceptionCause] = null

  override def setup(pipeline: VexRiscv): Unit = {
    import pipeline.config._

    // Retrieve the DecoderService instance
    val decoderService = pipeline.service(classOf[DecoderService])

    // Specify the default values
    decoderService.addDefault(CHECK_TAG, CheckTags.LOW)
    decoderService.addDefault(CHECK_TAG_INST, False)
    decoderService.addDefault(TAG_PROPAGATION, CheckTags.LOW)

    // custom decoding rules
     decoderService.add(
      // Pretty sure these do the same thing but i need to check 
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

    // Exception Service
    val exceptionService = pipeline.service(classOf[ExceptionService])
    exceptionPort = exceptionService.newExceptionPort(pipeline.decode)
  }

  override def build(pipeline: VexRiscv): Unit = {
    import pipeline._
    import pipeline.config._

    // Execute stage
    execute plug new Area {
      import execute._
      val currentTag = input(CHECK_TAG)
      val checkTagInst = input(CHECK_TAG_INST)

       // If the custom instruction for checking tags is executed
      when(checkTagInst) {
        when(currentTag === CheckTags.HIGH) {
          maliciousDetected := True
        }
      }
      // Propagate the tag to the next stage
      execute.output(TAG_PROPAGATION) := execute.input(CHECK_TAG)
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
      memory.output(TAG_PROPAGATION) := memory.input(CHECK_TAG)
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
      writeBack.output(TAG_PROPAGATION) := writeBack.input(CHECK_TAG)
    }

    // Exception handling for malicious activity
    when(maliciousDetected) {
      exceptionPort.valid := True
      exceptionPort.code := U"4'd2"  // Assuming 2 is the exception code for malicious activity
    }
  }
}