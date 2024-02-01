// See LICENSE.Sifive for license details.
#include <platform.h>
// #include <smp.h>
#include "common.h"
#include "include/platform.h"
#include "include/devices/clint.h"

// The hart that non-SMP tests should run on
#ifndef NONSMP_HART
#define NONSMP_HART 0
#endif

  .section .text.init
  .option norvc
  .globl _prog_start
  .globl hart0_sym
_prog_start:
  li sp, (0x80000000 + 0xffff000)
  call main
_loop:
  j _loop
_trap:
  j _trap


  .section .dtb
  .align 8
dtb:
  hart0_sym: .8byte 0x0