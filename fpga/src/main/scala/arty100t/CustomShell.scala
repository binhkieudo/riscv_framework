package chipyard.fpga.arty100t

import chisel3.{Bool, Wire}
import freechips.rocketchip.diplomacy.InModuleBody
import sifive.fpgashells.shell.{ClockInputDesignInput, ClockInputOverlayKey, ClockInputShellInput, DDROverlayKey, DDRShellInput, GPIOOverlayKey, GPIOShellInput, JTAGDebugOverlayKey, JTAGDebugShellInput, SPIOverlayKey, SPIShellInput, UARTOverlayKey, UARTShellInput}
import sifive.fpgashells.shell.xilinx._
import org.chipsalliance.cde.config._
import sifive.blocks.devices.gpio.PeripheryGPIOKey

class Arty100TCustomShell()(implicit p: Parameters) extends Series7Shell {
  val sys_clock = Overlay(ClockInputOverlayKey, new SysClockArty100ShellPlacer(this, ClockInputShellInput()))

  val gpio      = Seq.tabulate(16)(i => {Overlay(GPIOOverlayKey, new GPIOArty100ShellPlacer(this, GPIOShellInput()))})
  val jtag      = Overlay(JTAGDebugOverlayKey, new JTAGDebugArty100ShellPlacer(this, JTAGDebugShellInput()))
  val uart      = Overlay(UARTOverlayKey, new UARTArty100ShellPlacer(this, UARTShellInput()))
  val sdcard  = Overlay(SPIOverlayKey, new SDCardArty100ShellPlacer(this, SPIShellInput()))
  val ddr       = Overlay(DDROverlayKey, new DDRArty100ShellPlacer(this, DDRShellInput()))

  /*** Reset ***/
  val resetPin = InModuleBody { Wire(Bool()) }
  // PLL reset causes
  val pllReset = InModuleBody { Wire(Bool()) }
  // Place the sys_clock at the Shell if the user didn't ask for it
  p(ClockInputOverlayKey).foreach(_.place(ClockInputDesignInput()))
}