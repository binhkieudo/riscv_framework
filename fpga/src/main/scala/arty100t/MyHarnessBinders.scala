package riscv_framework.fpga

import chipyard._
import chipyard.harness._
import chipyard.iobinders._
import chisel3._
import freechips.rocketchip.diplomacy.LazyRawModuleImp
import freechips.rocketchip.util.HeterogeneousBag
import sifive.blocks.devices.uart.UARTPortIO
import sifive.fpgashells.clocks._
import sifive.fpgashells.ip.xilinx._
import sifive.fpgashells.shell._
import sifive.fpgashells.shell.xilinx._
import testchipip.serdes._

class MyWithArty100TUARTTSI extends HarnessBinder({
  case (th: HasHarnessInstantiators, port: UARTTSIPort, chipId: Int) => {

    val ath = th.asInstanceOf[LazyRawModuleImp].wrapper.asInstanceOf[Arty100THarness]
    val harnessIO = IO(new UARTPortIO(port.io.uartParams)).suggestName("uart_tsi")
    harnessIO <> port.io.uart
    val packagePinsWithPackageIOs = Seq(
      ("A9" , IOPin(harnessIO.rxd)),
      ("D10", IOPin(harnessIO.txd)))
    packagePinsWithPackageIOs foreach { case (pin, io) => {
      ath.xdc.addPackagePin(io, pin)
      ath.xdc.addIOStandard(io, "LVCMOS33")
      ath.xdc.addIOB(io)
    } }
    print ("Config TSI done!\r\n")
//    ath.other_leds(1) := port.io.dropped
//    ath.other_leds(9) := port.io.tsi2tl_state(0)
//    ath.other_leds(10) := port.io.tsi2tl_state(1)
//    ath.other_leds(11) := port.io.tsi2tl_state(2)
//    ath.other_leds(12) := port.io.tsi2tl_state(3)
  }
})


class MyWithArty100TDDRTL extends HarnessBinder({
  case (th: HasHarnessInstantiators, port: TLMemPort, chipId: Int) => {
    val artyTh = th.asInstanceOf[LazyRawModuleImp].wrapper.asInstanceOf[Arty100THarness]
    val bundles = artyTh.ddrClient.out.map(_._1)
    val ddrClientBundle = Wire(new HeterogeneousBag(bundles.map(_.cloneType)))
    bundles.zip(ddrClientBundle).foreach { case (bundle, io) => bundle <> io }
    ddrClientBundle <> port.io
    print ("Config DDR done!!!\r\n")
  }
})

// Uses PMOD JA/JB
class MyWithArty100TSerialTLToGPIO extends HarnessBinder({
  case (th: HasHarnessInstantiators, port: SerialTLPort, chipId: Int) => {
    val artyTh = th.asInstanceOf[LazyRawModuleImp].wrapper.asInstanceOf[Arty100THarness]
    val harnessIO = IO(chiselTypeOf(port.io)).suggestName("serial_tl")
    harnessIO <> port.io

    harnessIO match {
      case io: DecoupledSerialIO => {
        val clkIO = io match {
          case io: InternalSyncSerialIO => IOPin(io.clock_out)
          case io: ExternalSyncSerialIO => IOPin(io.clock_in)
        }
        val packagePinsWithPackageIOs = Seq(
          ("G13", clkIO),
          ("B11", IOPin(io.out.valid)),
          ("A11", IOPin(io.out.ready)),
          ("D12", IOPin(io.in.valid)),
          ("D13", IOPin(io.in.ready)),
          ("B18", IOPin(io.out.bits, 0)),
          ("A18", IOPin(io.out.bits, 1)),
          ("K16", IOPin(io.out.bits, 2)),
          ("E15", IOPin(io.out.bits, 3)),
          ("E16", IOPin(io.in.bits, 0)),
          ("D15", IOPin(io.in.bits, 1)),
          ("C15", IOPin(io.in.bits, 2)),
          ("J17", IOPin(io.in.bits, 3))
        )
        packagePinsWithPackageIOs foreach { case (pin, io) => {
          artyTh.xdc.addPackagePin(io, pin)
          artyTh.xdc.addIOStandard(io, "LVCMOS33")
        }}

        // Don't add IOB to the clock, if its an input
        io match {
          case io: InternalSyncSerialIO => packagePinsWithPackageIOs foreach { case (pin, io) => {
            artyTh.xdc.addIOB(io)
          }}
          case io: ExternalSyncSerialIO => packagePinsWithPackageIOs.drop(1).foreach { case (pin, io) => {
            artyTh.xdc.addIOB(io)
          }}
        }

        artyTh.sdc.addClock("ser_tl_clock", clkIO, 100)
        artyTh.sdc.addGroup(pins = Seq(clkIO))
        artyTh.xdc.clockDedicatedRouteFalse(clkIO)
      }
    }
    print ("Config GPIO done!\r\n")
  }
})

// Maps the UART device to the on-board USB-UART
class MyWithArty100TUART(rxdPin: String = "A9", txdPin: String = "D10") extends HarnessBinder({
  case (th: HasHarnessInstantiators, port: UARTPort, chipId: Int) => {

    val ath = th.asInstanceOf[LazyRawModuleImp].wrapper.asInstanceOf[Arty100THarness]
    val harnessIO = IO(chiselTypeOf(port.io)).suggestName("uart")
    harnessIO <> port.io
    val packagePinsWithPackageIOs = Seq(
      (rxdPin, IOPin(harnessIO.rxd)),
      (txdPin, IOPin(harnessIO.txd)))
    packagePinsWithPackageIOs foreach { case (pin, io) => {
      ath.xdc.addPackagePin(io, pin)
      ath.xdc.addIOStandard(io, "LVCMOS33")
      ath.xdc.addIOB(io)
    } }
    print ("Config UART done!\r\n")
  }
})

class MyWithArty100TPMODUART extends MyWithArty100TUART("G2", "F3")

class MyWithArty100TJTAG extends HarnessBinder({
  case (th: HasHarnessInstantiators, port: JTAGPort, chipId: Int) => {
    val ath = th.asInstanceOf[LazyRawModuleImp].wrapper.asInstanceOf[Arty100THarness]
    val harnessIO = IO(chiselTypeOf(port.io)).suggestName("jtag")
    harnessIO <> port.io

    ath.sdc.addClock("JTCK", IOPin(harnessIO.TCK), 10)
    ath.sdc.addGroup(clocks = Seq("JTCK"))
    ath.xdc.clockDedicatedRouteFalse(IOPin(harnessIO.TCK))
    val packagePinsWithPackageIOs = Seq(
      ("F4", IOPin(harnessIO.TCK)),
      ("D2", IOPin(harnessIO.TMS)),
      ("E2", IOPin(harnessIO.TDI)),
      ("D4", IOPin(harnessIO.TDO))
    )
    packagePinsWithPackageIOs foreach { case (pin, io) => {
      ath.xdc.addPackagePin(io, pin)
      ath.xdc.addIOStandard(io, "LVCMOS33")
      ath.xdc.addPullup(io)
    } }
    print ("Config JTAG done!\r\n")
  }
})
