// See LICENSE.Sifive for license details.
#include <platform.h>
#include <smp.h>
#include "common.h"

  .section .text.init
  .option norvc
  .globl _prog_start
_prog_start:
  csrr a0, mhartid
  beqz a0, program
  wfi
program:
  li sp, (PAYLOAD_DEST + 0xffff000)
  call main
  wfi
  j  program
end_loop:
  j end_loop
  .section .dtb
  .align 3
dtb:
//.incbin "device.dtb"
