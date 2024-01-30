// See LICENSE for license details.

#ifndef _CHIPYARD_PLATFORM_H
#define _CHIPYARD_PLATFORM_H

#include "const.h"
/****************************************************************************
 * Platform definitions
 *****************************************************************************/
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

// Misc
#define GPIO_OUTPUT_EN  (0x08)
#define GPIO_OUTPUT_VAL (0x0C)

#endif /* _CHIPYARD_PLATFORM_H */
