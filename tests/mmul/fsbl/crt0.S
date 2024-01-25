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

  csrr a0, mhartid
  li s1, 0
  beq a0, s1, _hart0
  li s1, 1
  beq a0, s1, _hart1
  li s1, 2
  beq a0, s1, _hart2
  li s1, 3
  beq a0, s1, _hart3  
_hart0:
  li s1, 0
  li t0, BRAM_MEM_ADDR
  amoswap.w t1, s1, (t0)
_hart0_loop:  
  //li sp, (CORE0_PRIVATE_MEM + 0xFF0)
  //call main
  j  _exit
_hart1:
  wfi
  csrw mie, zero    
  li sp, (CORE1_PRIVATE_MEM + 0xFF0)
  call main
  li s1, 0x8
  csrw mie, s1  
  j  _hart1
_hart2:
  wfi
  csrw mie, zero  
  li sp, (CORE2_PRIVATE_MEM + 0xFF0)
  call main
  li s1, 0x8
  csrw mie, s1  
  j  _hart2
_hart3:
  wfi
  csrw mie, zero
  li sp, (CORE3_PRIVATE_MEM + 0xFF0)
  call main
  li s1, 0x8
  csrw mie, s1  
  call _hart3
_exit:
  li s1, 0x8
  csrw mie, s1
  csrr a0, mhartid
  la a1, dtb // dtb address for next level bootloader
  li s1, PAYLOAD_DEST
  jr s1
_trap:
  j _trap


  .section .dtb
  .align 3
dtb:
