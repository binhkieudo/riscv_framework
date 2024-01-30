// See LICENSE.Sifive for license details.
#include <platform.h>

  .section .sdata
  .section .text.init
  .option norvc
  .globl _prog_start
_prog_start:
  li sp, (MEMORY_MEM_ADDR + 0x7f0)
  csrr a0, mhartid // hartid for next level bootloader
  li t0, (GPIO_CTRL_ADDR + GPIO_OUTPUT_EN)
  li t1, 1
  amoswap.w t1, t1, (t0)
  li t0, (GPIO_CTRL_ADDR + GPIO_OUTPUT_VAL)
  li t1, 1
  amoswap.w t1, t1, (t0)
  wfi
  fence.i
  li s1, MEMORY_MEM_ADDR
  jr s1
