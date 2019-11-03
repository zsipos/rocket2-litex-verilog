// See LICENSE.SiFive for license details.
// See LICENSE.Berkeley for license details.

package freechips.rocketchip.system

import Chisel._
import chipsalliance.rocketchip.config.Field
import freechips.rocketchip.amba.axi4.{AXI4IdIndexer, AXI4SlaveNode, AXI4SlaveParameters, AXI4SlavePortParameters, AXI4UserYanker}
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.tilelink.TLToAXI4
import freechips.rocketchip.util.{DontTouch, HeterogeneousBag}

case object ExtMem2 extends Field[Option[MemoryPortParams]](None)

/** Adds a second port to the system intended to master an AXI4 DRAM controller. */
trait CanHaveMasterAXI4MemPort2 { this: BaseSubsystem =>
  val module: CanHaveMasterAXI4MemPort2ModuleImp

  val memAXI4Node2 = p(ExtMem2).map { case MemoryPortParams(memPortParams, nMemoryChannels) =>
    val portName = "axi4"
    val device = new MemoryDevice

    val memAXI4Node = AXI4SlaveNode(Seq.tabulate(nMemoryChannels) { channel =>
      val base = AddressSet.misaligned(memPortParams.base, memPortParams.size)
      val filter = AddressSet(channel * mbus.blockBytes, ~((nMemoryChannels-1) * mbus.blockBytes))

      AXI4SlavePortParameters(
        slaves = Seq(AXI4SlaveParameters(
          address       = base.flatMap(_.intersect(filter)),
          resources     = device.reg,
          regionType    = RegionType.UNCACHED, // cacheable
          executable    = true,
          supportsWrite = TransferSizes(1, mbus.blockBytes),
          supportsRead  = TransferSizes(1, mbus.blockBytes),
          interleavedId = Some(0))), // slave does not interleave read responses
        beatBytes = memPortParams.beatBytes)
    })

    memAXI4Node := mbus.toDRAMController(Some(portName)) {
      AXI4UserYanker() := AXI4IdIndexer(memPortParams.idBits) := TLToAXI4()
    }

    memAXI4Node
  }
}

/** Actually generates the corresponding IO in the concrete Module */
trait CanHaveMasterAXI4MemPort2ModuleImp extends LazyModuleImp {
  val outer: CanHaveMasterAXI4MemPort2

  val mem2_axi4 = outer.memAXI4Node2.map(x => IO(HeterogeneousBag.fromNode(x.in)))
  (mem2_axi4 zip outer.memAXI4Node2) foreach { case (io, node) =>
    (io zip node.in).foreach { case (io, (bundle, _)) => io <> bundle }
  }

  def connectSimAXIMem2() {
    (mem2_axi4 zip outer.memAXI4Node2).foreach { case (io, node) =>
      (io zip node.in).foreach { case (io, (_, edge)) =>
        val mem = LazyModule(new SimAXIMem(edge, size = p(ExtMem).get.master.size))
        Module(mem.module).io.axi4.head <> io
      }
    }
  }
}

/** Example Top with periphery devices and ports, and a Rocket subsystem */
class LitexRocketSystem(implicit p: Parameters) extends RocketSubsystem
    with HasHierarchicalBusTopology
    with HasAsyncExtInterrupts
    with CanHaveMasterAXI4MemPort
    with CanHaveMasterAXI4MemPort2
    with CanHaveMasterAXI4MMIOPort
    with CanHaveSlaveAXI4Port
{
  override lazy val module = new LitexRocketSystemModuleImp(this)
}

class LitexRocketSystemModuleImp[+L <: LitexRocketSystem](_outer: L) extends RocketSubsystemModuleImp(_outer)
    with HasRTCModuleImp
    with HasExtInterruptsModuleImp
    with CanHaveMasterAXI4MemPortModuleImp
    with CanHaveMasterAXI4MemPort2ModuleImp
    with CanHaveMasterAXI4MMIOPortModuleImp
    with CanHaveSlaveAXI4PortModuleImp
    with DontTouch
{
  //ExtMem maps to litex bios
  global_reset_vector := UInt(p(ExtMem).get.master.base)
}
