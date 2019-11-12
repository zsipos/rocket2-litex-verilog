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

/** Example Top with periphery devices and ports, and a Rocket subsystem */
class LitexRocketSystem(implicit p: Parameters) extends RocketSubsystem
    with HasHierarchicalBusTopology
    with HasAsyncExtInterrupts
    with CanHaveMasterAXI4MemPort
    with CanHaveMasterAXI4MMIOPort
    with CanHaveSlaveAXI4Port
{
  override lazy val module = new LitexRocketSystemModuleImp(this)
}

class LitexRocketSystemModuleImp[+L <: LitexRocketSystem](_outer: L) extends RocketSubsystemModuleImp(_outer)
    with HasRTCModuleImp
    with HasExtInterruptsModuleImp
    with CanHaveMasterAXI4MemPortModuleImp
    with CanHaveMasterAXI4MMIOPortModuleImp
    with CanHaveSlaveAXI4PortModuleImp
    with DontTouch
{
  //ExtBus maps to litex bios
  global_reset_vector := UInt(p(ExtBus).get.base)
}
