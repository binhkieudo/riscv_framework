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
import sifive.blocks.devices.gpio.{PeripheryGPIOKey, GPIOParams}
import sifive.fpgashells.shell.{DesignKey}

import scala.sys.process._
import testchipip.{SerialTLKey}

import chipyard.{BuildSystem}

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
  case SerialTLKey => None // remove serialized tl port
})

class WithDefaultPeripherals extends Config((site, here, up) => {
  case PeripheryUARTKey => List(UARTParams(address = BigInt(0x64000000L)))
  case PeripheryGPIOKey => List(GPIOParams(address = BigInt(0x64002000L), width = 16))
})

class WithArty100TTweaks extends Config(
//  new WithArty100TUARTTSI ++
//  new WithArty100TDDRTL ++
//  new WithNoDesignKey ++
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
  new WithArty100JTAG ++
  //  new chipyard.config.WithNoUART ++ // use UART for the UART-TSI thing instad
  // io binder
  new WithUARTIOPassthrough ++
  new WithGPIOIOPassthrough ++
  new WithTLIOPassthrough ++
  // other configuration
  new WithDefaultPeripherals ++
  new WithSystemModifications ++
//  new chipyard.config.WithNoDebug ++ // no jtag
  new chipyard.config.WithTLBackingMemory ++ // FPGA-shells converts the AXI to TL for us
  new freechips.rocketchip.subsystem.WithExtMemSize(BigInt(256) << 20) ++ // 256mb on ARTY
  new freechips.rocketchip.subsystem.WithoutTLMonitors)

class RocketArty100TConfig extends Config(
  new WithArty100TTweaks ++
  new chipyard.config.WithBroadcastManager ++ // no l2
  new chipyard.RocketConfig)

//class UART230400RocketArty100TConfig extends Config(
//  new WithArty100TUARTTSI(uartBaudRate = 230400) ++
//  new RocketArty100TConfig)
//
//class UART460800RocketArty100TConfig extends Config(
//  new WithArty100TUARTTSI(uartBaudRate = 460800) ++
//  new RocketArty100TConfig)
//
//class UART921600RocketArty100TConfig extends Config(
//  new WithArty100TUARTTSI(uartBaudRate = 921600) ++
//  new RocketArty100TConfig)


//class NoCoresArty100TConfig extends Config(
//  new WithArty100TTweaks ++
//  new chipyard.config.WithMemoryBusFrequency(50.0) ++
//  new chipyard.config.WithPeripheryBusFrequency(50.0) ++  // Match the sbus and pbus frequency
//  new chipyard.config.WithBroadcastManager ++ // no l2
//  new chipyard.NoCoresConfig)
