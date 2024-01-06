package chipyard.fpga.arty100t

import chisel3._
import chisel3.util._
import freechips.rocketchip.diplomacy._
import org.chipsalliance.cde.config.Parameters
import freechips.rocketchip.tilelink._
import freechips.rocketchip.prci.{ClockBundle, ClockBundleParameters}
import freechips.rocketchip.subsystem.SystemBusKey
import sifive.fpgashells.shell.xilinx._
import sifive.fpgashells.shell._
import sifive.fpgashells.clocks.{ClockGroup, ClockSinkNode, PLLFactoryKey, ResetWrangler}
import sifive.fpgashells.ip.xilinx.{IBUF, PowerOnResetFPGAOnly, mmcm}
import sifive.blocks.devices.uart._
import chipyard._
import chipyard.harness._
import chipyard.iobinders.HasIOBinders
import freechips.rocketchip.devices.debug.{HasPeripheryDebug, JtagDTMKey}
import freechips.rocketchip.jtag.JTAGIO
import sifive.blocks.devices.gpio.{GPIOPortIO, PeripheryGPIOKey}
import sifive.blocks.devices.spi.{PeripherySPIKey, SPIParams, SPIPortIO}


class Arty100THarness(override implicit val p: Parameters) extends Arty100TCustomShell { outer =>
  def dp = designParameters

  /*** Clock ***/
  val clockOverlay = dp(ClockInputOverlayKey).map(_.place(ClockInputDesignInput())).head
  val harnessSysPLL = dp(PLLFactoryKey)
  val harnessSysPLLNode = harnessSysPLL()
  val dutFreqMHz = (dp(SystemBusKey).dtsFrequency.get / (1000 * 1000)).toInt
  val dutClock = ClockSinkNode(freqMHz = dutFreqMHz)
  println(s"Arty100T FPGA Base Clock Freq: ${dutFreqMHz} MHz")
  val dutWrangler = LazyModule(new ResetWrangler())
  val dutGroup = ClockGroup()
  dutClock := dutWrangler.node := dutGroup := harnessSysPLLNode

  harnessSysPLLNode := clockOverlay.overlayOutput.node
  
  /*** GPIO ***/
  val io_gpio_bb = dp(PeripheryGPIOKey).map(p => BundleBridgeSource(() => (new GPIOPortIO(p))))
  (dp(GPIOOverlayKey) zip dp(PeripheryGPIOKey)).zipWithIndex.map { case ((placer, params), i) =>
    placer.place(GPIODesignInput(params, io_gpio_bb(i)))
  }

  /*** Debugger ***/
  val jtagOverlay = dp(JTAGDebugOverlayKey).head.place(JTAGDebugDesignInput()).overlayOutput.jtag

  /*** UART ***/
  val io_uart_bb = BundleBridgeSource(() => new UARTPortIO(dp(PeripheryUARTKey).headOption.getOrElse(UARTParams(0))))
  val uartOverlay = dp(UARTOverlayKey).head.place(UARTDesignInput(io_uart_bb))

  /*** SDCard ***/
  val io_sdcard_bb = BundleBridgeSource(() => new SPIPortIO(dp(PeripherySPIKey).head))
  val sdcardOverlay = dp(SPIOverlayKey).head.place(SPIDesignInput(dp(PeripherySPIKey).head, io_sdcard_bb))

  /*** DDR ***/
  val ddrOverlay = dp(DDROverlayKey).head.place(DDRDesignInput(dp(ExtTLMem).get.master.base, dutWrangler.node, harnessSysPLLNode)).asInstanceOf[DDRArty100PlacedOverlay]
  val ddrClient = TLClientNode(Seq(TLMasterPortParameters.v1(Seq(TLMasterParameters.v1(
    name = "chip_ddr",
    sourceId = IdRange(0, 1 << dp(ExtTLMem).get.master.idBits)
  )))))
  val ddrBlockDuringReset = LazyModule(new TLBlockDuringReset(4))
  ddrOverlay.overlayOutput.ddr := ddrBlockDuringReset.node := ddrClient

  override lazy val module = new Arty100TTestHarnessImp(_outer = this)

}

class Arty100TTestHarnessImp(_outer: Arty100THarness) extends LazyRawModuleImp(_outer)
  with HasHarnessInstantiators
{
  val athOuter = _outer

  val reset = IO(Input(Bool()))
  _outer.xdc.addBoardPin(reset, "reset")

  val resetIBUF = Module(new IBUF)
  resetIBUF.io.I := reset

  val sysclk: Clock = _outer.sys_clock.get() match {
    case Some(x: SysClockArty100PlacedOverlay) => x.clock
  }
  val powerOnReset = PowerOnResetFPGAOnly(sysclk)
  _outer.sdc.addAsyncPath(Seq(powerOnReset))

  _outer.resetPin := resetIBUF.io.O

  _outer.clockOverlay.overlayOutput.node.out(0)._1.reset := ~_outer.resetPin


  val clk_100mhz = _outer.clockOverlay.overlayOutput.node.out.head._1.clock

  _outer.pllReset := (!resetIBUF.io.O) || powerOnReset
  _outer.harnessSysPLL.plls.foreach(_._1.getReset.get := _outer.pllReset)

  def referenceClockFreqMHz = _outer.dutFreqMHz
  def referenceClock = _outer.dutClock.in.head._1.clock
  def referenceReset = _outer.dutClock.in.head._1.reset
  def success = { require(false, "Unused"); false.B }

  _outer.ddrOverlay.mig.module.clock := harnessBinderClock
  _outer.ddrOverlay.mig.module.reset := harnessBinderReset
  _outer.ddrBlockDuringReset.module.clock := harnessBinderClock
  _outer.ddrBlockDuringReset.module.reset := harnessBinderReset.asBool || !_outer.ddrOverlay.mig.module.io.port.init_calib_complete

  instantiateChipTops()
}

class Arty100TinyHarness(override implicit val p: Parameters) extends Arty100TCustomShell { outer =>
  def dp = designParameters

  /*** Clock ***/
  val clockOverlay = dp(ClockInputOverlayKey).map(_.place(ClockInputDesignInput())).head
  val harnessSysPLL = dp(PLLFactoryKey)
  val harnessSysPLLNode = harnessSysPLL()
  val dutFreqMHz = (dp(SystemBusKey).dtsFrequency.get / (1000 * 1000)).toInt
  val dutClock = ClockSinkNode(freqMHz = dutFreqMHz)
  println(s"Arty100T FPGA Base Clock Freq: ${dutFreqMHz} MHz")
  val dutWrangler = LazyModule(new ResetWrangler())
  val dutGroup = ClockGroup()
  dutClock := dutWrangler.node := dutGroup := harnessSysPLLNode

  harnessSysPLLNode := clockOverlay.overlayOutput.node

  /*** GPIO ***/
  val io_gpio_bb = dp(PeripheryGPIOKey).map(p => BundleBridgeSource(() => (new GPIOPortIO(p))))
  (dp(GPIOOverlayKey) zip dp(PeripheryGPIOKey)).zipWithIndex.map { case ((placer, params), i) =>
    placer.place(GPIODesignInput(params, io_gpio_bb(i)))
  }

  /*** Debugger ***/
  val jtagOverlay = dp(JTAGDebugOverlayKey).head.place(JTAGDebugDesignInput()).overlayOutput.jtag

  /*** UART ***/
  val io_uart_bb = BundleBridgeSource(() => new UARTPortIO(dp(PeripheryUARTKey).headOption.getOrElse(UARTParams(0))))
  val uartOverlay = dp(UARTOverlayKey).head.place(UARTDesignInput(io_uart_bb))

  /*** SDCard ***/
  val io_sdcard_bb = BundleBridgeSource(() => new SPIPortIO(dp(PeripherySPIKey).head))
  val sdcardOverlay = dp(SPIOverlayKey).head.place(SPIDesignInput(dp(PeripherySPIKey).head, io_sdcard_bb))

  /*** DDR ***/
//  val ddrOverlay = dp(DDROverlayKey).head.place(DDRDesignInput(dp(ExtTLMem).get.master.base, dutWrangler.node, harnessSysPLLNode)).asInstanceOf[DDRArty100PlacedOverlay]
//  val ddrClient = TLClientNode(Seq(TLMasterPortParameters.v1(Seq(TLMasterParameters.v1(
//    name = "chip_ddr",
//    sourceId = IdRange(0, 1 << dp(ExtTLMem).get.master.idBits)
//  )))))
//  val ddrBlockDuringReset = LazyModule(new TLBlockDuringReset(4))
//  ddrOverlay.overlayOutput.ddr := ddrBlockDuringReset.node := ddrClient

  override lazy val module = new Arty100TinyTestHarnessImp(_outer = this)
}

class Arty100TinyTestHarnessImp(_outer: Arty100TinyHarness) extends LazyRawModuleImp(_outer)
  with HasHarnessInstantiators
{
  val athOuter = _outer

  val reset = IO(Input(Bool()))
  _outer.xdc.addBoardPin(reset, "reset")

  val resetIBUF = Module(new IBUF)
  resetIBUF.io.I := reset

  val sysclk: Clock = _outer.sys_clock.get() match {
    case Some(x: SysClockArty100PlacedOverlay) => x.clock
  }
  val powerOnReset = PowerOnResetFPGAOnly(sysclk)
  _outer.sdc.addAsyncPath(Seq(powerOnReset))

  _outer.resetPin := resetIBUF.io.O

  _outer.clockOverlay.overlayOutput.node.out(0)._1.reset := ~_outer.resetPin


  val clk_100mhz = _outer.clockOverlay.overlayOutput.node.out.head._1.clock

  _outer.pllReset := (!resetIBUF.io.O) || powerOnReset
  _outer.harnessSysPLL.plls.foreach(_._1.getReset.get := _outer.pllReset)

  def referenceClockFreqMHz = _outer.dutFreqMHz
  def referenceClock = _outer.dutClock.in.head._1.clock
  def referenceReset = _outer.dutClock.in.head._1.reset
  def success = { require(false, "Unused"); false.B }

//  _outer.ddrOverlay.mig.module.clock := harnessBinderClock
//  _outer.ddrOverlay.mig.module.reset := harnessBinderReset
//  _outer.ddrBlockDuringReset.module.clock := harnessBinderClock
//  _outer.ddrBlockDuringReset.module.reset := harnessBinderReset.asBool || !_outer.ddrOverlay.mig.module.io.port.init_calib_complete

  instantiateChipTops()
}

class Arty100TinyWithMemHarness(override implicit val p: Parameters) extends Arty100TCustomShell { outer =>
  def dp = designParameters

  /*** Clock ***/
  val clockOverlay = dp(ClockInputOverlayKey).map(_.place(ClockInputDesignInput())).head
  val harnessSysPLL = dp(PLLFactoryKey)
  val harnessSysPLLNode = harnessSysPLL()
  val dutFreqMHz = (dp(SystemBusKey).dtsFrequency.get / (1000 * 1000)).toInt
  val dutClock = ClockSinkNode(freqMHz = dutFreqMHz)
  println(s"Arty100T FPGA Base Clock Freq: ${dutFreqMHz} MHz")
  val dutWrangler = LazyModule(new ResetWrangler())
  val dutGroup = ClockGroup()
  dutClock := dutWrangler.node := dutGroup := harnessSysPLLNode

  harnessSysPLLNode := clockOverlay.overlayOutput.node

  /*** GPIO ***/
  val io_gpio_bb = dp(PeripheryGPIOKey).map(p => BundleBridgeSource(() => (new GPIOPortIO(p))))
  (dp(GPIOOverlayKey) zip dp(PeripheryGPIOKey)).zipWithIndex.map { case ((placer, params), i) =>
    placer.place(GPIODesignInput(params, io_gpio_bb(i)))
  }

  /*** Debugger ***/
  val jtagOverlay = dp(JTAGDebugOverlayKey).head.place(JTAGDebugDesignInput()).overlayOutput.jtag

  /*** UART ***/
  val io_uart_bb = BundleBridgeSource(() => new UARTPortIO(dp(PeripheryUARTKey).headOption.getOrElse(UARTParams(0))))
  val uartOverlay = dp(UARTOverlayKey).head.place(UARTDesignInput(io_uart_bb))

  /*** SDCard ***/
  val io_sdcard_bb = BundleBridgeSource(() => new SPIPortIO(dp(PeripherySPIKey).head))
  val sdcardOverlay = dp(SPIOverlayKey).head.place(SPIDesignInput(dp(PeripherySPIKey).head, io_sdcard_bb))

  /*** DDR ***/
  val ddrOverlay = dp(DDROverlayKey).head.place(DDRDesignInput(dp(ExtTLMem).get.master.base, dutWrangler.node, harnessSysPLLNode)).asInstanceOf[DDRArty100PlacedOverlay]
  val ddrClient = TLClientNode(Seq(TLMasterPortParameters.v1(Seq(TLMasterParameters.v1(
      name = "chip_ddr",
      sourceId = IdRange(0, 1 << dp(ExtTLMem).get.master.idBits)
  )))))
  val ddrBlockDuringReset = LazyModule(new TLBlockDuringReset(4))
  ddrOverlay.overlayOutput.ddr := ddrBlockDuringReset.node := ddrClient

  override lazy val module = new Arty100TinyWithMemTestHarnessImp(_outer = this)
}

class Arty100TinyWithMemTestHarnessImp(_outer: Arty100TinyWithMemHarness) extends LazyRawModuleImp(_outer)
  with HasHarnessInstantiators
{
  val athOuter = _outer

  val reset = IO(Input(Bool()))
  _outer.xdc.addBoardPin(reset, "reset")

  val resetIBUF = Module(new IBUF)
  resetIBUF.io.I := reset

  val sysclk: Clock = _outer.sys_clock.get() match {
    case Some(x: SysClockArty100PlacedOverlay) => x.clock
  }
  val powerOnReset = PowerOnResetFPGAOnly(sysclk)
  _outer.sdc.addAsyncPath(Seq(powerOnReset))

  _outer.resetPin := resetIBUF.io.O

  _outer.clockOverlay.overlayOutput.node.out(0)._1.reset := ~_outer.resetPin


  val clk_100mhz = _outer.clockOverlay.overlayOutput.node.out.head._1.clock

  _outer.pllReset := (!resetIBUF.io.O) || powerOnReset
  _outer.harnessSysPLL.plls.foreach(_._1.getReset.get := _outer.pllReset)

  def referenceClockFreqMHz = _outer.dutFreqMHz
  def referenceClock = _outer.dutClock.in.head._1.clock
  def referenceReset = _outer.dutClock.in.head._1.reset
  def success = { require(false, "Unused"); false.B }

  _outer.ddrOverlay.mig.module.clock := harnessBinderClock
  _outer.ddrOverlay.mig.module.reset := harnessBinderReset
  _outer.ddrBlockDuringReset.module.clock := harnessBinderClock
  _outer.ddrBlockDuringReset.module.reset := harnessBinderReset.asBool || !_outer.ddrOverlay.mig.module.io.port.init_calib_complete

  instantiateChipTops()
}