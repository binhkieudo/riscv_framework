// See LICENSE for license details.
package chipyard.fpga.arty100t

import org.chipsalliance.cde.config._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.devices.debug._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.system._
import freechips.rocketchip.tile._
import sifive.blocks.devices.uart._
import sifive.blocks.devices.gpio.{GPIOParams, PeripheryGPIOKey}
import sifive.fpgashells.shell.DesignKey

import scala.sys.process._
import testchipip.SerialTLKey
import chipyard.{BuildSystem, ExtTLMem}
import sifive.blocks.devices.spi.{PeripherySPIKey, SPIParams}

// don't use FPGAShell's DesignKey
//class WithNoDesignKey extends Config((site, here, up) => {
//  case DesignKey => (p: Parameters) => new SimpleLazyModule()(p)
//})
class WithSystemModifications extends Config((site, here,up) => {
  case DTSTimebase => BigInt((1e6).toLong)
  case BootROMLocated(x) => up(BootROMLocated(x), site).map{ p =>
      val freqMHz = (site(SystemBusKey).dtsFrequency.get / (1000 * 1000)).toLong
      val make = s"make -C fpga/src/main/resources/arty100T/sdboot PBUS_CLK=${freqMHz} bin"
      require (make.! == 0, "Failed to build bootrom")
      p.copy(hang = 0x10000, contentFileName = s"./fpga/src/main/resources/arty100T/sdboot/build/sdboot.bin")
    }
  case ExtTLMem => Some(MemoryPortParams(MasterPortParams(
    base = x"8000_0000",
    size = x"1000_0000",
    beatBytes = site(MemoryBusKey).beatBytes,
    idBits = 4), 1))
  case DebugModuleKey => up(DebugModuleKey).map{ debug =>
    debug.copy(clockGate = false)
  }
  case SerialTLKey => None // remove serialized tl port
})

class WithTinySystemModifications extends Config((site, here,up) => {
  case DTSTimebase => BigInt((1e6).toLong)
  case DebugModuleKey => up(DebugModuleKey).map{ debug =>
    debug.copy(clockGate = false)
  }
  case SerialTLKey => None // remove serialized tl port
})

class WithDMEMBootROM extends Config((site, here, up) => {
  case BootROMLocated(x) => up(BootROMLocated(x), site).map{ p =>
    val freqMHz = (site(SystemBusKey).dtsFrequency.get / (1000 * 1000)).toLong
    // Make sure that the bootrom is always rebuilt
    val clean = s"make -C fpga/src/main/resources/arty100T/sdboot clean"
    require (clean.! == 0, "Failed to clean")
    // Build the bootrom
    val make = s"make -C fpga/src/main/resources/arty100T/sdboot XLEN=${site(XLen)} PBUS_CLK=${freqMHz}"
    require (make.! == 0, "Failed to build bootrom")
    p.copy(hang = 0x10000, contentFileName = s"./fpga/src/main/resources/arty100T/sdboot/build/sdboot.bin")
  }
})

class WithDDRBootROM extends Config((site, here, up) => {
  case BootROMLocated(x) => up(BootROMLocated(x), site).map{ p =>
    val freqMHz = (site(SystemBusKey).dtsFrequency.get / (1000 * 1000)).toLong
    // Make sure that the sdboot is always rebuilt
    val clean = s"make -C fpga/src/main/resources/arty100TMem/sdboot clean"
    require (clean.! == 0, "Failed to clean")
    // Build the bootrom
    val make = s"make -C fpga/src/main/resources/arty100TMem/sdboot XLEN=${site(XLen)} PBUS_CLK=${freqMHz}"
    require (make.! == 0, "Failed to build bootrom")
    p.copy(hang = 0x10000, contentFileName = s"./fpga/src/main/resources/arty100TMem/sdboot/build/sdboot.bin")
  }
})

class WithDefaultPeripherals extends Config((site, here, up) => {
  case PeripheryUARTKey => List(UARTParams(address = BigInt(0x64000000L)))
  case PeripheryGPIOKey => List(GPIOParams(address = BigInt(0x64002000L), width = 16))
  case PeripherySPIKey => List(SPIParams(rAddress = BigInt(0x64003000L)))
})

class WithArty100TTweaks extends Config(
  // clocking
  new chipyard.harness.WithAllClocksFromHarnessClockInstantiator ++
  new chipyard.harness.WithHarnessBinderClockFreqMHz(50) ++
  new chipyard.config.WithMemoryBusFrequency(50.0) ++
  new chipyard.config.WithSystemBusFrequency(50.0) ++
  new chipyard.config.WithPeripheryBusFrequency(50.0) ++
  new chipyard.harness.WithAllClocksFromHarnessClockInstantiator ++
  new chipyard.clocking.WithPassthroughClockGenerator ++
  // harness binder
  new WithArty100TDDR ++
  new WithArty100TUART ++
  new WithArty100TGPIO ++
  new WithArty100TJTAG ++
  new WithArty100TSDCard ++
  // io binder
  new WithUARTIOPassthrough ++
  new WithGPIOIOPassthrough ++
  new WithTLIOPassthrough ++
  new WithSPIIOPassthrough ++
  // other configuration
  new WithDefaultPeripherals ++
  new WithDDRBootROM ++
  new WithSystemModifications ++
  new chipyard.config.WithTLBackingMemory ++ // FPGA-shells converts the AXI to TL for us
  new freechips.rocketchip.subsystem.WithExtMemSize(BigInt(64) << 20) ++ // 256mb on ARTY
  new freechips.rocketchip.subsystem.WithoutTLMonitors)

class WithArty100TinyTweaks extends Config(
  // clocking
  new chipyard.harness.WithAllClocksFromHarnessClockInstantiator ++
  new chipyard.harness.WithHarnessBinderClockFreqMHz(50) ++
  new chipyard.config.WithMemoryBusFrequency(50.0) ++
  new chipyard.config.WithSystemBusFrequency(50.0) ++
  new chipyard.config.WithPeripheryBusFrequency(50.0) ++
  new chipyard.harness.WithAllClocksFromHarnessClockInstantiator ++
  new chipyard.clocking.WithPassthroughClockGenerator ++
  // harness binder
  new WithArty100TUART ++
  new WithArty100TGPIO ++
  new WithArty100TJTAG ++
  new WithArty100TSDCard ++
  // io binder
  new WithUARTIOPassthrough ++
  new WithGPIOIOPassthrough ++
  new WithTLIOPassthrough ++
  new WithSPIIOPassthrough ++
  // other configuration
  new WithDefaultPeripherals ++
  new WithDMEMBootROM ++
  new WithTinySystemModifications ++
  new freechips.rocketchip.subsystem.WithoutTLMonitors
)

class RocketArty100TConfig extends Config(
  new WithArty100TTweaks ++
    new chipyard.config.WithBroadcastManager ++ // no l2
    new chipyard.RocketConfig)

class RocketTinyArty100TConfig extends Config(
  new WithArty100TinyTweaks ++
  new chipyard.config.WithBroadcastManager ++ // no l2
  new chipyard.SmallRocketConfig)

class RocketTinyMemNoCacheArty100TConfig extends Config(
  new WithArty100TTweaks ++
  new chipyard.config.WithBroadcastManager ++ // no l2
  new chipyard.SmallRocketMemConfig)

class RocketTinyMemCacheArty100TConfig extends Config(
  new WithArty100TTweaks ++
  new chipyard.SmallRocketMemConfig)