// See LICENSE.SiFive for license details.
// See LICENSE.Berkeley for license details.

package freechips.rocketchip.system

import Chisel._
import freechips.rocketchip.config.Config
import freechips.rocketchip.subsystem._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.rocket.{DCacheParams, ICacheParams, MulDivParams, RocketCoreParams}
import freechips.rocketchip.tile.{RocketTileParams, XLen}

// memory width = 64

class WithLitexMemPortsMem64 extends Config((site, here, up) => {
  case ExtMem => Some(MemoryPortParams(MasterPortParams(
    base = x"1000_0000", //litex bios base
    size = x"0200_0000", //litex bios size
    beatBytes = 4, //site(MemoryBusKey).beatBytes,
    idBits = 4), 1))
  case ExtMem2 => Some(MemoryPortParams(MasterPortParams(
    base = x"8000_0000", //litex dram base
    size = x"8000_0000", //up to end
    beatBytes = 8, //site(MemoryBusKey).beatBytes,
    idBits = 4), 1))
})

// memory width = 128

class WithLitexMemPortsMem128 extends Config((site, here, up) => {
  case ExtMem => Some(MemoryPortParams(MasterPortParams(
    base = x"1000_0000", //litex bios base
    size = x"0200_0000", //litex bios size
    beatBytes = 4, //site(MemoryBusKey).beatBytes,
    idBits = 4), 1))
  case ExtMem2 => Some(MemoryPortParams(MasterPortParams(
    base = x"8000_0000", //litex dram base
    size = x"8000_0000", //up to end
    beatBytes = 16, //site(MemoryBusKey).beatBytes,
    idBits = 4), 1))
})

// memory width = 256

class WithLitexMemPortsMem256 extends Config((site, here, up) => {
  case ExtMem => Some(MemoryPortParams(MasterPortParams(
    base = x"1000_0000", //litex bios base
    size = x"0200_0000", //litex bios size
    beatBytes = 4, //site(MemoryBusKey).beatBytes,
    idBits = 4), 1))
  case ExtMem2 => Some(MemoryPortParams(MasterPortParams(
    base = x"8000_0000", //litex dram base
    size = x"8000_0000", //up to end
    beatBytes = 32, //site(MemoryBusKey).beatBytes,
    idBits = 4), 1))
})

class WithLitexMMIOPort extends Config((site, here, up) => {
  case ExtBus => Some(MasterPortParams(
    base = x"1200_0000", //litex io base
    size = x"6E00_0000", //up to dram base
    beatBytes = 4, //site(MemoryBusKey).beatBytes,
    idBits = 4))
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

class With64Bits extends Config((site, here, up) => {
  case XLen => 64
})

class With32Bits extends Config((site, here, up) => {
  case XLen => 32
})

class BaseLitexConfig extends Config(
  new WithLitexMMIOPort ++
  new WithNoSlavePort ++
  new WithNExtTopInterrupts(8) ++
  new WithoutTLMonitors ++
  new BaseConfig
)

// memory width = 64

class LitexConfig64Mem64 extends Config(
  new With64Bits ++
  new WithNSmallCores(1) ++
  new WithLitexMemPortsMem64() ++
  new BaseLitexConfig
)

class LitexLinuxConfig64Mem64 extends Config(
  new With64Bits ++
  new WithNMediumCores(1) ++
  new WithLitexMemPortsMem64 ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore64Mem64 extends Config(
  new With64Bits ++
  new WithNMediumCores(2) ++
  new WithLitexMemPortsMem64 ++
  new BaseLitexConfig
)

class LitexFullConfig64Mem64 extends Config(
  new With64Bits ++
  new WithNBigCores(1) ++
  new WithLitexMemPortsMem64 ++
  new BaseLitexConfig
)

class LitexConfig32Mem64 extends Config(
  new With32Bits ++
  new WithNSmallCores(1) ++
  new WithLitexMemPortsMem64 ++
  new BaseLitexConfig
)

class LitexLinuxConfig32Mem64 extends Config(
  new With32Bits ++
  new WithNMediumCores(1) ++
  new WithLitexMemPortsMem64 ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore32Mem64 extends Config(
  new With32Bits ++
  new WithNMediumCores(2) ++
  new WithLitexMemPortsMem64 ++
  new BaseLitexConfig
)

// memory width = 128

class LitexConfig64Mem128 extends Config(
  new With64Bits ++
  new WithNSmallCores(1) ++
  new WithLitexMemPortsMem128() ++
  new BaseLitexConfig
)

class LitexLinuxConfig64Mem128 extends Config(
  new With64Bits ++
  new WithNMediumCores(1) ++
  new WithLitexMemPortsMem128 ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore64Mem128 extends Config(
  new With64Bits ++
  new WithNMediumCores(1) ++
  new WithLitexMemPortsMem128 ++
  new BaseLitexConfig
)

class LitexFullConfig64Mem128 extends Config(
  new With64Bits ++
  new WithNBigCores(1) ++
  new WithLitexMemPortsMem128 ++
  new BaseLitexConfig
)

class LitexConfig32Mem128 extends Config(
  new With32Bits ++
  new WithNSmallCores(1) ++
  new WithLitexMemPortsMem128 ++
  new BaseLitexConfig
)

class LitexLinuxConfig32Mem128 extends Config(
  new With32Bits ++
  new WithNMediumCores(1) ++
  new WithLitexMemPortsMem128 ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore32Mem128 extends Config(
  new With32Bits ++
  new WithNMediumCores(2) ++
  new WithLitexMemPortsMem128 ++
  new BaseLitexConfig
)

// memory width = 256

class LitexConfig64Mem256 extends Config(
  new With64Bits ++
  new WithNSmallCores(1) ++
  new WithLitexMemPortsMem256() ++
  new BaseLitexConfig
)

class LitexLinuxConfig64Mem256 extends Config(
  new With64Bits ++
  new WithNMediumCores(1) ++
  new WithLitexMemPortsMem256 ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore64Mem256 extends Config(
  new With64Bits ++
  new WithNMediumCores(2) ++
  new WithLitexMemPortsMem256 ++
  new BaseLitexConfig
)

class LitexFullConfig64Mem256 extends Config(
  new With64Bits ++
  new WithNBigCores(1) ++
  new WithLitexMemPortsMem256 ++
  new BaseLitexConfig
)

class LitexConfig32Mem256 extends Config(
  new With32Bits ++
  new WithNSmallCores(1) ++
  new WithLitexMemPortsMem256 ++
  new BaseLitexConfig
)

class LitexLinuxConfig32Mem256 extends Config(
  new With32Bits ++
  new WithNMediumCores(1) ++
  new WithLitexMemPortsMem256 ++
  new BaseLitexConfig
)

class LitexLinuxConfigDualCore32Mem256 extends Config(
  new With32Bits ++
  new WithNMediumCores(2) ++
  new WithLitexMemPortsMem256 ++
  new BaseLitexConfig
)
