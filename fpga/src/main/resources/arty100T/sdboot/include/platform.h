// See LICENSE for license details.

#ifndef _CHIPYARD_PLATFORM_H
#define _CHIPYARD_PLATFORM_H

#include "const.h"
#include "devices/gpio.h"
#include "devices/spi.h"
#include "devices/uart.h"

 // Some things missing from the official encoding.h
#if __riscv_xlen == 32
  #define MCAUSE_INT         0x80000000UL
  #define MCAUSE_CAUSE       0x800003FFUL
#else
   #define MCAUSE_INT         0x8000000000000000UL
   #define MCAUSE_CAUSE       0x80000000000003FFUL
#endif

/****************************************************************************
 * Platform definitions
 *****************************************************************************/

// CPU info
#define NUM_CORES 1
#define GLOBAL_INT_SIZE 38
#define GLOBAL_INT_MAX_PRIORITY 7

// Memory map
#define MASKROM_MEM_ADDR _AC(0x10000,UL)
#define MASKROM_MEM_SIZE _AC(0x1000,UL)
#define MEMORY_MEM_ADDR _AC(0x80000000,UL)
#define MEMORY_MEM_SIZE _AC(0x00002000,UL)

#define UART_CTRL_ADDR _AC(0x64000000,UL)
#define UART_CTRL_SIZE _AC(0x1000,UL)
#define GPIO_CTRL_ADDR _AC(0x64002000,UL)
#define GPIO_CTRL_SIZE _AC(0x1000,UL)
#define SPI_CTRL_ADDR _AC(0x64003000,UL)
#define SPI_CTRL_SIZE _AC(0x1000,UL)

// IOF masks


// Interrupt numbers
#define UART_INT_BASE 1
#define GPIO_INT_BASE 2
#define SPI_INT_BASE 18

// Helper functions
#define _REG32(p, i) (*(volatile uint32_t *)((p) + (i)))
// Bulk set bits in `reg` to either 0 or 1.
// E.g. SET_BITS(MY_REG, 0x00000007, 0) would generate MY_REG &= ~0x7
// E.g. SET_BITS(MY_REG, 0x00000007, 1) would generate MY_REG |= 0x7
#define SET_BITS(reg, mask, value) if ((value) == 0) { (reg) &= ~(mask); } else { (reg) |= (mask); }
#define GPIO_REG(offset) _REG32(GPIO_CTRL_ADDR, offset)
#define MASKROM_REG(offset) _REG32(MASKROM_CTRL_ADDR, offset)
#define MEMORY_REG(offset) _REG32(MEMORY_CTRL_ADDR, offset)
#define SPI_REG(offset) _REG32(SPI_CTRL_ADDR, offset)
#define UART_REG(offset) _REG32(UART_CTRL_ADDR, offset)
#define PLIC_REG64(offset) _REG64(PLIC_CTRL_ADDR, offset)

// Misc


#endif /* _CHIPYARD_PLATFORM_H */
