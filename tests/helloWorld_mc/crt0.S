// See LICENSE.Sifive for license details.
#include <platform.h>
#include <smp.h>
#include "common.h"

  //.section .text.hart
  //.globl _hart0
//_hart0:
  //li s0, 0
  .section .text.init
  .option norvc
  .globl _prog_start
_prog_start:
  // smp_pause(s1, s2)
  li sp, (PAYLOAD_DEST + 0xffff000)
  call main
  // smp_resume(s1, s2)
  // csrr a0, mhartid // hartid for next level bootloader
  // la a1, dtb // dtb address for next level bootloader
  // li s1, PAYLOAD_DEST
  // jr s1
loop:
  j loop
  
  .section .dtb
  .align 3
dtb:
//.incbin "device.dtb"
