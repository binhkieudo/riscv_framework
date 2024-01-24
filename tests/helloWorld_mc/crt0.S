// See LICENSE.Sifive for license details.
#include <platform.h>
#include <smp.h>
#include "common.h"

  .section .text.init
  .option norvc
  .globl _prog_start
_prog_start:
  li sp, (PAYLOAD_DEST + 0xffff000)
  call main
end_loop:
  j end_loop
  
  .section .dtb
  .align 3
dtb:
//.incbin "device.dtb"
