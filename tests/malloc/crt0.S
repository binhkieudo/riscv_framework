// See LICENSE.Sifive for license details.
#include <platform.h>
#include <smp.h>
#include "common.h"

  .section .text.init
  .option norvc
  .globl _prog_start
_prog_start:
  //smp_pause(s1, s2)
  li sp, (PAYLOAD_DEST + 0xffff000)
  call main
  //smp_resume(s1, s2)
  csrr a0, mhartid // hartid for next level bootloader
_loop:
  j _loop

  .section .dtb
  .align 3
dtb:
//.incbin "device.dtb"
