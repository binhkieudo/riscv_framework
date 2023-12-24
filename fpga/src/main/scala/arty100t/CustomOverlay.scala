package chipyard.fpga.arty100t

import freechips.rocketchip.diplomacy.{InModuleBody, ValName}
import chisel3._
import org.chipsalliance.cde.config._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.util.SyncResetSynchronizerShiftReg
import sifive.fpgashells.clocks._
import sifive.fpgashells.shell._
import sifive.fpgashells.ip.xilinx._
import sifive.fpgashells.devices.xilinx.xilinxarty100tmig._
import sifive.fpgashells.shell.xilinx._

/* =============================================================
===================== System clock =============================
===============================================================*/
class SysClockArty100PlacedOverlay(val shell: Arty100TCustomShell,
                                name: String,
                                val designInput: ClockInputDesignInput,
                                val shellInput: ClockInputShellInput)
  extends SingleEndedClockInputXilinxPlacedOverlay(name, designInput, shellInput)
{
  val node = shell { ClockSourceNode(freqMHz = 100, jitterPS = 50) }

  shell { InModuleBody {
    val clk: Clock = io
    shell.xdc.addPackagePin(clk, "E3")
    shell.xdc.addIOStandard(clk, "LVCMOS33")
  } }
}
class SysClockArty100ShellPlacer(val shell: Arty100TCustomShell,
                              val shellInput: ClockInputShellInput)(implicit val valName: ValName)
  extends ClockInputShellPlacer[Arty100TCustomShell] {
  def place(designInput: ClockInputDesignInput) = new SysClockArty100PlacedOverlay(shell, valName.name, designInput, shellInput)
}

/* =============================================================
========================== DDR =================================
===============================================================*/
case object ArtyDDRSize extends Field[BigInt](0x10000000L * 1) // 256 MB
class DDRArty100PlacedOverlay(val shell: Arty100TCustomShell,
                              name: String,
                              val designInput: DDRDesignInput,
                              val shellInput: DDRShellInput)
  extends DDRPlacedOverlay[XilinxArty100TMIGPads](name, designInput, shellInput)
{
  val size = p(ArtyDDRSize)

  val ddrClk1 = shell { ClockSinkNode(freqMHz = 166.666)}
  val ddrClk2 = shell { ClockSinkNode(freqMHz = 200)}
  val ddrGroup = shell { ClockGroup() }
  ddrClk1 := di.wrangler := ddrGroup := di.corePLL
  ddrClk2 := di.wrangler := ddrGroup

  val migParams = XilinxArty100TMIGParams(address = AddressSet.misaligned(di.baseAddress, size))
  val mig = LazyModule(new XilinxArty100TMIG(migParams))
  val ioNode = BundleBridgeSource(() => mig.module.io.cloneType)
  val topIONode = shell { ioNode.makeSink() }
  val ddrUI     = shell { ClockSourceNode(freqMHz = 100) }
  val areset    = shell { ClockSinkNode(Seq(ClockSinkParameters())) }
  areset := di.wrangler := ddrUI

  def overlayOutput = DDROverlayOutput(ddr = mig.node)
  def ioFactory = new XilinxArty100TMIGPads(size)

  InModuleBody { ioNode.bundle <> mig.module.io }

  shell { InModuleBody {
    require (shell.sys_clock.get.isDefined, "Use of DDRArtyPlacedOverlay depends on SysClockArtyPlacedOverlay")
    val (sys, _) = shell.sys_clock.get.get.overlayOutput.node.out(0)
    val (ui, _) = ddrUI.out(0)
    val (dclk1, _) = ddrClk1.in(0)
    val (dclk2, _) = ddrClk2.in(0)
    val (ar, _) = areset.in(0)
    val port = topIONode.bundle.port

    io <> port
    ui.clock := port.ui_clk
    ui.reset := !port.mmcm_locked || port.ui_clk_sync_rst
    port.sys_clk_i := dclk1.clock.asUInt
    port.clk_ref_i := dclk2.clock.asUInt
    port.sys_rst := shell.pllReset
    port.aresetn := !ar.reset
  } }

  shell.sdc.addGroup(clocks = Seq("clk_pll_i"), pins = Seq(mig.island.module.blackbox.io.ui_clk))
}
class DDRArty100ShellPlacer(val shell: Arty100TCustomShell,
                            val shellInput: DDRShellInput)(implicit val valName: ValName)
  extends DDRShellPlacer[Arty100TCustomShell] {
  def place(designInput: DDRDesignInput) = new DDRArty100PlacedOverlay(shell, valName.name, designInput, shellInput)
}

/* =============================================================
========================= GPIO =================================
===============================================================*/
class GPIOArty100PlacedOverlay(val shell: Arty100TCustomShell,
                               name: String,
                               val designInput: GPIODesignInput,
                               val shellInput: GPIOShellInput)
  extends GPIOXilinxPlacedOverlay(name, designInput, shellInput)
{
  shell { InModuleBody {
    val gpioLocations = List("H5", "J5", "T9", "T10", "E1", "F6", "G6", "G4", "J4", "G3", "H4", "J2", "J3", "K2", "H6", "K1") //J3 pins 1-16
    val iosWithLocs = io.gpio.zip(gpioLocations)
    val packagePinsWithPackageIOs = iosWithLocs.map { case (io, pin) => (pin, IOPin(io)) }
    println("Placing GPIO...")
    packagePinsWithPackageIOs foreach { case (pin, io) => {
      println(s"$pin <-> $io")
    }
    }

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
      shell.xdc.addIOB(io)
    } }
  } }
}
class GPIOArty100ShellPlacer(val shell: Arty100TCustomShell,
                             val shellInput: GPIOShellInput)(implicit val valName: ValName)
  extends GPIOShellPlacer[Arty100TCustomShell] {
  def place(designInput: GPIODesignInput) = new GPIOArty100PlacedOverlay(shell, valName.name, designInput, shellInput)
}

/* =============================================================
==================== SDCard (PMOD-JB) ==========================
===============================================================*/

/* Digilent Pmod MicroSD <-> Arty PMOD-JB
  Protocol: SDIO (https://en.wikipedia.org/wiki/SD_card#SDIO_cards)

  PIN ArtyPIN Signal   Desc                  PIN ArtyPIN  Signal    Desc
   1    E15    ~CS     Chip select/Data3      7    J17     DAT1     Data 1
   2    E16    MOSI    MOSI/Command           8    J18     DAT2     Data 2
   3    D15    MISO    MISO/Data0             9    K15     CD       Card Detect
   4    C15    SCK     Serial Clock           10   J15     NC       Not Connected
   5           GND     Ground                 11           GND      Ground
   6           VCC     3.3V                   12           VCC      3.3V
 */
class SDCardArty100PlacedOverlay(val shell: Arty100TCustomShell,
                                 name: String,
                                 val designInput: SPIDesignInput,
                                 val shellInput: SPIShellInput)
  extends SDIOXilinxPlacedOverlay(name, designInput, shellInput)
  {
    shell { InModuleBody {
      // Map SDIO pins with the physical FPGA pins
      val packagePinsWithPackageIOs = Seq(
        ("C15", IOPin(io.spi_clk)),
        ("E16", IOPin(io.spi_cs)),
        ("D15", IOPin(io.spi_dat(0))),
        ("J17", IOPin(io.spi_dat(1))),
        ("J18", IOPin(io.spi_dat(2))),
        ("E15", IOPin(io.spi_dat(3)))
      )
      println("Placing SDIO...")
      packagePinsWithPackageIOs foreach { case (pin, io) =>
        println(s"$pin <-> $io")
      }

      packagePinsWithPackageIOs foreach { case (pin, io) => {
       shell.xdc.addPackagePin(io, pin)
       shell.xdc.addIOStandard(io, "LVCMOS33")
       shell.xdc.addIOB(io)
      }
      }
      packagePinsWithPackageIOs drop 1 foreach { case (pin, io) => {
       shell.xdc.addPullup(io)
      }
      }
    }
    }
  }
class SDCardArty100ShellPlacer (val shell: Arty100TCustomShell,
                                val shellInput: SPIShellInput)(implicit val valName: ValName)
  extends SPIShellPlacer[Arty100TCustomShell] {
  def place(designInput: SPIDesignInput) = new SDCardArty100PlacedOverlay(shell, valName.name, designInput, shellInput)
}

/* =============================================================
================= JTAG Debugger (PMOD-JD) ======================
===============================================================*/
class JTAGDebugArty100PlacedOverlay(val shell: Arty100TCustomShell,
                                 name: String,
                                 val designInput: JTAGDebugDesignInput,
                                 val shellInput: JTAGDebugShellInput)
  extends JTAGDebugXilinxPlacedOverlay(name, designInput, shellInput)
{
  shell { InModuleBody {
    shell.sdc.addClock("JTCK", IOPin(io.jtag_TCK), 10)
    shell.sdc.addGroup(clocks = Seq("JTCK"))
    shell.xdc.clockDedicatedRouteFalse(IOPin(io.jtag_TCK))
    val packagePinsWithPackageIOs = Seq(("F4", IOPin(io.jtag_TCK)),  //pin JD-3
      ("D3", IOPin(io.jtag_TMS)),  //pin JD-2
      ("D4", IOPin(io.jtag_TDI)),  //pin JD-1
      ("F3", IOPin(io.jtag_TDO)),  //pin JD-3
      ("E2", IOPin(io.srst_n)))    //pin JD-7

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
      shell.xdc.addPullup(io)
    } }
  } }
}
class JTAGDebugArty100ShellPlacer(val shell: Arty100TCustomShell, val shellInput: JTAGDebugShellInput)(implicit val valName: ValName)
  extends JTAGDebugShellPlacer[Arty100TCustomShell] {
  def place(designInput: JTAGDebugDesignInput) = new JTAGDebugArty100PlacedOverlay(shell, valName.name, designInput, shellInput)
}

/* =============================================================
============================== UART ============================
===============================================================*/
class UARTArty100PlacedOverlay(val shell: Arty100TCustomShell, name: String, val designInput: UARTDesignInput, val shellInput: UARTShellInput)
  extends UARTXilinxPlacedOverlay(name, designInput, shellInput, false)
{
  shell { InModuleBody {
    val packagePinsWithPackageIOs = Seq(("A9", IOPin(io.rxd)),
      ("D10", IOPin(io.txd)))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
      shell.xdc.addIOB(io)
    } }
  } }
}
class UARTArty100ShellPlacer(val shell: Arty100TCustomShell, val shellInput: UARTShellInput)(implicit val valName: ValName)
  extends UARTShellPlacer[Arty100TCustomShell] {
  def place(designInput: UARTDesignInput) = new UARTArty100PlacedOverlay(shell, valName.name, designInput, shellInput)
}