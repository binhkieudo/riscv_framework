package chipyard

import chisel3._
import freechips.rocketchip.subsystem.{SBUS, TLBusWrapperLocation}
import org.chipsalliance.cde.config.{Config, Parameters}
import testchipip.{BankedScratchpadKey, BankedScratchpadParams}

// ---------------------
// Ibex Configs
// ---------------------

// Multi-core and 32b heterogeneous configs are supported

class IbexConfig extends Config(
  new freechips.rocketchip.subsystem.WithIncoherentBusTopology ++
  new WithIbexSRAM++
  new freechips.rocketchip.subsystem.WithNoMemPort ++
  new ibex.WithNIbexCores(1) ++
  new chipyard.config.AbstractConfig)


class WithIbexSRAM(base: BigInt = 0x80000000L, size: BigInt = (4 << 10), banks: Int = 1, partitions: Int = 1, busWhere: TLBusWrapperLocation = SBUS) extends Config((site, here, up) => {
  case BankedScratchpadKey => up(BankedScratchpadKey) ++
    (0 until partitions).map { pa => BankedScratchpadParams(
      base + pa * (size / partitions),
      size / partitions,
      busWhere = busWhere,
      name = s"${busWhere.name}-scratchpad",
      banks = banks)
    }
})