import org.scalatest._
import chisel3._
import chiseltest._

class InformationFlowTrackingPluginTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "InformationFlowTrackingPlugin"

  it should "detect high-risk instructions" in {
    test(new InformationFlowTrackingPlugin) { dut =>
      // Initialize signals
      dut.clock.step(1)
      dut.io.checkTag.poke(CheckTags.LOW.B)
      dut.io.checkTagInst.poke(false.B)

      // Execute stage: set custom instruction and tag
      dut.io.checkTagInst.poke(true.B)
      dut.io.checkTag.poke(CheckTags.HIGH.B)
      dut.clock.step(1)

      // Verify that malicious activity is detected
      dut.maliciousDetected.expect(true.B)
    }
  }

  it should "not detect low-risk instructions" in {
    test(new InformationFlowTrackingPlugin) { dut =>
      // Initialize signals
      dut.clock.step(1)
      dut.io.checkTag.poke(CheckTags.HIGH.B)
      dut.io.checkTagInst.poke(false.B)

      // Execute stage: set custom instruction and tag
      dut.io.checkTagInst.poke(true.B)
      dut.io.checkTag.poke(CheckTags.LOW.B)
      dut.clock.step(1)

      // Verify that no malicious activity is detected
      dut.maliciousDetected.expect(false.B)
    }
  }
}
