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
_prog_start:
  li s2, 0x8  
  csrw mie, s2
  li s1, 0
  csrr s2, mhartid
  beq s1, s2, _exit
//_hart0:
//  li s1, 0
//  bne s1, s2, _hart1  
//  li sp, (CORE1_PRIVATE_MEM - 0x10)
//  call main
//  j _hart0
_hart1:
  li s1, 1
  bne s1, s2, _hart2
_hart1_loop:
  wfi
  li s1, 0
  csrw mie, s1
  li s1, 0
  li s2, (CLINT_CTRL_ADDR + CLINT_MSIP1)
  sw s1, 0(s2)
  li sp, (CORE2_PRIVATE_MEM - 0x10)
  csrr a0, mhartid
  call main
  li s1, 0x8
  csrw mie, s1
  j _hart1_loop
_hart2:
  li s1, 2
  bne s1, s2, _hart3
_hart2_loop:
  wfi
  li s1, 0
  csrw mie, s1
  li s1, 0
  li s2, (CLINT_CTRL_ADDR + CLINT_MSIP2)
  sw s1, 0(s2)  
  li sp, (CORE3_PRIVATE_MEM - 0x10)
  csrr a0, mhartid
  call main
  li s1, 0x8
  csrw mie, s1  
  j _hart2_loop
_hart3:
  li s1, 3
  bne s1, s2, _trap
_hart3_loop:
  wfi
  li s1, 0
  csrw mie, s1
  li s1, 0
  li s2, (CLINT_CTRL_ADDR + CLINT_MSIP3)
  sw s1, 0(s2) 
  li sp, (END_PRIVATE_MEM - 0x10)
  csrr a0, mhartid
  call main
  li s1, 0x8
  csrw mie, s1  
  j _hart3_loop  
_exit:
  // If hart id is 0, perform the boot code
  li sp, (CORE1_PRIVATE_MEM - 0x10)
  csrr a0, mhartid
  call main
  csrr a0, mhartid
  la a1, dtb // dtb address for next level bootloader
  li s1, PAYLOAD_DEST
  jr s1
_trap:
  j _trap

  .section .dtb
  .align 3
dtb:
