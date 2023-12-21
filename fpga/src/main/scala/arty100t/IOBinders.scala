package chipyard.fpga.arty100t

import chisel3._
import chisel3.experimental.{DataMirror, IO}
import freechips.rocketchip.diplomacy.{InModuleBody, Resource, ResourceAddress, ResourceBinding}
import freechips.rocketchip.subsystem.BaseSubsystem
import freechips.rocketchip.util.HeterogeneousBag
import freechips.rocketchip.tilelink.TLBundle
import sifive.blocks.devices.uart.HasPeripheryUARTModuleImp
import sifive.blocks.devices.spi.{HasPeripherySPI, HasPeripherySPIModuleImp, MMCDevice}
import sifive.fpgashells.devices.xilinx.xilinxvc707pciex1.HasSystemXilinxVC707PCIeX1ModuleImp
import chipyard.CanHaveMasterTLMemPort
import chipyard.iobinders.{ComposeIOBinder, OverrideIOBinder, OverrideLazyIOBinder}
import freechips.rocketchip.devices.debug.HasPeripheryDebug
import sifive.blocks.devices.gpio.{HasPeripheryGPIO, HasPeripheryGPIOModuleImp}

class WithGPIOIOPassthrough extends OverrideIOBinder({
  (system: HasPeripheryGPIOModuleImp) => {
    val io_gpio_pins_temp = system.gpio.zipWithIndex.map { case (dio, i) => IO(dio.cloneType).suggestName(s"gpio_$i") }
    (io_gpio_pins_temp zip system.gpio).map { case (io, sysio) =>
      io <> sysio
    }
    (io_gpio_pins_temp, Nil)
  }
})

class WithUARTIOPassthrough extends OverrideIOBinder({
  (system: HasPeripheryUARTModuleImp) => {
    val io_uart_pins_temp = system.uart.zipWithIndex.map { case (dio, i) => IO(dio.cloneType).suggestName(s"uart_$i") }
    (io_uart_pins_temp zip system.uart).map { case (io, sysio) =>
      io <> sysio
    }
    (io_uart_pins_temp, Nil)
  }
})

class WithTLIOPassthrough extends OverrideIOBinder({
  (system: CanHaveMasterTLMemPort) => {
    val io_tl_mem_pins_temp = IO(DataMirror.internal.chiselTypeClone[HeterogeneousBag[TLBundle]](system.mem_tl)).suggestName("tl_slave")
    io_tl_mem_pins_temp <> system.mem_tl
    (Seq(io_tl_mem_pins_temp), Nil)
  }
})
