// See LICENSE.Sifive for license details.
#include <platform.h>
#include "common.h"

  .section .sdata
  myid    0
  .section .text.init
  .option norvc
  .globl _prog_start
_prog_start:
  li sp, (PAYLOAD_DEST + 0x1ff0)
  csrr a0, mhartid
  sw   a0, myid
  addi a1, zero, 0
  call main
  csrr a0, mhartid // hartid for next level bootloader
  li s1, PAYLOAD_DEST
  jr s1

  .section .dtb
  .align 3
dtb:
