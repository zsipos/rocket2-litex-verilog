// See LICENSE.SiFive for license details.
// See LICENSE.Berkeley for license details.

package freechips.rocketchip.system

import Chisel._
import freechips.rocketchip.config.Config
import freechips.rocketchip.subsystem._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.rocket.{DCacheParams, ICacheParams, MulDivParams, RocketCoreParams}
import freechips.rocketchip.tile.{RocketTileParams, XLen}

/* widening the internal memory bus costs more fpga resources and benchmarks equal..

class WithLitexMemPort(beatBytes:Int) extends Config((site, here, up) => {
  case MemoryBusKey => up(MemoryBusKey, site).copy(beatBytes = beatBytes)
  case ExtMem => Some(MemoryPortParams(MasterPortParams(
    base = x"8000_0000", //litex dram base
    size = x"8000_0000", //up to end
    beatBytes = site(MemoryBusKey).beatBytes,
    idBits = 4), 1))
})

 */

class WithLitexMemPort(beatBytes:Int) extends Config((site, here, up) => {
  case ExtMem => Some(MemoryPortParams(MasterPortParams(
    base = x"8000_0000", //litex dram base
    size = x"8000_0000", //up to end
    beatBytes = beatBytes,
    idBits = 4), 1))
})

// fixed to 32bit for LiteX
class WithLitexIOPort(beatBytes:Int) extends Config((site, here, up) => {
  case ExtBus => Some(MasterPortParams(
    base = x"1000_0000", //litex io base
    size = x"7000_0000", //up to dram base
    beatBytes = beatBytes,
    idBits = 4))
})

// fixed to 32bit for LiteX
class WithLitexSlavePort(beatBytes:Int) extends Config((site, here, up) => {
  case ExtIn  => Some(SlavePortParams(beatBytes = beatBytes, idBits = 4, sourceBits = 4))
})

class WithNMediumCores(n: Int) extends Config((site, here, up) => {
  case RocketTilesKey => {
    val med = RocketTileParams(
      core   = RocketCoreParams(fpu = None, mulDiv = Some(MulDivParams(
        mulUnroll = 8,
        mulEarlyOut = true,
        divEarlyOut = true))),
      dcache = Some(DCacheParams(
        //nWays = 8,
        //nTLBEntries = 64,
        rowBits = site(SystemBusKey).beatBits,
        nMSHRs = 0,
        blockBytes = site(CacheBlockBytes))),
      icache = Some(ICacheParams(
        //nWays = 8,
        //nTLBEntries = 64,
        rowBits = site(SystemBusKey).beatBits,
        blockBytes = site(CacheBlockBytes))))
    List.tabulate(n)(i => med.copy(hartId = i))
  }
})

class BaseLitexConfig extends Config(
  new WithLitexIOPort(4) ++
  new WithLitexSlavePort(4) ++
  new WithNExtTopInterrupts(8) ++
  new WithoutTLMonitors ++
  new BaseConfig
)

// memory width = 64

class LitexConfig64Mem64 extends Config(
  new WithNSmallCores(1) ++
  new WithLitexMemPort(8) ++
  new BaseLitexConfig
)

class LitexLinuxConfig64Mem64 extends Config(
  new WithNMediumCores(1) ++
  new WithLitexMemPort(8) ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore64Mem64 extends Config(
  new WithNMediumCores(2) ++
  new WithLitexMemPort(8) ++
  new BaseLitexConfig
)

class LitexFullConfig64Mem64 extends Config(
  new WithNBigCores(1) ++
  new WithLitexMemPort(8) ++
  new BaseLitexConfig
)

class LitexConfig32Mem64 extends Config(
  new WithRV32 ++
  new WithNSmallCores(1) ++
  new WithLitexMemPort(8) ++
  new BaseLitexConfig
)

class LitexLinuxConfig32Mem64 extends Config(
  new WithRV32 ++
  new WithNMediumCores(1) ++
  new WithLitexMemPort(8) ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore32Mem64 extends Config(
  new WithRV32 ++
  new WithNMediumCores(2) ++
  new WithLitexMemPort(8) ++
  new BaseLitexConfig
)

class LitexFullConfig32Mem64 extends Config(
  new WithRV32 ++
  new WithNBigCores(1) ++
  new WithLitexMemPort(8) ++
  new BaseLitexConfig
)

// memory width = 128

class LitexConfig64Mem128 extends Config(
  new WithNSmallCores(1) ++
  new WithLitexMemPort(16) ++
  new BaseLitexConfig
)

class LitexLinuxConfig64Mem128 extends Config(
  new WithNMediumCores(1) ++
  new WithLitexMemPort(16) ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore64Mem128 extends Config(
  new WithNMediumCores(1) ++
  new WithLitexMemPort(16) ++
  new BaseLitexConfig
)

class LitexFullConfig64Mem128 extends Config(
  new WithNBigCores(1) ++
  new WithLitexMemPort(16) ++
  new BaseLitexConfig
)

class LitexConfig32Mem128 extends Config(
  new WithRV32 ++
  new WithNSmallCores(1) ++
  new WithLitexMemPort(16) ++
  new BaseLitexConfig
)

class LitexLinuxConfig32Mem128 extends Config(
  new WithRV32 ++
  new WithNMediumCores(1) ++
  new WithLitexMemPort(16) ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore32Mem128 extends Config(
  new WithRV32 ++
  new WithNMediumCores(2) ++
  new WithLitexMemPort(16) ++
  new BaseLitexConfig
)

class LitexFullConfig32Mem128 extends Config(
  new WithRV32 ++
  new WithNBigCores(1) ++
  new WithLitexMemPort(16) ++
  new BaseLitexConfig
)

// memory width = 256

class LitexConfig64Mem256 extends Config(
  new WithNSmallCores(1) ++
  new WithLitexMemPort(32) ++
  new BaseLitexConfig
)

class LitexLinuxConfig64Mem256 extends Config(
  new WithNMediumCores(1) ++
  new WithLitexMemPort(32) ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore64Mem256 extends Config(
  new WithNMediumCores(2) ++
  new WithLitexMemPort(32) ++
  new BaseLitexConfig
)

class LitexFullConfig64Mem256 extends Config(
  new WithNBigCores(1) ++
  new WithLitexMemPort(32) ++
  new BaseLitexConfig
)

class LitexConfig32Mem256 extends Config(
  new WithRV32 ++
  new WithNSmallCores(1) ++
  new WithLitexMemPort(32) ++
  new BaseLitexConfig
)

class LitexLinuxConfig32Mem256 extends Config(
  new WithRV32 ++
  new WithNMediumCores(1) ++
  new WithLitexMemPort(32) ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore32Mem256 extends Config(
  new WithRV32 ++
  new WithNMediumCores(2) ++
  new WithLitexMemPort(32) ++
  new BaseLitexConfig
)

class LitexFullConfig32Mem256 extends Config(
  new WithRV32 ++
  new WithNBigCores(1) ++
  new WithLitexMemPort(32) ++
  new BaseLitexConfig
)

