// See LICENSE.Sifive for license details.
#include <platform.h>
// #include <smp.h>
#include "common.h"

// The hart that non-SMP tests should run on
#ifndef NONSMP_HART
#define NONSMP_HART 0
#endif

  .section .text.init
  .option norvc
  .globl _prog_start
_prog_start:
  // Enable machine software interrupt
  li s2, 0x8  
  csrw mie, s2
  // Check if the running hart id is 0
  li s1, NONSMP_HART
  csrr s2, mhartid
  // If hart id is not 0, wait for interrupt (wfi)
  beq s1, s2, _exit
_sub_loop:
  wfi
  li sp, (PAYLOAD_DEST + 0xffff000)
  call main
  j _sub_loop
_exit:
  // If hart id is 0, perform the boot code
  li sp, (PAYLOAD_DEST + 0xffff000)
  csrr a0, mhartid
  call main
  csrr a0, mhartid
  // If hart id is 0, run the program in Memory
  la a1, dtb // dtb address for next level bootloader
  li s1, PAYLOAD_DEST
  jr s1

  .section .dtb
  .align 3
dtb:
