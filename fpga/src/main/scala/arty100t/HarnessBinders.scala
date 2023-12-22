package chipyard.fpga.arty100t

import chisel3._
import freechips.rocketchip.jtag.JTAGIO
import freechips.rocketchip.subsystem.PeripheryBusKey
import freechips.rocketchip.tilelink.TLBundle
import freechips.rocketchip.util.HeterogeneousBag
import freechips.rocketchip.diplomacy.LazyRawModuleImp
import sifive.blocks.devices.uart.{HasPeripheryUARTModuleImp, UARTParams, UARTPortIO}
import sifive.blocks.devices.jtag.{JTAGPins, JTAGPinsFromPort}
import sifive.blocks.devices.pinctrl.BasePin
import sifive.fpgashells.ip.xilinx.{IBUFG, IOBUF, PULLUP, PowerOnResetFPGAOnly}
import chipyard._
import chipyard.harness._
import chipyard.iobinders.JTAGChipIO
import chisel3.experimental.BaseModule
import freechips.rocketchip.devices.debug.HasPeripheryDebug
import sifive.blocks.devices.gpio.{GPIOPortIO, HasPeripheryGPIOModuleImp}
import sifive.fpgashells.shell.FlippedJTAGIO
import testchipip._

class WithArty100TUART extends OverrideHarnessBinder({
  (system: HasPeripheryUARTModuleImp, th: BaseModule, ports: Seq[UARTPortIO]) => {
    th match { case ath: Arty100TTestHarnessImp => {
      ath.athOuter.io_uart_bb.bundle <> ports.head
    }}
  }
})

class WithArty100JTAG extends OverrideHarnessBinder({
  (system: HasPeripheryDebug, th: BaseModule, ports: Seq[Data]) => {
    th match { case ath: Arty100TTestHarnessImp => {
      ports.map {
        case j: JTAGChipIO =>
          val jtagModule = ath.athOuter.jtagOverlay
          jtagModule.TDO.data := j.TDO
          jtagModule.TDO.driven := true.B
          j.TCK := jtagModule.TCK
          j.TMS := jtagModule.TMS
          j.TDI := jtagModule.TDI
      }
    }}
  }})

class WithArty100TDDR extends OverrideHarnessBinder({
  (system: CanHaveMasterTLMemPort, th: BaseModule, ports: Seq[HeterogeneousBag[TLBundle]]) => {
    th match { case ath: Arty100TTestHarnessImp => {
      require(ports.size == 1)

      val bundles = ath.athOuter.ddrClient.out.map(_._1)
      val ddrClientBundle = Wire(new HeterogeneousBag(bundles.map(_.cloneType)))
      bundles.zip(ddrClientBundle).foreach { case (bundle, io) => bundle <> io }
      ddrClientBundle <> ports.head
    }}
  }
})

class WithArty100TGPIO extends OverrideHarnessBinder({
  (system: HasPeripheryGPIOModuleImp, th: BaseModule, ports: Seq[GPIOPortIO]) => {
    th match {case ath: Arty100TTestHarnessImp => {
      (ath.athOuter.io_gpio_bb zip ports).map{ case (gpio, port) =>
        gpio.bundle <> port
      }
    }}
  }
})